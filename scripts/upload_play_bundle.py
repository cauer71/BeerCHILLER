#!/usr/bin/env python3
"""
Upload an Android App Bundle to Google Play using the Play Developer API.

Flow:
1. Create an edit.
2. Upload the AAB into that edit.
3. Assign the bundle to a track.
4. Commit the edit.

Prerequisites:
- A Google Play service account with Play Console access and release permissions.
- The service account JSON key file.
- The app must already be set up in Play Console, including Play App Signing.

Official API docs:
- https://developers.google.com/android-publisher/getting_started
- https://developers.google.com/android-publisher/api-ref/rest/v3/edits/insert
- https://developers.google.com/android-publisher/api-ref/rest/v3/edits.bundles/upload
- https://developers.google.com/android-publisher/api-ref/rest/v3/edits.tracks/update
- https://developers.google.com/android-publisher/api-ref/rest/v3/edits/commit
"""

from __future__ import annotations

import argparse
import json
import re
import sys
from pathlib import Path

import requests
from google.auth.transport.requests import AuthorizedSession
from google.oauth2 import service_account


API_ROOT = "https://androidpublisher.googleapis.com/androidpublisher/v3"
UPLOAD_ROOT = "https://androidpublisher.googleapis.com/upload/androidpublisher/v3"
SCOPE = "https://www.googleapis.com/auth/androidpublisher"


def eprint(*args: object) -> None:
    print(*args, file=sys.stderr)


def load_credentials(service_account_json: Path):
    if not service_account_json.exists():
        raise FileNotFoundError(f"Service account JSON not found: {service_account_json}")
    return service_account.Credentials.from_service_account_file(
        str(service_account_json),
        scopes=[SCOPE],
    )


def read_version_name(app_build_file: Path) -> str | None:
    if not app_build_file.exists():
        return None

    match = re.search(r'versionName\s*=\s*"([^"]+)"', app_build_file.read_text(encoding="utf-8"))
    if match:
        return match.group(1)
    return None


def read_release_notes(notes_file: Path) -> list[dict[str, str]] | None:
    if not notes_file.exists():
        return None

    lines = notes_file.read_text(encoding="utf-8").splitlines()
    locales: dict[str, list[str]] = {}
    current_locale: str | None = None
    current_lines: list[str] = []

    def flush_locale() -> None:
        nonlocal current_locale, current_lines
        if current_locale and current_lines:
            locales[current_locale] = current_lines[:]
        current_lines = []

    for raw_line in lines:
        line = raw_line.strip()
        if line.startswith("## "):
            flush_locale()
            current_locale = line[3:].strip()
            continue

        if current_locale is None:
            continue

        if line == "":
            continue

        if line.startswith("#"):
            continue

        if line.startswith("- "):
            current_lines.append(line[2:].strip())
            continue

        current_lines.append(line)

    flush_locale()

    if not locales:
        return None

    release_notes = []
    for language, notes in locales.items():
        release_notes.append(
            {
                "language": language,
                "text": "\n".join(notes),
            }
        )
    return release_notes


def request_json(session: AuthorizedSession, method: str, url: str, **kwargs):
    response = session.request(method, url, timeout=180, **kwargs)
    if response.ok:
        if response.content:
            return response.json()
        return None

    try:
        details = response.json()
    except ValueError:
        details = response.text
    raise RuntimeError(
        f"{method} {url} failed with {response.status_code}: {details}"
    )


def create_edit(session: AuthorizedSession, package_name: str) -> str:
    url = f"{API_ROOT}/applications/{package_name}/edits"
    payload = {}
    result = request_json(session, "POST", url, json=payload)
    edit_id = result.get("id") or result.get("editId")
    if not edit_id:
        raise RuntimeError(f"Create edit response did not include an edit id: {result}")
    return edit_id


def upload_bundle(session: AuthorizedSession, package_name: str, edit_id: str, aab_path: Path):
    url = f"{UPLOAD_ROOT}/applications/{package_name}/edits/{edit_id}/bundles"
    params = {"uploadType": "media"}
    headers = {"Content-Type": "application/octet-stream"}
    with aab_path.open("rb") as bundle_file:
        result = request_json(
            session,
            "POST",
            url,
            params=params,
            data=bundle_file,
            headers=headers,
        )

    version_code = result.get("versionCode")
    if version_code is None:
        raise RuntimeError(f"Upload response did not include versionCode: {result}")
    return result, str(version_code)


def update_track(
    session: AuthorizedSession,
    package_name: str,
    edit_id: str,
    track: str,
    version_code: str,
    release_name: str,
    release_notes: list[dict[str, str]] | None,
    status: str,
    user_fraction: float | None,
):
    url = f"{API_ROOT}/applications/{package_name}/edits/{edit_id}/tracks/{track}"
    release = {
        "name": release_name,
        "status": status,
        "versionCodes": [version_code],
    }
    if release_notes:
        release["releaseNotes"] = release_notes
    if user_fraction is not None:
        release["userFraction"] = user_fraction

    payload = {"releases": [release]}
    return request_json(session, "PUT", url, json=payload)


def commit_edit(
    session: AuthorizedSession,
    package_name: str,
    edit_id: str,
    changes_not_sent_for_review: bool,
):
    url = f"{API_ROOT}/applications/{package_name}/edits/{edit_id}:commit"
    params = {}
    if changes_not_sent_for_review:
        params["changesNotSentForReview"] = "true"
    return request_json(session, "POST", url, params=params)


def parse_args():
    parser = argparse.ArgumentParser(
        description="Upload an Android App Bundle to Google Play."
    )
    parser.add_argument(
        "--package",
        default="com.bierchiller.app",
        help="Android applicationId / package name.",
    )
    parser.add_argument(
        "--aab",
        default=r"app\build\outputs\bundle\release\app-release.aab",
        help="Path to the AAB file.",
    )
    parser.add_argument(
        "--service-account",
        required=True,
        help="Path to the Google service account JSON key.",
    )
    parser.add_argument(
        "--track",
        default="BeerChiller",
        help="Play track to update, for example BeerChiller, production, internal, beta, or alpha.",
    )
    parser.add_argument(
        "--release-name",
        default=None,
        help="Release name shown in Play Console. Defaults to BierCHILLER <versionName>.",
    )
    parser.add_argument(
        "--release-notes-file",
        default=None,
        help="Markdown file with release notes grouped by locale under ## de-DE / ## en-US.",
    )
    parser.add_argument(
        "--status",
        default="completed",
        choices=["draft", "inProgress", "completed", "halted"],
        help="Release status to set on the track.",
    )
    parser.add_argument(
        "--user-fraction",
        type=float,
        default=None,
        help="Optional rollout fraction for staged production releases.",
    )
    parser.add_argument(
        "--changes-not-sent-for-review",
        action="store_true",
        help="Commit the edit without sending changes for review immediately.",
    )
    return parser.parse_args()


def main() -> int:
    args = parse_args()

    aab_path = Path(args.aab).expanduser().resolve()
    service_account_json = Path(args.service_account).expanduser().resolve()

    if not aab_path.exists():
        eprint(f"AAB not found: {aab_path}")
        return 2

    if args.user_fraction is not None and not (0.0 < args.user_fraction <= 1.0):
        eprint("--user-fraction must be between 0 and 1.")
        return 2

    if args.user_fraction is not None and args.status != "inProgress":
        eprint("--user-fraction is only valid with --status inProgress.")
        return 2

    credentials = load_credentials(service_account_json)
    session = AuthorizedSession(credentials)

    release_name = args.release_name
    if not release_name:
        version_name = read_version_name(Path("app/build.gradle.kts"))
        release_name = f"BierCHILLER {version_name}" if version_name else "BierCHILLER release"

    release_notes = None
    if args.release_notes_file:
        release_notes = read_release_notes(Path(args.release_notes_file).expanduser().resolve())

    eprint(f"Creating edit for {args.package} ...")
    edit_id = create_edit(session, args.package)
    eprint(f"Edit id: {edit_id}")

    eprint(f"Uploading AAB: {aab_path.name} ...")
    upload_result, version_code = upload_bundle(session, args.package, edit_id, aab_path)
    eprint(
        "Uploaded bundle versionCode="
        f"{version_code}"
        + (f", sha256={upload_result.get('sha256')}" if upload_result.get("sha256") else "")
    )

    eprint(f"Updating track {args.track} ...")
    update_track(
        session,
        args.package,
        edit_id,
        args.track,
        version_code,
        release_name,
        release_notes,
        args.status,
        args.user_fraction,
    )

    eprint("Committing edit ...")
    commit_result = commit_edit(
        session,
        args.package,
        edit_id,
        args.changes_not_sent_for_review,
    )

    print(json.dumps(
        {
            "package": args.package,
            "track": args.track,
            "editId": edit_id,
            "versionCode": version_code,
            "releaseName": release_name,
            "releaseNotesFile": args.release_notes_file,
            "commitResult": commit_result,
        },
        indent=2,
        ensure_ascii=False,
    ))
    return 0


if __name__ == "__main__":
    raise SystemExit(main())

#!/usr/bin/env python3
from __future__ import annotations

import argparse
import json
from pathlib import Path

from google.auth.transport.requests import AuthorizedSession
from google.oauth2 import service_account


API_ROOT = "https://androidpublisher.googleapis.com/androidpublisher/v3"
UPLOAD_ROOT = "https://androidpublisher.googleapis.com/upload/androidpublisher/v3"
SCOPE = "https://www.googleapis.com/auth/androidpublisher"


def request_json(session: AuthorizedSession, method: str, url: str, **kwargs):
    response = session.request(method, url, timeout=180, **kwargs)
    if response.ok:
        return response.json() if response.content else None
    try:
        details = response.json()
    except ValueError:
        details = response.text
    raise RuntimeError(f"{method} {url} failed with {response.status_code}: {details}")


def main() -> int:
    parser = argparse.ArgumentParser(description="Replace Google Play phone screenshots.")
    parser.add_argument("--service-account", required=True)
    parser.add_argument("--package", default="com.bierchiller.app")
    parser.add_argument("--language", default="en-US")
    parser.add_argument("--image-type", default="phoneScreenshots")
    parser.add_argument("screenshots", nargs="+")
    args = parser.parse_args()

    credentials = service_account.Credentials.from_service_account_file(
        args.service_account,
        scopes=[SCOPE],
    )
    session = AuthorizedSession(credentials)

    edit = request_json(session, "POST", f"{API_ROOT}/applications/{args.package}/edits", json={})
    edit_id = edit["id"]

    base = f"{API_ROOT}/applications/{args.package}/edits/{edit_id}/listings/{args.language}/{args.image_type}"
    request_json(session, "DELETE", base)

    uploaded = []
    for screenshot in args.screenshots:
        path = Path(screenshot)
        url = f"{UPLOAD_ROOT}/applications/{args.package}/edits/{edit_id}/listings/{args.language}/{args.image_type}"
        with path.open("rb") as fh:
            result = request_json(
                session,
                "POST",
                url,
                params={"uploadType": "media"},
                headers={"Content-Type": "image/png"},
                data=fh,
            )
        uploaded.append({"file": str(path), "result": result})

    commit = request_json(session, "POST", f"{API_ROOT}/applications/{args.package}/edits/{edit_id}:commit")
    print(json.dumps({"editId": edit_id, "uploaded": uploaded, "commitResult": commit}, indent=2))
    return 0


if __name__ == "__main__":
    raise SystemExit(main())

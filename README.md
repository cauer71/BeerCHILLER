# BierCHILLER

BierCHILLER is a native Android application for estimating the cooling time of bottled beer in a refrigerator or freezer and for starting a reliable Android alarm at the calculated target time. The project combines a compact thermodynamic cooling model with a fullscreen mobile user interface optimized for quick use at home.

## Scientific Model

The cooling calculation is based on a lumped-capacitance approximation of a beer bottle. The beer and glass are treated as one thermally well-mixed body, while the surrounding refrigerator or freezer air is modeled as a constant-temperature reservoir.

The implemented model follows the free-convection derivation used for a horizontal cylindrical bottle:

```text
theta = (T - T_L) / (T_a - T_L)

dtheta / dtau + theta^(5/4) = 0

theta(tau) = (4 / (tau + 4))^4
```

Where:

- `T` is the current beer temperature.
- `T_a` is the initial beer temperature.
- `T_L` is the device temperature, such as freezer or refrigerator temperature.
- `theta` is the dimensionless temperature difference.
- `tau` is the dimensionless cooling time.

For a requested target temperature `T_e`, the target state is:

```text
theta_e = (T_e - T_L) / (T_a - T_L)
tau_e = 4 * (theta_e^(-1/4) - 1)
```

The app maps this dimensionless time to minutes through an empirically calibrated cooling-rate constant and bottle-size factors. This keeps the physically motivated curve shape from the free-convection model while allowing practical calibration for real household refrigerators, freezers, bottle sizes, bottle placement, airflow, and glass geometry.

During an active timer, BierCHILLER uses the same curve to estimate and display the current beer temperature:

```text
T(t) = T_L + (T_a - T_L) * (4 / (tau + 4))^4
```

## Assumptions And Limits

The model intentionally abstracts real cooling behavior. The result is an estimate, not a laboratory measurement.

Main assumptions:

- Beer and bottle glass are represented by one average temperature.
- The device air temperature is constant during the cooling process.
- Bottle orientation and geometry are approximated by a cylindrical model.
- Heat transfer is dominated by convection between bottle and air.
- Bottle-size corrections are applied by empirical scaling factors.

Practical deviations can occur because domestic freezers cycle, air movement varies, bottles differ in shape and wall thickness, and the starting temperature may not be uniform.

## Android Features

- Standby-safe alarm scheduling through Android alarm APIs.
- Persistent timer state across app restarts.
- Persistent bottle size, device mode, temperature settings, and visual mode.
- Portrait and landscape layouts.
- Classic UI and beer-background visual mode.
- Multilingual interface.
- Google Play compatible package name: `com.bierchiller.app`.
- Release builds target the current Android SDK line used by the project.

## Build

Use the local Android toolchain in `toolchain/` or a standard Android Studio setup.

Build the release APK:

```powershell
.\toolchain\gradle-9.1.0\gradle-9.1.0\bin\gradle.bat :app:assembleRelease
```

Build the release Android App Bundle:

```powershell
.\toolchain\gradle-9.1.0\gradle-9.1.0\bin\gradle.bat :app:bundleRelease
```

The generated Play Console bundle is written to:

```text
app/build/outputs/bundle/release/app-release.aab
```

## Publish To Google Play

The repo includes `scripts/upload_play_bundle.py` for the Google Play Developer API.

Prerequisites:

- A Google Play service account JSON key.
- The service account must be added in Play Console with permission to release to the target track.
- Play App Signing must be enabled for the app.

Example:

```powershell
python .\scripts\upload_play_bundle.py `
  --service-account C:\path\to\play-service-account.json `
  --aab .\app\build\outputs\bundle\release\app-release.aab `
  --track internal
```

Useful flags:

- `--track production|internal|closed|open`
- `--status completed|inProgress|draft|halted`
- `--release-name "1.3.44"`
- `--changes-not-sent-for-review`

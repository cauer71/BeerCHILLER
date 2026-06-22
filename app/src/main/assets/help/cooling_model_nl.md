# Calculation model

The app calculates beer cooling time in a refrigerator or freezer with **BeerChiller Calibrated V2**.

The model is a practical approximation. It uses the starting temperature, target temperature, device temperature, container, volume, and position. Real appliances can cool faster or slower because of airflow, contact surfaces, loading, and door openings.

## 1. Temperature difference

\[
\Delta_0 = T_0 - T_D
\]

\[
\theta = \frac{T_Z - T_D}{T_0 - T_D}
\]

Where:

- \(T_0\): starting beer temperature
- \(T_Z\): target beer temperature
- \(T_D\): refrigerator or freezer temperature
- \(\theta\): dimensionless target ratio

## 2. Cooling curve

Cooling slows down as the beer approaches the appliance temperature. BeerChiller models this with an empirical exponent:

\[
n = 0.15
\]

\[
\frac{d\Delta}{dt} = -k \cdot \Delta^{1+n}
\]

## 3. Final app formula

\[
t =
\tau_0
\cdot f_D
\cdot f_P
\cdot
\left(\frac{25}{T_0-T_D}\right)^{0.15}
\cdot
\frac{
\left(
\frac{T_Z-T_D}{T_0-T_D}
\right)^{-0.15}
-1
}{0.15}
\]

\[
t_{app}=\lceil t \rceil
\]

## 4. Constants

Base values for \(\tau_0\):

| Container | Volume | \(\tau_0\) |
|---|---:|---:|
| Bottle | 0.33 l | 87 min |
| Bottle | 0.5 l | 110 min |
| Bottle | 1.0 l | 155 min |
| Can | 0.33 l | 85 min |
| Can | 0.5 l | 105 min |

Device factors:

| Device | \(f_D\) |
|---|---:|
| Refrigerator | 1.00 |
| Freezer | 0.84 |

Position factors:

| Container | Standing | Lying |
|---|---:|---:|
| Bottle | 1.00 | 0.95 |
| Can | 1.00 | 0.92 |

## 5. Temperature during the timer

\[
\theta(t)=
\left(
1+n\cdot\frac{t}{\tau_{eff}}
\right)^{-1/n}
\]

\[
T(t)=T_D+(T_0-T_D)\cdot\theta(t)
\]

## 6. Validity rules

- If \(T_0 \le T_Z\), the beer is already cold enough.
- If \(T_Z \le T_D\), the target temperature is not meaningfully reachable.
- Only \(0 < \theta < 1\) is valid.

## Example

A 0.33 l glass bottle from 39.5 degrees Celsius to 6 degrees Celsius in a freezer at -17.5 degrees Celsius gives:

\[
t_{app} \approx 62\,\text{min}
\]

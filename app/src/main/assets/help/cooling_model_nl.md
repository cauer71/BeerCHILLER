# Berekeningsmodel

De app berekent de koeltijd van bier in een koelkast of vriezer met het model **BeerCHILLER Calibrated V2**.

Het model is een praktische benadering. Het gebruikt de starttemperatuur, doeltemperatuur, apparaattemperatuur, verpakking, volume en positie. In de praktijk kunnen apparaten sneller of langzamer koelen door luchtstroming, contactoppervlakken, belading en het openen van de deur.

## 1. Temperatuurverschil

\[
\Delta_0 = T_0 - T_D
\]

\[
\theta = \frac{T_Z - T_D}{T_0 - T_D}
\]

Waarbij:

- \(T_0\): starttemperatuur van het bier
- \(T_Z\): doeltemperatuur
- \(T_D\): temperatuur van koelkast of vriezer
- \(\theta\): dimensieloze verhouding voor de doeltemperatuur

## 2. Koelcurve

Het koelen vertraagt naarmate het bier dichter bij de apparaattemperatuur komt. BeerCHILLER modelleert dit met een empirische exponent:

\[
n = 0.15
\]

\[
\frac{d\Delta}{dt} = -k \cdot \Delta^{1+n}
\]

## 3. Eindformule van de app

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

## 4. Constanten

Basiswaarden voor \(\tau_0\):

| Verpakking | Volume | \(\tau_0\) |
|---|---:|---:|
| Fles | 0.33 l | 87 min |
| Fles | 0.5 l | 110 min |
| Fles | 1.0 l | 155 min |
| Blik | 0.33 l | 85 min |
| Blik | 0.5 l | 105 min |

Apparaatfactoren:

| Apparaat | \(f_D\) |
|---|---:|
| Koelkast | 1.00 |
| Vriezer | 0.84 |

Positiefactoren:

| Verpakking | Staand | Liggend |
|---|---:|---:|
| Fles | 1.00 | 0.95 |
| Blik | 1.00 | 0.92 |

## 5. Temperatuur tijdens de timer

\[
\theta(t)=
\left(
1+n\cdot\frac{t}{\tau_{eff}}
\right)^{-1/n}
\]

\[
T(t)=T_D+(T_0-T_D)\cdot\theta(t)
\]

## 6. Geldigheidsregels

- Als \(T_0 \le T_Z\), is het bier al koud genoeg.
- Als \(T_Z \le T_D\), is de doeltemperatuur niet zinvol bereikbaar.
- Alleen \(0 < \theta < 1\) is geldig.

## Voorbeeld

Een glazen fles van 0.33 l van 39.5 graden Celsius naar 6 graden Celsius in een vriezer van -17.5 graden Celsius geeft:

\[
t_{app} \approx 62\,\text{min}
\]

# Model obliczeniowy

Aplikacja oblicza czas chlodzenia piwa w lodowce lub zamrazarce za pomoca modelu **BeerCHILLER Calibrated V2**.

Model jest praktycznym przyblizeniem. Uwzglednia temperature poczatkowa, temperature docelowa, temperature urzadzenia, pojemnik, objetosc i polozenie. W praktyce urzadzenia moga chlodzic szybciej lub wolniej z powodu przeplywu powietrza, powierzchni kontaktu, zaladowania i otwierania drzwi.

## 1. Roznica temperatur

\[
\Delta_0 = T_0 - T_D
\]

\[
\theta = \frac{T_Z - T_D}{T_0 - T_D}
\]

Gdzie:

- \(T_0\): poczatkowa temperatura piwa
- \(T_Z\): temperatura docelowa
- \(T_D\): temperatura lodowki lub zamrazarki
- \(\theta\): bezwymiarowy stosunek temperatury docelowej

## 2. Krzywa chlodzenia

Chlodzenie zwalnia, gdy piwo zbliza sie do temperatury urzadzenia. BeerCHILLER modeluje to za pomoca wykladnika empirycznego:

\[
n = 0.15
\]

\[
\frac{d\Delta}{dt} = -k \cdot \Delta^{1+n}
\]

## 3. Koncowy wzor aplikacji

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

## 4. Stale

Wartosci bazowe dla \(\tau_0\):

| Pojemnik | Objetosc | \(\tau_0\) |
|---|---:|---:|
| Butelka | 0.33 l | 87 min |
| Butelka | 0.5 l | 110 min |
| Butelka | 1.0 l | 155 min |
| Puszka | 0.33 l | 85 min |
| Puszka | 0.5 l | 105 min |

Wspolczynniki urzadzenia:

| Urzadzenie | \(f_D\) |
|---|---:|
| Lodowka | 1.00 |
| Zamrazarka | 0.84 |

Wspolczynniki polozenia:

| Pojemnik | Stojaco | Lezaco |
|---|---:|---:|
| Butelka | 1.00 | 0.95 |
| Puszka | 1.00 | 0.92 |

## 5. Temperatura podczas timera

\[
\theta(t)=
\left(
1+n\cdot\frac{t}{\tau_{eff}}
\right)^{-1/n}
\]

\[
T(t)=T_D+(T_0-T_D)\cdot\theta(t)
\]

## 6. Reguly poprawnosci

- Jesli \(T_0 \le T_Z\), piwo jest juz wystarczajaco zimne.
- Jesli \(T_Z \le T_D\), temperatura docelowa nie jest realnie osiagalna.
- Poprawny jest tylko zakres \(0 < \theta < 1\).

## Przyklad

Szklana butelka 0.33 l od 39.5 stopni Celsjusza do 6 stopni Celsjusza w zamrazarce o temperaturze -17.5 stopni Celsjusza daje:

\[
t_{app} \approx 62\,\text{min}
\]

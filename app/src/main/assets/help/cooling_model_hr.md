# Model izracuna

Aplikacija izracunava vrijeme hladenja piva u hladnjaku ili zamrzivacu pomocu modela **BeerCHILLER Calibrated V2**.

Model je prakticna aproksimacija. Koristi pocetnu temperaturu, ciljnu temperaturu, temperaturu uredaja, ambalazu, volumen i polozaj. U stvarnim uvjetima uredaji mogu hladiti brze ili sporije zbog protoka zraka, kontaktnih povrsina, napunjenosti i otvaranja vrata.

## 1. Razlika temperature

\[
\Delta_0 = T_0 - T_D
\]

\[
\theta = \frac{T_Z - T_D}{T_0 - T_D}
\]

Gdje:

- \(T_0\): pocetna temperatura piva
- \(T_Z\): ciljna temperatura
- \(T_D\): temperatura hladnjaka ili zamrzivaca
- \(\theta\): bezdimenzijski omjer ciljne temperature

## 2. Krivulja hladenja

Hladenje se usporava kako se pivo priblizava temperaturi uredaja. BeerCHILLER to modelira empirijskim eksponentom:

\[
n = 0.15
\]

\[
\frac{d\Delta}{dt} = -k \cdot \Delta^{1+n}
\]

## 3. Konacna formula aplikacije

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

## 4. Konstante

Osnovne vrijednosti za \(\tau_0\):

| Ambalaza | Volumen | \(\tau_0\) |
|---|---:|---:|
| Boca | 0.33 l | 87 min |
| Boca | 0.5 l | 110 min |
| Boca | 1.0 l | 155 min |
| Limenka | 0.33 l | 85 min |
| Limenka | 0.5 l | 105 min |

Faktori uredaja:

| Uredaj | \(f_D\) |
|---|---:|
| Hladnjak | 1.00 |
| Zamrzivac | 0.84 |

Faktori polozaja:

| Ambalaza | Uspravno | Polegnuto |
|---|---:|---:|
| Boca | 1.00 | 0.95 |
| Limenka | 1.00 | 0.92 |

## 5. Temperatura tijekom timera

\[
\theta(t)=
\left(
1+n\cdot\frac{t}{\tau_{eff}}
\right)^{-1/n}
\]

\[
T(t)=T_D+(T_0-T_D)\cdot\theta(t)
\]

## 6. Pravila valjanosti

- Ako je \(T_0 \le T_Z\), pivo je vec dovoljno hladno.
- Ako je \(T_Z \le T_D\), ciljna temperatura nije smisleno dostizna.
- Vrijedi samo raspon \(0 < \theta < 1\).

## Primjer

Staklena boca od 0.33 l od 39.5 stupnjeva Celzija do 6 stupnjeva Celzija u zamrzivacu na -17.5 stupnjeva Celzija daje:

\[
t_{app} \approx 62\,\text{min}
\]

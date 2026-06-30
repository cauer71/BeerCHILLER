# Modello di calcolo

L'app calcola il tempo di raffreddamento della birra in frigorifero o in congelatore con il modello **BeerChiller Calibrated V2**.

Il modello è una stima pratica. Usa la temperatura iniziale, la temperatura desiderata, la temperatura dell'apparecchio, il contenitore, il volume e la posizione. Gli apparecchi reali possono raffreddare più rapidamente o più lentamente a causa del flusso d'aria, delle superfici di contatto, del carico e delle aperture dello sportello.

## 1. Differenza di temperatura

\[
\Delta_0 = T_0 - T_D
\]

\[
\theta = \frac{T_Z - T_D}{T_0 - T_D}
\]

Dove:

- \(T_0\): temperatura iniziale della birra
- \(T_Z\): temperatura desiderata
- \(T_D\): temperatura del frigorifero o del congelatore
- \(\theta\): rapporto adimensionale della temperatura desiderata

## 2. Curva di raffreddamento

Il raffreddamento rallenta man mano che la birra si avvicina alla temperatura dell'apparecchio. BeerCHILLER lo modella con un esponente empirico:

\[
n = 0.15
\]

\[
\frac{d\Delta}{dt} = -k \cdot \Delta^{1+n}
\]

## 3. Formula finale dell'app

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

## 4. Costanti

Valori base per \(\tau_0\):

| Contenitore | Volume | \(\tau_0\) |
|---|---:|---:|
| Bottiglia | 0.33 l | 87 min |
| Bottiglia | 0.5 l | 110 min |
| Bottiglia | 1.0 l | 155 min |
| Lattina | 0.33 l | 85 min |
| Lattina | 0.5 l | 105 min |

Fattori dell'apparecchio:

| Apparecchio | \(f_D\) |
|---|---:|
| Frigorifero | 1.00 |
| Congelatore | 0.84 |

Fattori di posizione:

| Contenitore | Verticale | Orizzontale |
|---|---:|---:|
| Bottiglia | 1.00 | 0.95 |
| Lattina | 1.00 | 0.92 |

## 5. Temperatura durante il timer

\[
\theta(t)=
\left(
1+n\cdot\frac{t}{\tau_{eff}}
\right)^{-1/n}
\]

\[
T(t)=T_D+(T_0-T_D)\cdot\theta(t)
\]

## 6. Regole di validità

- Se \(T_0 \le T_Z\), la birra è già abbastanza fredda.
- Se \(T_Z \le T_D\), la temperatura desiderata non è raggiungibile in modo significativo.
- È valido solo \(0 < \theta < 1\).

## Esempio

Una bottiglia di vetro da 0.33 l da 39.5 gradi Celsius a 6 gradi Celsius in un congelatore a -17.5 gradi Celsius richiede:

\[
t_{app} \approx 62\,\text{min}
\]

# Vypocetni model

Aplikace pocita dobu chlazeni piva v lednici nebo mrazaku pomoci modelu **BeerCHILLER Calibrated V2**.

Model je prakticke priblizeni. Pouziva pocatecni teplotu, cilovou teplotu, teplotu zarizeni, obal, objem a polohu. Ve skutecnosti mohou spotrebice chladit rychleji nebo pomaleji kvuli proudění vzduchu, kontaktnim plocham, naplneni a otevirani dveri.

## 1. Teplotni rozdil

\[
\Delta_0 = T_0 - T_D
\]

\[
\theta = \frac{T_Z - T_D}{T_0 - T_D}
\]

Kde:

- \(T_0\): pocatecni teplota piva
- \(T_Z\): cilova teplota
- \(T_D\): teplota lednice nebo mrazaku
- \(\theta\): bezrozmerny pomer cilove teploty

## 2. Krivka chlazeni

Chlazeni se zpomaluje, kdyz se pivo blizi teplote spotrebice. BeerCHILLER to modeluje empirickym exponentem:

\[
n = 0.15
\]

\[
\frac{d\Delta}{dt} = -k \cdot \Delta^{1+n}
\]

## 3. Konecny vzorec aplikace

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

## 4. Konstanty

Zakladni hodnoty pro \(\tau_0\):

| Obal | Objem | \(\tau_0\) |
|---|---:|---:|
| Lahev | 0.33 l | 87 min |
| Lahev | 0.5 l | 110 min |
| Lahev | 1.0 l | 155 min |
| Plechovka | 0.33 l | 85 min |
| Plechovka | 0.5 l | 105 min |

Faktory zarizeni:

| Zarizeni | \(f_D\) |
|---|---:|
| Lednice | 1.00 |
| Mrazak | 0.84 |

Faktory polohy:

| Obal | Nastojato | Nalezato |
|---|---:|---:|
| Lahev | 1.00 | 0.95 |
| Plechovka | 1.00 | 0.92 |

## 5. Teplota behem timeru

\[
\theta(t)=
\left(
1+n\cdot\frac{t}{\tau_{eff}}
\right)^{-1/n}
\]

\[
T(t)=T_D+(T_0-T_D)\cdot\theta(t)
\]

## 6. Pravidla platnosti

- Pokud \(T_0 \le T_Z\), pivo je uz dostatecne studene.
- Pokud \(T_Z \le T_D\), cilove teploty nelze smysluplne dosahnout.
- Platny je pouze rozsah \(0 < \theta < 1\).

## Priklad

Sklenena lahev 0.33 l z 39.5 stupne Celsia na 6 stupnu Celsia v mrazaku pri -17.5 stupne Celsia dava:

\[
t_{app} \approx 62\,\text{min}
\]

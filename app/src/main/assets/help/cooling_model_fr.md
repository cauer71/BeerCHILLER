# Modele de calcul

L'application calcule le temps de refroidissement de la biere dans un refrigerateur ou un congelateur avec le modele **BeerCHILLER Calibrated V2**.

Le modele est une approximation pratique. Il utilise la temperature initiale, la temperature cible, la temperature de l'appareil, le contenant, le volume et la position. En pratique, les appareils peuvent refroidir plus vite ou plus lentement selon le flux d'air, les surfaces de contact, le chargement et les ouvertures de porte.

## 1. Difference de temperature

\[
\Delta_0 = T_0 - T_D
\]

\[
\theta = \frac{T_Z - T_D}{T_0 - T_D}
\]

Ou:

- \(T_0\): temperature initiale de la biere
- \(T_Z\): temperature cible
- \(T_D\): temperature du refrigerateur ou du congelateur
- \(\theta\): rapport sans dimension de la temperature cible

## 2. Courbe de refroidissement

Le refroidissement ralentit lorsque la biere se rapproche de la temperature de l'appareil. BeerCHILLER modelise cela avec un exposant empirique:

\[
n = 0.15
\]

\[
\frac{d\Delta}{dt} = -k \cdot \Delta^{1+n}
\]

## 3. Formule finale de l'application

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

## 4. Constantes

Valeurs de base pour \(\tau_0\):

| Contenant | Volume | \(\tau_0\) |
|---|---:|---:|
| Bouteille | 0.33 l | 87 min |
| Bouteille | 0.5 l | 110 min |
| Bouteille | 1.0 l | 155 min |
| Canette | 0.33 l | 85 min |
| Canette | 0.5 l | 105 min |

Facteurs de l'appareil:

| Appareil | \(f_D\) |
|---|---:|
| Refrigerateur | 1.00 |
| Congelateur | 0.84 |

Facteurs de position:

| Contenant | Debout | Couche |
|---|---:|---:|
| Bouteille | 1.00 | 0.95 |
| Canette | 1.00 | 0.92 |

## 5. Temperature pendant le minuteur

\[
\theta(t)=
\left(
1+n\cdot\frac{t}{\tau_{eff}}
\right)^{-1/n}
\]

\[
T(t)=T_D+(T_0-T_D)\cdot\theta(t)
\]

## 6. Regles de validite

- Si \(T_0 \le T_Z\), la biere est deja assez froide.
- Si \(T_Z \le T_D\), la temperature cible n'est pas atteignable de facon significative.
- Seul \(0 < \theta < 1\) est valide.

## Exemple

Une bouteille en verre de 0.33 l de 39.5 degres Celsius a 6 degres Celsius dans un congelateur a -17.5 degres Celsius donne:

\[
t_{app} \approx 62\,\text{min}
\]

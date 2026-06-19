# Modèle de calcul

L'application calcule le temps de refroidissement d'une bière dans le congélateur à l'aide d'un modèle physique d'approximation.

La boisson et le contenant sont considérés comme un seul réservoir thermique. La bouteille ou la canette sont approximées géométriquement par un cylindre. La chaleur est principalement transférée par la surface extérieure vers l'air froid du congélateur.

Le calcul reste une approximation. Les congélateurs réels peuvent refroidir plus vite ou plus lentement selon le mouvement de l'air, les surfaces de contact et le type d'appareil.

## 1. Capacité thermique de la boisson et du contenant

La bière et le contenant stockent la chaleur ensemble.

\[
W = m_B c_B + m_G c_G
\]

Où :

- \(W\) : capacité thermique totale en J/K
- \(m_B\) : masse de la bière
- \(c_B\) : capacité thermique massique de la bière
- \(m_G\) : masse du contenant, en verre ou en aluminium
- \(c_G\) : capacité thermique massique du contenant

Pour la bière, l'application utilise :

\[
c_B = 4200 \,\frac{J}{kgK}
\]

Pour le verre :

\[
c_G = 840 \,\frac{J}{kgK}
\]

Pour les canettes en aluminium :

\[
c_G = 900 \,\frac{J}{kgK}
\]

## 2. Surface du contenant

Le transfert de chaleur dépend de la surface.

Pour les bouteilles, on utilise la surface latérale du cylindre :

\[
A = \pi d L
\]

Pour les canettes, on tient aussi compte des faces :

\[
A = \pi d L + \frac{\pi d^2}{2}
\]

Où :

- \(A\) : surface efficace
- \(d\) : diamètre
- \(L\) : longueur ou hauteur de la partie cylindrique

## 3. Température sans dimension

Pour la dérivation, l'écart de température est rendu sans dimension :

\[
\theta = \frac{T - T_L}{T_a - T_L}
\]

Où :

- \(T\) : température actuelle de la boisson
- \(T_a\) : température initiale
- \(T_L\) : température du congélateur

La température cible devient :

\[
\theta_e = \frac{T_e - T_L}{T_a - T_L}
\]

## 4. Échange thermique dépendant de la température

En convection naturelle, le coefficient de transfert thermique n'est pas constant.

L'application utilise :

\[
h = h_{\max} \theta^{1/4}
\]

Ainsi, le refroidissement commence plus rapidement au début, car la différence de température avec l'air du congélateur est plus grande. Ensuite, le refroidissement ralentit.

## 5. Coefficient maximal de transfert thermique

Pour la convection naturelle approximée sur un cylindre horizontal, l'application utilise :

\[
h_{\max}
=
\frac{k_L}{l}
\cdot 0{,}402
\cdot
\left(
\frac{g l^3}{T_{L,K} \nu_L \alpha_L}
\right)^{1/4}
\cdot
(T_a - T_L)^{1/4}
\]

avec la longueur caractéristique :

\[
l = \frac{\pi d}{2}
\]

Où :

- \(k_L\) : conductivité thermique de l'air
- \(\nu_L\) : viscosité cinématique de l'air
- \(\alpha_L\) : diffusivité thermique de l'air
- \(g\) : accélération de la pesanteur
- \(T_{L,K}\) : température du congélateur en Kelvin

Pour la température en Kelvin :

\[
T_{L,K} = T_L + 273{,}15
\]

## 6. Équation différentielle

À partir du bilan énergétique :

\[
W \frac{dT}{dt} = -h A (T - T_L)
\]

Avec la température sans dimension :

\[
\frac{d\theta}{dt}
=
-\frac{h_{\max} A}{W}
\theta^{5/4}
\]

L'exposant \(5/4\) apparaît parce que le coefficient d'échange thermique dépend lui-même de l'écart de température.

## 7. Formule du temps

Après résolution de l'équation différentielle, le temps jusqu'à la température cible est :

\[
t =
\frac{W}{h_{\max} A}
\cdot
4
\left[
\theta_e^{-1/4}
-1
\right]
\]

avec :

\[
\theta_e = \frac{T_e - T_L}{T_a - T_L}
\]

## 8. Calibration pour de vrais congélateurs

Un congélateur réel ne correspond pas exactement au modèle idéalisé. Le mouvement de l'air, le contact avec les clayettes et les surfaces froides peuvent accélérer le refroidissement.

C'est pourquoi l'application utilise un facteur de calibration :

\[
f_\text{calib}
\]

L'application est calibrée à partir d'un essai pratique avec une bouteille en verre de 0,33 l :

- Volume de la bouteille : 0,33 l
- Masse du verre : 214 g
- Température du congélateur : −17,5 °C
- Température initiale : 39,5 °C
- Température cible : 8,0 °C
- temps mesuré : 54,4 minutes

La formule finale est donc :

\[
t =
\frac{W}{h_{\max} A f_\text{calib}}
\cdot
4
\left[
\left(
\frac{T_e - T_L}{T_a - T_L}
\right)^{-1/4}
-1
\right]
\]

## 9. Extension à d'autres contenants

Les autres bouteilles et canettes sont extrapolées à partir de la bouteille en verre calibrée de 0,33 l en utilisant la capacité thermique et la surface.

L'application prend en charge :

- Bouteilles : 0,33 l, 0,5 l et 1,0 l
- Canettes : 0,33 l et 0,5 l

La bouteille en verre de 0,33 l est le cas le plus précis, car elle est calibrée avec une mesure réelle. Les autres contenants sont des approximations.

## 10. Orientation du contenant

Le calcul repose sur un modèle de convection libre pour un contenant cylindrique. Le cas de base est un contenant couché.

Pour les contenants debout, l’application applique actuellement un facteur d’approximation, car l’écoulement réel de l’air et le transfert thermique peuvent changer:

\[
t_\text{réel} = \frac{t_\text{modèle}}{f_\text{calib} \cdot f_\text{orientation}}
\]

Pour un contenant couché:

\[
f_\text{orientation} = 1{,}0
\]

Pour un contenant debout, la valeur actuelle est:

\[
f_\text{orientation} = 1{,}17
\]

Le temps calculé pour les contenants debout est ainsi divisé environ par 1,17.
## 11. Limites du modèle

Le calcul ne prend pas en compte :

- la formation de glace
- la chaleur de cristallisation
- les changements d'état
- l'agitation ou le mouvement de la bière
- le flux d'air exact dans le congélateur
- les différentes formes de bouteilles
- les surfaces de contact exactes avec la clayette

Près du point de congélation, le calcul devient moins fiable. Pour des températures de consommation normales comme 8 °C ou 6 °C, le modèle reste une approximation pratique.

## Exemple

Une bouteille en verre de 0,33 l passant de 20 °C à 8 °C dans un congélateur à −18 °C donne environ :

\[
t \approx 27\,\text{min}
\]

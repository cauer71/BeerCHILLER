# Berechnungsmodell

Die App berechnet die Abkühlzeit eines Bieres im Gefrierschrank mit einem physikalischen Näherungsmodell.

Das Getränk und der Behälter werden als gemeinsamer Wärmespeicher betrachtet. Die Flasche oder Dose wird geometrisch als Zylinder angenähert. Die Wärme wird hauptsächlich über die Außenfläche an die kalte Luft im Gefrierschrank abgegeben.

Die Berechnung ist eine Näherung. Reale Gefrierschränke können durch Luftbewegung, Kontaktflächen und unterschiedliche Bauarten schneller oder langsamer kühlen.

## 1. Wärmekapazität von Getränk und Behälter

Das Bier und der Behälter speichern gemeinsam Wärme.

\[
W = m_B c_B + m_G c_G
\]

Dabei bedeutet:

- \(W\): gesamte Wärmekapazität in J/K
- \(m_B\): Masse des Bieres
- \(c_B\): spezifische Wärmekapazität des Bieres
- \(m_G\): Masse des Behälters, also Glas oder Aluminium
- \(c_G\): spezifische Wärmekapazität des Behälters

Für Bier wird näherungsweise verwendet:

\[
c_B = 4200 \,\frac{J}{kgK}
\]

Für Glas:

\[
c_G = 840 \,\frac{J}{kgK}
\]

Für Aluminiumdosen:

\[
c_G = 900 \,\frac{J}{kgK}
\]

## 2. Oberfläche des Gebindes

Die Wärmeabgabe hängt von der Oberfläche ab.

Für Flaschen wird die Mantelfläche des Zylinders verwendet:

\[
A = \pi d L
\]

Für Dosen werden zusätzlich die Stirnflächen berücksichtigt:

\[
A = \pi d L + \frac{\pi d^2}{2}
\]

Dabei bedeutet:

- \(A\): wirksame Oberfläche
- \(d\): Durchmesser
- \(L\): Länge oder Höhe des zylindrischen Anteils

## 3. Dimensionslose Temperatur

Für die Herleitung wird die Temperaturdifferenz dimensionslos gemacht:

\[
\theta = \frac{T - T_L}{T_a - T_L}
\]

Dabei bedeutet:

- \(T\): aktuelle Getränketemperatur
- \(T_a\): Anfangstemperatur
- \(T_L\): Temperatur im Gefrierschrank

Die Zieltemperatur wird entsprechend:

\[
\theta_e = \frac{T_e - T_L}{T_a - T_L}
\]

## 4. Temperaturabhängiger Wärmeübergang

Bei freier Konvektion ist der Wärmeübergangskoeffizient nicht konstant.

Die App verwendet:

\[
h = h_{\max} \theta^{1/4}
\]

Dadurch kühlt das Bier am Anfang schneller, weil der Temperaturunterschied zur Gefrierschrankluft größer ist. Später wird die Abkühlung langsamer.

## 5. Maximaler Wärmeübergangskoeffizient

Für die angenäherte freie Konvektion am liegenden Zylinder wird verwendet:

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

mit der charakteristischen Länge:

\[
l = \frac{\pi d}{2}
\]

Dabei bedeutet:

- \(k_L\): Wärmeleitfähigkeit der Luft
- \(\nu_L\): kinematische Viskosität der Luft
- \(\alpha_L\): Temperaturleitfähigkeit der Luft
- \(g\): Erdbeschleunigung
- \(T_{L,K}\): Gefrierschranktemperatur in Kelvin

Für die Temperatur in Kelvin gilt:

\[
T_{L,K} = T_L + 273{,}15
\]

## 6. Differentialgleichung

Aus der Energiebilanz folgt:

\[
W \frac{dT}{dt} = -h A (T - T_L)
\]

Mit der dimensionslosen Temperatur ergibt sich:

\[
\frac{d\theta}{dt}
=
-\frac{h_{\max} A}{W}
\theta^{5/4}
\]

Die Potenz \(5/4\) entsteht, weil der Wärmeübergangskoeffizient selbst von der Temperaturdifferenz abhängt.

## 7. Zeitformel

Nach dem Lösen der Differentialgleichung ergibt sich die Zeit bis zur Zieltemperatur:

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

mit:

\[
\theta_e = \frac{T_e - T_L}{T_a - T_L}
\]

## 8. Kalibrierung für reale Gefrierschränke

Ein realer Gefrierschrank entspricht nicht exakt dem idealisierten Modell. Luftbewegung, Kontakt zur Ablage und kalte Flächen können die Kühlung beschleunigen.

Deshalb verwendet die App einen Kalibrierfaktor:

\[
f_\text{calib}
\]

Die App ist auf einen praktischen Versuch mit einer 0,33-l-Glasflasche kalibriert:

- Flaschenvolumen: 0,33 l
- Glasgewicht: 214 g
- Gefrierschranktemperatur: −17,5 °C
- Anfangstemperatur: 39,5 °C
- Zieltemperatur: 8,0 °C
- gemessene Zeit: 54,4 Minuten

Die endgültige Formel lautet daher:

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

## 9. Hochrechnung auf andere Gebinde

Andere Flaschen und Dosen werden aus der kalibrierten 0,33-l-Glasflasche über Wärmekapazität und Oberfläche hochgerechnet.

Die App unterstützt:

- Flaschen: 0,33 l, 0,5 l und 1,0 l
- Dosen: 0,33 l und 0,5 l

Die 0,33-l-Glasflasche ist der genaueste Fall, weil sie mit einem echten Versuch kalibriert wurde. Andere Gebinde sind Näherungen.

## 10. Lage des Gefäßes

Die Berechnung basiert auf einem Modell für freie Konvektion am zylindrischen Gefäß. Der Basisfall ist ein liegendes Gefäß.

Für stehende Gefäße verwendet die App derzeit einen Näherungsfaktor, weil die reale Luftströmung und Wärmeabgabe anders sein können:

\[
t_\text{real} = \frac{t_\text{model}}{f_\text{calib} \cdot f_\text{lage}}
\]

Für liegend gilt:

\[
f_\text{lage} = 1{,}0
\]

Für stehend gilt derzeit:

\[
f_\text{lage} = 1{,}17
\]

Dadurch wird die berechnete Zeit für stehende Gefäße ungefähr durch 1,17 geteilt.
## 11. Grenzen des Modells

Die Berechnung berücksichtigt nicht:

- Vereisung
- Kristallisationswärme
- Phasenänderung
- Schütteln oder Bewegung des Bieres
- exakte Luftströmung im Gefrierschrank
- unterschiedliche Flaschenformen
- genaue Kontaktflächen zur Ablage

Nahe am Gefrierpunkt wird die Berechnung unsicherer. Für normale Trinktemperaturen wie 8 °C oder 6 °C ist das Modell als praktische Näherung gut verwendbar.

## Beispiel

Eine 0,33-l-Glasflasche von 20 °C auf 8 °C im Gefrierschrank bei −18 °C ergibt mit dem kalibrierten Modell ungefähr:

\[
t \approx 27\,\text{min}
\]

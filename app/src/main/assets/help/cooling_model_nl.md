# Berekeningsmodel

De app berekent de afkoeltijd van bier in de vriezer met een natuurkundig benaderingsmodel.

Drank en verpakking worden beschouwd als één gezamenlijke warmteopslag. De fles of het blik wordt geometrisch benaderd als een cilinder. De warmte wordt vooral via het buitenoppervlak aan de koude lucht in de vriezer afgegeven.

De berekening is een benadering. Werkelijke vriezers kunnen sneller of langzamer koelen door luchtbeweging, contactvlakken en verschillende apparaatontwerpen.

## 1. Warmtecapaciteit van drank en verpakking

Bier en verpakking slaan samen warmte op.

\[
W = m_B c_B + m_G c_G
\]

Waarbij:

- \(W\): totale warmtecapaciteit in J/K
- \(m_B\): massa van het bier
- \(c_B\): soortelijke warmtecapaciteit van het bier
- \(m_G\): massa van de verpakking, glas of aluminium
- \(c_G\): soortelijke warmtecapaciteit van de verpakking

Voor bier gebruikt de app:

\[
c_B = 4200 \,\frac{J}{kgK}
\]

Voor glas:

\[
c_G = 840 \,\frac{J}{kgK}
\]

Voor aluminium blikjes:

\[
c_G = 900 \,\frac{J}{kgK}
\]

## 2. Oppervlak van de verpakking

Warmteverlies hangt af van het oppervlak.

Voor flessen wordt het manteloppervlak van de cilinder gebruikt:

\[
A = \pi d L
\]

Voor blikjes worden ook de eindvlakken meegenomen:

\[
A = \pi d L + \frac{\pi d^2}{2}
\]

Waarbij:

- \(A\): effectief oppervlak
- \(d\): diameter
- \(L\): lengte of hoogte van het cilindrische deel

## 3. Dimensieloze temperatuur

Voor de afleiding wordt het temperatuurverschil dimensieloos gemaakt:

\[
\theta = \frac{T - T_L}{T_a - T_L}
\]

Waarbij:

- \(T\): huidige dranktemperatuur
- \(T_a\): begintemperatuur
- \(T_L\): temperatuur van de vriezer

De doeltemperatuur wordt:

\[
\theta_e = \frac{T_e - T_L}{T_a - T_L}
\]

## 4. Temperatuurafhankelijke warmteoverdracht

Bij vrije convectie is de warmteoverdrachtscoëfficiënt niet constant.

De app gebruikt:

\[
h = h_{\max} \theta^{1/4}
\]

Daardoor koelt het bier in het begin sneller, omdat het temperatuurverschil met de lucht in de vriezer groter is. Later wordt het koelen langzamer.

## 5. Maximale warmteoverdrachtscoëfficiënt

Voor de benaderde vrije convectie aan een liggende cilinder gebruikt de app:

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

met de karakteristieke lengte:

\[
l = \frac{\pi d}{2}
\]

Waarbij:

- \(k_L\): thermische geleidbaarheid van lucht
- \(\nu_L\): kinematische viscositeit van lucht
- \(\alpha_L\): thermische diffusiviteit van lucht
- \(g\): zwaartekrachtsversnelling
- \(T_{L,K}\): vriezertemperatuur in Kelvin

Voor de temperatuur in Kelvin:

\[
T_{L,K} = T_L + 273{,}15
\]

## 6. Differentiaalvergelijking

Uit de energiebalans volgt:

\[
W \frac{dT}{dt} = -h A (T - T_L)
\]

Met de dimensieloze temperatuur:

\[
\frac{d\theta}{dt}
=
-\frac{h_{\max} A}{W}
\theta^{5/4}
\]

De exponent \(5/4\) ontstaat omdat de warmteoverdrachtscoëfficiënt zelf van het temperatuurverschil afhangt.

## 7. Tijdformule

Na het oplossen van de differentiaalvergelijking is de tijd tot de doeltemperatuur:

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

met:

\[
\theta_e = \frac{T_e - T_L}{T_a - T_L}
\]

## 8. Kalibratie voor echte vriezers

Een echte vriezer komt niet exact overeen met het geïdealiseerde model. Luchtbeweging, contact met schappen en koude oppervlakken kunnen het koelen versnellen.

Daarom gebruikt de app een kalibratiefactor:

\[
f_\text{calib}
\]

De app is gekalibreerd met een praktijktest met een glazen fles van 0,33 l:

- Flesvolume: 0,33 l
- Glasgewicht: 214 g
- Vriezer temperatuur: −17,5 °C
- Begintemperatuur: 39,5 °C
- Doeltemperatuur: 8,0 °C
- gemeten tijd: 54,4 minuten

De uiteindelijke formule is:

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

## 9. Opschaling naar andere verpakkingen

Andere flessen en blikjes worden afgeleid van de gekalibreerde 0,33 l glazen fles op basis van warmtecapaciteit en oppervlak.

De app ondersteunt:

- Flessen: 0,33 l, 0,5 l en 1,0 l
- Blikjes: 0,33 l en 0,5 l

De 0,33 l glazen fles is het nauwkeurigst, omdat deze op een echte meting is gekalibreerd. Andere verpakkingen zijn benaderingen.

## 10. Grenzen van het model

De berekening houdt geen rekening met:

- ijsvorming
- kristallisatiewarmte
- faseovergangen
- schudden of beweging van het bier
- exacte luchtstroming in de vriezer
- verschillende flesvormen
- exacte contactvlakken met de plank

Dicht bij het vriespunt wordt de berekening minder betrouwbaar. Voor normale drinktemperaturen zoals 8 °C of 6 °C is het model een bruikbare benadering.

## Voorbeeld

Een glazen fles van 0,33 l van 20 °C naar 8 °C in een vriezer op −18 °C komt ongeveer uit op:

\[
t \approx 27\,\text{min}
\]

# Modello di calcolo

L'app calcola il tempo di raffreddamento di una birra nel congelatore con un modello fisico di approssimazione.

La bevanda e il contenitore sono considerati come un unico serbatoio termico. La bottiglia o la lattina vengono approssimate geometricamente come un cilindro. Il calore viene trasferito soprattutto attraverso la superficie esterna verso l'aria fredda del congelatore.

Il calcolo è un'approssimazione. I congelatori reali possono raffreddare più velocemente o più lentamente a causa del movimento dell'aria, delle superfici di contatto e di diversi tipi di apparecchi.

## 1. Capacità termica di bevanda e contenitore

Birra e contenitore accumulano calore insieme.

\[
W = m_B c_B + m_G c_G
\]

Dove:

- \(W\): capacità termica totale in J/K
- \(m_B\): massa della birra
- \(c_B\): calore specifico della birra
- \(m_G\): massa del contenitore, vetro o alluminio
- \(c_G\): calore specifico del contenitore

Per la birra l'app usa:

\[
c_B = 4200 \,\frac{J}{kgK}
\]

Per il vetro:

\[
c_G = 840 \,\frac{J}{kgK}
\]

Per le lattine in alluminio:

\[
c_G = 900 \,\frac{J}{kgK}
\]

## 2. Superficie del contenitore

La cessione di calore dipende dalla superficie.

Per le bottiglie si usa la superficie laterale del cilindro:

\[
A = \pi d L
\]

Per le lattine si considerano anche le basi:

\[
A = \pi d L + \frac{\pi d^2}{2}
\]

Dove:

- \(A\): superficie efficace
- \(d\): diametro
- \(L\): lunghezza o altezza della parte cilindrica

## 3. Temperatura adimensionale

Per la derivazione, la differenza di temperatura viene resa adimensionale:

\[
\theta = \frac{T - T_L}{T_a - T_L}
\]

Dove:

- \(T\): temperatura attuale della bevanda
- \(T_a\): temperatura iniziale
- \(T_L\): temperatura del congelatore

La temperatura obiettivo è:

\[
\theta_e = \frac{T_e - T_L}{T_a - T_L}
\]

## 4. Scambio termico dipendente dalla temperatura

Per convezione naturale il coefficiente di scambio termico non è costante.

L'app usa:

\[
h = h_{\max} \theta^{1/4}
\]

Così il raffreddamento è più rapido all'inizio, perché la differenza di temperatura con l'aria del congelatore è maggiore. In seguito il raffreddamento rallenta.

## 5. Coefficiente massimo di scambio termico

Per la convezione naturale approssimata su un cilindro orizzontale si usa:

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

con la lunghezza caratteristica:

\[
l = \frac{\pi d}{2}
\]

Dove:

- \(k_L\): conducibilità termica dell'aria
- \(\nu_L\): viscosità cinematica dell'aria
- \(\alpha_L\): diffusività termica dell'aria
- \(g\): accelerazione di gravità
- \(T_{L,K}\): temperatura del congelatore in Kelvin

Per la temperatura in Kelvin:

\[
T_{L,K} = T_L + 273{,}15
\]

## 6. Equazione differenziale

Dall'equilibrio energetico:

\[
W \frac{dT}{dt} = -h A (T - T_L)
\]

Con la temperatura adimensionale:

\[
\frac{d\theta}{dt}
=
-\frac{h_{\max} A}{W}
\theta^{5/4}
\]

L'esponente \(5/4\) compare perché il coefficiente di scambio termico dipende esso stesso dalla differenza di temperatura.

## 7. Formula del tempo

Dopo la soluzione dell'equazione differenziale, il tempo fino alla temperatura obiettivo è:

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

con:

\[
\theta_e = \frac{T_e - T_L}{T_a - T_L}
\]

## 8. Calibrazione per congelatori reali

Un congelatore reale non corrisponde esattamente al modello idealizzato. Movimento dell'aria, contatto con i ripiani e superfici fredde possono accelerare il raffreddamento.

Per questo l'app usa un fattore di calibrazione:

\[
f_\text{calib}
\]

L'app è calibrata con una prova pratica su una bottiglia di vetro da 0,33 l:

- Volume bottiglia: 0,33 l
- Massa vetro: 214 g
- Temperatura congelatore: −17,5 °C
- Temperatura iniziale: 39,5 °C
- Temperatura obiettivo: 8,0 °C
- tempo misurato: 54,4 minuti

La formula finale è quindi:

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

## 9. Estensione ad altri contenitori

Altre bottiglie e lattine vengono ricavate dalla bottiglia di vetro calibrata da 0,33 l usando capacità termica e superficie.

L'app supporta:

- Bottiglie: 0,33 l, 0,5 l e 1,0 l
- Lattine: 0,33 l e 0,5 l

La bottiglia di vetro da 0,33 l è il caso più accurato perché è calibrata con una misura reale. Gli altri contenitori sono approssimazioni.

## 10. Limiti del modello

Il calcolo non considera:

- formazione di ghiaccio
- calore di cristallizzazione
- cambiamenti di fase
- agitazione o movimento della birra
- flusso d'aria esatto nel congelatore
- forme diverse delle bottiglie
- superfici di contatto esatte con il ripiano

Vicino al punto di congelamento il calcolo diventa meno affidabile. Per temperature di consumo normali come 8 °C o 6 °C il modello è una buona approssimazione pratica.

## Esempio

Una bottiglia di vetro da 0,33 l da 20 °C a 8 °C in un congelatore a −18 °C dà circa:

\[
t \approx 27\,\text{min}
\]

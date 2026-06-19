# Model izračuna

Aplikacija izračunava vrijeme hlađenja piva u zamrzivaču pomoću fizikalnog aproksimacijskog modela.

Piće i posuda smatraju se jednim zajedničkim toplinskim spremnikom. Boca ili limenka geometrijski se aproksimiraju kao cilindar. Toplina se uglavnom prenosi preko vanjske površine u hladan zrak u zamrzivaču.

Izračun je aproksimacija. Stvarni zamrzivači mogu hladiti brže ili sporije zbog strujanja zraka, dodirnih površina i različitih konstrukcija uređaja.

## 1. Toplinski kapacitet pića i posude

Pivo i posuda zajedno pohranjuju toplinu.

\[
W = m_B c_B + m_G c_G
\]

Gdje:

- \(W\): ukupni toplinski kapacitet u J/K
- \(m_B\): masa piva
- \(c_B\): specifični toplinski kapacitet piva
- \(m_G\): masa posude, stakla ili aluminija
- \(c_G\): specifični toplinski kapacitet posude

Za pivo aplikacija koristi:

\[
c_B = 4200 \,\frac{J}{kgK}
\]

Za staklo:

\[
c_G = 840 \,\frac{J}{kgK}
\]

Za aluminijske limenke:

\[
c_G = 900 \,\frac{J}{kgK}
\]

## 2. Površina posude

Gubitak topline ovisi o površini.

Za boce se koristi plašt cilindra:

\[
A = \pi d L
\]

Za limenke se dodatno uzimaju u obzir i čelne plohe:

\[
A = \pi d L + \frac{\pi d^2}{2}
\]

Gdje:

- \(A\): učinkovita površina
- \(d\): promjer
- \(L\): duljina ili visina cilindričnog dijela

## 3. Bezbrižna temperatura

Za izvođenje se temperaturna razlika zapisuje bezdimenzijski:

\[
\theta = \frac{T - T_L}{T_a - T_L}
\]

Gdje:

- \(T\): trenutna temperatura pića
- \(T_a\): početna temperatura
- \(T_L\): temperatura zamrzivača

Ciljna temperatura je:

\[
\theta_e = \frac{T_e - T_L}{T_a - T_L}
\]

## 4. Prijenos topline ovisan o temperaturi

Kod slobodne konvekcije koeficijent prijenosa topline nije stalan.

Aplikacija koristi:

\[
h = h_{\max} \theta^{1/4}
\]

Zbog toga hlađenje na početku ide brže, jer je temperaturna razlika prema zraku u zamrzivaču veća. Kasnije hlađenje usporava.

## 5. Maksimalni koeficijent prijenosa topline

Za približenu slobodnu konvekciju na vodoravnom cilindru aplikacija koristi:

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

uz karakterističnu duljinu:

\[
l = \frac{\pi d}{2}
\]

Gdje:

- \(k_L\): toplinska vodljivost zraka
- \(\nu_L\): kinematička viskoznost zraka
- \(\alpha_L\): toplinska difuzivnost zraka
- \(g\): gravitacijsko ubrzanje
- \(T_{L,K}\): temperatura zamrzivača u Kelvinima

Za temperaturu u Kelvinima:

\[
T_{L,K} = T_L + 273{,}15
\]

## 6. Diferencijalna jednadžba

Iz energetske bilance:

\[
W \frac{dT}{dt} = -h A (T - T_L)
\]

Uz bezdimenzijsku temperaturu:

\[
\frac{d\theta}{dt}
=
-\frac{h_{\max} A}{W}
\theta^{5/4}
\]

Eksponent \(5/4\) nastaje zato što i sam koeficijent prijenosa topline ovisi o temperaturnoj razlici.

## 7. Formula vremena

Nakon rješavanja diferencijalne jednadžbe, vrijeme do ciljne temperature je:

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

uz:

\[
\theta_e = \frac{T_e - T_L}{T_a - T_L}
\]

## 8. Kalibracija za stvarne zamrzivače

Stvarni zamrzivač ne odgovara točno idealiziranom modelu. Strujanje zraka, kontakt s policama i hladne površine mogu ubrzati hlađenje.

Zato aplikacija koristi kalibracijski faktor:

\[
f_\text{calib}
\]

Aplikacija je kalibrirana praktičnim testom sa staklenom bocom od 0,33 l:

- Volumen boce: 0,33 l
- Masa stakla: 214 g
- Temperatura zamrzivača: −17,5 °C
- Početna temperatura: 39,5 °C
- Ciljna temperatura: 8,0 °C
- izmjereno vrijeme: 54,4 minute

Konačna formula je:

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

## 9. Preračun na druge posude

Ostale boce i limenke izvode se iz kalibrirane staklene boce od 0,33 l pomoću toplinskog kapaciteta i površine.

Aplikacija podržava:

- Boce: 0,33 l, 0,5 l i 1,0 l
- Limenke: 0,33 l i 0,5 l

Staklena boca od 0,33 l najtočniji je slučaj jer je kalibrirana stvarnim mjerenjem. Ostale posude su aproksimacije.

## 10. Položaj posude

Izračun se temelji na modelu slobodne konvekcije oko cilindrične posude. Osnovni slučaj je posuda u ležećem položaju.

Za uspravne posude aplikacija trenutačno koristi približni faktor, jer se stvarno strujanje zraka i prijenos topline mogu razlikovati:

\[
t_\text{stvarno} = \frac{t_\text{model}}{f_\text{calib} \cdot f_\text{položaj}}
\]

Za ležeći položaj vrijedi:

\[
f_\text{položaj} = 1{,}0
\]

Za uspravni položaj trenutačno vrijedi:

\[
f_\text{položaj} = 1{,}17
\]

Time se izračunato vrijeme za uspravne posude približno dijeli s 1,17.
## 11. Ograničenja modela

Izračun ne uzima u obzir:

- stvaranje leda
- toplinu kristalizacije
- fazne promjene
- tresenje ili kretanje piva
- točan protok zraka u zamrzivaču
- različite oblike boca
- točne dodirne površine s policom

Blizu točke smrzavanja izračun je manje pouzdan. Za uobičajene temperature za piće kao što su 8 °C ili 6 °C model je praktična aproksimacija.

## Primjer

Staklena boca od 0,33 l od 20 °C do 8 °C u zamrzivaču na −18 °C daje približno:

\[
t \approx 27\,\text{min}
\]

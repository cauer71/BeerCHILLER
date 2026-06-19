# Výpočetní model

Aplikace počítá dobu chlazení piva v mrazáku pomocí fyzikálního aproximačního modelu.

Nápoj a obal jsou považovány za jednu společnou tepelnou zásobu. Láhev nebo plechovka jsou geometricky aproximovány jako válec. Teplo se přenáší hlavně přes vnější povrch do studeného vzduchu v mrazáku.

Výpočet je aproximace. Skutečné mrazáky mohou chladit rychleji nebo pomaleji kvůli pohybu vzduchu, kontaktním plochám a různým konstrukcím spotřebičů.

## 1. Tepelná kapacita nápoje a obalu

Pivo a obal společně ukládají teplo.

\[
W = m_B c_B + m_G c_G
\]

Kde:

- \(W\): celková tepelná kapacita v J/K
- \(m_B\): hmotnost piva
- \(c_B\): měrná tepelná kapacita piva
- \(m_G\): hmotnost obalu, tedy skla nebo hliníku
- \(c_G\): měrná tepelná kapacita obalu

Pro pivo aplikace používá:

\[
c_B = 4200 \,\frac{J}{kgK}
\]

Pro sklo:

\[
c_G = 840 \,\frac{J}{kgK}
\]

Pro hliníkové plechovky:

\[
c_G = 900 \,\frac{J}{kgK}
\]

## 2. Povrch obalu

Odvod tepla závisí na povrchu.

U lahví se používá plášť válce:

\[
A = \pi d L
\]

U plechovek se navíc započítávají čelní plochy:

\[
A = \pi d L + \frac{\pi d^2}{2}
\]

Kde:

- \(A\): účinný povrch
- \(d\): průměr
- \(L\): délka nebo výška válcové části

## 3. Bezrozměrná teplota

Pro odvození se teplotní rozdíl zapisuje bezrozměrně:

\[
\theta = \frac{T - T_L}{T_a - T_L}
\]

Kde:

- \(T\): aktuální teplota nápoje
- \(T_a\): počáteční teplota
- \(T_L\): teplota mrazáku

Cílová teplota je:

\[
\theta_e = \frac{T_e - T_L}{T_a - T_L}
\]

## 4. Na teplotě závislý přestup tepla

Při volné konvekci není součinitel přestupu tepla konstantní.

Aplikace používá:

\[
h = h_{\max} \theta^{1/4}
\]

Díky tomu chlazení na začátku probíhá rychleji, protože rozdíl teplot vůči vzduchu v mrazáku je větší. Později se chlazení zpomaluje.

## 5. Maximální součinitel přestupu tepla

Pro přibližnou volnou konvekci na vodorovném válci aplikace používá:

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

s charakteristickou délkou:

\[
l = \frac{\pi d}{2}
\]

Kde:

- \(k_L\): tepelná vodivost vzduchu
- \(\nu_L\): kinematická viskozita vzduchu
- \(\alpha_L\): teplotní difuzivita vzduchu
- \(g\): tíhové zrychlení
- \(T_{L,K}\): teplota mrazáku v Kelvinech

Pro teplotu v Kelvinech:

\[
T_{L,K} = T_L + 273{,}15
\]

## 6. Diferenciální rovnice

Z energetické bilance:

\[
W \frac{dT}{dt} = -h A (T - T_L)
\]

S bezrozměrnou teplotou:

\[
\frac{d\theta}{dt}
=
-\frac{h_{\max} A}{W}
\theta^{5/4}
\]

Exponent \(5/4\) vzniká proto, že samotný součinitel přestupu tepla závisí na teplotním rozdílu.

## 7. Vzorec pro čas

Po řešení diferenciální rovnice je čas do cílové teploty:

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

s:

\[
\theta_e = \frac{T_e - T_L}{T_a - T_L}
\]

## 8. Kalibrace pro reálné mrazáky

Skutečný mrazák neodpovídá ideálnímu modelu přesně. Pohyb vzduchu, kontakt s policemi a chladné plochy mohou chlazení urychlit.

Proto aplikace používá kalibrační faktor:

\[
f_\text{calib}
\]

Aplikace je kalibrována praktickým pokusem se skleněnou lahví 0,33 l:

- Objem láhve: 0,33 l
- Hmotnost skla: 214 g
- Teplota mrazáku: −17,5 °C
- Počáteční teplota: 39,5 °C
- Cílová teplota: 8,0 °C
- naměřený čas: 54,4 minuty

Konečný vzorec:

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

## 9. Přepočet na jiné obaly

Ostatní lahve a plechovky jsou odvozeny z kalibrované skleněné lahve 0,33 l pomocí tepelné kapacity a povrchu.

Aplikace podporuje:

- Lahve: 0,33 l, 0,5 l a 1,0 l
- Plechovky: 0,33 l a 0,5 l

Skleněná lahev 0,33 l je nejpřesnější případ, protože je kalibrována skutečným měřením. Ostatní obaly jsou aproximace.

## 10. Poloha nádoby

Výpočet vychází z modelu volné konvekce kolem válcové nádoby. Základním případem je nádoba vleže.

Pro nádoby nastojato používá aplikace aktuálně aproximační faktor, protože skutečné proudění vzduchu a přenos tepla se mohou lišit:

\[
t_\text{skutečný} = \frac{t_\text{model}}{f_\text{calib} \cdot f_\text{poloha}}
\]

Pro polohu vleže platí:

\[
f_\text{poloha} = 1{,}0
\]

Pro polohu nastojato platí aktuálně:

\[
f_\text{poloha} = 1{,}17
\]

Vypočtený čas pro nádoby nastojato se tím přibližně dělí hodnotou 1,17.
## 11. Omezení modelu

Výpočet nezohledňuje:

- tvorbu ledu
- krystalizační teplo
- fázové přeměny
- třesení nebo pohyb piva
- přesné proudění vzduchu v mrazáku
- různé tvary lahví
- přesné kontaktní plochy s policí

Blízko bodu mrazu je výpočet méně spolehlivý. Pro běžné teploty k pití, jako je 8 °C nebo 6 °C, je model praktickou aproximací.

## Příklad

Skleněná lahev 0,33 l z 20 °C na 8 °C v mrazáku při −18 °C vychází přibližně:

\[
t \approx 27\,\text{min}
\]

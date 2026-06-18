# Model obliczeniowy

Aplikacja oblicza czas chłodzenia piwa w zamrażarce za pomocą fizycznego modelu przybliżonego.

Napój i opakowanie są traktowane jako jeden wspólny magazyn ciepła. Butelka lub puszka są geometrycznie przybliżane do cylindra. Ciepło oddawane jest głównie przez powierzchnię zewnętrzną do zimnego powietrza w zamrażarce.

Obliczenie jest przybliżeniem. Rzeczywiste zamrażarki mogą chłodzić szybciej lub wolniej ze względu na ruch powietrza, powierzchnie kontaktu i różne konstrukcje urządzeń.

## 1. Pojemność cieplna napoju i opakowania

Piwo i opakowanie magazynują ciepło wspólnie.

\[
W = m_B c_B + m_G c_G
\]

Gdzie:

- \(W\): całkowita pojemność cieplna w J/K
- \(m_B\): masa piwa
- \(c_B\): ciepło właściwe piwa
- \(m_G\): masa opakowania, czyli szkła lub aluminium
- \(c_G\): ciepło właściwe opakowania

Dla piwa aplikacja używa:

\[
c_B = 4200 \,\frac{J}{kgK}
\]

Dla szkła:

\[
c_G = 840 \,\frac{J}{kgK}
\]

Dla puszek aluminiowych:

\[
c_G = 900 \,\frac{J}{kgK}
\]

## 2. Powierzchnia opakowania

Oddawanie ciepła zależy od powierzchni.

Dla butelek używana jest powierzchnia boczna cylindra:

\[
A = \pi d L
\]

Dla puszek dodatkowo uwzględniane są powierzchnie den i wieczka:

\[
A = \pi d L + \frac{\pi d^2}{2}
\]

Gdzie:

- \(A\): efektywna powierzchnia
- \(d\): średnica
- \(L\): długość lub wysokość części cylindrycznej

## 3. Temperatura bezwymiarowa

Na potrzeby wyprowadzenia różnica temperatur jest zapisana bezwymiarowo:

\[
\theta = \frac{T - T_L}{T_a - T_L}
\]

Gdzie:

- \(T\): aktualna temperatura napoju
- \(T_a\): temperatura początkowa
- \(T_L\): temperatura zamrażarki

Temperatura docelowa to:

\[
\theta_e = \frac{T_e - T_L}{T_a - T_L}
\]

## 4. Współczynnik wymiany ciepła zależny od temperatury

Przy konwekcji swobodnej współczynnik wymiany ciepła nie jest stały.

Aplikacja używa:

\[
h = h_{\max} \theta^{1/4}
\]

Dzięki temu chłodzenie na początku przebiega szybciej, ponieważ różnica temperatur względem powietrza w zamrażarce jest większa. Później chłodzenie zwalnia.

## 5. Maksymalny współczynnik wymiany ciepła

Dla przybliżonej konwekcji swobodnej na poziomym cylindrze aplikacja używa:

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

z charakterystyczną długością:

\[
l = \frac{\pi d}{2}
\]

Gdzie:

- \(k_L\): przewodność cieplna powietrza
- \(\nu_L\): kinematyczna lepkość powietrza
- \(\alpha_L\): dyfuzyjność cieplna powietrza
- \(g\): przyspieszenie ziemskie
- \(T_{L,K}\): temperatura zamrażarki w Kelvinach

Dla temperatury w Kelvinach:

\[
T_{L,K} = T_L + 273{,}15
\]

## 6. Równanie różniczkowe

Z bilansu energii:

\[
W \frac{dT}{dt} = -h A (T - T_L)
\]

Z temperaturą bezwymiarową:

\[
\frac{d\theta}{dt}
=
-\frac{h_{\max} A}{W}
\theta^{5/4}
\]

Wykładnik \(5/4\) wynika z tego, że sam współczynnik wymiany ciepła zależy od różnicy temperatur.

## 7. Wzór czasu

Po rozwiązaniu równania różniczkowego czas do temperatury docelowej wynosi:

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

z:

\[
\theta_e = \frac{T_e - T_L}{T_a - T_L}
\]

## 8. Kalibracja dla rzeczywistych zamrażarek

Rzeczywista zamrażarka nie odpowiada dokładnie modelowi idealnemu. Ruch powietrza, kontakt z półkami i zimne powierzchnie mogą przyspieszyć chłodzenie.

Dlatego aplikacja używa współczynnika kalibracji:

\[
f_\text{calib}
\]

Aplikacja jest skalibrowana na podstawie praktycznego testu dla szklanej butelki 0,33 l:

- Pojemność butelki: 0,33 l
- Masa szkła: 214 g
- Temperatura zamrażarki: −17,5 °C
- Temperatura początkowa: 39,5 °C
- Temperatura docelowa: 8,0 °C
- zmierzony czas: 54,4 min

Końcowy wzór:

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

## 9. Przeliczenie na inne opakowania

Pozostałe butelki i puszki są wyprowadzane z kalibrowanej szklanej butelki 0,33 l na podstawie pojemności cieplnej i powierzchni.

Aplikacja obsługuje:

- Butelki: 0,33 l, 0,5 l i 1,0 l
- Puszki: 0,33 l i 0,5 l

Szklana butelka 0,33 l jest przypadkiem najdokładniejszym, ponieważ została skalibrowana na podstawie rzeczywistego pomiaru. Pozostałe opakowania są przybliżeniami.

## 10. Ograniczenia modelu

Obliczenie nie uwzględnia:

- zamarzania
- ciepła krystalizacji
- przemian fazowych
- potrząsania lub ruchu piwa
- dokładnego przepływu powietrza w zamrażarce
- różnych kształtów butelek
- dokładnych powierzchni kontaktu z półką

Blisko punktu zamarzania obliczenie staje się mniej pewne. Dla normalnych temperatur do picia, takich jak 8 °C lub 6 °C, model jest praktycznym przybliżeniem.

## Przykład

Szklana butelka 0,33 l z 20 °C do 8 °C w zamrażarce przy −18 °C daje około:

\[
t \approx 27\,\text{min}
\]

# Modelo de cálculo

O app calcula o tempo de arrefecimento de uma cerveja no congelador usando um modelo físico de aproximação.

A bebida e o recipiente são tratados como um único reservatório térmico. A garrafa ou lata são aproximadas geometricamente como um cilindro. O calor é transferido principalmente pela superfície exterior para o ar frio do congelador.

O cálculo é uma aproximação. Congeladores reais podem arrefecer mais depressa ou mais devagar devido ao movimento do ar, às superfícies de contacto e a diferentes modelos de aparelho.

## 1. Capacidade térmica da bebida e do recipiente

A cerveja e o recipiente armazenam calor em conjunto.

\[
W = m_B c_B + m_G c_G
\]

Onde:

- \(W\): capacidade térmica total em J/K
- \(m_B\): massa da cerveja
- \(c_B\): capacidade térmica específica da cerveja
- \(m_G\): massa do recipiente, vidro ou alumínio
- \(c_G\): capacidade térmica específica do recipiente

Para a cerveja, a app usa:

\[
c_B = 4200 \,\frac{J}{kgK}
\]

Para o vidro:

\[
c_G = 840 \,\frac{J}{kgK}
\]

Para latas de alumínio:

\[
c_G = 900 \,\frac{J}{kgK}
\]

## 2. Superfície do recipiente

A perda de calor depende da superfície.

Para garrafas, é usada a superfície lateral do cilindro:

\[
A = \pi d L
\]

Para latas, também são incluídas as tampas:

\[
A = \pi d L + \frac{\pi d^2}{2}
\]

Onde:

- \(A\): superfície efetiva
- \(d\): diâmetro
- \(L\): comprimento ou altura da parte cilíndrica

## 3. Temperatura adimensional

Para a derivação, a diferença de temperatura é escrita de forma adimensional:

\[
\theta = \frac{T - T_L}{T_a - T_L}
\]

Onde:

- \(T\): temperatura atual da bebida
- \(T_a\): temperatura inicial
- \(T_L\): temperatura do congelador

A temperatura alvo é:

\[
\theta_e = \frac{T_e - T_L}{T_a - T_L}
\]

## 4. Transferência de calor dependente da temperatura

Na convecção natural, o coeficiente de transferência de calor não é constante.

A app usa:

\[
h = h_{\max} \theta^{1/4}
\]

Assim, o arrefecimento começa mais rapidamente no início, porque a diferença de temperatura para o ar do congelador é maior. Depois o arrefecimento torna-se mais lento.

## 5. Coeficiente máximo de transferência de calor

Para a convecção natural aproximada num cilindro horizontal, a app usa:

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

com o comprimento característico:

\[
l = \frac{\pi d}{2}
\]

Onde:

- \(k_L\): condutividade térmica do ar
- \(\nu_L\): viscosidade cinemática do ar
- \(\alpha_L\): difusividade térmica do ar
- \(g\): aceleração da gravidade
- \(T_{L,K}\): temperatura do congelador em Kelvin

Para a temperatura em Kelvin:

\[
T_{L,K} = T_L + 273{,}15
\]

## 6. Equação diferencial

Do balanço de energia:

\[
W \frac{dT}{dt} = -h A (T - T_L)
\]

Com a temperatura adimensional:

\[
\frac{d\theta}{dt}
=
-\frac{h_{\max} A}{W}
\theta^{5/4}
\]

O expoente \(5/4\) aparece porque o próprio coeficiente de transferência de calor depende da diferença de temperatura.

## 7. Fórmula do tempo

Depois de resolver a equação diferencial, o tempo até à temperatura alvo é:

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

com:

\[
\theta_e = \frac{T_e - T_L}{T_a - T_L}
\]

## 8. Calibração para congeladores reais

Um congelador real não corresponde exatamente ao modelo idealizado. O movimento do ar, o contacto com as prateleiras e as superfícies frias podem acelerar o arrefecimento.

Por isso a app usa um fator de calibração:

\[
f_\text{calib}
\]

A app está calibrada com um teste prático usando uma garrafa de vidro de 0,33 l:

- Volume da garrafa: 0,33 l
- Massa do vidro: 214 g
- Temperatura do congelador: −17,5 °C
- Temperatura inicial: 39,5 °C
- Temperatura alvo: 8,0 °C
- tempo medido: 54,4 minutos

A fórmula final é:

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

## 9. Escalamento para outros recipientes

As outras garrafas e latas são calculadas a partir da garrafa de vidro calibrada de 0,33 l usando capacidade térmica e superfície.

A app suporta:

- Garrafas: 0,33 l, 0,5 l e 1,0 l
- Latas: 0,33 l e 0,5 l

A garrafa de vidro de 0,33 l é o caso mais preciso porque foi calibrada com uma medição real. Os outros recipientes são aproximações.

## 10. Limites do modelo

O cálculo não considera:

- formação de gelo
- calor de cristalização
- mudanças de fase
- agitação ou movimento da cerveja
- fluxo de ar exato no congelador
- diferentes formatos de garrafa
- superfícies de contacto exatas com a prateleira

Perto do ponto de congelação, o cálculo torna-se menos fiável. Para temperaturas normais de consumo como 8 °C ou 6 °C, o modelo continua a ser uma aproximação prática.

## Exemplo

Uma garrafa de vidro de 0,33 l de 20 °C para 8 °C num congelador a −18 °C dá aproximadamente:

\[
t \approx 27\,\text{min}
\]

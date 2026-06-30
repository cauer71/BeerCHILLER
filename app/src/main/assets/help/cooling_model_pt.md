# Modelo de calculo

A app calcula o tempo de arrefecimento da cerveja no frigorifico ou congelador com o modelo **BeerCHILLER Calibrated V2**.

O modelo e uma aproximacao pratica. Usa a temperatura inicial, a temperatura alvo, a temperatura do aparelho, o recipiente, o volume e a posicao. Na pratica, os aparelhos podem arrefecer mais depressa ou mais devagar devido ao fluxo de ar, superficies de contacto, carga e aberturas da porta.

## 1. Diferenca de temperatura

\[
\Delta_0 = T_0 - T_D
\]

\[
\theta = \frac{T_Z - T_D}{T_0 - T_D}
\]

Onde:

- \(T_0\): temperatura inicial da cerveja
- \(T_Z\): temperatura alvo
- \(T_D\): temperatura do frigorifico ou congelador
- \(\theta\): razao adimensional da temperatura alvo

## 2. Curva de arrefecimento

O arrefecimento abranda quando a cerveja se aproxima da temperatura do aparelho. O BeerCHILLER modela isto com um expoente empirico:

\[
n = 0.15
\]

\[
\frac{d\Delta}{dt} = -k \cdot \Delta^{1+n}
\]

## 3. Formula final da app

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

Valores base para \(\tau_0\):

| Recipiente | Volume | \(\tau_0\) |
|---|---:|---:|
| Garrafa | 0.33 l | 87 min |
| Garrafa | 0.5 l | 110 min |
| Garrafa | 1.0 l | 155 min |
| Lata | 0.33 l | 85 min |
| Lata | 0.5 l | 105 min |

Fatores do aparelho:

| Aparelho | \(f_D\) |
|---|---:|
| Frigorifico | 1.00 |
| Congelador | 0.84 |

Fatores de posicao:

| Recipiente | Em pe | Deitado |
|---|---:|---:|
| Garrafa | 1.00 | 0.95 |
| Lata | 1.00 | 0.92 |

## 5. Temperatura durante o temporizador

\[
\theta(t)=
\left(
1+n\cdot\frac{t}{\tau_{eff}}
\right)^{-1/n}
\]

\[
T(t)=T_D+(T_0-T_D)\cdot\theta(t)
\]

## 6. Regras de validade

- Se \(T_0 \le T_Z\), a cerveja ja esta suficientemente fria.
- Se \(T_Z \le T_D\), a temperatura alvo nao e significativamente atingivel.
- Apenas \(0 < \theta < 1\) e valido.

## Exemplo

Uma garrafa de vidro de 0.33 l de 39.5 graus Celsius para 6 graus Celsius num congelador a -17.5 graus Celsius resulta em:

\[
t_{app} \approx 62\,\text{min}
\]

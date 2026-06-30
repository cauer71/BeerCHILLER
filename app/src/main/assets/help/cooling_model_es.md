# Modelo de calculo

La app calcula el tiempo de enfriamiento de la cerveza en el frigorifico o el congelador con el modelo **BeerCHILLER Calibrated V2**.

El modelo es una aproximacion practica. Usa la temperatura inicial, la temperatura deseada, la temperatura del aparato, el recipiente, el volumen y la posicion. En la practica, los aparatos pueden enfriar mas rapido o mas lento por el flujo de aire, las superficies de contacto, la carga y las aperturas de la puerta.

## 1. Diferencia de temperatura

\[
\Delta_0 = T_0 - T_D
\]

\[
\theta = \frac{T_Z - T_D}{T_0 - T_D}
\]

Donde:

- \(T_0\): temperatura inicial de la cerveza
- \(T_Z\): temperatura deseada
- \(T_D\): temperatura del frigorifico o congelador
- \(\theta\): relacion adimensional de la temperatura deseada

## 2. Curva de enfriamiento

El enfriamiento se ralentiza cuando la cerveza se acerca a la temperatura del aparato. BeerCHILLER lo modela con un exponente empirico:

\[
n = 0.15
\]

\[
\frac{d\Delta}{dt} = -k \cdot \Delta^{1+n}
\]

## 3. Formula final de la app

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

| Recipiente | Volumen | \(\tau_0\) |
|---|---:|---:|
| Botella | 0.33 l | 87 min |
| Botella | 0.5 l | 110 min |
| Botella | 1.0 l | 155 min |
| Lata | 0.33 l | 85 min |
| Lata | 0.5 l | 105 min |

Factores del aparato:

| Aparato | \(f_D\) |
|---|---:|
| Frigorifico | 1.00 |
| Congelador | 0.84 |

Factores de posicion:

| Recipiente | Vertical | Horizontal |
|---|---:|---:|
| Botella | 1.00 | 0.95 |
| Lata | 1.00 | 0.92 |

## 5. Temperatura durante el temporizador

\[
\theta(t)=
\left(
1+n\cdot\frac{t}{\tau_{eff}}
\right)^{-1/n}
\]

\[
T(t)=T_D+(T_0-T_D)\cdot\theta(t)
\]

## 6. Reglas de validez

- Si \(T_0 \le T_Z\), la cerveza ya esta suficientemente fria.
- Si \(T_Z \le T_D\), la temperatura deseada no se puede alcanzar de forma significativa.
- Solo es valido \(0 < \theta < 1\).

## Ejemplo

Una botella de vidrio de 0.33 l de 39.5 grados Celsius a 6 grados Celsius en un congelador a -17.5 grados Celsius da:

\[
t_{app} \approx 62\,\text{min}
\]

# Modelo de cálculo

La app calcula el tiempo de enfriamiento de una cerveza en el congelador mediante un modelo físico de aproximación.

La bebida y el envase se consideran un único depósito térmico. La botella o lata se aproximan geométricamente como un cilindro. El calor se transfiere principalmente a través de la superficie exterior hacia el aire frío del congelador.

El cálculo es una aproximación. Los congeladores reales pueden enfriar más rápido o más lento por el movimiento del aire, las superficies de contacto y los distintos diseños de aparato.

## 1. Capacidad térmica de la bebida y el envase

La cerveza y el envase almacenan calor conjuntamente.

\[
W = m_B c_B + m_G c_G
\]

Donde:

- \(W\): capacidad térmica total en J/K
- \(m_B\): masa de la cerveza
- \(c_B\): capacidad térmica específica de la cerveza
- \(m_G\): masa del envase, vidrio o aluminio
- \(c_G\): capacidad térmica específica del envase

Para la cerveza, la app usa:

\[
c_B = 4200 \,\frac{J}{kgK}
\]

Para el vidrio:

\[
c_G = 840 \,\frac{J}{kgK}
\]

Para las latas de aluminio:

\[
c_G = 900 \,\frac{J}{kgK}
\]

## 2. Superficie del envase

La pérdida de calor depende de la superficie.

Para las botellas se usa la superficie lateral del cilindro:

\[
A = \pi d L
\]

Para las latas también se incluyen las caras:

\[
A = \pi d L + \frac{\pi d^2}{2}
\]

Donde:

- \(A\): superficie efectiva
- \(d\): diámetro
- \(L\): longitud o altura de la parte cilíndrica

## 3. Temperatura adimensional

Para la derivación, la diferencia de temperatura se expresa de forma adimensional:

\[
\theta = \frac{T - T_L}{T_a - T_L}
\]

Donde:

- \(T\): temperatura actual de la bebida
- \(T_a\): temperatura inicial
- \(T_L\): temperatura del congelador

La temperatura objetivo es:

\[
\theta_e = \frac{T_e - T_L}{T_a - T_L}
\]

## 4. Transferencia de calor dependiente de la temperatura

En convección natural, el coeficiente de transferencia de calor no es constante.

La app usa:

\[
h = h_{\max} \theta^{1/4}
\]

Así, el enfriamiento empieza más rápido al principio, porque la diferencia de temperatura con el aire del congelador es mayor. Después el enfriamiento se vuelve más lento.

## 5. Coeficiente máximo de transferencia de calor

Para la convección natural aproximada en un cilindro horizontal se usa:

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

con la longitud característica:

\[
l = \frac{\pi d}{2}
\]

Donde:

- \(k_L\): conductividad térmica del aire
- \(\nu_L\): viscosidad cinemática del aire
- \(\alpha_L\): difusividad térmica del aire
- \(g\): aceleración de la gravedad
- \(T_{L,K}\): temperatura del congelador en Kelvin

Para la temperatura en Kelvin:

\[
T_{L,K} = T_L + 273{,}15
\]

## 6. Ecuación diferencial

Del balance de energía:

\[
W \frac{dT}{dt} = -h A (T - T_L)
\]

Con la temperatura adimensional:

\[
\frac{d\theta}{dt}
=
-\frac{h_{\max} A}{W}
\theta^{5/4}
\]

El exponente \(5/4\) aparece porque el propio coeficiente de transferencia de calor depende de la diferencia de temperatura.

## 7. Fórmula del tiempo

Tras resolver la ecuación diferencial, el tiempo hasta la temperatura objetivo es:

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

## 8. Calibración para congeladores reales

Un congelador real no coincide exactamente con el modelo idealizado. El movimiento del aire, el contacto con las baldas y las superficies frías pueden acelerar el enfriamiento.

Por eso la app usa un factor de calibración:

\[
f_\text{calib}
\]

La app está calibrada con una prueba práctica usando una botella de vidrio de 0,33 l:

- Volumen de la botella: 0,33 l
- Masa del vidrio: 214 g
- Temperatura del congelador: −17,5 °C
- Temperatura inicial: 39,5 °C
- Temperatura objetivo: 8,0 °C
- tiempo medido: 54,4 minutos

La fórmula final es:

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

## 9. Escalado a otros envases

Las demás botellas y latas se calculan a partir de la botella de vidrio calibrada de 0,33 l usando capacidad térmica y superficie.

La app admite:

- Botellas: 0,33 l, 0,5 l y 1,0 l
- Latas: 0,33 l y 0,5 l

La botella de vidrio de 0,33 l es el caso más preciso porque está calibrada con una medición real. Los demás envases son aproximaciones.

## 10. Límites del modelo

El cálculo no tiene en cuenta:

- formación de hielo
- calor de cristalización
- cambios de fase
- agitación o movimiento de la cerveza
- flujo de aire exacto en el congelador
- distintas formas de botella
- superficies de contacto exactas con la balda

Cerca del punto de congelación, el cálculo es menos fiable. Para temperaturas normales de consumo como 8 °C o 6 °C, el modelo sigue siendo una aproximación práctica.

## Ejemplo

Una botella de vidrio de 0,33 l de 20 °C a 8 °C en un congelador a −18 °C da aproximadamente:

\[
t \approx 27\,\text{min}
\]

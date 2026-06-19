# Calculation model

The app calculates the cooling time of a beer in a freezer using a physical approximation model.

The drink and the container are treated as one shared thermal store. The bottle or can is approximated geometrically as a cylinder. Heat is mainly transferred through the outer surface to the cold air in the freezer.

The calculation is an approximation. Real freezers can cool faster or slower because of air movement, contact surfaces, and different appliance designs.

## 1. Heat capacity of drink and container

Beer and container store heat together.

\[
W = m_B c_B + m_G c_G
\]

Where:

- \(W\): total heat capacity in J/K
- \(m_B\): mass of the beer
- \(c_B\): specific heat capacity of the beer
- \(m_G\): mass of the container, either glass or aluminum
- \(c_G\): specific heat capacity of the container

For beer, the app uses:

\[
c_B = 4200 \,\frac{J}{kgK}
\]

For glass:

\[
c_G = 840 \,\frac{J}{kgK}
\]

For aluminum cans:

\[
c_G = 900 \,\frac{J}{kgK}
\]

## 2. Surface area of the container

Heat loss depends on the surface area.

For bottles, the cylindrical side area is used:

\[
A = \pi d L
\]

For cans, the end faces are also included:

\[
A = \pi d L + \frac{\pi d^2}{2}
\]

Where:

- \(A\): effective surface area
- \(d\): diameter
- \(L\): length or height of the cylindrical part

## 3. Dimensionless temperature

For the derivation, the temperature difference is written in dimensionless form:

\[
\theta = \frac{T - T_L}{T_a - T_L}
\]

Where:

- \(T\): current drink temperature
- \(T_a\): initial temperature
- \(T_L\): freezer temperature

The target temperature is:

\[
\theta_e = \frac{T_e - T_L}{T_a - T_L}
\]

## 4. Temperature-dependent heat transfer

For free convection, the heat transfer coefficient is not constant.

The app uses:

\[
h = h_{\max} \theta^{1/4}
\]

This means cooling starts faster at the beginning, because the temperature difference to the freezer air is larger. Later the cooling becomes slower.

## 5. Maximum heat transfer coefficient

For the approximated free convection on a horizontal cylinder, the app uses:

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

with the characteristic length:

\[
l = \frac{\pi d}{2}
\]

Where:

- \(k_L\): thermal conductivity of air
- \(\nu_L\): kinematic viscosity of air
- \(\alpha_L\): thermal diffusivity of air
- \(g\): gravitational acceleration
- \(T_{L,K}\): freezer temperature in Kelvin

For Kelvin temperature:

\[
T_{L,K} = T_L + 273{,}15
\]

## 6. Differential equation

From the energy balance:

\[
W \frac{dT}{dt} = -h A (T - T_L)
\]

With the dimensionless temperature:

\[
\frac{d\theta}{dt}
=
-\frac{h_{\max} A}{W}
\theta^{5/4}
\]

The exponent \(5/4\) appears because the heat transfer coefficient itself depends on the temperature difference.

## 7. Time formula

After solving the differential equation, the time until the target temperature is:

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

with:

\[
\theta_e = \frac{T_e - T_L}{T_a - T_L}
\]

## 8. Calibration for real freezers

A real freezer does not match the idealized model exactly. Air movement, contact with shelves, and cold surfaces can speed up cooling.

Therefore the app uses a calibration factor:

\[
f_\text{calib}
\]

The app is calibrated with a practical test using a 0.33 l glass bottle:

- Bottle volume: 0.33 l
- Glass mass: 214 g
- Freezer temperature: −17.5 °C
- Initial temperature: 39.5 °C
- Target temperature: 8.0 °C
- measured time: 54.4 minutes

The final formula is:

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

## 9. Scaling to other containers

Other bottles and cans are scaled from the calibrated 0.33 l glass bottle using heat capacity and surface area.

The app supports:

- Bottles: 0.33 l, 0.5 l and 1.0 l
- Cans: 0.33 l and 0.5 l

The 0.33 l glass bottle is the most accurate case because it is calibrated with a real measurement. Other containers are approximations.

## 10. Container orientation

The calculation is based on a free-convection model for a cylindrical container. The base case is a lying container.

For standing containers, the app currently applies an approximation factor because real airflow and heat transfer can differ:

\[
t_\text{real} = \frac{t_\text{model}}{f_\text{calib} \cdot f_\text{orientation}}
\]

For lying containers:

\[
f_\text{orientation} = 1.0
\]

For standing containers, the current value is:

\[
f_\text{orientation} = 1.17
\]

This divides the calculated cooling time for standing containers by about 1.17.
## 11. Model limits

The calculation does not consider:

- icing
- crystallization heat
- phase changes
- shaking or movement of the beer
- exact air flow in the freezer
- different bottle shapes
- exact contact surfaces with the shelf

Near the freezing point the calculation becomes less reliable. For normal drinking temperatures such as 8 °C or 6 °C, the model is a practical approximation.

## Example

A 0.33 l glass bottle from 20 °C to 8 °C in a freezer at −18 °C gives roughly:

\[
t \approx 27\,\text{min}
\]

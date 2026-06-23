package com.bierchiller.app;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TemperaturePreferenceDefaultsTest {
    @Test
    public void freshInstallTargetTemperatureDefaultsToEightCelsius() {
        assertEquals(8, MainActivity.restoreTemperaturePreference(
                false,
                6,
                MainActivity.DEFAULT_TARGET_TEMP,
                MainActivity.MIN_TARGET_TEMP,
                MainActivity.MAX_TARGET_TEMP
        ));
    }

    @Test
    public void savedSixDegreeTargetTemperatureRemainsValid() {
        assertEquals(6, MainActivity.restoreTemperaturePreference(
                true,
                6,
                MainActivity.DEFAULT_TARGET_TEMP,
                MainActivity.MIN_TARGET_TEMP,
                MainActivity.MAX_TARGET_TEMP
        ));
    }

    @Test
    public void targetTemperaturePreferenceIsClampedToSupportedRange() {
        assertEquals(MainActivity.MIN_TARGET_TEMP, MainActivity.clamp(
                -20,
                MainActivity.MIN_TARGET_TEMP,
                MainActivity.MAX_TARGET_TEMP
        ));
        assertEquals(MainActivity.MAX_TARGET_TEMP, MainActivity.clamp(
                30,
                MainActivity.MIN_TARGET_TEMP,
                MainActivity.MAX_TARGET_TEMP
        ));
    }
}

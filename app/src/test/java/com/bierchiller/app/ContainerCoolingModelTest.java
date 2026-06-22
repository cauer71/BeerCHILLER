package com.bierchiller.app;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

public class ContainerCoolingModelTest {
    @Test
    public void matchesFridgeCalibrationAt12C() {
        assertEquals(136, coolingMinutes(32.94, 12.0, 5.3, 0, 0, 1, 1));
    }

    @Test
    public void matchesFridgeCalibrationAt10C() {
        assertEquals(174, coolingMinutes(32.94, 10.0, 5.3, 0, 0, 1, 1));
    }

    @Test
    public void matchesFreezerCalibrationAt6C() {
        assertEquals(62, coolingMinutes(39.5, 6.0, -17.5, 0, 0, 0, 1));
    }

    @Test
    public void halfLiterBottleFridgeExampleMatchesSpecification() {
        assertEquals(288, coolingMinutes(20.0, 6.0, 4.0, 0, 1, 1, 1));
    }

    @Test
    public void alreadyColdEnoughReturnsZero() {
        MainActivity.CoolingModel model = MainActivity.calculateCoolingModelSeconds(
                6.0,
                8.0,
                4.0,
                MainActivity.containerPresetFor(0, 0),
                1,
                1
        );

        assertEquals(0.0, model.seconds, 0.0001);
    }

    @Test
    public void targetAtDeviceTemperatureIsInvalid() {
        MainActivity.CoolingModel model = MainActivity.calculateCoolingModelSeconds(
                20.0,
                4.0,
                4.0,
                MainActivity.containerPresetFor(0, 0),
                1,
                1
        );

        assertFalse(model.valid);
    }

    @Test
    public void oneLiterCanIsInvalid() {
        assertFalse(MainActivity.containerPresetFor(1, 2).isValid());
    }

    private static int coolingMinutes(double startTempC, double targetTempC, double deviceTempC,
                                      int containerType, int volumeIndex,
                                      int deviceMode, int orientation) {
        MainActivity.CoolingModel model = MainActivity.calculateCoolingModelSeconds(
                startTempC,
                targetTempC,
                deviceTempC,
                MainActivity.containerPresetFor(containerType, volumeIndex),
                deviceMode,
                orientation
        );

        return Math.max(1, (int) Math.ceil(model.seconds / 60.0));
    }
}

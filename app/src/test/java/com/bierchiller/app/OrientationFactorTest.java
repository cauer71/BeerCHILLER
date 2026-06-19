package com.bierchiller.app;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class OrientationFactorTest {
    @Test
    public void lyingKeepsPreviousCoolingTime() {
        double modelSeconds = 1200.0;
        double environmentFactor = 2.0;

        double seconds = MainActivity.applyCalibrationFactors(
                modelSeconds,
                environmentFactor,
                MainActivity.orientationFactorFor(0)
        );

        assertEquals(modelSeconds / environmentFactor, seconds, 0.0001);
    }

    @Test
    public void standingCoolsFasterThanLying() {
        double modelSeconds = 1200.0;
        double environmentFactor = 2.0;
        double lyingSeconds = MainActivity.applyCalibrationFactors(
                modelSeconds,
                environmentFactor,
                MainActivity.orientationFactorFor(0)
        );
        double standingSeconds = MainActivity.applyCalibrationFactors(
                modelSeconds,
                environmentFactor,
                MainActivity.orientationFactorFor(1)
        );

        assertEquals(lyingSeconds / 1.17, standingSeconds, 0.0001);
    }

    @Test
    public void unknownOrientationFallsBackToLying() {
        assertEquals(1.0, MainActivity.orientationFactorFor(99), 0.0001);
    }
}

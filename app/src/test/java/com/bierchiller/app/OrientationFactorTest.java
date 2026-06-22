package com.bierchiller.app;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class OrientationFactorTest {
    @Test
    public void bottleLyingUsesSmallSlowdownFactor() {
        double modelSeconds = 1200.0;
        double environmentFactor = 2.0;

        double seconds = MainActivity.applyCalibrationFactors(
                modelSeconds,
                environmentFactor,
                MainActivity.orientationFactorFor(0)
        );

        assertEquals(modelSeconds * environmentFactor * 0.95, seconds, 0.0001);
    }

    @Test
    public void bottleStandingIsReferencePosition() {
        assertEquals(1.0, MainActivity.orientationFactorFor(1), 0.0001);
    }

    @Test
    public void canLyingUsesCanSpecificFactor() {
        assertEquals(0.92, MainActivity.positionFactorFor(1, 0), 0.0001);
    }

    @Test
    public void unknownOrientationFallsBackToStanding() {
        assertEquals(1.0, MainActivity.orientationFactorFor(99), 0.0001);
    }
}

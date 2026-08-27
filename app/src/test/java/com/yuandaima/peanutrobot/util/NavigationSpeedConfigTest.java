package com.yuandaima.peanutrobot.util;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class NavigationSpeedConfigTest {
    @Test
    public void defaultSpeedIsTheMinimumSupportedSpeed() {
        assertTrue(NavigationSpeedConfig.DEFAULT_SPEED == NavigationSpeedConfig.MIN_SPEED);
        assertTrue(NavigationSpeedConfig.isSupportedSpeed(NavigationSpeedConfig.DEFAULT_SPEED));
    }

    @Test
    public void acceptsInclusiveSpeedBoundaries() {
        assertTrue(NavigationSpeedConfig.isSupportedSpeed(NavigationSpeedConfig.MIN_SPEED));
        assertTrue(NavigationSpeedConfig.isSupportedSpeed(NavigationSpeedConfig.MAX_SPEED));
    }

    @Test
    public void rejectsSpeedsOutsideSupportedRange() {
        assertFalse(NavigationSpeedConfig.isSupportedSpeed(NavigationSpeedConfig.MIN_SPEED - 1));
        assertFalse(NavigationSpeedConfig.isSupportedSpeed(NavigationSpeedConfig.MAX_SPEED + 1));
    }
}

package com.yuandaima.peanutrobot.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class RobotIdConfigTest {
    @Test
    public void defaultRobotIdIsValid() {
        assertEquals(3, RobotIdConfig.DEFAULT_ROBOT_ID);
        assertTrue(RobotIdConfig.isValidRobotId(RobotIdConfig.DEFAULT_ROBOT_ID));
    }

    @Test
    public void acceptsInclusiveRobotIdBoundaries() {
        assertTrue(RobotIdConfig.isValidRobotId(RobotIdConfig.MIN_ROBOT_ID));
        assertTrue(RobotIdConfig.isValidRobotId(RobotIdConfig.MAX_ROBOT_ID));
    }

    @Test
    public void rejectsRobotIdsOutsideSupportedRange() {
        assertFalse(RobotIdConfig.isValidRobotId(0));
        assertFalse(RobotIdConfig.isValidRobotId(-1));
        assertFalse(RobotIdConfig.isValidRobotId(RobotIdConfig.MAX_ROBOT_ID + 1));
    }

    @Test
    public void parsesPositiveIntegers() {
        assertEquals(Integer.valueOf(3), RobotIdConfig.parseRobotId("3"));
        assertEquals(Integer.valueOf(12), RobotIdConfig.parseRobotId("  12  "));
        assertEquals(Integer.valueOf(7), RobotIdConfig.parseRobotId("007"));
        assertEquals(Integer.valueOf(999999999), RobotIdConfig.parseRobotId("999999999"));
    }

    @Test
    public void rejectsInvalidInput() {
        assertNull(RobotIdConfig.parseRobotId(null));
        assertNull(RobotIdConfig.parseRobotId(""));
        assertNull(RobotIdConfig.parseRobotId("   "));
        assertNull(RobotIdConfig.parseRobotId("0"));
        assertNull(RobotIdConfig.parseRobotId("-1"));
        assertNull(RobotIdConfig.parseRobotId("+3"));
        assertNull(RobotIdConfig.parseRobotId("abc"));
        assertNull(RobotIdConfig.parseRobotId("1.5"));
        assertNull(RobotIdConfig.parseRobotId("1 2"));
        assertNull(RobotIdConfig.parseRobotId("1234567890"));
    }
}

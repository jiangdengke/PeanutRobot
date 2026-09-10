package com.yuandaima.peanutrobot.util;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SettingsAccessConfigTest {
    @Test
    public void acceptsFixedSettingsPassword() {
        assertTrue(SettingsAccessConfig.isValidPassword("0"));
    }

    @Test
    public void trimsSurroundingWhitespaceBeforeValidating() {
        assertTrue(SettingsAccessConfig.isValidPassword(" 0 "));
    }

    @Test
    public void rejectsNullEmptyAndBlankPasswords() {
        assertFalse(SettingsAccessConfig.isValidPassword(null));
        assertFalse(SettingsAccessConfig.isValidPassword(""));
        assertFalse(SettingsAccessConfig.isValidPassword("   "));
    }

    @Test
    public void rejectsOtherPasswords() {
        assertFalse(SettingsAccessConfig.isValidPassword("00"));
        assertFalse(SettingsAccessConfig.isValidPassword("1"));
        assertFalse(SettingsAccessConfig.isValidPassword("123456"));
        assertFalse(SettingsAccessConfig.isValidPassword("o"));
    }
}

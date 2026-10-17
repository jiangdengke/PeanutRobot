package com.yuandaima.peanutrobot.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class EmergencyButtonStatusParserTest {
    @Test
    public void parsesBooleanInfo() {
        assertEquals(Boolean.TRUE,
                EmergencyButtonStatusParser.parseEmergencyOpen("{\"data\":{\"info\":true}}"));
        assertEquals(Boolean.FALSE,
                EmergencyButtonStatusParser.parseEmergencyOpen("{\"data\":{\"info\":false}}"));
    }

    @Test
    public void ignoresExtraFields() {
        assertEquals(Boolean.TRUE, EmergencyButtonStatusParser.parseEmergencyOpen(
                "{\"code\":0,\"msg\":\"ok\",\"data\":{\"info\":true,\"other\":1}}"));
    }

    @Test
    public void parsesStringInfo() {
        assertEquals(Boolean.TRUE,
                EmergencyButtonStatusParser.parseEmergencyOpen("{\"data\":{\"info\":\"TRUE\"}}"));
        assertEquals(Boolean.FALSE,
                EmergencyButtonStatusParser.parseEmergencyOpen("{\"data\":{\"info\":\" false \"}}"));
    }

    @Test
    public void returnsNullForMissingOrInvalidData() {
        assertNull(EmergencyButtonStatusParser.parseEmergencyOpen(null));
        assertNull(EmergencyButtonStatusParser.parseEmergencyOpen(""));
        assertNull(EmergencyButtonStatusParser.parseEmergencyOpen("not json"));
        assertNull(EmergencyButtonStatusParser.parseEmergencyOpen("[]"));
        assertNull(EmergencyButtonStatusParser.parseEmergencyOpen("{}"));
        assertNull(EmergencyButtonStatusParser.parseEmergencyOpen("{\"data\":null}"));
        assertNull(EmergencyButtonStatusParser.parseEmergencyOpen("{\"data\":{}}"));
        assertNull(EmergencyButtonStatusParser.parseEmergencyOpen("{\"data\":{\"info\":1}}"));
        assertNull(EmergencyButtonStatusParser.parseEmergencyOpen("{\"data\":{\"info\":\"yes\"}}"));
        assertNull(EmergencyButtonStatusParser.parseEmergencyOpen("{\"data\":{\"info\":{}}}"));
    }
}

package com.yuandaima.peanutrobot.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class UpstreamServerConfigTest {
    @Test
    public void acceptsValidIpv4Addresses() {
        assertTrue(UpstreamServerConfig.isValidHost(UpstreamServerConfig.DEFAULT_HOST));
        assertTrue(UpstreamServerConfig.isValidHost("10.0.0.1"));
        assertTrue(UpstreamServerConfig.isValidHost("0.0.0.0"));
        assertTrue(UpstreamServerConfig.isValidHost("255.255.255.255"));
    }

    @Test
    public void trimsSurroundingWhitespaceBeforeValidating() {
        assertTrue(UpstreamServerConfig.isValidHost("  192.168.112.194  "));
    }

    @Test
    public void rejectsNullEmptyAndBlankHosts() {
        assertFalse(UpstreamServerConfig.isValidHost(null));
        assertFalse(UpstreamServerConfig.isValidHost(""));
        assertFalse(UpstreamServerConfig.isValidHost("   "));
    }

    @Test
    public void rejectsMalformedHosts() {
        assertFalse(UpstreamServerConfig.isValidHost("abc"));
        assertFalse(UpstreamServerConfig.isValidHost("192.168.1"));
        assertFalse(UpstreamServerConfig.isValidHost("192.168.1.1.1"));
        assertFalse(UpstreamServerConfig.isValidHost("192.168.1."));
        assertFalse(UpstreamServerConfig.isValidHost("192.168..1"));
        assertFalse(UpstreamServerConfig.isValidHost("192.168.1.-1"));
        assertFalse(UpstreamServerConfig.isValidHost("192.168.1.1a"));
    }

    @Test
    public void rejectsSegmentsAboveTwoHundredFiftyFive() {
        assertFalse(UpstreamServerConfig.isValidHost("192.168.1.256"));
        assertFalse(UpstreamServerConfig.isValidHost("256.1.1.1"));
        assertFalse(UpstreamServerConfig.isValidHost("192.168.1.9999"));
    }

    @Test
    public void buildsAllUpstreamEndpointsFromSingleHost() {
        String host = "10.1.2.3";
        assertEquals(
                "http://10.1.2.3:9088/nav_arrive",
                UpstreamServerConfig.buildNavArriveUrl(host)
        );
        assertEquals(
                "http://10.1.2.3:9089/delivery.wav",
                UpstreamServerConfig.buildDeliveryVoiceUrl(host)
        );
        assertEquals("ws://10.1.2.3:9096", UpstreamServerConfig.buildStatusReportWs(host));
        assertEquals("ws://10.1.2.3:9098", UpstreamServerConfig.buildWarehouseTaskWs(host));
    }

    @Test
    public void defaultHostKeepsExistingUpstreamEndpoints() {
        String host = UpstreamServerConfig.DEFAULT_HOST;
        assertEquals(
                "http://192.168.112.194:9088/nav_arrive",
                UpstreamServerConfig.buildNavArriveUrl(host)
        );
        assertEquals(
                "http://192.168.112.194:9089/delivery.wav",
                UpstreamServerConfig.buildDeliveryVoiceUrl(host)
        );
        assertEquals("ws://192.168.112.194:9096", UpstreamServerConfig.buildStatusReportWs(host));
        assertEquals("ws://192.168.112.194:9098", UpstreamServerConfig.buildWarehouseTaskWs(host));
    }
}

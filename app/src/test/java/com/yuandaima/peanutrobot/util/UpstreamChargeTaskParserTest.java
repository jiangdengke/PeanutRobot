package com.yuandaima.peanutrobot.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class UpstreamChargeTaskParserTest {

    @Test
    public void validPositivePileIdIsReturnedAfterTrimming() {
        Integer pileId = UpstreamChargeTaskParser.parsePositivePileId(
                "{\"data\":[{\"id\":\" 42 \"}]}"
        );

        assertEquals(Integer.valueOf(42), pileId);
    }

    @Test
    public void malformedJsonIsRejectedWithoutThrowing() {
        assertNull(UpstreamChargeTaskParser.parsePositivePileId("{invalid-json"));
    }

    @Test
    public void missingModelAndDataStructuresAreRejected() {
        assertNull(UpstreamChargeTaskParser.parsePositivePileId("null"));
        assertNull(UpstreamChargeTaskParser.parsePositivePileId("{}"));
        assertNull(UpstreamChargeTaskParser.parsePositivePileId("{\"data\":null}"));
        assertNull(UpstreamChargeTaskParser.parsePositivePileId("{\"data\":[]}"));
        assertNull(UpstreamChargeTaskParser.parsePositivePileId("{\"data\":[null]}"));
    }

    @Test
    public void emptyAndNonNumericPileIdsAreRejected() {
        assertNull(UpstreamChargeTaskParser.parsePositivePileId(
                "{\"data\":[{\"id\":\"\"}]}"
        ));
        assertNull(UpstreamChargeTaskParser.parsePositivePileId(
                "{\"data\":[{\"id\":\"   \"}]}"
        ));
        assertNull(UpstreamChargeTaskParser.parsePositivePileId(
                "{\"data\":[{\"id\":\"not-a-number\"}]}"
        ));
    }

    @Test
    public void zeroAndNegativePileIdsAreRejected() {
        assertNull(UpstreamChargeTaskParser.parsePositivePileId(
                "{\"data\":[{\"id\":\"0\"}]}"
        ));
        assertNull(UpstreamChargeTaskParser.parsePositivePileId(
                "{\"data\":[{\"id\":\"-7\"}]}"
        ));
    }
}

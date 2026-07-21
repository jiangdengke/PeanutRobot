package com.yuandaima.peanutrobot.bean;

import com.google.gson.Gson;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

public class MapPointConfigTest {
    private static final float FLOAT_TOLERANCE = 0.0001f;

    @Test
    public void gsonRoundTripPreservesMapVersionAndMarkerFields() {
        MapPointConfig originalConfig = new MapPointConfig("root_map_v1");
        originalConfig.getMarkers().add(new MapPointConfig.Marker(
                42,
                "会议室",
                0.35f,
                0.72f,
                true
        ));

        Gson gson = new Gson();
        MapPointConfig restoredConfig = gson.fromJson(gson.toJson(originalConfig), MapPointConfig.class);
        MapPointConfig.Marker restoredMarker = restoredConfig.getMarkers().get(0);

        assertEquals("root_map_v1", restoredConfig.getMapVersion());
        assertEquals(42, restoredMarker.getRobotPointId());
        assertEquals("会议室", restoredMarker.getDisplayName());
        assertEquals(0.35f, restoredMarker.getNormalizedX(), FLOAT_TOLERANCE);
        assertEquals(0.72f, restoredMarker.getNormalizedY(), FLOAT_TOLERANCE);
        assertTrue(restoredMarker.isEnabled());
    }

    @Test
    public void copyCreatesIndependentEditingWorkingState() {
        MapPointConfig savedConfig = new MapPointConfig("root_map_v1");
        savedConfig.getMarkers().add(new MapPointConfig.Marker(
                7,
                "取餐区",
                0.2f,
                0.4f,
                true
        ));

        MapPointConfig editingConfig = savedConfig.copy();
        editingConfig.getMarkers().get(0).setDisplayName("临时修改");
        editingConfig.getMarkers().get(0).setEnabled(false);

        assertNotSame(savedConfig.getMarkers().get(0), editingConfig.getMarkers().get(0));
        assertEquals("取餐区", savedConfig.getMarkers().get(0).getDisplayName());
        assertTrue(savedConfig.getMarkers().get(0).isEnabled());
        assertFalse(editingConfig.getMarkers().get(0).isEnabled());
    }
}

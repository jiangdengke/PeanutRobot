package com.yuandaima.peanutrobot.util;

import com.yuandaima.peanutrobot.bean.MapPointConfig;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

public class MapPointConfigSanitizerTest {
    private static final String MAP_VERSION = "root_map_v1";

    @Test
    public void nullAndMismatchedConfigsBecomeEmptyCurrentVersionConfigs() {
        MapPointConfig nullResult = MapPointConfigSanitizer.sanitize(null, MAP_VERSION);
        MapPointConfig mismatchedConfig = new MapPointConfig("root_map_v0");
        mismatchedConfig.getMarkers().add(createMarker(1, 0.2f, 0.3f, true));

        MapPointConfig mismatchedResult = MapPointConfigSanitizer.sanitize(
                mismatchedConfig,
                MAP_VERSION
        );

        assertEquals(MAP_VERSION, nullResult.getMapVersion());
        assertTrue(nullResult.getMarkers().isEmpty());
        assertEquals(MAP_VERSION, mismatchedResult.getMapVersion());
        assertTrue(mismatchedResult.getMarkers().isEmpty());
    }

    @Test
    public void invalidDisabledAndDuplicateMarkersAreRemovedBeforeUse() {
        MapPointConfig sourceConfig = new MapPointConfig(MAP_VERSION);
        MapPointConfig.Marker enabledMarkerAfterDisabledDuplicate = createMarker(
                7,
                0.35f,
                0.65f,
                true
        );
        sourceConfig.setMarkers(Arrays.asList(
                null,
                createMarker(1, Float.NaN, 0.5f, true),
                createMarker(2, 0.5f, Float.POSITIVE_INFINITY, true),
                createMarker(3, -0.1f, 0.5f, true),
                createMarker(4, 0.5f, 1.1f, true),
                createMarker(7, 0.1f, 0.1f, false),
                enabledMarkerAfterDisabledDuplicate,
                createMarker(7, 0.8f, 0.8f, true)
        ));

        MapPointConfig sanitizedConfig = MapPointConfigSanitizer.sanitize(
                sourceConfig,
                MAP_VERSION
        );

        assertEquals(1, sanitizedConfig.getMarkers().size());
        assertEquals(7, sanitizedConfig.getMarkers().get(0).getRobotPointId());
        assertNotSame(enabledMarkerAfterDisabledDuplicate, sanitizedConfig.getMarkers().get(0));
    }

    private MapPointConfig.Marker createMarker(
            int robotPointId,
            float normalizedX,
            float normalizedY,
            boolean enabled
    ) {
        return new MapPointConfig.Marker(
                robotPointId,
                "Point " + robotPointId,
                normalizedX,
                normalizedY,
                enabled
        );
    }
}

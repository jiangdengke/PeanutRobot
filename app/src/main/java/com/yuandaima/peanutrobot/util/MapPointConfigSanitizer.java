package com.yuandaima.peanutrobot.util;

import com.yuandaima.peanutrobot.bean.MapPointConfig;

import java.util.HashSet;
import java.util.Set;

public final class MapPointConfigSanitizer {
    private MapPointConfigSanitizer() {
    }

    public static MapPointConfig sanitize(MapPointConfig sourceConfig, String expectedMapVersion) {
        MapPointConfig sanitizedConfig = new MapPointConfig(expectedMapVersion);
        if (sourceConfig == null
                || expectedMapVersion == null
                || !expectedMapVersion.equals(sourceConfig.getMapVersion())) {
            return sanitizedConfig;
        }

        Set<Integer> configuredRobotPointIds = new HashSet<>();
        for (MapPointConfig.Marker marker : sourceConfig.getMarkers()) {
            if (!isUsableMarker(marker)) {
                continue;
            }
            if (!configuredRobotPointIds.add(marker.getRobotPointId())) {
                continue;
            }
            sanitizedConfig.getMarkers().add(marker.copy());
        }
        return sanitizedConfig;
    }

    public static boolean isUsableMarker(MapPointConfig.Marker marker) {
        return marker != null
                && marker.isEnabled()
                && hasValidNormalizedCoordinates(marker);
    }

    public static boolean hasValidNormalizedCoordinates(MapPointConfig.Marker marker) {
        if (marker == null) {
            return false;
        }
        float normalizedX = marker.getNormalizedX();
        float normalizedY = marker.getNormalizedY();
        return !Float.isNaN(normalizedX)
                && !Float.isInfinite(normalizedX)
                && !Float.isNaN(normalizedY)
                && !Float.isInfinite(normalizedY)
                && normalizedX >= 0f
                && normalizedX <= 1f
                && normalizedY >= 0f
                && normalizedY <= 1f;
    }
}

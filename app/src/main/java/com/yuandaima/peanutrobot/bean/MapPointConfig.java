package com.yuandaima.peanutrobot.bean;

import java.util.ArrayList;
import java.util.List;

public class MapPointConfig {
    private String mapVersion;
    private List<Marker> markers = new ArrayList<>();

    public MapPointConfig() {
    }

    public MapPointConfig(String mapVersion) {
        this.mapVersion = mapVersion;
    }

    public String getMapVersion() {
        return mapVersion;
    }

    public void setMapVersion(String mapVersion) {
        this.mapVersion = mapVersion;
    }

    public List<Marker> getMarkers() {
        if (markers == null) {
            markers = new ArrayList<>();
        }
        return markers;
    }

    public void setMarkers(List<Marker> markers) {
        this.markers = markers == null ? new ArrayList<>() : markers;
    }

    public MapPointConfig copy() {
        MapPointConfig copiedConfig = new MapPointConfig(mapVersion);
        List<Marker> copiedMarkers = new ArrayList<>();
        for (Marker marker : getMarkers()) {
            if (marker != null) {
                copiedMarkers.add(marker.copy());
            }
        }
        copiedConfig.setMarkers(copiedMarkers);
        return copiedConfig;
    }

    public static class Marker {
        private int robotPointId;
        private String displayName;
        private float normalizedX;
        private float normalizedY;
        private boolean enabled;

        public Marker() {
        }

        public Marker(
                int robotPointId,
                String displayName,
                float normalizedX,
                float normalizedY,
                boolean enabled
        ) {
            this.robotPointId = robotPointId;
            this.displayName = displayName;
            this.normalizedX = normalizedX;
            this.normalizedY = normalizedY;
            this.enabled = enabled;
        }

        public int getRobotPointId() {
            return robotPointId;
        }

        public void setRobotPointId(int robotPointId) {
            this.robotPointId = robotPointId;
        }

        public String getDisplayName() {
            return displayName;
        }

        public void setDisplayName(String displayName) {
            this.displayName = displayName;
        }

        public float getNormalizedX() {
            return normalizedX;
        }

        public void setNormalizedX(float normalizedX) {
            this.normalizedX = normalizedX;
        }

        public float getNormalizedY() {
            return normalizedY;
        }

        public void setNormalizedY(float normalizedY) {
            this.normalizedY = normalizedY;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public Marker copy() {
            return new Marker(
                    robotPointId,
                    displayName,
                    normalizedX,
                    normalizedY,
                    enabled
            );
        }
    }
}

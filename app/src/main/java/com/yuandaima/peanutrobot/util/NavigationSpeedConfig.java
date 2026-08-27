package com.yuandaima.peanutrobot.util;

public final class NavigationSpeedConfig {
    public static final int DEFAULT_SPEED = 20;
    public static final int MIN_SPEED = 20;
    public static final int MAX_SPEED = 100;

    private NavigationSpeedConfig() {
    }

    public static boolean isSupportedSpeed(int speed) {
        return speed >= MIN_SPEED && speed <= MAX_SPEED;
    }
}

package com.yuandaima.peanutrobot.util;

/**
 * 设置入口访问控制。
 *
 * <p>设置入口使用固定密码，不随锁屏密码变化，也不提供界面修改入口。
 * 锁屏解锁和长按地图进入编辑仍使用可配置的锁屏密码。
 */
public final class SettingsAccessConfig {
    private static final String SETTINGS_PASSWORD = "0";

    private SettingsAccessConfig() {
    }

    /**
     * 校验设置入口密码。空值、空白和其他内容都不放行。
     */
    public static boolean isValidPassword(String password) {
        return password != null && SETTINGS_PASSWORD.equals(password.trim());
    }
}

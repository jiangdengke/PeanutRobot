package com.yuandaima.peanutrobot.util;

/**
 * 机器人 ID 配置。
 *
 * <p>机器人 ID 用于回充、巡仓、召回 3 种仓库任务指令的 {@code robot_id} 字段。
 * ID 保存在 MMKV 中，每次发送指令时读取，因此修改后不需要重启 App。
 */
public final class RobotIdConfig {
    public static final int DEFAULT_ROBOT_ID = 3;
    public static final int MIN_ROBOT_ID = 1;
    public static final int MAX_ROBOT_ID = 999999999;
    public static final String KEY_ROBOT_ID = "warehouse_robot_id";

    private static final int MAX_DIGITS = 9;

    private RobotIdConfig() {
    }

    /**
     * 返回当前生效的机器人 ID。未保存或保存值非法时回退到默认 ID。
     */
    public static int getRobotId() {
        int savedRobotId = MmkvUtils.decodeInt(KEY_ROBOT_ID, DEFAULT_ROBOT_ID);
        return isValidRobotId(savedRobotId) ? savedRobotId : DEFAULT_ROBOT_ID;
    }

    /**
     * 保存机器人 ID。ID 非法时不写入并返回 false。
     */
    public static boolean saveRobotId(int robotId) {
        if (!isValidRobotId(robotId)) {
            return false;
        }
        MmkvUtils.encode(KEY_ROBOT_ID, robotId);
        return true;
    }

    public static void resetRobotId() {
        MmkvUtils.encode(KEY_ROBOT_ID, DEFAULT_ROBOT_ID);
    }

    public static boolean isValidRobotId(int robotId) {
        return robotId >= MIN_ROBOT_ID && robotId <= MAX_ROBOT_ID;
    }

    /**
     * 解析界面输入的机器人 ID。只接受去除首尾空白后的纯数字正整数，非法时返回 null。
     */
    public static Integer parseRobotId(String text) {
        if (text == null) {
            return null;
        }
        String trimmedText = text.trim();
        if (trimmedText.isEmpty() || trimmedText.length() > MAX_DIGITS) {
            return null;
        }
        int value = 0;
        for (int index = 0; index < trimmedText.length(); index++) {
            char digit = trimmedText.charAt(index);
            if (digit < '0' || digit > '9') {
                return null;
            }
            value = value * 10 + (digit - '0');
        }
        return isValidRobotId(value) ? value : null;
    }
}

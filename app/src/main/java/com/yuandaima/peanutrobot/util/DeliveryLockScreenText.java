package com.yuandaima.peanutrobot.util;

/**
 * 全屏配送锁屏界面的文字。点位序号从 0 开始传入，显示时从 1 开始。
 */
public final class DeliveryLockScreenText {
    public static final String TRAVELING_TITLE = "配送中";
    public static final String WAITING_TITLE = "请取餐";

    private static final String SEPARATOR = " · ";

    private DeliveryLockScreenText() {
    }

    public static String title(boolean waiting) {
        return waiting ? WAITING_TITLE : TRAVELING_TITLE;
    }

    public static String travelingDetail(int routePosition, int routeSize, String pointName) {
        String progress = isValidPosition(routePosition, routeSize)
                ? "正在前往 第 " + (routePosition + 1) + "/" + routeSize + " 个点位"
                : "正在前往配送点位";
        return appendPointName(progress, pointName);
    }

    public static String waitingDetail(int routePosition, int routeSize, String pointName) {
        String progress = isValidPosition(routePosition, routeSize)
                ? "第 " + (routePosition + 1) + "/" + routeSize + " 个点位"
                : "已到达配送点位";
        return appendPointName(progress, pointName);
    }

    /**
     * 仓号无效时返回 null，界面不显示仓号提示。
     */
    public static String bayPrompt(int bay) {
        return bay > 0 ? "请从 " + bay + " 号仓取餐" : null;
    }

    private static boolean isValidPosition(int routePosition, int routeSize) {
        return routeSize > 0 && routePosition >= 0 && routePosition < routeSize;
    }

    private static String appendPointName(String progress, String pointName) {
        if (pointName == null || pointName.trim().isEmpty()) {
            return progress;
        }
        return progress + SEPARATOR + pointName.trim();
    }
}

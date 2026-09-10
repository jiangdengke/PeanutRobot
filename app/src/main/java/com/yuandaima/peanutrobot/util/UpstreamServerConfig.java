package com.yuandaima.peanutrobot.util;

/**
 * 上游服务器地址配置。
 *
 * <p>4 个上游端点部署在同一台服务器上，端口固定，只有 IP 允许现场修改。
 * 地址保存在 MMKV 中，每次使用时读取，因此修改后不需要重启 App。
 */
public final class UpstreamServerConfig {
    public static final String DEFAULT_HOST = "192.168.112.194";
    public static final String KEY_UPSTREAM_HOST = "upstream_server_host";

    private static final int PORT_NAV_ARRIVE = 9088;
    private static final int PORT_DELIVERY_VOICE = 9089;
    private static final int PORT_STATUS_REPORT = 9096;
    private static final int PORT_WAREHOUSE_TASK = 9098;

    private UpstreamServerConfig() {
    }

    /**
     * 返回当前生效的上游 IP。未保存或保存值非法时回退到默认地址。
     */
    public static String getHost() {
        String savedHost = MmkvUtils.decodeString(KEY_UPSTREAM_HOST);
        return isValidHost(savedHost) ? savedHost : DEFAULT_HOST;
    }

    /**
     * 保存上游 IP。地址非法时不写入并返回 false。
     */
    public static boolean saveHost(String host) {
        if (!isValidHost(host)) {
            return false;
        }
        MmkvUtils.saveString(KEY_UPSTREAM_HOST, host.trim());
        return true;
    }

    public static void resetHost() {
        MmkvUtils.saveString(KEY_UPSTREAM_HOST, DEFAULT_HOST);
    }

    public static String getNavArriveUrl() {
        return buildNavArriveUrl(getHost());
    }

    public static String getWarehouseTaskWs() {
        return buildWarehouseTaskWs(getHost());
    }

    public static String getDeliveryVoiceUrl() {
        return buildDeliveryVoiceUrl(getHost());
    }

    public static String getStatusReportWs() {
        return buildStatusReportWs(getHost());
    }

    static String buildNavArriveUrl(String host) {
        return "http://" + host + ":" + PORT_NAV_ARRIVE + "/nav_arrive";
    }

    static String buildWarehouseTaskWs(String host) {
        return "ws://" + host + ":" + PORT_WAREHOUSE_TASK;
    }

    static String buildDeliveryVoiceUrl(String host) {
        return "http://" + host + ":" + PORT_DELIVERY_VOICE + "/delivery.wav";
    }

    static String buildStatusReportWs(String host) {
        return "ws://" + host + ":" + PORT_STATUS_REPORT;
    }

    /**
     * 当前只接受 IPv4 地址，每段取值范围 0-255，不接受前导零以外的多余字符。
     */
    public static boolean isValidHost(String host) {
        if (host == null) {
            return false;
        }
        String trimmedHost = host.trim();
        if (trimmedHost.isEmpty()) {
            return false;
        }
        String[] segments = trimmedHost.split("\\.", -1);
        if (segments.length != 4) {
            return false;
        }
        for (String segment : segments) {
            if (!isValidSegment(segment)) {
                return false;
            }
        }
        return true;
    }

    private static boolean isValidSegment(String segment) {
        if (segment.isEmpty() || segment.length() > 3) {
            return false;
        }
        int value = 0;
        for (int index = 0; index < segment.length(); index++) {
            char digit = segment.charAt(index);
            if (digit < '0' || digit > '9') {
                return false;
            }
            value = value * 10 + (digit - '0');
        }
        return value <= 255;
    }
}

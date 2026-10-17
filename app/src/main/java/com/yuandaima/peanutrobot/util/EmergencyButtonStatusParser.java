package com.yuandaima.peanutrobot.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

/**
 * 解析 Peanut SDK 急停按钮（{@code ButtonStatusApi}）回调。
 *
 * <p>数据格式为 {@code {"data":{"info":true}}}，{@code info} 即 SDK {@code RuntimeInfo} 中的
 * {@code emergencyOpen}，{@code true} 表示急停按下。
 */
public final class EmergencyButtonStatusParser {
    private EmergencyButtonStatusParser() {
    }

    /**
     * 返回急停是否按下；数据缺失或格式错误时返回 null。
     */
    public static Boolean parseEmergencyOpen(String response) {
        if (response == null || response.trim().isEmpty()) {
            return null;
        }
        try {
            JsonElement root = new JsonParser().parse(response);
            if (!root.isJsonObject()) {
                return null;
            }
            JsonElement data = root.getAsJsonObject().get("data");
            if (data == null || !data.isJsonObject()) {
                return null;
            }
            JsonElement info = ((JsonObject) data).get("info");
            if (info == null || !info.isJsonPrimitive()) {
                return null;
            }
            JsonPrimitive infoValue = info.getAsJsonPrimitive();
            if (infoValue.isBoolean()) {
                return infoValue.getAsBoolean();
            }
            if (infoValue.isString()) {
                String infoText = infoValue.getAsString().trim();
                if ("true".equalsIgnoreCase(infoText)) {
                    return true;
                }
                if ("false".equalsIgnoreCase(infoText)) {
                    return false;
                }
            }
            return null;
        } catch (RuntimeException exception) {
            return null;
        }
    }
}

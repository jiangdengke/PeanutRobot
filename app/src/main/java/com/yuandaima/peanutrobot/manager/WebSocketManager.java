package com.yuandaima.peanutrobot.manager;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class WebSocketManager {
    private static final String TAG = "WebSocketManager";

    private WebSocket webSocket;
    private OkHttpClient client;
    private String serverUrl;
    private WebSocketListener listener;

    public WebSocketManager(String url) {
        this.serverUrl = url;
        this.client = new OkHttpClient.Builder()
                .build();
    }
    private int retryCount = 0;
    private Handler reconnectHandler = new Handler(Looper.getMainLooper());
    private Runnable reconnectRunnable = null;

    public void reconnect() {
        if (retryCount > 5) { // 最多重试5次
            Log.e(TAG, "重连次数过多，停止重试");
            return;
        }

        // 取消之前的重连任务
        if (reconnectRunnable != null) {
            reconnectHandler.removeCallbacks(reconnectRunnable);
        }

        reconnectRunnable = () -> {
            Log.d(TAG, "尝试重连 (" + (retryCount + 1) + "/5)");
            disconnect(); // 清理旧连接
            connect();    // 建立新连接
        };

        long delay = (long) Math.pow(2, retryCount) * 1000;
        reconnectHandler.postDelayed(reconnectRunnable, delay);
        retryCount++;
    }

    public void connect() {
        Request request = new Request.Builder()
                .url(serverUrl)
                .build();

        webSocket = client.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                Log.d(TAG, serverUrl+"连接成功");
                if (listener != null) {
                    listener.onOpen(webSocket, response);
                }
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                Log.d(TAG, "收到消息: " + text);
                if (listener != null) {
                    listener.onMessage(webSocket, text);
                }
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                Log.e(TAG, "连接失败: " + t.getMessage());
                if (listener != null) {
                    listener.onFailure(webSocket, t, response);
                }
            }

            @Override
            public void onClosed(@NonNull WebSocket webSocket, int code, @NonNull String reason) {
                Log.e(TAG, "连接失败 code: " + code+"reason:"+reason);
                super.onClosed(webSocket, code, reason);
            }
        });
    }


    public void sendMessage(String message) {
        if (webSocket != null) {
            boolean sent = webSocket.send(message);
            Log.d(TAG, serverUrl+":发送" + (sent ? "成功" : "失败") + ": " + message);
        } else {
            Log.e(TAG, "WebSocket 未连接");
        }
    }

    public void sendMessage(okio.ByteString message) {
      //  Log.d(TAG, serverUrl+":sendMessage="+message);
        if (webSocket != null) {
            boolean sent = webSocket.send(message);
            Log.d("WebSocketsend", serverUrl+":发送" + (sent ? "成功" : "失败") + ": " + message);
        } else {
            Log.e("WebSocketsend", "WebSocket 未连接");
        }
    }
    public void sendMessage2(okio.ByteString message) {
        if (webSocket != null) {
            boolean sent = webSocket.send(message);
            if (!sent) {
                Log.e(TAG, "发送失败，触发重连: " + serverUrl);
                reconnect();
            } else {
                Log.d(TAG, "发送成功，大小: " + message.size() + " bytes");
            }
        } else {
            Log.e(TAG, "WebSocket 未连接，尝试连接");
            connect();
        }
    }
    public void disconnect() {
        if (webSocket != null) {
            webSocket.close(1000, "手动关闭");
            webSocket = null;
        }
    }

    public void setWebSocketListener(WebSocketListener listener) {
        this.listener = listener;
    }

    public boolean isConnected() {
        return webSocket != null;
    }
}
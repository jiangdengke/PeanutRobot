package com.yuandaima.peanutrobot.manager;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class WebSocketManager2 {
    private static final String TAG = "WebSocketManager2";
    private static final int PING_INTERVAL_SEC = 25;
    private static final int MAX_RETRY = 3;
    private static final int RETRY_DELAY_MS = 2000;

    private WebSocket webSocket;
    private OkHttpClient client;
    private String serverUrl;
    private WebSocketListener listener;
    private Handler mainHandler = new Handler(Looper.getMainLooper());

    // 消息队列：存储发送失败的消息，等待重连后重发
    private final Queue<ByteString> pendingMessages = new ConcurrentLinkedQueue<>();
    private boolean isReconnecting = false;
    private boolean isManualDisconnect = false;

    public WebSocketManager2(String url) {
        this.serverUrl = url;
        this.client = new OkHttpClient.Builder()
                .pingInterval(PING_INTERVAL_SEC, TimeUnit.SECONDS)
                .build();
    }

    public void connect() {
        isManualDisconnect = false;
        doConnect();
    }

    private void doConnect() {
        Request request = new Request.Builder()
                .url(serverUrl)
                .build();

        webSocket = client.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                Log.d(TAG, "连接成功: " + serverUrl);
                isReconnecting = false;

                // 重连成功后，发送队列中积压的消息
                flushPendingMessages();

                if (listener != null) {
                    mainHandler.post(() -> listener.onOpen(webSocket, response));
                }
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                if (listener != null) {
                    mainHandler.post(() -> listener.onMessage(webSocket, text));
                }
            }

            @Override
            public void onMessage(WebSocket webSocket, ByteString bytes) {
                if (listener != null) {
                    mainHandler.post(() -> listener.onMessage(webSocket, bytes));
                }
            }

            @Override
            public void onClosed(WebSocket webSocket, int code, String reason) {
                Log.d(TAG, "连接关闭: " + reason+",code=="+code);
                WebSocketManager2.this.webSocket = null;

                if (listener != null) {
                    mainHandler.post(() -> listener.onClosed(webSocket, code, reason));
                }

                if (!isManualDisconnect && !isReconnecting) {
                    doConnect();
                   // scheduleReconnect();
                }
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                Log.e(TAG, "连接失败: " + t.getMessage());
                WebSocketManager2.this.webSocket = null;
                if (listener != null) {
                    mainHandler.post(() -> listener.onFailure(webSocket, t, response));
                }

                if (!isManualDisconnect && !isReconnecting) {
                    doConnect();
                   // scheduleReconnect();
                }
            }
        });
    }

    /**
     * 发送二进制消息（核心方法）
     * 发送失败时自动加入队列，等待重连后重发
     */
    public void sendMessage(ByteString message) {
        if (webSocket != null) {
            try {
                boolean sent = webSocket.send(message);
                if (sent) {
                    Log.d(TAG, "发送成功, size=" + message.size());
                } else {
                    Log.w(TAG, "发送失败, size=" + message.size() + "，加入队列");
                    pendingMessages.offer(message);
                    scheduleReconnect();
                }
            } catch (Exception e) {
                Log.e(TAG, "发送异常: " + e.getMessage());
                pendingMessages.offer(message);
                scheduleReconnect();
            }
        } else {
            Log.w(TAG, "WebSocket 未连接，消息加入队列");
            pendingMessages.offer(message);
            scheduleReconnect();
        }
    }

    /**
     * 发送文本消息
     */
    public void sendMessage(String message) {
        if (webSocket != null) {
            try {
                webSocket.send(message);
                Log.d(TAG, "文本发送成功: " + message);
            } catch (Exception e) {
                Log.e(TAG, "文本发送失败: " + e.getMessage());
                scheduleReconnect();
            }
        } else {
            Log.w(TAG, "WebSocket 未连接");
            scheduleReconnect();
        }
    }

    /**
     * 重连调度
     */
    private void scheduleReconnect() {
        if (isManualDisconnect || isReconnecting) {
            return;
        }

        isReconnecting = true;
        Log.d(TAG, RETRY_DELAY_MS + "ms 后尝试重连");

        mainHandler.postDelayed(() -> {
            Log.d(TAG, "开始重连...");
            doConnect();
        }, RETRY_DELAY_MS);
    }

    /**
     * 发送队列中积压的消息
     */
    private void flushPendingMessages() {
        int count = pendingMessages.size();
        if (count > 0) {
            Log.d(TAG, "开始发送队列中的 " + count + " 条消息");
            ByteString msg;
            while ((msg = pendingMessages.poll()) != null) {
                if (webSocket != null) {
                    boolean sent = webSocket.send(msg);
                    Log.d(TAG, "重发 " + (sent ? "成功" : "失败") + ", size=" + msg.size());
                    if (!sent) {
                        // 重发失败，放回队列
                        pendingMessages.offer(msg);
                    }
                }
            }
        }
    }

    /**
     * 手动断开连接
     */
    public void disconnect() {
        isManualDisconnect = true;
        isReconnecting = false;
        pendingMessages.clear();

        if (webSocket != null) {
            webSocket.close(1000, "手动关闭");
            webSocket = null;
        }
    }

    public boolean isConnected() {
        return webSocket != null;
    }

    public void setWebSocketListener(WebSocketListener listener) {
        this.listener = listener;
    }
}

package com.yuandaima.peanutrobot.server;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import javax.net.ssl.SSLHandshakeException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class WebSocketService extends Service {

    private static final String TAG = "websocket=======";
//    private static final String WS = "wss://echo.websocket.org";//测试地址
//    private static final String WS = "ws://192.168.78.19:9096";
    private static final String WS = "ws://192.168.112.194:9096";
    private static final String WS2 = "Ws://192.168.108.19:9094";

    private WebSocket webSocket;
    private WebSocket webSocket2;
    private WebSocketCallback webSocketCallback;
    private int reconnectTimeout = 5000;
    private boolean connected = false;
   // private Handler mHandler = new Handler();
    private static final long HEART_BEAT_RATE = 10 * 1000;//每隔10秒进行一次对长连接的心跳检测
    private Handler handler = new Handler();
    private OkHttpClient client;
    public class LocalBinder extends Binder {
        public WebSocketService getService() {
            return WebSocketService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new LocalBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        webSocket = connect();
     //   mHandler.postDelayed(heartBeatRunnable, HEART_BEAT_RATE);//开启心跳检测
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        if (webSocket != null) {
            close();
        }
        super.onDestroy();
    }

    public WebSocketService() {
        client = new OkHttpClient.Builder().build();
    }

    private WebSocket connect() {
        Log.d(TAG, "connect " + WS);
      //  OkHttpClient client = new OkHttpClient.Builder().build();
        Request request = new Request.Builder()
                .url(WS)
                .build();

        return client.newWebSocket(request, new WebSocketHandler("Server1"));

    }
    public WebSocket connect2() {
        Log.d(TAG, "connect " + WS2);
        Request request = new Request.Builder()
                .url(WS2)
                .build();
        return client.newWebSocket(request, new WebSocketHandler("Server2"));
    }
    public void send(String text) {
        if (webSocket != null) {
            Log.d(TAG, "send ===" + text);
            webSocket.send(text);
        }
    }

    public void close() {
        if (webSocket != null) {
            boolean shutDownFlag = webSocket.close(1000, "manual close");
            Log.d(TAG, "shutDownFlag " + shutDownFlag);
            webSocket = null;
        }
    }
    public void open(){
        if (webSocket==null){
            webSocket=connect();
        }
    }

    private void reconnect() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "reconnect...");
                if (!connected) {
                    webSocket = connect();
               //     handler.postDelayed(this, reconnectTimeout);
                }
            }
        }, reconnectTimeout);
    }
    private class WebSocketHandler extends WebSocketListener {
        private String serverName="";

        public WebSocketHandler(String serverName) {
            this.serverName = serverName;
        }
        @Override
        public void onOpen(WebSocket webSocket, Response response) {
            Log.d(TAG, serverName+"WebSocket 连接成功");

            if (webSocketCallback != null) {
                Log.d(TAG, "webSocketCallbackOpen=======");
                webSocketCallback.onOpen();
            }
            connected = true;
        }
        @Override
        public void onMessage(@NonNull WebSocket webSocket, @NonNull ByteString bytes) {
            Log.d(TAG, serverName+"收到二进制数据，大小: " + bytes.size() + " 字节");
            super.onMessage(webSocket, bytes);
            if (webSocketCallback != null) {
                webSocketCallback.onMessage(bytes);
            }else {
                Log.d(TAG, "webSocketCallback=null");
            }
        }
        @Override
        public void onMessage(@NonNull WebSocket webSocket, String text) {
            Log.d(TAG, serverName+"WebSocketService收到的消息：" + text);
            if (webSocketCallback != null) {
                webSocketCallback.onMessage(text);
            }else {
                Log.d(TAG, "webSocketCallback=null");
            }
        }

        @Override
        public void onClosed(WebSocket webSocket, int code, String reason) {
            Log.d(TAG, serverName+"onClose() 连接断开_reason：" + reason );
            if (webSocketCallback != null) {
                webSocketCallback.onClosed();
            }
            connected = false;
            reconnect();
        }

        /**
         * Invoked when a web socket has been closed due to an error reading from or writing to the
         * network. Both outgoing and incoming messages may have been lost. No further calls to this
         * listener will be made.
         */
        @Override
        public void onFailure(WebSocket webSocket, Throwable t, Response response) {
            Log.d(TAG, serverName+"onError() 连接出错：" + t.getMessage() +",code=");
            if (t instanceof SocketTimeoutException) {
                Log.e(TAG, "连接超时，服务器无响应，response=null");
            }
            if (t instanceof ConnectException) {
                Log.e(TAG, "服务器主动拒绝连接，response=null");
            }



            if (t instanceof UnknownHostException) {
                Log.e(TAG, "地址解析失败，response=null");
            }
            if (t instanceof SSLHandshakeException) {
                Log.e(TAG, "SSL握手失败，response=null");
            }
            connected = false;
            reconnect();
        }

    }
    private long sendTime = 0L;
    // 发送心跳包
//    private Runnable heartBeatRunnable = new Runnable() {
//        @Override
//        public void run() {
//            if (System.currentTimeMillis() - sendTime >= HEART_BEAT_RATE) {
//                if(webSocket!=null) {
//                    Log.e(TAG, "发送心跳包");
//                    boolean isSuccess = webSocket.send("android-心跳");//发送一个消息给服务器，通过发送消息的成功失败来判断长连接的连接状态
//                    if (!isSuccess) {//长连接已断开，
//                        Log.e(TAG, "发送心跳包-------------长连接已断开");
//                        mHandler.removeCallbacks(heartBeatRunnable);
//                        webSocket.cancel();//取消掉以前的长连接
//                       // new InitSocketThread().start();//创建一个新的连接
//                        reconnect();
//                    } else {//长连接处于连接状态---
//                        Log.e(TAG, "发送心跳包-------------长连接处于连接状态");
//                    }
//                }
//
//                sendTime = System.currentTimeMillis();
//            }
//            mHandler.postDelayed(this, HEART_BEAT_RATE);//每隔一定的时间，对长连接进行一次心跳检测
//        }
//    };
    public interface WebSocketCallback {
        void onMessage(String text);
        void onMessage(ByteString bytes);

        void onOpen();

        void onClosed();
    }

    public void setWebSocketCallback(WebSocketCallback webSocketCallback) {
        this.webSocketCallback = webSocketCallback;
    }
}

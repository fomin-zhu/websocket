package com.fomin.websocket;

import android.util.Log;
import okhttp3.*;
import okio.ByteString;

import java.util.concurrent.TimeUnit;

/**
 * Created by Fomin on 2019/1/5.
 */
public final class WebSocketManager {
    private final static String TAG = WebSocketManager.class.getSimpleName();
    private final static int MAX_NUM = 5;       // 最大重连数
    private final static int MILLIS = 5000;     // 重连间隔时间，毫秒
    private volatile static WebSocketManager manager;

    private OkHttpClient client;
    private Request request;
    private IReceiveMessage receiveMessage;
    private WebSocket mWebSocket;

    private boolean isConnect = false;
    private int connectNum = 0;

    private WebSocketManager() {
    }

    public static WebSocketManager getInstance() {
        if (manager == null) {
            synchronized (WebSocketManager.class) {
                if (manager == null) {
                    manager = new WebSocketManager();
                }
            }
        }
        return manager;
    }

    public void init(String url, IReceiveMessage message) {
        client = new OkHttpClient.Builder()
                .writeTimeout(5, TimeUnit.SECONDS)
                .readTimeout(5, TimeUnit.SECONDS)
                .connectTimeout(10, TimeUnit.SECONDS)
                .build();
        request = new Request.Builder().url(url).build();
        receiveMessage = message;
        connect();
    }

    /**
     * 连接
     */
    public void connect() {
        if (isConnect()) {
            Log.i(TAG, "web socket connected");
            return;
        }
        client.newWebSocket(request, createListener());
    }

    /**
     * 重连
     */
    public void reconnect() {
        if (connectNum <= MAX_NUM) {
            try {
                Thread.sleep(MILLIS);
                connect();
                connectNum++;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            Log.i(TAG, "reconnect over " + MAX_NUM + ",please check url or network");
        }
    }

    /**
     * 是否连接
     */
    public boolean isConnect() {
        return mWebSocket != null && isConnect;
    }

    /**
     * 发送消息
     *
     * @param text 字符串
     * @return boolean
     */
    public boolean sendMessage(String text) {
        if (!isConnect()) return false;
        return mWebSocket.send(text);
    }

    /**
     * 发送消息
     *
     * @param byteString 字符集
     * @return boolean
     */
    public boolean sendMessage(ByteString byteString) {
        if (!isConnect()) return false;
        return mWebSocket.send(byteString);
    }

    /**
     * 关闭连接
     */
    public void close() {
        if (isConnect()) {
            mWebSocket.cancel();
            mWebSocket.close(1001, "客户端主动关闭连接");
        }
    }

    private WebSocketListener createListener() {
        return new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                super.onOpen(webSocket, response);
                Log.d(TAG, "open:" + response.toString());
                mWebSocket = webSocket;
                isConnect = response.code() == 101;
                if (!isConnect) {
                    reconnect();
                } else {
                    Log.i(TAG, "connect success.");
                    if (receiveMessage != null) {
                        receiveMessage.onConnectSuccess();
                    }
                }
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                super.onMessage(webSocket, text);
                if (receiveMessage != null) {
                    receiveMessage.onMessage(text);
                }
            }

            @Override
            public void onMessage(WebSocket webSocket, ByteString bytes) {
                super.onMessage(webSocket, bytes);
                if (receiveMessage != null) {
                    receiveMessage.onMessage(bytes.base64());
                }
            }

            @Override
            public void onClosing(WebSocket webSocket, int code, String reason) {
                super.onClosing(webSocket, code, reason);
                mWebSocket = null;
                isConnect = false;
                if (receiveMessage != null) {
                    receiveMessage.onClose();
                }
            }

            @Override
            public void onClosed(WebSocket webSocket, int code, String reason) {
                super.onClosed(webSocket, code, reason);
                mWebSocket = null;
                isConnect = false;
                if (receiveMessage != null) {
                    receiveMessage.onClose();
                }
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                super.onFailure(webSocket, t, response);
                if (response != null) {
                    Log.i(TAG, "connect failed：" + response.message());
                }
                Log.i(TAG, "connect failed throwable：" + t.getMessage());
                isConnect = false;
                if (receiveMessage != null) {
                    receiveMessage.onConnectFailed();
                }
                reconnect();
            }
        };
    }
}

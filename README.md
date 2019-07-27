### 什么是WebSocket
WebSocket是一种在单个TCP连接上进行全双工通信的协议。它使得客户端和服务器之间的数据交换变得更加简单，允许服务端主动向客户端推送数据。在WebSocket API中，客户端和服务器只需要完成一次握手，两者之间就直接可以创建持久性的连接，并进行双向数据传输。
![在这里插入图片描述](https://img-blog.csdnimg.cn/20190107105658962.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2ZvbWluX3podQ==,size_16,color_FFFFFF,t_70)
Websocket使用和 HTTP 相同的 TCP 端口，可以绕过大多数防火墙的限制。默认情况下，Websocket协议使用80端口；运行在TLS之上时，默认使用443端口。其协议标识符为ws，如果加密使用wss，服务器网址为URL，如：
```
ws://www.example.com/
wss://www.example.com/
```
Webscoket具有以下优点：
* 较少的控制开销：在连接创建后，服务器和客户端之间交换数据时，用于协议控制的数据包头部相对较小
* 更强的实时性：由于协议是全双工的，所以服务器可以随时主动给客户端下发数据
* 保持连接状态：与HTTP不同的是，Websocket需要先创建连接，这就使得其成为一种有状态的协议，之后通信时可以省略部分状态信息。而HTTP请求可能需要在每个请求都携带状态信息（如身份认证等）
* 更好的二进制支持：Websocket定义了二进制帧，相对HTTP，可以更轻松地处理二进制内容
* 更好的压缩效果：相对于HTTP压缩，Websocket在适当的扩展支持下，可以沿用之前内容的上下文，在传递类似的数据时，可以显著地提高压缩率
### OkHttp WebSocket使用
引用okhttp库
```
implementation 'com.squareup.okhttp3:okhttp:3.12.1'
```
初始化okhttp，创建request，并开始连接websocket
```
client = new OkHttpClient.Builder()
        .writeTimeout(5, TimeUnit.SECONDS)
        .readTimeout(5, TimeUnit.SECONDS)
        .connectTimeout(10, TimeUnit.SECONDS)
        .build();
request = new Request.Builder().url(url).build();
client.newWebSocket(request, createListener());
```
createListener创建相关监听事件
```
private WebSocketListener createListener() {
    return new WebSocketListener() {
        @Override
        public void onOpen(WebSocket webSocket, Response response) {
            super.onOpen(webSocket, response);
            Log.d(TAG, "open:" + response.toString());
            mWebSocket = webSocket;
        }

        @Override
        public void onMessage(WebSocket webSocket, String text) {
            super.onMessage(webSocket, text);
        }

        @Override
        public void onMessage(WebSocket webSocket, ByteString bytes) {
            super.onMessage(webSocket, bytes);
        }

        @Override
        public void onClosing(WebSocket webSocket, int code, String reason) {
            super.onClosing(webSocket, code, reason);
        }

        @Override
        public void onClosed(WebSocket webSocket, int code, String reason) {
            super.onClosed(webSocket, code, reason);
        }

        @Override
        public void onFailure(WebSocket webSocket, Throwable t, Response response) {
            super.onFailure(webSocket, t, response);
        }
    };
}
```
发送消息，WebSocket 提供了两个方法，一个直接发送字符串，一个发送二进制数据
```
mWebSocket.send(text);
mWebSocket.send(byteString);
```
简单的websocket已经搭建好，详细代码在[github](https://github.com/fomin-zhu/websocket.git)上，后续还会在跟进





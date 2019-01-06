package com.fomin.websocket

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okio.ByteString
import kotlin.concurrent.thread


class MainActivity : AppCompatActivity(), IReceiveMessage {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btn_connect.setOnClickListener {
            thread {
                kotlin.run {
                    WebSocketManager.getInstance().init("", this)
                }
            }
        }
        btn_client_send.setOnClickListener {
            if (WebSocketManager.getInstance().sendMessage("客户端发送")) {
                addText("客户端发送")
            }
        }
        btn_client_close.setOnClickListener {
            WebSocketManager.getInstance().close()
        }
    }

    override fun onConnectSuccess() {
        addText("连接成功\n")
    }

    override fun onConnectFailed() {
        addText("连接失败\n")
    }

    override fun onClose() {
        addText("关闭成功\n")
    }

    override fun onMessage(text: String?) {
        addText("接收消息：$text\n")
    }

    private fun addText(text: String?) {
        runOnUiThread {
            et_content.text.append(text)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        WebSocketManager.getInstance().close()
    }
}

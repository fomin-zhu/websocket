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


class MainActivity : AppCompatActivity() {
    private val TAG = MainActivity::class.java.simpleName
    private lateinit var mockWebServer: MockWebServer
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initMock()
        btn_connect.setOnClickListener {
            initSocket()
        }
    }

    private fun initMock() {
        mockWebServer = MockWebServer()
        mockWebServer.enqueue(MockResponse().withWebSocketUpgrade(object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket?, response: Response?) {
                super.onOpen(webSocket, response)
                Log.d(TAG, "onOpen:" + response!!.message())
            }

            override fun onMessage(webSocket: WebSocket?, text: String?) {
                super.onMessage(webSocket, text)
                Log.d(TAG, "onMessage:" + text!!)
            }

            override fun onMessage(webSocket: WebSocket?, bytes: ByteString?) {
                super.onMessage(webSocket, bytes)
                Log.d(TAG, "onMessage:" + bytes!!.base64())
            }

            override fun onClosing(webSocket: WebSocket?, code: Int, reason: String?) {
                super.onClosing(webSocket, code, reason)
                Log.d(TAG, "onClosing:" + reason!!)
            }

            override fun onClosed(webSocket: WebSocket?, code: Int, reason: String?) {
                super.onClosed(webSocket, code, reason)
                Log.d(TAG, "onClosed:" + reason!!)
            }

            override fun onFailure(webSocket: WebSocket?, t: Throwable?, response: Response?) {
                super.onFailure(webSocket, t, response)
                Log.d(TAG, "onFailure:" + response!!.message())
            }
        }))
    }

    private fun initSocket() {
        thread {
            kotlin.run {
                val hostName = mockWebServer.hostName
                val port = mockWebServer.port
                val url = "ws://$hostName:$port/"
                Log.d(TAG, "mockWebServer:$url")
                WebSocketManager.getInstance().init(url, createReceive())
                runOnUiThread {
                    et_content.text = Editable.Factory.getInstance().newEditable("开始连接:$url")
                }
            }
        }
    }

    private fun createReceive(): IReceiveMessage {
        return IReceiveMessage {
            Log.d(TAG, "receive:$it")
        }
    }
}

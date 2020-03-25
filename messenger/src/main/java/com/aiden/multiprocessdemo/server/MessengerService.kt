package com.aiden.multiprocessdemo.server

import android.app.Service
import android.content.Intent
import android.os.*
import android.util.Log

/**
 * @author sunwei
 * email：tianmu19@gmail.com
 * date：2020/3/25 14:39
 * package：com.aiden.multiprocessdemo.server
 * version：1.0
 * <p>description：   服务端           </p>
 */
class MessengerService : Service() {
    companion object {
        const val TAG: String = "server>"
    }

    //此Messenger将客户端发送的消息传递给 MessengerHandler
    private val messenger: Messenger = Messenger(MessengerHandler())

    class MessengerHandler : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                1008 -> {
                    Log.e(TAG, msg.data.get("msg").toString())
                    val message = Message.obtain()
                    val messenger = msg.replyTo
                    val bdl = Bundle()
                    bdl.putString("msg", "我是服务端的数据哦！")
                    message.what = 1000
                    message.data = bdl
                    messenger.send(message)
                }
                else -> Log.e(TAG, "no data.")
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return messenger.binder
    }
}
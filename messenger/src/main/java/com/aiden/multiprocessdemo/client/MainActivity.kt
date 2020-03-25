package com.aiden.multiprocessdemo.client

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.*
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.aiden.multiprocessdemo.R
import com.aiden.multiprocessdemo.server.MessengerService

/**
 * @author sunwei
 * email：tianmu19@gmail.com
 * date：2020/3/25 15:54
 * version：1.0
 * <p>description：客户端   </p>
 */

class MainActivity : AppCompatActivity() {
    companion object {
        const val TAG: String = "client>"
    }

    lateinit var messenger: Messenger
    val replyMessenger = Messenger(MessengerHandler())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        processLogic()
    }

    private fun processLogic() {
        val intent = Intent(this, MessengerService::class.java)
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    private val serviceConnection: ServiceConnection by lazy {
        object : ServiceConnection {
            override fun onServiceDisconnected(name: ComponentName?) {

            }

            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                messenger = Messenger(service)
                val message = Message.obtain()
                message.what = 1008
                val bdl = Bundle()
                bdl.putString("msg", "此消息是来自activity里client")
                message.data = bdl
                message.replyTo = replyMessenger
                messenger.send(message)
            }
        }
    }


    private class MessengerHandler : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                1000 -> Log.e(TAG, msg.data.getString("msg").toString())
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unbindService(serviceConnection)
    }

}

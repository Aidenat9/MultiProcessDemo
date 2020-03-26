package com.aiden.aidl

import android.app.Service
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.aiden.aidl.data.MsgModel
import com.aiden.aidl.server.ServerService

class ClientActivity : AppCompatActivity() {

    private var messageSender: IMessageSender? = null

    companion object {
        const val TAG = "aidl> "
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_client)

        //start service
        startService1()
    }

    private fun startService1() {
        val intent = Intent(this, ServerService::class.java)
        bindService(intent, serviceConnection, Service.BIND_AUTO_CREATE)
        startService(intent)
    }

    private val serviceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {

        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            messageSender = IMessageSender.Stub.asInterface(service)
            val msg = MsgModel("client", "server", "从客户端到服务端的数据"+Thread.currentThread().name)
            //死亡监听
            messageSender?.asBinder()?.linkToDeath(deathRecipient, 0)
            messageSender?.registerReceiver(receiver)
            //send
            messageSender?.sendMsg(msg)
        }
    }

    //callback
    private var receiver: IMessageReceiver = object : IMessageReceiver.Stub() {
        override fun onReceived(model: MsgModel?) {
            //收到服务端发过来的信息
            Log.e(TAG, model.toString())
        }

    }

    private var deathRecipient = object : IBinder.DeathRecipient {
        override fun binderDied() {
            if (null != messageSender) {
                messageSender!!.asBinder().unlinkToDeath(this, 0)
                messageSender = null
                startService1()
            }
        }
    }

    override fun onDestroy() {
        if (null != messageSender && messageSender?.asBinder()?.isBinderAlive!!) {
            try {
                messageSender!!.detachReceiver(receiver)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        unbindService(serviceConnection)
        super.onDestroy()
    }

}

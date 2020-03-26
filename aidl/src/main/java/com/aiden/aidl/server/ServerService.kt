package com.aiden.aidl.server

import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Binder
import android.os.IBinder
import android.os.Parcel
import android.os.RemoteCallbackList
import android.util.Log
import com.aiden.aidl.IMessageReceiver
import com.aiden.aidl.IMessageSender
import com.aiden.aidl.data.MsgModel

/**
 * @author sunwei
 * email：tianmu19@gmail.com
 * date：2020/3/26 9:18
 * package：com.aiden.aidl.server
 * version：1.0
 * <p>description：              </p>
 */
class ServerService : Service() {

    companion object {
        const val TAG = "aidl> "
    }

    //管理多进程的list (专门用来管理多进程回调接口
    private var remoteCallbackList: RemoteCallbackList<IMessageReceiver> = RemoteCallbackList()

    private var sender: IMessageSender = object : IMessageSender.Stub() {
        override fun registerReceiver(receiver: IMessageReceiver?) {
            remoteCallbackList.register(receiver)
        }

        override fun sendMsg(msg: MsgModel?) {
            //客户端发送过来的数据
            Log.e(TAG, msg.toString())
        }

        override fun detachReceiver(receiver: IMessageReceiver?) {
            remoteCallbackList.unregister(receiver)
        }

        override fun onTransact(code: Int, data: Parcel, reply: Parcel?, flags: Int): Boolean {
            //验证包名权限，才可以互相通信，避免其他程序调用我们的服务
            val nameForUid = packageManager.getNameForUid(Binder.getCallingUid())
            if (nameForUid == null || !nameForUid.startsWith("com.aiden.aidl")) {
                Log.e(TAG, "拒绝其他包调用我们的服务")
                return false
            }
            return super.onTransact(code, data, reply, flags)
        }

    }

    override fun onBind(intent: Intent?): IBinder? {
        if (!checkUserPermission()) {
            Log.e(TAG,"没有自定义的权限，不能调用此服务")
            return null
        }
        return sender.asBinder()
    }

    private fun checkUserPermission(): Boolean {
        if (checkCallingOrSelfPermission("com.aiden.aidl.permission.Aidl") == PackageManager.PERMISSION_DENIED) {
            return false
        }
        return true
    }

    override fun onCreate() {
        super.onCreate()
        flag = true
        Thread(runnable).start()
    }

    override fun onDestroy() {
        super.onDestroy()
        flag = false
    }

    private var flag = true
    private val runnable = object : Runnable {
        override fun run() {
            while (flag) {
                Thread.sleep(5000)
                val msg = MsgModel("server", "client", "从服务端到客户端的数据"+Thread.currentThread().name)
                val listenerCount = remoteCallbackList.beginBroadcast()
                Log.e(
                    TAG,
                    "listenerCount == $listenerCount"
                )
                for (i in 0 until listenerCount) {
                    remoteCallbackList.getBroadcastItem(i)?.onReceived(msg)
                }
                remoteCallbackList.finishBroadcast()
            }
        }
    }


}
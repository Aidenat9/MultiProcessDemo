// IMessageSender.aidl
package com.aiden.aidl;
import com.aiden.aidl.IMessageReceiver;
import com.aiden.aidl.data.MsgModel;
// Declare any non-default types here with import statements
//client
interface IMessageSender {
    void registerReceiver(IMessageReceiver receiver);
    void detachReceiver(IMessageReceiver receiver);
    void sendMsg(in MsgModel msg);
}

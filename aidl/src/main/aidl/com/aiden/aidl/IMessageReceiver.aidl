// IMessageReceiver.aidl
package com.aiden.aidl;
import com.aiden.aidl.data.MsgModel;

// Declare any non-default types here with import statements
//server
interface IMessageReceiver {
    void onReceived(in MsgModel model);
}

package com.aiden.aidl.data

import android.os.Parcel
import android.os.Parcelable

/**
 * @author sunwei
 * email：tianmu19@gmail.com
 * date：2020/3/26 9:09
 * package：com.aiden.aidl.data
 * version：1.0
 * <p>description：              </p>
 */
data class MsgModel(
    val from: String?,
    val to: String?,
    val content: String?
) : Parcelable {
    constructor(source: Parcel) : this(
        source.readString(),
        source.readString(),
        source.readString()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(from)
        writeString(to)
        writeString(content)
    }

    override fun toString(): String {
        return "MsgModel(from=$from, to=$to, content=$content)"
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<MsgModel> = object : Parcelable.Creator<MsgModel> {
            override fun createFromParcel(source: Parcel): MsgModel = MsgModel(source)
            override fun newArray(size: Int): Array<MsgModel?> = arrayOfNulls(size)
        }
    }


}
package com.example.cortoescritura.models

import android.os.Parcel
import android.os.Parcelable
import java.util.*

data class Comment(
    val storyId :String="",
    val comment : String ="",
    var commentedBy : String ="",
    val date : Date = Date()
):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readDate()!!
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int)= with(parcel) {
        writeString(storyId)
        writeString(comment)
        writeString(commentedBy)
        writeDate(date)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Comment> {
        override fun createFromParcel(parcel: Parcel): Comment {
            return Comment(parcel)
        }

        override fun newArray(size: Int): Array<Comment?> {
            return arrayOfNulls(size)
        }

        fun Parcel.writeDate(date: Date?) {
            writeLong(date?.time ?: -1)
        }

        fun Parcel.readDate(): Date? {
            val long = readLong()
            return if (long != -1L) Date(long) else null
        }
    }
}
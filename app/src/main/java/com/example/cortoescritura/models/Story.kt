package com.example.cortoescritura.models

import android.os.Parcel
import android.os.Parcelable
import java.util.Date
import kotlin.collections.ArrayList

data class Story(
    var name : String ="",
    val createdBy: String ="",
    var genre : String = "",
    var ratings :  ArrayList<Rating> = ArrayList(),
    var comments : ArrayList<Comment> = ArrayList(),
    var averageRating : Double = 0.0,
    var storyContent : String ="",
    var storyId : String = "",
    var dateCreated: Date = Date()
):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.createTypedArrayList(Rating.CREATOR)!!,
        parcel.createTypedArrayList(Comment.CREATOR)!!,
        parcel.readDouble(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readDate()!!
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int)=with(parcel) {
        writeString(name)
        writeString(createdBy)
        writeString(genre)
        writeDouble(averageRating)
        writeString(storyContent)
        writeTypedList(ratings)
        writeTypedList(comments)
        writeString(storyId)
        writeDate(dateCreated)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Story> {
        override fun createFromParcel(parcel: Parcel): Story {
            return Story(parcel)
        }

        override fun newArray(size: Int): Array<Story?> {
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
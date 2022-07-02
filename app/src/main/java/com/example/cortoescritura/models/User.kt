package com.example.cortoescritura.models

import android.os.Parcel
import android.os.Parcelable

data class User (
    val id: String ="",
    val name : String = "",
    val email : String = "",
    val image : String ="",
    val mobile : Long = 0,
    val fcmToken : String ="",
    var selected:Boolean = false,
    var confirmedEmail: Boolean = false
): Parcelable{
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readLong(),
        parcel.readString()!!
    ) {
    }

    override fun describeContents(): Int = 0

    override fun writeToParcel(dest: Parcel, p1: Int) = with(dest) {
        writeString(id)
        writeString(name)
        writeString(email)
        writeString(image)
        writeLong(mobile)
        writeString(fcmToken)
    }

    companion object CREATOR : Parcelable.Creator<User> {
        override fun createFromParcel(parcel: Parcel): User {
            return User(parcel)
        }

        override fun newArray(size: Int): Array<User?> {
            return arrayOfNulls(size)
        }
    }

}
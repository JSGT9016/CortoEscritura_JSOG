package com.example.cortoescritura.models

import android.os.Parcel
import android.os.Parcelable

data class Rating(
    var user : String ="",
    var rate : Float = 0.0F
):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readFloat()!!
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int)=with(parcel) {
        writeString(user)
        writeFloat(rate)
    }

    override fun describeContents(): Int =0

    companion object CREATOR : Parcelable.Creator<Rating> {
        override fun createFromParcel(parcel: Parcel): Rating {
            return Rating(parcel)
        }

        override fun newArray(size: Int): Array<Rating?> {
            return arrayOfNulls(size)
        }
    }
}
package com.example.cortoescritura.models

import android.os.Parcel
import android.os.Parcelable

data class Report(
    var userReporting :String = "",
    var storyReported :String ="",
    var reasonReported : String = "",
    var furtherExplanation : String =""
):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int)=with(parcel) {
        writeString(userReporting)
        writeString(storyReported)
        writeString(reasonReported)
        writeString(furtherExplanation)
    }

    override fun describeContents(): Int =0

    companion object CREATOR : Parcelable.Creator<Report> {
        override fun createFromParcel(parcel: Parcel): Report {
            return Report(parcel)
        }

        override fun newArray(size: Int): Array<Report?> {
            return arrayOfNulls(size)
        }
    }

}
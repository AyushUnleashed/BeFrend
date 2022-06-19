package com.ayushunleashed.mitram.models

import android.os.Parcel
import android.os.Parcelable

data class UserModel(
    val uid: String? = "",
    val displayName: String? = "",
    val imageUrl: String? ="",
    val bio: String? ="",
    val likedBy: ArrayList<String> = ArrayList(),
    val connections: ArrayList<String> = ArrayList(),
    val usersYouLiked:ArrayList<String> = ArrayList()
):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        TODO("likedBy"),
        TODO("connections"),
        TODO("usersYouLiked")
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(uid)
        parcel.writeString(displayName)
        parcel.writeString(imageUrl)
        parcel.writeString(bio)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<UserModel> {
        override fun createFromParcel(parcel: Parcel): UserModel {
            return UserModel(parcel)
        }

        override fun newArray(size: Int): Array<UserModel?> {
            return arrayOfNulls(size)
        }
    }
}




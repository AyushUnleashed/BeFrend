package com.ayushunleashed.mitram.models

import android.os.Parcel
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserModel(
    val uid: String? = "",
    var displayName: String? = "",
    var imageUrl: String? ="",
    var bio: String? ="",
    var isOnline:Boolean = false,
    val likedBy: ArrayList<String> = ArrayList(),
    val connections: ArrayList<String> = ArrayList(),
    val usersYouLiked:ArrayList<String> = ArrayList(),
    var fcmToken:String? = ""

): Parcelable
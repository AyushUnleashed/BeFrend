package com.ayushunleashed.mitram.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
public data class UserModel(
    public val uid: String? = "",
    public var displayName: String? = "",
    public var imageUrl: String? ="",
    public var bio: String? ="",
    public var email:String?="",
    public var isOnline:Boolean = false,
    public var userCollegeName: String? ="",
    public var userCollegeYear: String? ="",
    public var userCollegeStream: String? ="",
    public val likedBy: ArrayList<String> = ArrayList(),
    public val connections: ArrayList<String> = ArrayList(),
    public val usersYouLiked:ArrayList<String> = ArrayList(),
    public var fcmToken:String? = "",
    public var skills:ArrayList<String> = ArrayList(),
    public var interests:ArrayList<String> = ArrayList(),
): Parcelable
package com.ayushunleashed.mitram.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UtilityModel(
    var allUsersUid:ArrayList<String> = ArrayList(),
): Parcelable
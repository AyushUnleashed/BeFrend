package com.ayushunleashed.mitram.models

data class UserModel(val uid:String = "",
                     val displayName:String = "",
                     val imageUrl:String ="",
                     val bio:String ="",
                     val likedBy: ArrayList<String> = ArrayList(),
                     val connections: ArrayList<String> = ArrayList(),
                     val usersYouLiked:ArrayList<String> = ArrayList()
)






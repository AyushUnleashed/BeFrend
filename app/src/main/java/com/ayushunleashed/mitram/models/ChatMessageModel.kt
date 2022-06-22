package com.ayushunleashed.mitram.models

data class ChatMessageModel(
    var senderId:String="",
    var receiverId:String="",
    var messageText:String="",
    var dateTime:String=""
)

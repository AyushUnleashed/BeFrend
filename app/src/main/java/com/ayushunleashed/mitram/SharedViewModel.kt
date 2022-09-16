package com.ayushunleashed.mitram

import androidx.lifecycle.ViewModel
import com.ayushunleashed.mitram.models.UserModel

class SharedViewModel: ViewModel() {
     var currentUserModel =UserModel()
     public var myUsersList:MutableList<UserModel> = mutableListOf()
     public var myConnectionsList:MutableList<UserModel> = mutableListOf()
     public var myLikesList:MutableList<UserModel> = mutableListOf()
     public var myLikeRequestsSentList:MutableList<UserModel> = mutableListOf()
     public var loadedDiscoverFragmentBefore:Boolean = false
     var isUsersPresentForDiscoverFragment = true
     public var loadedConnectionsFragmentBefore:Boolean = false
     public var loadedLikesFragmentBefore:Boolean = false
     public var loadedLikeRequestsSentFragmentBefore:Boolean = false
     public var numOfSwipes:Int =0

     public  var messagesHashMap : HashMap<String,String>
             = HashMap<String,String> ()
}
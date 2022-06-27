package com.ayushunleashed.mitram

import android.view.ViewGroup
import androidx.lifecycle.ViewModel
import com.ayushunleashed.mitram.models.UserModel

class SharedViewModel: ViewModel() {
     var currentUserModel =UserModel()
     public var myUsersList:MutableList<UserModel> = mutableListOf()
     public var myConnectionsList:MutableList<UserModel> = mutableListOf()
     public var loadedDiscoverFragmentBefore:Boolean = false
     var isUsersPresentForDiscoverFragment = true
     public var loadedConnectionsFragmentBefore:Boolean = false
     public var numOfSwipes:Int =0
}
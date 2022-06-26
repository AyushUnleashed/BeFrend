package com.ayushunleashed.mitram

import android.view.ViewGroup
import androidx.lifecycle.ViewModel
import com.ayushunleashed.mitram.models.UserModel

class SharedViewModel: ViewModel() {
     public var myUsersList:MutableList<UserModel> = mutableListOf()
     public var myConnectionsList:MutableList<UserModel> = mutableListOf()
     public var loadedDiscoverFragmentBefore:Boolean = false
     public var loadedConnectionsFragmentBefore:Boolean = false
     public var numOfSwipes:Int =0
}
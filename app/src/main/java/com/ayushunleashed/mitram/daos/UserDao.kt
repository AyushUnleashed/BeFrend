package com.ayushunleashed.notezen.daos

import com.ayushunleashed.mitram.models.UserModel
import com.google.android.gms.tasks.Task
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


class UserDao {
    private val db = FirebaseFirestore.getInstance()
    private val usersCollection = db.collection("users")

    fun addUsers(user: UserModel?)
    {

        GlobalScope.launch (Dispatchers.IO){

            //if user is not null
            user?.let{
                if(usersCollection.document(user.uid!!).get().await().exists())
                {
                    //do nothing
                }else
                {
                    usersCollection.document(user.uid).set(it)
                }
            }
        }
    }

    fun getUserById(uId:String): Task<DocumentSnapshot>
    {
        return usersCollection.document(uId).get()
    }
}
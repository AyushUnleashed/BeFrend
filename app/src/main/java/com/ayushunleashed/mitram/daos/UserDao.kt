package com.ayushunleashed.notezen.daos

import com.ayushunleashed.mitram.models.UserModel
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class UserDao {
    private val db = FirebaseFirestore.getInstance()
    private val usersCollection = db.collection("users")

    fun addUsers(user: UserModel?)
    {
        GlobalScope.launch (Dispatchers.IO){
            //if user is not null
            user?.let{
                usersCollection.document(user.uid).set(it)
            }
        }
    }

    fun getUserById(uId:String): Task<DocumentSnapshot>
    {
        return usersCollection.document(uId).get()
    }
}
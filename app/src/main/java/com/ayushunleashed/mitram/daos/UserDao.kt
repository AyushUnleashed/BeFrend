package com.ayushunleashed.mitram.daos

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import com.ayushunleashed.mitram.FragmentHomeActivity
import com.ayushunleashed.mitram.SignInActivity
import com.ayushunleashed.mitram.models.UserModel
import com.ayushunleashed.mitram.models.UtilityModel
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await


class UserDao() {
    private val db = FirebaseFirestore.getInstance()
    private val usersCollection = db.collection("users")

    fun addUsers(user: UserModel?)
    {   Log.d("GENERAL","INSIDE Add user function in dao")
        //GlobalScope.launch (Dispatchers.Main)

        runBlocking{
            Log.d("GENERAL","coroutine of Add user function in dao")
            //if user is not null
            user?.let{
                if(usersCollection.document(user.uid!!).get().await().exists())
                {
                    Log.d("GENERAL","user already exist corountine doesn't add")
                    //do nothing
                }else
                {
                    Log.d("GENERAL","coroutine trying to add user")
                    usersCollection.document(user.uid).set(it).addOnSuccessListener{
                        Log.d("GENERAL","User added to db");
                    }.addOnFailureListener {
                        Log.d("GENERAL","User failed to be added to db");
                        Log.d("GENERAL",it.toString())
                    }.addOnCanceledListener {
                        Log.d("GENERAL","Can't add user to db");
                    }.addOnCompleteListener {
                        Log.d("GENERAL","User Completely added to db");

                    }
                    //addCurrentUserToUtilityList(user)

                }
            }
        }
    }



    fun getUserById(uId:String): Task<DocumentSnapshot>
    {
        return usersCollection.document(uId).get()
    }
}
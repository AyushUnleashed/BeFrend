package com.ayushunleashed.mitram

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.ayushunleashed.mitram.models.UserModel
import com.ayushunleashed.notezen.daos.UserDao
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await

class MainActivity : AppCompatActivity() {
    lateinit var context: Context
    var currentUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
    private lateinit var sharedViewModel: SharedViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        context = this
        supportActionBar?.hide()
        sharedViewModel = ViewModelProvider(this).get(SharedViewModel::class.java)
    }

}
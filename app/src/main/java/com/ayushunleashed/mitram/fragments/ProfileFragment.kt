package com.ayushunleashed.mitram.fragments

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.ayushunleashed.mitram.R
import com.ayushunleashed.mitram.models.UserModel
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import org.w3c.dom.Text


class ProfileFragment : Fragment() {
    lateinit var thisContext: Context
    lateinit var btnLogout:Button
    lateinit var db: FirebaseFirestore
    lateinit var userImage: ImageView
    lateinit var tvUserName:TextView
    lateinit var currentUser: FirebaseUser

    private lateinit var mAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        if (container != null) {
            thisContext = container.getContext()
        };
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        db  = FirebaseFirestore.getInstance()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mAuth= Firebase.auth
        btnLogout = view.findViewById(R.id.btnLogout)

        btnLogout.setOnClickListener {
            logOut()
        }

        loadUserImage()
        userImage = view.findViewById(R.id.userImage)
        tvUserName = view.findViewById(R.id.tvUserName)
        currentUser = FirebaseAuth.getInstance().currentUser!!

    }

    fun logOut() {
        val user = Firebase.auth.currentUser!!
        deleteToken()
        mAuth.signOut();
        Toast.makeText(thisContext,"LoggedOut",Toast.LENGTH_SHORT).show()

        findNavController().navigate(R.id.action_profileFragment_to_signInActivity)
    }


    fun deleteToken()
    {
        currentUser = FirebaseAuth.getInstance().currentUser!!
        db  = FirebaseFirestore.getInstance()

        GlobalScope.launch {

            var currentUserModel = db.collection("users").document(currentUser.uid).get().await().toObject(
                UserModel::class.java)
            currentUserModel?.fcmToken = null

            db.collection("users").document(currentUser.uid).set(currentUserModel!!)
        }
    }

    fun loadUserImage()
    {
        GlobalScope.launch(Dispatchers.IO) {
            val user = db.collection("users").document(currentUser.uid).get().await().toObject(
                UserModel::class.java)
            val displayName = user?.displayName

            withContext(Dispatchers.Main)
            {
                if (user != null) {
                    tvUserName.text = displayName

                    Glide.with(userImage.context).load(user.imageUrl).circleCrop().placeholder(R.drawable.img_user_place_holder)
                        .error(R.drawable.img_user_profile_sample).into(userImage)
                }
            }
        }

    }
}
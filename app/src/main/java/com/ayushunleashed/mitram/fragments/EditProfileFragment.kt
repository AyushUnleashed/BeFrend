package com.ayushunleashed.mitram.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.ayushunleashed.mitram.R
import com.ayushunleashed.mitram.databinding.FragmentEditProfileBinding
import com.ayushunleashed.mitram.databinding.FragmentFullUserDetailBinding
import com.ayushunleashed.mitram.models.UserModel
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class EditProfileFragment : Fragment() {

    lateinit var db: FirebaseFirestore
    private lateinit var binding: FragmentEditProfileBinding
    lateinit var currentUser: FirebaseUser

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_edit_profile, container, false)
        db  = FirebaseFirestore.getInstance()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentEditProfileBinding.bind(view)

        handleButtons()

        currentUser = FirebaseAuth.getInstance().currentUser!!
        loadUserImage()
    }

    fun handleButtons()
    {
        binding.btnEditImage.setOnClickListener {

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

                    Glide.with(binding.userImage.context).load(user.imageUrl).circleCrop().placeholder(R.drawable.img_user_place_holder)
                        .error(R.drawable.img_user_profile_sample).into(binding.userImage)
                }
            }
        }

    }
}
package com.ayushunleashed.mitram.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.ayushunleashed.mitram.R
import com.ayushunleashed.mitram.databinding.FragmentFullUserDetailBinding
import com.ayushunleashed.mitram.models.UserModel
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class FullUserDetailFragment : Fragment() {
    lateinit var thisContext: Context

    private lateinit var binding:FragmentFullUserDetailBinding

    var userToLoad:UserModel? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        if (container != null) {
            thisContext = container.context
        };

        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_full_user_detail, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentFullUserDetailBinding.bind(view)
        userToLoad = requireArguments().getParcelable<UserModel>("currentUser")
        Log.d("GENERAL","userToLoad:${userToLoad.toString()}")
        loadUserDetails()
        handleButtons()

    }

    fun handleButtons()
    {
        binding.btnGoBackToDiscoverPage.setOnClickListener{
            findNavController().navigate(R.id.action_fullUserProfileFragment_to_discoverFragment)
        }

        binding.btnRightSwipe.setOnClickListener {
            Toast.makeText(thisContext,"This Feature is Not Yet Available",Toast.LENGTH_SHORT).show()
        }
    }

    fun loadUserDetails()
    {
        // load image
        Glide.with(binding.imgViewUserProfile.context).load(userToLoad!!.imageUrl).placeholder(R.drawable.img_user_place_holder)
            .error(R.drawable.img_keep_calm_reload).into(binding.imgViewUserProfile)

        //load name
        binding.tvUserNameDetailPage.text = userToLoad!!.displayName

        //load bio
        binding.tvUserBioDetailPage.text = userToLoad!!.bio

    }

}
package com.ayushunleashed.mitram.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
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

    private lateinit var binding:FragmentFullUserDetailBinding

    var userToLoad:UserModel? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

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
        handleBackButton()

    }

    fun handleBackButton()
    {
        binding.btnGoBackToDiscoverPage.setOnClickListener{
            findNavController().navigate(R.id.action_fullUserProfileFragment_to_discoverFragment)
        }
    }

    fun loadUserDetails()
    {

        // load image
        Glide.with(binding.imgViewUserProfile.context).load(userToLoad!!.imageUrl).placeholder(R.drawable.img_user_place_holder)
            .error(R.drawable.img_user_not_found).into(binding.imgViewUserProfile)

        //load name
        binding.tvUserNameDetailPage.text = userToLoad!!.displayName

        //load bio
        binding.tvUserBioDetailPage.text = userToLoad!!.bio


    }

}
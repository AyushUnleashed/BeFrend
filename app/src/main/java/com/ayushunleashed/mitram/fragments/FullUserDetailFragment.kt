package com.ayushunleashed.mitram.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import com.ayushunleashed.mitram.R
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

    lateinit var btnGoBackToDiscoverPage: ImageButton
    lateinit var tvUserName:TextView
    lateinit var tvUserBio:TextView
    lateinit var imgViewUserProfile:ImageView

    lateinit var userIdToLoad:String


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        userIdToLoad = requireArguments().getString("userId").toString()

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_full_user_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViews(view)
        handleBackButton()
        loadUserDetails()
    }

    fun handleBackButton()
    {
        btnGoBackToDiscoverPage.setOnClickListener{
            findNavController().navigate(R.id.action_fullUserProfileFragment_to_discoverFragment)
        }
    }

    fun setupViews(view: View)
    {
        btnGoBackToDiscoverPage = view.findViewById(R.id.btnGoBackToDiscoverPage)
        tvUserName = view.findViewById<TextView>(R.id.tvUserNameDetailPage)
        tvUserBio = view.findViewById<TextView>(R.id.tvUserBioDetailPage)
        imgViewUserProfile = view.findViewById<ImageView>(R.id.imgViewUserProfile)
    }


    fun loadUserDetails()
    {
        val db = FirebaseFirestore.getInstance()
        val currentUser = FirebaseAuth.getInstance().currentUser
        //progressBar.visibility = View.VISIBLE

        GlobalScope.launch(Dispatchers.IO){

            val userModelToLoad = db.collection("users").document(userIdToLoad).get().await().toObject(
                UserModel::class.java)

            if(userModelToLoad!=null)
            {
                // updating ui with users
                withContext(Dispatchers.Main)
                {
                    tvUserName.text = userModelToLoad.displayName
                    tvUserBio.text = userModelToLoad.bio
                    Glide.with(imgViewUserProfile.context).load(userModelToLoad.imageUrl).placeholder(R.drawable.img_user_place_holder)
                        .error(R.drawable.img_user_not_found).into(imgViewUserProfile)
                }
            }

        }
    }

}
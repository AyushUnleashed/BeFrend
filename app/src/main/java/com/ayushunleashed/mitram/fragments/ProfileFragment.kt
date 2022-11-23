package com.ayushunleashed.mitram.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.ayushunleashed.mitram.R
import com.ayushunleashed.mitram.SharedViewModel
import com.ayushunleashed.mitram.databinding.FragmentProfileBinding
import com.ayushunleashed.mitram.models.UserModel
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await


class ProfileFragment : Fragment() {

    lateinit var binding:FragmentProfileBinding

    lateinit var thisContext: Context

    lateinit var db: FirebaseFirestore
    lateinit var currentUser: FirebaseUser
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var mAuth: FirebaseAuth

    lateinit var sharedViewModel:SharedViewModel
    lateinit var currentUserModel:UserModel
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

        binding = FragmentProfileBinding.bind(view)
        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
        currentUserModel = sharedViewModel.currentUserModel;

        currentUser = FirebaseAuth.getInstance().currentUser!!

        mAuth= Firebase.auth

        loadAllDetails()
        handleButtons()

    }


    fun reloadCurrentUserModel(){
        runBlocking {
            Log.d("GENERAL","Inside run blocking")

            currentUserModel = currentUserModel.uid?.let { db.collection("users").document(it).get().await().toObject(UserModel::class.java) }!!
            Log.d("GENERAL","After Model Request")
            sharedViewModel.currentUserModel = currentUserModel
        }
        loadAllDetails()
        binding.refreshLayout.isRefreshing = false
    }

    fun loadAllDetails(){
        binding.tvUserName.text = currentUserModel.displayName
        binding.tvUserBio.text = currentUserModel.bio
        binding.tvUserEmail.text = currentUserModel.email
        binding.tvUserCollegeName.text = currentUserModel.userCollegeName
        binding.tvUserCollegeStream.text = currentUserModel.userCollegeStream
        val collegeYearText ="Year:${currentUserModel.userCollegeYear}"
        binding.tvUserCollegeYear.text = collegeYearText
        loadUserImage()
    }

    fun loadUserImage()
    {
        Glide.with(binding.userImage.context).load(currentUserModel.imageUrl).circleCrop().placeholder(R.drawable.img_user_place_holder)
            .error(R.drawable.img_keep_calm_reload).into(binding.userImage)
    }

    fun handleButtons(){
        binding.btnLogout.setOnClickListener {
            logOut()
        }

        binding.btnEditProfile.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_editProfileFragment)
        }

        binding.btnGoToUtility.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_utilityFragment)
        }
        binding.refreshLayout.setOnRefreshListener {
            reloadCurrentUserModel()
        }

        binding.btnGoToCreditsPage.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_creditsFragment)
        }

    }

    fun logOut() {
        val user = Firebase.auth.currentUser!!
        deleteToken()
        mAuth.signOut();
        clearOldLogin()
        Toast.makeText(thisContext,"Logged out",Toast.LENGTH_SHORT).show()

        findNavController().navigate(R.id.action_profileFragment_to_signInActivity)
    }

    fun clearOldLogin()
    {
        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("584245718070-rmeqcca7nb9irbe156hu07u2ij6sv2g4.apps.googleusercontent.com")
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(thisContext, gso)
        googleSignInClient.signOut()
        googleSignInClient.revokeAccess()
    }



    fun deleteToken()
    {
        currentUser = FirebaseAuth.getInstance().currentUser!!
        db  = FirebaseFirestore.getInstance()

        GlobalScope.launch {

            var currentUserModel = db.collection("users").document(currentUser.uid).get().await().toObject(
                UserModel::class.java)
            currentUserModel?.fcmToken = null

            if (currentUserModel != null) {
                currentUserModel.uid?.let { db.collection("users").document(it).set(currentUserModel) }
            }
        }
    }

}
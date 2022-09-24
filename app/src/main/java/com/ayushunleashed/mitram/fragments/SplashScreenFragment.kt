package com.ayushunleashed.mitram.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.ayushunleashed.mitram.R
import com.ayushunleashed.mitram.SharedViewModel
import com.ayushunleashed.mitram.databinding.FragmentSplashScreenBinding
import com.ayushunleashed.mitram.databinding.FragmentUtilityBinding
import com.ayushunleashed.mitram.models.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


class SplashScreenFragment : Fragment() {

    lateinit var binding: FragmentSplashScreenBinding
    lateinit var sharedViewModel: SharedViewModel

    lateinit var thisContext: Context
    lateinit var currentUserModel: UserModel
    lateinit var currentUser: FirebaseUser
    lateinit var db:FirebaseFirestore


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

        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
        currentUser = FirebaseAuth.getInstance().currentUser!!
        db  = FirebaseFirestore.getInstance()
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_splash_screen, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSplashScreenBinding.bind(view)

        getCurrentUser()
    }

    fun getCurrentUser()
    {
        GlobalScope.launch(Dispatchers.Main) {
            loadCurrentUserModel()
            findNavController().navigate(R.id.action_splashScreenFragment_to_discoverFragment)
        }
    }

    suspend fun loadCurrentUserModel(){
        currentUserModel = db.collection("users").document(currentUser.uid).get().await().toObject(UserModel::class.java)!!
        Log.d("GENERAL","After Model Request")
        sharedViewModel.currentUserModel = currentUserModel
    }

}
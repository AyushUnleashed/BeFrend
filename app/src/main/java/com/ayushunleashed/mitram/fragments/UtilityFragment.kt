package com.ayushunleashed.mitram.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.ayushunleashed.mitram.R
import com.ayushunleashed.mitram.SharedViewModel
import com.ayushunleashed.mitram.databinding.FragmentUtilityBinding
import com.ayushunleashed.mitram.models.UserModel
import com.ayushunleashed.mitram.models.UtilityModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext


class UtilityFragment : Fragment() {

    lateinit var binding:FragmentUtilityBinding

    lateinit var thisContext: Context

    lateinit var db: FirebaseFirestore
    lateinit var currentUser: FirebaseUser
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
        val view = inflater.inflate(R.layout.fragment_utility, container, false)

        db  = FirebaseFirestore.getInstance()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentUtilityBinding.bind(view)
        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
        currentUserModel = sharedViewModel.currentUserModel;


        handleButtons()
        mAuth= Firebase.auth

        binding.refreshLayout.setOnRefreshListener {


        }
        currentUser = FirebaseAuth.getInstance().currentUser!!
    }


    fun handleButtons(){
        binding.btnLogout.setOnClickListener {

        }

        binding.btnCollect.setOnClickListener {
            binding.progressBar.visibility = View.VISIBLE
            binding.btnCollect.visibility = View.GONE
            handleCollectAllUserData()

        }
    }

    fun handleCollectAllUserData(){
        var allUsersUidArray: ArrayList<String> = ArrayList()


        GlobalScope.launch(Dispatchers.IO) {
            allUsersUidArray = getALlUsersUidFromUsersCollection()
            uploadAllUsersIdToUtilityCollection(allUsersUidArray)
            withContext(Dispatchers.Main){
                binding.progressBar.visibility = View.GONE
                binding.btnCollect.visibility = View.VISIBLE
                Toast.makeText(thisContext,"List of All users added to db",Toast.LENGTH_SHORT).show()
            }
        }



    }

    fun uploadAllUsersIdToUtilityCollection(allUsersUidArray: ArrayList<String>) {

        var utilityModel = UtilityModel(allUsersUidArray)
        db.collection("utility").document("utility_doc").set(utilityModel)
    }

    suspend fun getALlUsersUidFromUsersCollection():ArrayList<String>{

        var listOfAllUserIds = ArrayList<String>()
        var allUsersModel = db.collection("users").get().await().toObjects(UserModel::class.java)

        for( userModel in allUsersModel){
            userModel.uid?.let { listOfAllUserIds.add(it) }
        }
        return listOfAllUserIds
    }
}
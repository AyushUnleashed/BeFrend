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
import com.ayushunleashed.mitram.models.UtilityModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await


class SplashScreenFragment : Fragment() {

    lateinit var binding: FragmentSplashScreenBinding
    lateinit var sharedViewModel: SharedViewModel

    lateinit var thisContext: Context
    lateinit var currentUserModel: UserModel
    lateinit var currentUser: FirebaseUser
    lateinit var db:FirebaseFirestore
    var mAuth = Firebase.auth
    var firebaseuser = mAuth.currentUser

    var userCollegeName = ""
    var userCollegeYear = ""
    var userCollegeStream = ""



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

        db  = FirebaseFirestore.getInstance()
        runBlocking {
            if(!firebaseuser?.uid?.let { db.collection("users").document(it).get().await().exists() }!!) {
                userCollegeName = requireArguments().getString("collegeName").toString()
                userCollegeYear = requireArguments().getString("year").toString()
                userCollegeStream = requireArguments().getString("stream").toString()
            }
        }



        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
        currentUser = FirebaseAuth.getInstance().currentUser!!

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_splash_screen, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSplashScreenBinding.bind(view)

        updateUI()
    }

    private fun updateUI() {

        if(firebaseuser!=null){
            val user = firebaseuser!!.displayName?.let {
                UserModel(
                    firebaseuser!!.uid,
                    it, firebaseuser!!.photoUrl.toString(),"Hey there! My name is ${firebaseuser!!.displayName} . \nI am glad to be here"
                    , firebaseuser!!.email,true,userCollegeName,userCollegeYear,userCollegeStream
                )
            }

            GlobalScope.launch(Dispatchers.Main) {
                if (user != null) {

                    if(!user.uid?.let { db.collection("users").document(it).get().await().exists() }!!) {
                        user.uid.let { db.collection("users").document(it).set(user) }
                        addCurrentUserToUtilityList(user)
                        addCurrentUserToCollegeSpecificList(user)
                    }
                    loadCurrentUserModel()

                    findNavController().navigate(R.id.action_splashScreenFragment_to_discoverFragment)
                }

            }

        }

    }

    suspend fun loadCurrentUserModel(){
        currentUserModel = db.collection("users").document(currentUser.uid).get().await().toObject(UserModel::class.java)!!
        Log.d("GENERAL","After Model Request")
        sharedViewModel.currentUserModel = currentUserModel
    }

    suspend fun addCurrentUserToUtilityList(user: UserModel?){
        var utilityDoc = db.collection("utility").document("utility_doc").get().await()
            .toObject(UtilityModel::class.java)

        if (utilityDoc != null) {
            if (user != null) {
                user.uid?.let { utilityDoc.allUsersUid.add(it) }
            }
        }
        if (utilityDoc != null) {
            db.collection("utility").document("utility_doc").set(utilityDoc).await()
        }
    }

    suspend fun addCurrentUserToCollegeSpecificList(user: UserModel?){
        val allUsersUtilityDoc = db.collection("utility").document("all_users_utility_doc").get().await()
        var currentCollegeName = userCollegeName
        var myHashMap = allUsersUtilityDoc.data
        var currentCollegeAllUsersList = myHashMap?.get(currentCollegeName) as ArrayList<String>
        user?.uid?.let { currentCollegeAllUsersList.add(it) }

        if(allUsersUtilityDoc!=null){
            myHashMap[currentCollegeName] = currentCollegeAllUsersList
            db.collection("utility").document("all_users_utility_doc").set(myHashMap, SetOptions.merge()).await()
            Log.d("NETWORK_DB","New User added to college specific list")
        }
    }

}
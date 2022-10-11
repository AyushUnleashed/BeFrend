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
import com.ayushunleashed.mitram.R
import com.ayushunleashed.mitram.SharedViewModel
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
        binding.btnSetOnlineStatus.setOnClickListener {
            binding.progressBar.visibility = View.VISIBLE
            binding.btnCollect.visibility = View.GONE
            binding.btnSetOnlineStatus.visibility = View.GONE
            handleSetOnlineStatus()
        }

        binding.btnCollect.setOnClickListener {
            binding.progressBar.visibility = View.VISIBLE
            binding.btnCollect.visibility = View.GONE
            handleSetAllUsersToJEC()

        }

        binding.btnSetCollegeDetails.setOnClickListener {
            binding.progressBar.visibility = View.VISIBLE
            binding.btnSetCollegeDetails.visibility = View.GONE
            handleSetCollegeDetailsToJEC()
        }
    }



    private fun handleSetOnlineStatus() {

        GlobalScope.launch(Dispatchers.IO) {
            setOnlineStatusForAllUsers()
            withContext(Dispatchers.Main){
                binding.progressBar.visibility = View.GONE
                binding.btnCollect.visibility = View.VISIBLE
                binding.btnSetOnlineStatus.visibility = View.VISIBLE
                Toast.makeText(thisContext,"Online Status of All users changed to false in db",Toast.LENGTH_SHORT).show()
            }
        }
    }

    suspend fun setOnlineStatusForAllUsers() {

        var allUsersModel:MutableList<UserModel> = db.collection("users").get().await().toObjects(UserModel::class.java)

        for( userModel in allUsersModel){

            Log.d("UserModel","Fetched UserModel Before:${userModel.toString()}")
            userModel.isOnline = false //set all people status to false
            Log.d("UserModel","Fetched UserModel After:${userModel.toString()}")

            if(!userModel.uid.isNullOrEmpty()){
                userModel.uid.let {
                    Log.d("UserModel","Updating to DB:${userModel.displayName.toString()}")
                    db.collection("users").document(it).set(userModel, SetOptions.merge()).await() }
            }

        }
    }

    fun xyx(){
        //if email is not there , get email
        if(currentUserModel.email.isNullOrEmpty()){
            currentUserModel.email = currentUser.email
            GlobalScope.launch(Dispatchers.IO) {
                db.collection("users").document(currentUser.uid).set(currentUserModel).await()
                Log.d("GENERAL","Email added to Server")
            }
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

    fun handleSetAllUsersToJEC(){
        var allUsersUidArray: ArrayList<String> = ArrayList()

        GlobalScope.launch(Dispatchers.IO) {
            allUsersUidArray = getALlUsersUidFromUsersCollection()
            uploadAllUsersIdToJEC(allUsersUidArray)
            withContext(Dispatchers.Main){
                binding.progressBar.visibility = View.GONE
                binding.btnCollect.visibility = View.VISIBLE
                Toast.makeText(thisContext,"List of All users added to db",Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun handleSetCollegeDetailsToJEC(){
        GlobalScope.launch(Dispatchers.IO) {

            withContext(Dispatchers.Main){
                getAllUsersAndAddCollegeToJEC()
                binding.progressBar.visibility = View.GONE
                binding.btnSetCollegeDetails.visibility = View.VISIBLE
                Toast.makeText(thisContext,"List of All users added to db",Toast.LENGTH_SHORT).show()
            }
        }

    }

    fun uploadAllUsersIdToUtilityCollection(allUsersUidArray: ArrayList<String>) {

        var utilityModel = UtilityModel(allUsersUidArray)
        db.collection("utility").document("utility_doc").set(utilityModel)
    }

    fun uploadAllUsersIdToJEC(allUsersUidArray: ArrayList<String>){

        var myHashMap:HashMap<String,ArrayList<String>> = HashMap()
        var collegeName = currentUserModel.userCollegeName
        if (collegeName != null) {
            myHashMap.put("Jabalpur Engineering College",allUsersUidArray)
            db.collection("utility").document("all_users_utility_doc").set(myHashMap, SetOptions.merge())
        }
    }

    suspend fun getALlUsersUidFromUsersCollection():ArrayList<String>{

        var listOfAllUserIds = ArrayList<String>()
        var allUsersModel = db.collection("users").get().await().toObjects(UserModel::class.java)

        for( userModel in allUsersModel){
            userModel.uid?.let { listOfAllUserIds.add(it) }
        }
        return listOfAllUserIds
    }

    suspend fun getAllUsersAndAddCollegeToJEC(){
        var allUsersModel = db.collection("users").get().await().toObjects(UserModel::class.java)

        for( userModel in allUsersModel){
            if(userModel!=null){
                userModel.userCollegeName = "Jabalpur Engineering College"
                userModel.userCollegeStream = "Other"
                userModel.userCollegeYear = "3"
                userModel.uid?.let { db.collection("users").document(it).set(userModel).await() }
            }
        }
    }
}
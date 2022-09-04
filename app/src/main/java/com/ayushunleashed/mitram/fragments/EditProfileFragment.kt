package com.ayushunleashed.mitram.fragments

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.opengl.Visibility
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.ayushunleashed.mitram.FragmentHomeActivity
import com.ayushunleashed.mitram.R
import com.ayushunleashed.mitram.SharedViewModel
import com.ayushunleashed.mitram.databinding.FragmentEditProfileBinding
import com.ayushunleashed.mitram.databinding.FragmentFullUserDetailBinding
import com.ayushunleashed.mitram.models.UserModel
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.net.URI

class EditProfileFragment : Fragment() {
    lateinit var thisContext: Context
    lateinit var db: FirebaseFirestore
    private lateinit var binding: FragmentEditProfileBinding
    lateinit var currentUser: FirebaseUser
    lateinit var profileImageURI: Uri

    lateinit var sharedViewModel:SharedViewModel
    lateinit var currentUserModel:UserModel
    lateinit var checkPermission: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        checkPermission =        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            onActivityResult(123, result)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        if (container != null) {
            thisContext = container.getContext()
        };
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_edit_profile, container, false)
        db  = FirebaseFirestore.getInstance()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentEditProfileBinding.bind(view)
        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
        currentUserModel = sharedViewModel.currentUserModel;
        handleButtons()

        currentUser = FirebaseAuth.getInstance().currentUser!!
        loadUserImage()
        loadAllDetails()


    }

    fun handleButtons()
    {
        binding.btnEditImage.setOnClickListener {
            selectImage()
        }

        binding.btnSaveProfileDetails.setOnClickListener {
            saveDataToDB()
        }
    }

    fun loadAllDetails(){
        binding.etvUserNameEditProfile.setText(currentUserModel.displayName)
        binding.etvUserBioEditBio.setText(currentUserModel.bio)
    }

    fun uploadImage(){
        Log.d("GENERAL","Upload Image Called")
        val fileName = currentUserModel.uid + "_profile_image"

        val storageReference = FirebaseStorage.getInstance().getReference("images/profileImages/$fileName")
        storageReference.putFile(profileImageURI).addOnSuccessListener {
            Toast.makeText(thisContext,"Successfully Uploaded Profile Picture",Toast.LENGTH_SHORT).show()
            Log.d("GENERAL","Image Uploaded Successfully")

            binding.progressBar.visibility = View.GONE
            binding.btnSaveProfileDetails.visibility = View.VISIBLE

            storageReference.downloadUrl.addOnSuccessListener {
                Log.d("GENERAL","Image URL Fetched")
                currentUserModel.imageUrl = it.toString()
            }.addOnFailureListener{
                Log.d("GENERAL","Image URL cannot be Fetched\n$it")
            }
        }.addOnFailureListener{
            Toast.makeText(thisContext,"Upload Failed: $it",Toast.LENGTH_SHORT).show()
            Log.d("GENERAL","Image Failed: $it")

            binding.progressBar.visibility = View.GONE
            binding.btnSaveProfileDetails.visibility = View.VISIBLE

        }
    }



    fun selectImage(){
        Log.d("GENERAL","Select Image Called")
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        checkPermission.launch(intent)
    }


    private fun onActivityResult(requestCode: Int, result: ActivityResult) {
        binding.progressBar.visibility = View.VISIBLE
        binding.btnSaveProfileDetails.visibility = View.GONE
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            when (requestCode) {
                123 -> {
                    Log.d("GENERAL","Successful activity result")
                    profileImageURI = data?.data!!
                    binding.userImage.setImageURI(profileImageURI)
                    uploadImage()
                }
            }
        }
    }

    fun saveDataToDB(){

        currentUserModel.displayName =  binding.etvUserNameEditProfile.text.toString()
        currentUserModel.bio = binding.etvUserBioEditBio.text.toString()

        Log.d("GENERAL",currentUserModel.toString())

        GlobalScope.launch(Dispatchers.IO) {
            db.collection("users").document(currentUser.uid).set(currentUserModel).await()
            Log.d("GENERAL","Saving data to server")
        }
        Log.d("GENERAL","Statement after data save")

        findNavController().navigate(R.id.action_editProfileFragment_to_profileFragment)
    }

    fun loadUserImage()
    {
        Glide.with(binding.userImage.context).load(currentUserModel.imageUrl).circleCrop().placeholder(R.drawable.img_user_place_holder)
                        .error(R.drawable.img_user_profile_sample).into(binding.userImage)
    }
}
package com.ayushunleashed.mitram.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.ayushunleashed.mitram.R
import com.ayushunleashed.mitram.SharedViewModel
import com.ayushunleashed.mitram.UtilitySharedViewModel
import com.ayushunleashed.mitram.databinding.FragmentEditProfileBinding
import com.ayushunleashed.mitram.models.UserModel
import com.ayushunleashed.mitram.utils.StringHelperClass
import com.bumptech.glide.Glide
import com.google.android.material.chip.Chip
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import me.shouheng.compress.Compress
import me.shouheng.compress.concrete

class EditProfileFragment : Fragment() {
    lateinit var thisContext: Context
    lateinit var db: FirebaseFirestore
    private lateinit var binding: FragmentEditProfileBinding
    lateinit var currentUser: FirebaseUser
    lateinit var profileImageURI: Uri

    lateinit var sharedViewModel:SharedViewModel
    lateinit var utilitySharedViewModel:UtilitySharedViewModel
    lateinit var currentUserModel:UserModel
    lateinit var checkPermission: ActivityResultLauncher<Intent>
    var stringHelper:StringHelperClass = StringHelperClass()

    var year ="-"
    var stream="-"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        checkPermission =        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            onActivityResult(123, result)
        }
        // This callback will only be called when MyFragment is at least Started.
        val callback = requireActivity().onBackPressedDispatcher.addCallback(this) {
            // Handle the back button event
            utilitySharedViewModel.bioBufferEP =""
            utilitySharedViewModel.nameBufferEP =""
            findNavController().navigate(R.id.action_editProfileFragment_to_profileFragment)

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
        utilitySharedViewModel  = ViewModelProvider(requireActivity()).get(UtilitySharedViewModel::class.java)




        currentUserModel = sharedViewModel.currentUserModel;

        //loading old data
        binding.actvSelectYear.setText(currentUserModel.userCollegeYear)
        binding.actvSelectStream.setText(currentUserModel.userCollegeStream)

        handleYearSelectorLogic()
        handleStreamSelectorLogic()
        handleButtons()



        currentUser = FirebaseAuth.getInstance().currentUser!!
        loadUserImage()
        loadAllDetails()
        loadChipsFromDB()


    }

    fun handleButtons()
    {
        binding.btnEditImage.setOnClickListener {
            selectImage()
        }

        binding.btnSaveProfileDetails.setOnClickListener {
            saveDataToDB()
        }

        binding.btnEditSkills.setOnClickListener {
            utilitySharedViewModel.nameBufferEP = binding.etvUserNameEditProfile.text.toString()
            utilitySharedViewModel.bioBufferEP = binding.etvUserBioEditBio.text.toString()
            findNavController().navigate(R.id.action_editProfileFragment_to_editSkillsFragment);
        }

        binding.btnEditInterests.setOnClickListener {
            utilitySharedViewModel.nameBufferEP = binding.etvUserNameEditProfile.text.toString()
            utilitySharedViewModel.bioBufferEP = binding.etvUserBioEditBio.text.toString()
            findNavController().navigate(R.id.action_editProfileFragment_to_editInterestsFragment);
        }
    }

    private fun loadChipsFromDB(){
        for(skill in currentUserModel.skills){
            addSkillsChip(skill)
        }

        for(interest in currentUserModel.interests){
            addInterestsChip(interest)
        }
    }

    private fun addSkillsChip(input:String){
        val chip = layoutInflater.inflate(R.layout.single_chip_layout, binding.skillsChipGroup, false) as Chip
        chip.text = input
        chip.setOnCloseIconClickListener{
            binding.skillsChipGroup.removeView(chip)
        }
        binding.skillsChipGroup.addView(chip)
    }

    private fun addInterestsChip(input:String){
        val chip = layoutInflater.inflate(R.layout.single_chip_layout, binding.interestsChipGroup, false) as Chip
        chip.text = input
        chip.setOnCloseIconClickListener{
            binding.interestsChipGroup.removeView(chip)
        }
        binding.interestsChipGroup.addView(chip)
    }

    private fun handleYearSelectorLogic() {
        val years = resources.getStringArray(R.array.years)
        val yearsAdapter = ArrayAdapter(thisContext,android.R.layout.simple_spinner_dropdown_item,years)
        binding.actvSelectYear.setAdapter(yearsAdapter)
        binding.actvSelectYear.setOnItemClickListener { adapterView, view, i, l ->
            year = binding.actvSelectYear.text.toString()
            stream = binding.actvSelectStream.text.toString()
        }
    }

    private fun handleStreamSelectorLogic() {
        val streams = resources.getStringArray(R.array.streams)
        val streamsAdapter = ArrayAdapter(thisContext,android.R.layout.simple_spinner_dropdown_item,streams)
        binding.actvSelectStream.setAdapter(streamsAdapter)
        binding.actvSelectStream.setOnItemClickListener { adapterView, view, i, l ->
            stream = binding.actvSelectStream.text.toString()
            year = binding.actvSelectYear.text.toString()
        }
    }


    fun loadAllDetails(){
        binding.etvUserNameEditProfile.setText(currentUserModel.displayName)
        binding.etvUserBioEditBio.setText(currentUserModel.bio)

        if(utilitySharedViewModel.nameBufferEP!=""){
            binding.etvUserNameEditProfile.setText(utilitySharedViewModel.nameBufferEP)
            binding.etvUserBioEditBio.setText(utilitySharedViewModel.bioBufferEP)
        }



        var userSkillsString:String=""
        for(skill in currentUserModel.skills){
            userSkillsString+= "$skill,";
        }

        var userInterestsString:String=""
        for(interest in currentUserModel.interests){
            userInterestsString+= "$interest,";
        }

        //binding.etvUserInterestsEditProfile.setText(userInterestsString)
        //binding.etvUserSkillsEditProfile.setText(userSkillsString)
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

                    GlobalScope.launch {
                        val myResult = Compress.with(thisContext, profileImageURI)
                            .setQuality(80)
                            .concrete {
//                                withMaxWidth(400f)
//                                withMaxHeight(400f)
//                                withScaleMode(ScaleMode.SCALE_HEIGHT)
//                                withIgnoreIfSmaller(true)
                            }
                            .get(Dispatchers.IO)
                        withContext(Dispatchers.Main) {

                            profileImageURI = Uri.fromFile(myResult)
                            binding.userImage.setImageURI(profileImageURI)
                            uploadImage()
                        }
                    }

                }
            }
        }
    }

    fun saveDataToDB(){

        var userName = binding.etvUserNameEditProfile.text.toString()
        userName = stringHelper.removeEmptyLinesFromStartAndEnd(userName)
        var userBio = binding.etvUserBioEditBio.text.toString()
        userBio = stringHelper.removeEmptyLinesFromStartAndEnd(userBio)


        currentUserModel.displayName =  userName
        currentUserModel.bio = userBio
        currentUserModel.userCollegeYear = year
        currentUserModel.userCollegeStream = stream

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
                        .error(R.drawable.img_keep_calm_reload).into(binding.userImage)
    }
}
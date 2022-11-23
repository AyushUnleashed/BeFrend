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
import com.ayushunleashed.mitram.viewmodels.SharedViewModel
import com.ayushunleashed.mitram.databinding.FragmentEditInterestsBinding
import com.ayushunleashed.mitram.models.UserModel
import com.ayushunleashed.mitram.utils.StringHelperClass
import com.google.android.material.chip.Chip
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


class EditInterestsFragment : Fragment() {

    lateinit var thisContext: Context
    lateinit var db: FirebaseFirestore
    private lateinit var binding: FragmentEditInterestsBinding
    lateinit var currentUser: FirebaseUser

    lateinit var sharedViewModel: SharedViewModel
    lateinit var currentUserModel: UserModel

    var interestsArrayList = ArrayList<String>()
    var stringHelper: StringHelperClass = StringHelperClass()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        if (container != null) {
            thisContext = container.context
        };
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_edit_interests, container, false)
        db  = FirebaseFirestore.getInstance()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentEditInterestsBinding.bind(view)
        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
        currentUserModel = sharedViewModel.currentUserModel;
        handleButtons()

        currentUser = FirebaseAuth.getInstance().currentUser!!

        loadChipsFromDB()
    }

    private fun handleButtons() {


        binding.btnSaveInterests.setOnClickListener {

            var userInterestsInput =binding.etvUserInterests.text.toString()
            userInterestsInput = stringHelper.removeEmptyLinesFromStartAndEnd(userInterestsInput)
            userInterestsInput = userInterestsInput.trim()
            Log.d("GENERAL",userInterestsInput)
            if(userInterestsInput.trim().isNotEmpty()){
                addChip(userInterestsInput)
                binding.etvUserInterests.setText("")
            }
        }

        binding.btnSaveAllInterests.setOnClickListener {
            saveDataToDB()
            findNavController().navigate(R.id.action_editInterestsFragment_to_editProfileFragment)
        }
    }

    private fun loadChipsFromDB(){
        for(interest in currentUserModel.interests){
            addChip(interest)
        }
    }

    private fun addChip(input:String){
        val chip = layoutInflater.inflate(R.layout.single_chip_layout, binding.interestsChipGroup, false) as Chip
        //val chip = Chip(thisContext)
        chip.text = input
        chip.isCloseIconVisible = true
        chip.setOnCloseIconClickListener{
            binding.interestsChipGroup.removeView(chip)
            interestsArrayList.remove(chip.text.toString())
        }
        binding.interestsChipGroup.addView(chip)
        interestsArrayList.add(chip.text.toString());
    }

    fun saveDataToDB(){
        currentUserModel.interests = interestsArrayList
        GlobalScope.launch(Dispatchers.IO) {

            if(currentUserModel!=null){
                currentUserModel.uid?.let { db.collection("users").document(it).set(currentUserModel).await() }
                Log.d("GENERAL","Interests added to Server")
            }

        }
    }

}
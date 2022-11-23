package com.ayushunleashed.mitram.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.ayushunleashed.mitram.R
import com.ayushunleashed.mitram.viewmodels.SharedViewModel
import com.ayushunleashed.mitram.databinding.FragmentEditSkillsBinding
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


class EditSkillsFragment : Fragment() {

    lateinit var thisContext: Context
    lateinit var db: FirebaseFirestore
    private lateinit var binding: FragmentEditSkillsBinding
    lateinit var currentUser: FirebaseUser

    lateinit var sharedViewModel: SharedViewModel
    lateinit var currentUserModel: UserModel

    var skillsArrayList = ArrayList<String>()
    var stringHelper: StringHelperClass = StringHelperClass()

    lateinit var editText:EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        if (container != null) {
            thisContext = container.context
        };
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_edit_skills, container, false)
        db  = FirebaseFirestore.getInstance()

        editText = view.findViewById<EditText>(R.id.etvUserSkills)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentEditSkillsBinding.bind(view)
        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
        currentUserModel = sharedViewModel.currentUserModel;
        handleButtons()

        currentUser = FirebaseAuth.getInstance().currentUser!!

        loadChipsFromDB()
    }

    fun handleAddSkill(){
        var userSkillsInput =binding.etvUserSkills.text.toString()
        userSkillsInput = stringHelper.removeEmptyLinesFromStartAndEnd(userSkillsInput)
        userSkillsInput = userSkillsInput.trim()
        Log.d("GENERAL",userSkillsInput)
        if(userSkillsInput.trim().isNotEmpty()){
            addChip(userSkillsInput)
            binding.etvUserSkills.setText("")
        }
    }

    private fun handleButtons() {

        binding.btnSaveSkills.setOnClickListener {
            handleAddSkill()
        }

        editText.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            Log.d("GENERAL", "KeyClicked")
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                Log.d("GENERAL", "Enter Clicked")
                handleAddSkill()
                return@OnKeyListener true
            }
            false
        })

        binding.btnSaveAllSkills.setOnClickListener {
            saveDataToDB()
            findNavController().navigate(R.id.action_editSkillsFragment_to_editProfileFragment)
        }
    }

    private fun loadChipsFromDB(){
        for(skill in currentUserModel.skills){
            addChip(skill)
        }
    }


    private fun addChip(input:String){
        val chip = layoutInflater.inflate(R.layout.single_chip_layout, binding.skillsChipGroup, false) as Chip
        //val chip = Chip(thisContext)
        chip.text = input
        chip.isCloseIconVisible = true
        chip.setOnCloseIconClickListener{
            binding.skillsChipGroup.removeView(chip)
            skillsArrayList.remove(chip.text.toString())
        }
        binding.skillsChipGroup.addView(chip)
        skillsArrayList.add(chip.text.toString());
    }

    fun saveDataToDB(){
        currentUserModel.skills = skillsArrayList
        GlobalScope.launch(Dispatchers.IO) {

            if(currentUserModel!=null){
                currentUserModel.uid?.let { db.collection("users").document(it).set(currentUserModel).await() }
                Log.d("GENERAL","Skills added to Server")
            }

        }
    }

}
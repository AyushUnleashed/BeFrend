package com.ayushunleashed.mitram.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.ayushunleashed.mitram.R
import com.ayushunleashed.mitram.viewmodels.SharedViewModel
import com.ayushunleashed.mitram.databinding.FragmentFullUserDetailBinding
import com.ayushunleashed.mitram.models.UserModel
import com.bumptech.glide.Glide
import com.google.android.material.chip.Chip

class FullUserDetailFragment : Fragment() {
    lateinit var thisContext: Context

    private lateinit var binding:FragmentFullUserDetailBinding

    var userToLoad:UserModel? = null
    lateinit var previousFragmentName:String
    lateinit var sharedViewModel: SharedViewModel
    lateinit var currentUserModel: UserModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        if (container != null) {
            thisContext = container.context
        };

        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_full_user_detail, container, false)
        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
        currentUserModel = sharedViewModel.currentUserModel;

        binding = FragmentFullUserDetailBinding.bind(view)
        previousFragmentName = requireArguments().getString("previousFragmentName").toString()
        userToLoad = requireArguments().getParcelable<UserModel>("currentUser")
        if(userToLoad==null){
            Log.d("GENERAL","Got null user in user detail page")
            userToLoad = currentUserModel
        }
        Log.d("GENERAL","userToLoad:${userToLoad.toString()}")
        loadUserDetails()
        handleButtons()
        loadChipsFromDB()

    }


    private fun loadChipsFromDB(){
        for(skill in userToLoad?.skills!!){
            addSkillsChip(skill)
        }

        for(interest in userToLoad?.interests!!){
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

    fun handleButtons()
    {
        binding.btnGoBackToDiscoverPage.setOnClickListener{

            if(previousFragmentName == "ConnectionsFragment"){
                findNavController().navigate(R.id.action_fullUserProfileFragment_to_connectionsFragment)
            }else if(previousFragmentName == "LikesFragment"){
                findNavController().navigate(R.id.action_fullUserProfileFragment_to_likesFragment)
            }else if(previousFragmentName == "LikeRequestsSentFragment"){
                findNavController().navigate(R.id.action_fullUserProfileFragment_to_likeRequestsSentFragment)
            }else if(previousFragmentName == "ChatFragment"){
                // sending user back because chat fragment needs a user to load chat with
                val bundle = bundleOf("userToSend" to userToLoad)
                findNavController().navigate(R.id.action_fullUserProfileFragment_to_chatFragment,bundle)
            }else{
                findNavController().navigate(R.id.action_fullUserProfileFragment_to_discoverFragment)
            }
        }

        binding.btnRightSwipe.setOnClickListener {
            Toast.makeText(thisContext,"This Feature is Not Yet Available",Toast.LENGTH_SHORT).show()
        }
    }


    fun loadUserDetails()
    {
        // load image
        Glide.with(binding.imgViewUserProfile.context).load(userToLoad!!.imageUrl).placeholder(R.drawable.img_user_place_holder)
            .error(R.drawable.img_keep_calm_reload).into(binding.imgViewUserProfile)

        //load name
        binding.tvUserNameDetailPage.text = userToLoad!!.displayName

        //load bio
        binding.tvUserBioDetailPage.text = userToLoad!!.bio

        val email = "Email: "+userToLoad!!.email
        //load email
        binding.tvUserEmail.text = email

        if(userToLoad!!.email.isNullOrEmpty()){
            binding.tvUserEmail.visibility = View.GONE
        }

        val collegeNameText = "College: " + userToLoad!!.userCollegeName
        val collegeStreamText = "Stream: "+userToLoad!!.userCollegeStream
        val collegeYearText ="Year: ${userToLoad!!.userCollegeYear}"
        binding.tvUserCollegeName.text = collegeNameText
        binding.tvUserCollegeStream.text = collegeStreamText
        binding.tvUserCollegeYear.text = collegeYearText

    }

}
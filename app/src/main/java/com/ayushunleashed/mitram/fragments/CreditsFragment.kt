package com.ayushunleashed.mitram.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.ayushunleashed.mitram.R
import com.ayushunleashed.mitram.databinding.FragmentCreditsBinding
import com.ayushunleashed.mitram.databinding.FragmentEditSkillsBinding
import com.google.firebase.firestore.FirebaseFirestore


class CreditsFragment : Fragment() {

    lateinit var thisContext: Context
    private lateinit var binding: FragmentCreditsBinding

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
        return inflater.inflate(R.layout.fragment_credits, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentCreditsBinding.bind(view)
        handleButtons()
    }

    fun handleButtons(){
        binding.btnGoBackToDiscoverPage.setOnClickListener {
           findNavController().navigate(R.id.action_creditsFragment_to_profileFragment)
        }
    }

}
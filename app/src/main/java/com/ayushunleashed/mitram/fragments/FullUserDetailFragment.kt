package com.ayushunleashed.mitram.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.navigation.fragment.findNavController
import com.ayushunleashed.mitram.R

class FullUserDetailFragment : Fragment() {

    lateinit var btnGoBackToDiscoverPage: ImageButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_full_user_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViews(view)

        handleBackButton()
    }

    fun handleBackButton()
    {
        btnGoBackToDiscoverPage.setOnClickListener{
            findNavController().navigate(R.id.action_fullUserProfileFragment_to_discoverFragment)
        }
    }

    fun setupViews(view: View)
    {
        btnGoBackToDiscoverPage = view.findViewById(R.id.btnGoBackToDiscoverPage)
    }

}
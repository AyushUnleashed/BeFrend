package com.ayushunleashed.mitram.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.ayushunleashed.mitram.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class ProfileFragment : Fragment() {

    lateinit var btnLogout:Button

    private lateinit var mAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mAuth= Firebase.auth
        btnLogout = view.findViewById(R.id.btnLogout)

        btnLogout.setOnClickListener {
            logOut()
        }
    }

    fun logOut() {
        Toast.makeText(requireContext(),"LoggedOut",Toast.LENGTH_SHORT).show()

        mAuth.signOut();
        findNavController().navigate(R.id.action_profileFragment_to_signInActivity)
    }
}
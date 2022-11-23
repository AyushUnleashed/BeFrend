package com.ayushunleashed.mitram.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import com.ayushunleashed.mitram.FragmentHomeActivity
import com.ayushunleashed.mitram.R
import com.ayushunleashed.mitram.SignInActivity
import com.ayushunleashed.mitram.databinding.ActivitySignUpEmailBinding
import com.ayushunleashed.mitram.databinding.FragmentEditInterestsBinding
import com.ayushunleashed.mitram.utils.HelperClass
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SignUpEmail : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpEmailBinding
    private var mAuth: FirebaseAuth = Firebase.auth
    private var helperClass:HelperClass = HelperClass()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignUpEmailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        handleButtons()
    }

    private fun handleButtons(){

        binding.tvLogInBtn.setOnClickListener{
            goToLogInWithEmailPage()
        }

        binding.btnSignUpEmail.setOnClickListener {
            Toast.makeText(this,"Clicked on Signup",Toast.LENGTH_SHORT).show()
            createUser();
        }
    }


    private fun createUser() {
        Log.d("Cool", "create user running")
        val email = binding.etvEnterEmail.text.toString()
        val pass = binding.etvEnterPassword.text.toString()

        if (email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches() && helperClass.isEmailValidGmail(email)) {
            if (pass.isNotEmpty()) {

                mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(this)
                { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Successfully registered", Toast.LENGTH_SHORT)
                            .show()

                        //send verification email
                        sendVerificationEmail()
                        //go to login activity
                        val intent = Intent(this, LogInEmail::class.java)
                        startActivity(intent)
                    } else {
                        Toast.makeText(this, "Signup Failed", Toast.LENGTH_SHORT).show()
                    }
                }

            } else {
                binding.etvEnterPassword.error = "password cannot be empty"
                binding.etvEnterPassword.requestFocus()
            }
        } else if (email.isEmpty()) {
            binding.etvEnterEmail.error = "Email cannot be empty"
        } else {
            binding.etvEnterEmail.error = "Enter correct email"
        }
    }


    private fun sendVerificationEmail(){
        val user = mAuth.currentUser

        user?.sendEmailVerification()?.addOnSuccessListener {
            Toast.makeText(this, "Confirm Your Email Address", Toast.LENGTH_SHORT)
                .show()
            Log.d("GENERAL","Verification Email Sent")
        }?.addOnFailureListener{
            Toast.makeText(this, "Couldn't Send Verification Email", Toast.LENGTH_SHORT)
                .show()
            Log.d("GENERAL","Verification Email Sent")
        }
    }

    private fun goToLogInWithEmailPage(){
        val intent = Intent(this@SignUpEmail, LogInEmail::class.java)
        startActivity(intent);
        finish()
    }

}
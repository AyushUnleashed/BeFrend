package com.ayushunleashed.mitram.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import com.ayushunleashed.mitram.FragmentHomeActivity
import com.ayushunleashed.mitram.R
import com.ayushunleashed.mitram.databinding.ActivityLogInEmailBinding
import com.ayushunleashed.mitram.databinding.ActivitySignUpEmailBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LogInEmail : AppCompatActivity() {

    private lateinit var binding: ActivityLogInEmailBinding
    private var mAuth: FirebaseAuth = Firebase.auth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLogInEmailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        handleButtons()
    }

    private fun handleButtons(){

        binding.tvSignUpBtn.setOnClickListener{
            goToLogInWithEmailPage()
        }

        binding.btnLoginEmail.setOnClickListener{
            Log.d("GENERAL","Log in button clicked")
            logInUser()
        }
    }

    private fun logInUser()
    {
        //Toast.makeText(this,"logInUser()",Toast.LENGTH_SHORT).show()
        var email = binding.etvEnterEmail.text.toString()
        var pass = binding.etvEnterPassword.text.toString()

        if(email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            if(pass.isNotEmpty())
            {
                mAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(this)
                { task ->
                    if(task.isSuccessful)
                    {
                        Toast.makeText(this,"Log in Successful", Toast.LENGTH_SHORT).show()
                        //go to login activity
                        val intent = Intent(this,FragmentHomeActivity::class.java)
                        startActivity(intent)
                    }
                    else
                    {
                        Toast.makeText(this,"Log in Failed", Toast.LENGTH_SHORT).show()
                    }
                }

            }else
            {
                binding.etvEnterPassword.error = "password cannot be empty"
                binding.etvEnterPassword.requestFocus()
            }
        }
        else if(email.isEmpty())
        {
            binding.etvEnterEmail.error = "Email cannot be empty"
        }
        else
        {
            binding.etvEnterEmail.error = "Enter correct email"
        }

    }
    private fun goToLogInWithEmailPage(){
        val intent = Intent(this@LogInEmail, SignUpEmail::class.java)
        startActivity(intent);
    }
}
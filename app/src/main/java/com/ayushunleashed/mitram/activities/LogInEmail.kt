package com.ayushunleashed.mitram.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ayushunleashed.mitram.FragmentHomeActivity
import com.ayushunleashed.mitram.SignInActivity
import com.ayushunleashed.mitram.databinding.ActivityLogInEmailBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.regex.Pattern

class LogInEmail : AppCompatActivity() {

    private lateinit var binding: ActivityLogInEmailBinding
    private var mAuth: FirebaseAuth = Firebase.auth
    private var isEmailVerified = false

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

        binding.btnResendVerificationEmail.setOnClickListener{
            Log.d("GENERAL","Verification Email Resend button clicked")
            sendVerificationEmail()
        }

    }

//    override fun onPause() {
//        super.onPause()
//
//        if(!isEmailVerified){
//            Log.d("GENERAL","Logged out on Pause")
//            Log.d("GENERAL","isEmailVerified:$isEmailVerified")
//            mAuth.signOut()
//        }
//
//    }

    fun isEmailValidGmail(str:String): Boolean{
        val EMAIL_ADDRESS = Pattern.compile(
            "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                    "\\@" +
                    "gmail.com"
        )
        return EMAIL_ADDRESS.matcher(str).matches()
    }

    private fun sendVerificationEmail(){
        val user = mAuth.currentUser

        user?.sendEmailVerification()?.addOnSuccessListener {
            Toast.makeText(this, "Confirm Your Email Address", Toast.LENGTH_SHORT)
                .show()
            Log.d("GENERAL","Verification Email Sent")
            binding.btnResendVerificationEmail.visibility = View.GONE
            binding.tvEmailNotVerified.visibility = View.GONE
        }?.addOnFailureListener{
            Toast.makeText(this, "Couldn't Send Verification Email", Toast.LENGTH_SHORT)
                .show()
            Log.d("GENERAL","Verification Email Sent")
        }
    }

    private fun logInUser()
    {
        //Toast.makeText(this,"logInUser()",Toast.LENGTH_SHORT).show()
        var email = binding.etvEnterEmail.text.toString()
        var pass = binding.etvEnterPassword.text.toString()

        if(email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()  &&isEmailValidGmail(email))
        {
            if(pass.isNotEmpty())
            {
                mAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(this)
                { task ->
                    if(task.isSuccessful)
                    {
                        if(!mAuth.currentUser?.isEmailVerified!!){
                            isEmailVerified = false
                            binding.tvEmailNotVerified.visibility = View.VISIBLE
                            binding.btnResendVerificationEmail.visibility = View.VISIBLE
                        }else{
                            isEmailVerified = true
                            Toast.makeText(this,"Log in Successful", Toast.LENGTH_SHORT).show()
                            //go to login activity
                            val intent = Intent(this,FragmentHomeActivity::class.java)
                            startActivity(intent)

                        }
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
        finish()
    }

    private fun goToWelcomePage(){
        val intent = Intent(this, SignInActivity::class.java)
        startActivity(intent);
        finish()
    }
}
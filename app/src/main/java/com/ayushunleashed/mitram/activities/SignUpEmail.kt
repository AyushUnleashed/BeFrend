package com.ayushunleashed.mitram.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ayushunleashed.mitram.R
import com.ayushunleashed.mitram.databinding.ActivitySignUpEmailBinding
import com.ayushunleashed.mitram.databinding.FragmentEditInterestsBinding

class SignUpEmail : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpEmailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignUpEmailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        handleButtons()
    }

    fun handleButtons(){

        binding.tvLogInBtn.setOnClickListener{
            goToLogInWithEmailPage()
        }
    }
    fun goToLogInWithEmailPage(){
        val intent = Intent(this@SignUpEmail, LogInEmail::class.java)
        startActivity(intent);
    }
}
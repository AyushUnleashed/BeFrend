package com.ayushunleashed.mitram.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ayushunleashed.mitram.R

class LogInEmail : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in_email)
        supportActionBar?.hide()
    }
}
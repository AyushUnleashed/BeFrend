package com.ayushunleashed.mitram.utils

import java.util.regex.Pattern

class HelperClass {

    fun isEmailValidGmail(str:String): Boolean{
        val EMAIL_ADDRESS = Pattern.compile(
            "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                    "\\@" +
                    "gmail.com"
        )
        return EMAIL_ADDRESS.matcher(str).matches()
    }
}
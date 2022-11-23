package com.ayushunleashed.mitram.utils

class StringHelperClass {

    public fun removeEmptyLinesFromStartAndEnd(input:String):String{
        var modifiedInput = input.replace("(^[\\r\\n]+|[\\r\\n]+$)".toRegex(), "");
        return modifiedInput
    }

}
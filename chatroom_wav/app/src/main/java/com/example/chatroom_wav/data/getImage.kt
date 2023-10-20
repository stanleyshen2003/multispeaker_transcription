package com.example.chatroom_wav.data

import com.example.chatroom_wav.R

fun getImageForName(name: String): Int {
    return when (name) {
        "User 1" -> R.drawable.user_image1
        "User 2" -> R.drawable.user_image2
        "User 3" -> R.drawable.user_image3
        "User 4" -> R.drawable.user_image4
        "User 5" -> R.drawable.user_image5
        else -> R.drawable.user_image
    }
}
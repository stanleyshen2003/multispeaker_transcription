package com.example.chatroom_wav.data

import com.example.chatroom_wav.R

fun getImageForName(name: String): Int {
    return when (name) {
        "user1" -> R.drawable.user_image1
        "user2" -> R.drawable.user_image2
        "user3" -> R.drawable.user_image3
        "user4" -> R.drawable.user_image4
        "user5" -> R.drawable.user_image5
        else -> R.drawable.user_image
    }
}
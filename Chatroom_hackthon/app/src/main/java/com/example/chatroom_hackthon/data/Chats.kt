package com.example.chatroom_hackthon.data

import android.content.res.Resources
import com.example.chatroom_hackthon.R


fun chatList(resources: Resources): List<Chat> {
    return listOf(
        Chat(
            id = 1,
            name = "user4",
            image = R.drawable.user_image4,
            text = resources.getString(R.string.text1)
        ),
        Chat(
            id = 2,
            name = resources.getString(R.string.user2_name),
            image = R.drawable.user_image2,
            text = resources.getString(R.string.text2)
        ),
    )
}
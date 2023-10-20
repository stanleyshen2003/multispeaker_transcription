package com.example.chatroom_wav.data

import android.content.res.Resources
import com.example.chatroom_wav.R


fun chatList(resources: Resources): List<Chat> {
    return listOf(
        Chat(
            name = "user4",
            text = resources.getString(R.string.text1)
        ),
        Chat(
            name = resources.getString(R.string.user2_name),
            text = resources.getString(R.string.text2)
        ),
    )
}
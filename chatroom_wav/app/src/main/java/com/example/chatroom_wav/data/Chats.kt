package com.example.chatroom_wav.data

import android.content.res.Resources
import com.example.chatroom_wav.R


fun chatList(resources: Resources): List<Chat> {
    return listOf(
        Chat(
            name = "user1",
            text = "press the button to start"
        ),
    )
}

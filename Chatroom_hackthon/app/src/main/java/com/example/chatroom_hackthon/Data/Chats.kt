package com.example.chatroom_hackthon.Data

import android.content.res.Resources
import com.example.chatroom_hackthon.R


fun chatList(resources: Resources): List<Chat> {
    return listOf(
        Chat(
            id = 1,
            name = resources.getString(R.string.user1_name),
            image = R.drawable.user_image,
            text = resources.getString(R.string.text1)
        ),
        Chat(
            id = 2,
            name = resources.getString(R.string.user2_name),
            image = R.drawable.user_image,
            text = resources.getString(R.string.text2)
        ),
        Chat(
            id = 3,
            name = resources.getString(R.string.user3_name),
            image = R.drawable.user_image,
            text = resources.getString(R.string.text3)
        ),
        Chat(
            id = 1,
            name = resources.getString(R.string.user1_name),
            image = R.drawable.user_image,
            text = resources.getString(R.string.text4)
        ),
        Chat(
            id = 2,
            name = resources.getString(R.string.user2_name),
            image = R.drawable.user_image,
            text = resources.getString(R.string.text5)
        ),
        Chat(
            id = 3,
            name = resources.getString(R.string.user3_name),
            image = R.drawable.user_image,
            text = resources.getString(R.string.text6)
        )
    )
}
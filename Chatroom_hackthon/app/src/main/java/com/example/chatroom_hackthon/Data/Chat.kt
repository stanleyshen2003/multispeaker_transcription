package com.example.chatroom_hackthon.Data
import androidx.annotation.DrawableRes
data class Chat(
    val id: Long,
    val name: String,
    @DrawableRes
    val image: Int?,
    val text: String
)

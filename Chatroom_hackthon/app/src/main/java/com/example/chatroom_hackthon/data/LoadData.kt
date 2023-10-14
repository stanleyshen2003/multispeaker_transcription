package com.example.chatroom_hackthon.data

import android.content.Context
import java.io.IOException

fun readJSON(context: Context, fileName: String): String {
    try {
        val assetManager = context.assets
        return assetManager.open(fileName).bufferedReader().use {
            it.readText()
        }
    } catch (e: IOException) {
        e.printStackTrace()
        return ""
    }
}

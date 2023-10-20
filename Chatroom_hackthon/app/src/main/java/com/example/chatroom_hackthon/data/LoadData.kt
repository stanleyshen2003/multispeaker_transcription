package com.example.chatroom_hackthon.data

import android.content.Context
import org.json.JSONArray
import java.io.InputStream


fun loadJSONFromAsset(context:Context,fileName: String): String {
    val json: String
    try {
        val inputStream: InputStream = context.assets.open("$fileName")
        json = inputStream.bufferedReader().use { it.readText() }
    } catch (e: Exception) {
        e.printStackTrace()
        return ""
    }
    return json
}
// 解析 JSON 数据并返回 Chat 对象列表
fun parseChatJSON(json: String): List<Chat> {
    val chatList = mutableListOf<Chat>()
    try {
        val jsonArray = JSONArray(json)
        for (i in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(i)
            val name = jsonObject.getString("name")
            val text = jsonObject.getString("text")
            val image = getImageForName(name) // Assign image based on name
            val chat = Chat(i.toLong(), name, image, text)
            chatList.add(chat)
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return chatList
}
package com.example.chatroom_wav.data

import android.content.Context
import android.util.Log
import org.json.JSONArray
import java.io.InputStream
import com.example.chatroom_wav.data.Translator;


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
fun parseChatJSON(json: String, language: String): List<Chat> {
    val chatList = mutableListOf<Chat>()
    try {
        val jsonArray = JSONArray(json)
        for (i in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(i)
            val name = jsonObject.getString("name")
            var text = jsonObject.getString("text")

            if(text!="###") {
                if(language!="en")
                {
                    val translator = Translator()
                    text = translator.translate(text, language)
                }
                val chat = Chat(name, text)
                chatList.add(chat)
            }
            else
            {
                Log.d("###","###")
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return chatList
}
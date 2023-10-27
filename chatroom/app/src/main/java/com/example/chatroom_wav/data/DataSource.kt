package com.example.chatroom_wav.data

import android.content.res.Resources
import androidx.lifecycle.MutableLiveData

class DataSource(resources: Resources) {
    private val initialChatList = chatList(resources)
    private val chatsLiveData = MutableLiveData(initialChatList)

    fun getChatList(): MutableLiveData<List<Chat>> {
        return chatsLiveData
    }
    fun getLastChat(): Chat? {
        val currentList = chatsLiveData.value
        return currentList?.lastOrNull()
    }


    companion object {
        private var INSTANCE: DataSource? = null

        fun getDataSource(resources: Resources): DataSource {
            return synchronized(DataSource::class) {
                val newInstance = INSTANCE ?: DataSource(resources)
                INSTANCE = newInstance
                newInstance
            }
        }
    }
}
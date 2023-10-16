package com.example.chatroom_hackthon.data

import android.content.res.Resources
import androidx.lifecycle.MutableLiveData

class DataSource(resources: Resources) {
    private val initialChatList = chatList(resources)
    private val chatsLiveData = MutableLiveData(initialChatList)

    fun addChat(chat: Chat) {
        val currentList = chatsLiveData.value
        if (currentList == null) {
            chatsLiveData.postValue(listOf(chat))
        } else {
            val updatedList = currentList.toMutableList()
            updatedList.add(chat)
            chatsLiveData.postValue(updatedList)
        }
    }

    fun getChatForId(id: Long): Chat? {
        chatsLiveData.value?.let { chats ->
            return chats.firstOrNull{ it.id == id}
        }
        return null
    }

    fun getChatList(): MutableLiveData<List<Chat>> {
        return chatsLiveData
    }

    fun getRandomChatImageAsset(): Int? {
        val randomNumber = (initialChatList.indices).random()
        return initialChatList[randomNumber].image
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
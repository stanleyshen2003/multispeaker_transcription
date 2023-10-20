package com.example.chatroom_java.data;

import android.content.res.Resources;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.example.chatroom_java.data.Chats;

public class DataSource {
    private List<Chat> initialChatList;
    private MutableLiveData<List<Chat>> chatsLiveData;

    private DataSource(Resources resources) {
        Chats chats = new Chats(); // Create an instance of Chats
        initialChatList = chats.chatList(resources); // Call the chatList method
        chatsLiveData = new MutableLiveData<>(initialChatList);
    }

    public void addChat(Chat chat) {
        List<Chat> currentList = chatsLiveData.getValue();
        if (currentList == null) {
            chatsLiveData.postValue(Collections.singletonList(chat));
        } else {
            List<Chat> updatedList = new ArrayList<>(currentList);
            updatedList.add(chat);
            chatsLiveData.postValue(updatedList);
        }
    }

    public Chat getChatForId(long id) {
        List<Chat> chats = chatsLiveData.getValue();
        if (chats != null) {
            for (Chat chat : chats) {
                if (chat.getId() == id) {
                    return chat;
                }
            }
        }
        return null;
    }

    public MutableLiveData<List<Chat>> getChatList() {
        return chatsLiveData;
    }

    public Integer getRandomChatImageAsset() {
        int randomNumber = (int) (Math.random() * initialChatList.size());
        return initialChatList.get(randomNumber).getImage();
    }

    private static DataSource INSTANCE = null;

    public static synchronized DataSource getDataSource(Resources resources) {
        if (INSTANCE == null) {
            INSTANCE = new DataSource(resources);
        }
        return INSTANCE;
    }
}


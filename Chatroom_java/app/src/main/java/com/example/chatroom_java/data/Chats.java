package com.example.chatroom_java.data;

import android.content.res.Resources;
import com.example.chatroom_java.R;

import java.util.ArrayList;
import java.util.List;


public class Chats {
    public List<Chat> chatList(Resources resources) {
        List<Chat> chatList = new ArrayList<>();

        chatList.add(new Chat(
                1,
                "user4",
                R.drawable.user_image4,
                resources.getString(R.string.text1)
        ));

        chatList.add(new Chat(
                2,
                resources.getString(R.string.user2_name),
                R.drawable.user_image2,
                resources.getString(R.string.text2)
        ));

        return chatList;
    }
}
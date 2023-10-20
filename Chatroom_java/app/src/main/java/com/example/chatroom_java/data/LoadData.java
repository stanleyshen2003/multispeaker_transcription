package com.example.chatroom_java.data;

import android.content.Context;

import com.example.chatroom_java.R;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class LoadData {
    public static String loadJSONFromAsset(Context context, String fileName) {
        String json = "";
        try {
            InputStream inputStream = context.getAssets().open(fileName);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
            inputStream.close();
            json = stringBuilder.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return json;
    }

    public static List<Chat> parseChatJSON(String json) {
        List<Chat> chatList = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String name = jsonObject.getString("name");
                String text = jsonObject.getString("text");
                int image = getImageForName(name); // Assign image based on name
                Chat chat = new Chat((long) i, name, image, text);
                chatList.add(chat);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return chatList;
    }

    private static int getImageForName(String name) {
        switch (name) {
            case "User 1":
                return R.drawable.user_image1;
            case "User 2":
                return R.drawable.user_image2;
            case "User 3":
                return R.drawable.user_image3;
            case "User 4":
                return R.drawable.user_image4;
            case "User 5":
                return R.drawable.user_image5;
            default:
                return R.drawable.user_image;
        }

    }
}


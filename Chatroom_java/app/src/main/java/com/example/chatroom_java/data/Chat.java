package com.example.chatroom_java.data;

public class Chat {
    private long id;
    private String name;
    private int image;
    private String text;

    public Chat(long id, String name, int image, String text) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.text = text;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getImage() {
        return image;
    }

    public String getText() {
        return text;
    }
}

package com.carcar.mychat;

public class ChatListItem {
    public String phone;
    public String lastMessage;
    public long timestamp;

    // Required empty constructor for Firebase
    public ChatListItem() {
    }

    public ChatListItem(String phone, String lastMessage, long timestamp) {
        this.phone = phone;
        this.lastMessage = lastMessage;
        this.timestamp = timestamp;
    }
}

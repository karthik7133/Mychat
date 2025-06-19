package com.carcar.mychat;

public class Message {
    private String sender;
    private String receiver;
    private String message;
    private long timestamp;

    public Message() {}

    public Message(String sender, String receiver, String message, long timestamp) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.timestamp = timestamp;
    }


    public String getSender() {
        return sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public String getMessage() {
        return message;
    }

    public long getTimestamp() {
        return timestamp;
    }
}

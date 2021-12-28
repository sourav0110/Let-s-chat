package com.example.letschat.MODELS;

public class ChatRoomMessageModel {
    String uId,message,userName;
    long timestamp;

    public ChatRoomMessageModel(String uId, String message, long timestamp,String userName) {
        this.uId = uId;
        this.message = message;
        this.timestamp = timestamp;
        this.userName=userName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public ChatRoomMessageModel(String uId, String message) {
        this.uId = uId;
        this.message = message;
    }
    public ChatRoomMessageModel(){}

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}

package com.example.creatinguser.Models;

import com.google.firebase.Timestamp;

import java.util.Date;

public class PageChat {
    String chat_image;
    String chat_username;
    String chat_message;
    String chat_time;
    String room_id;
    String user_id;

    public PageChat() {
    }

    public PageChat(String chat_image, String chat_username, String chat_message, String chat_time, String room_id, String user_id) {
        this.chat_image = chat_image;
        this.chat_username = chat_username;
        this.chat_message = chat_message;
        this.chat_time = chat_time;
        this.room_id = room_id;
        this.user_id = user_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getChat_image() {
        return chat_image;
    }

    public void setChat_image(String chat_image) {
        this.chat_image = chat_image;
    }

    public String getChat_username() {
        return chat_username;
    }

    public void setChat_username(String chat_username) {
        this.chat_username = chat_username;
    }

    public String getChat_message() {
        return chat_message;
    }

    public void setChat_message(String chat_message) {
        this.chat_message = chat_message;
    }

    public String getChat_time() {
        return chat_time;
    }

    public void setChat_time(String chat_time) {
        this.chat_time = chat_time;
    }

    public String getRoom_id() {
        return room_id;
    }

    public void setRoom_id(String room_id) {
        this.room_id = room_id;
    }
}

package com.example.creatinguser.Models;

import java.util.Date;
import java.util.List;

public class GroupChatMessage {
    String chat_image;
    String chat_username;
    String chat_message;
    String chat_time;
    String group_chat_name;
    String user_id;

    public GroupChatMessage() {
    }

    public GroupChatMessage(String chat_image, String chat_username, String chat_message, String chat_time, String group_chat_name, String user_id) {
        this.chat_image = chat_image;
        this.chat_username = chat_username;
        this.chat_message = chat_message;
        this.chat_time = chat_time;
        this.group_chat_name = group_chat_name;
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

    public String getGroup_chat_name() {
        return group_chat_name;
    }

    public void setGroup_chat_name(String group_chat_name) {
        this.group_chat_name = group_chat_name;
    }
}


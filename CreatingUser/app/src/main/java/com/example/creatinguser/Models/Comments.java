package com.example.creatinguser.Models;

public class Comments {
    String currentDate;
    String currentTime;
    String comment;
    String username;
    String post_id;

    public Comments() {
    }

    public Comments(String currentDate, String currentTime, String comment, String username, String post_id) {
        this.currentDate = currentDate;
        this.currentTime = currentTime;
        this.comment = comment;
        this.username = username;
        this.post_id = post_id;
    }

    public String getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(String currentDate) {
        this.currentDate = currentDate;
    }

    public String getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(String currentTime) {
        this.currentTime = currentTime;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPost_id() {
        return post_id;
    }

    public void setPost_id(String post_id) {
        this.post_id = post_id;
    }
}

package com.example.creatinguser;

public class FanPage {
    String title;
    int total_members;
    String userID;

    public FanPage() {
    }

    public FanPage(String title, int total_members, String userID) {
        this.title = title;
        this.total_members = total_members;
        this.userID = userID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getTotal_members() {
        return total_members;
    }

    public void setTotal_members(int total_members) {
        this.total_members = total_members;
    }
}

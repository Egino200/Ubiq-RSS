package com.example.ubiqrss;

public class History {
    String userID, title, desc;

    public History(){

    }

    public History(String userID, String title, String desc){
        this.userID = userID;
        this.title = title;
        this.desc = desc;
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

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}

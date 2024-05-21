package com.example.bms;

public class Notification {
    private String id;
    private String message;
    private String time;
    private String userid;

    public Notification() {

    }

    public Notification(String message, String time, String userid) {
        this.message = message;
        this.time = time;
        this.userid = userid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }
}

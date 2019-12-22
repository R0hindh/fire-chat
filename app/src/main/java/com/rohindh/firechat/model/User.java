package com.rohindh.firechat.model;

public class User {
    private String userid;
    private String username;
    private String imageurl;
    private String status;
    private String search;

    public User(String userid, String username, String imageurl,String status,String search) {
        this.userid = userid;
        this.username = username;
        this.imageurl = imageurl;
        this.status = status;
        this.search = search;
    }
    public User(){}

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }
}


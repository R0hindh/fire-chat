package com.rohindh.firechat;

import android.app.Application;

public class FirechatApi extends Application {
    private String username;
    private String userid;
    private String imageurl;

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    private static FirechatApi instance;

    public FirechatApi() {}

    public static FirechatApi getInstance() {
        if(instance == null){
            instance = new FirechatApi();
            return instance;
        }
        return instance;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

}

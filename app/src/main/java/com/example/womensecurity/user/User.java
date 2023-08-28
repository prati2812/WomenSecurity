package com.example.womensecurity.user;

import java.io.Serializable;

public class User implements Serializable {

    private String userName;
    private String phoneNumber;
    private String userId;
    private String imagePath;
    private String deviceToken;

    private static final long serialVersionUID = 1L;
    private boolean isSelected;

    public User() {
    }

    public User(String userName, String phoneNumber, String userId, String imagePath , String deviceToken) {
        this.userName = userName;
        this.phoneNumber = phoneNumber;
        this.userId = userId;
        this.imagePath = imagePath;
        this.deviceToken = deviceToken;
    }

    public User(String userName, String phoneNumber, String imagePath) {
        this.userName = userName;
        this.phoneNumber = phoneNumber;
        this.imagePath = imagePath;
    }


    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }


    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }
}

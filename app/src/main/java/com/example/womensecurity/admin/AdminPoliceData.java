package com.example.womensecurity.admin;

public class AdminPoliceData {

    public String adminName;
    public String adminPhoneNumber;
    public String adminAddress;


    public AdminPoliceData() {
    }

    public AdminPoliceData(String adminName, String adminPhoneNumber, String adminAddress) {
        this.adminName = adminName;
        this.adminPhoneNumber = adminPhoneNumber;
        this.adminAddress = adminAddress;
    }

    public String getAdminName() {
        return adminName;
    }

    public void setAdminName(String adminName) {
        this.adminName = adminName;
    }

    public String getAdminPhoneNumber() {
        return adminPhoneNumber;
    }

    public void setAdminPhoneNumber(String adminPhoneNumber) {
        this.adminPhoneNumber = adminPhoneNumber;
    }

    public String getAdminAddress() {
        return adminAddress;
    }

    public void setAdminAddress(String adminAddress) {
        this.adminAddress = adminAddress;
    }
}

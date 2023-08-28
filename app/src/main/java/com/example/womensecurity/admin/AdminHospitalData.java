package com.example.womensecurity.admin;

public class AdminHospitalData {

    public String hospitalName;
    public String hospitalAddress;
    public String hospitalPhone;


    public AdminHospitalData() {
    }

    public AdminHospitalData(String hospitalName, String hospitalAddress, String hospitalPhone) {
        this.hospitalName = hospitalName;
        this.hospitalAddress = hospitalAddress;
        this.hospitalPhone = hospitalPhone;
    }

    public String getHospitalName() {
        return hospitalName;
    }

    public void setHospitalName(String hospitalName) {
        this.hospitalName = hospitalName;
    }

    public String getHospitalAddress() {
        return hospitalAddress;
    }

    public void setHospitalAddress(String hospitalAddress) {
        this.hospitalAddress = hospitalAddress;
    }

    public String getHospitalPhone() {
        return hospitalPhone;
    }

    public void setHospitalPhone(String hospitalPhone) {
        this.hospitalPhone = hospitalPhone;
    }
}

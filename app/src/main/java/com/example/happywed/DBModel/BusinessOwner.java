package com.example.happywed.DBModel;

public class BusinessOwner {

    private String ownerName;
    private String phoneNumber;
    private String uid;
    private String provider;
    private String status;

    public String getStatus() {
        return status;
    }

    public BusinessOwner setStatus(String status) {
        this.status = status;
        return this;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public BusinessOwner setOwnerName(String ownerName) {
        this.ownerName = ownerName;
        return this;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public BusinessOwner setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        return this;
    }

    public String getUid() {
        return uid;
    }

    public BusinessOwner setUid(String uid) {
        this.uid = uid;
        return this;
    }

    public String getProvider() {
        return provider;
    }

    public BusinessOwner setProvider(String provider) {
        this.provider = provider;
        return this;
    }
}

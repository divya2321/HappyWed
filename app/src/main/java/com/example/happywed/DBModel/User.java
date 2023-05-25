package com.example.happywed.DBModel;

import java.util.Date;

public class User {

    private String userName;
    private String partnerName;
    private String estimatedBudget;
    private String uid;
    private String mobileNo;
    private String email;
    private String profilePicUrl;
    private String weddingDate;
    private String provider;
    private String status;


    private String shopId;
    private String shopName;

    public String getStatus() {
        return status;
    }

    public User setStatus(String status) {
        this.status = status;
        return this;
    }

    public String getUserName() {
        return userName;
    }

    public User setUserName(String userName) {
        this.userName = userName;
        return this;
    }

    public String getPartnerName() {
        return partnerName;
    }

    public User setPartnerName(String partnerName) {
        this.partnerName = partnerName;
        return this;
    }

    public String getEstimatedBudget() {
        return estimatedBudget;
    }

    public User setEstimatedBudget(String estimatedBudget) {
        this.estimatedBudget = estimatedBudget;
        return this;
    }

    public String getUid() {
        return uid;
    }

    public User setUid(String uid) {
        this.uid = uid;
        return this;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public User setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public User setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getProfilePicUrl() {
        return profilePicUrl;
    }

    public User setProfilePicUrl(String profilePicUrl) {
        this.profilePicUrl = profilePicUrl;
        return this;
    }

    public String getWeddingDate() {
        return weddingDate;
    }

    public User setWeddingDate(String weddingDate) {
        this.weddingDate = weddingDate;
        return this;
    }

    public String getProvider() {
        return provider;
    }

    public User setProvider(String provider) {
        this.provider = provider;
        return this;
    }

    public String getShopId() {
        return shopId;
    }

    public User setShopId(String shopId) {
        this.shopId = shopId;
        return this;
    }

    public String getShopName() {
        return shopName;
    }

    public User setShopName(String shopName) {
        this.shopName = shopName;
        return this;
    }
}

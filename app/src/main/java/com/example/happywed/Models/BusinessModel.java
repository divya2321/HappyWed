package com.example.happywed.Models;

public class BusinessModel {

    private String businessProPic;
    private String businessKey;
    private String ownerKey;
    private String uid;
    private String businessName;
    private String businessCity;
    private String businessDistrict;
    private float rate;
    private boolean isFav;


    public String getBusinessProPic() {
        return businessProPic;
    }

    public BusinessModel setBusinessProPic(String businessProPic) {
        this.businessProPic = businessProPic;
        return this;
    }

    public String getBusinessName() {
        return businessName;
    }

    public BusinessModel setBusinessName(String businessName) {
        this.businessName = businessName;
        return this;
    }

    public String getBusinessCity() {
        return businessCity;
    }

    public BusinessModel setBusinessCity(String businessCity) {
        this.businessCity = businessCity;
        return this;
    }

    public String getBusinessDistrict() {
        return businessDistrict;
    }

    public BusinessModel setBusinessDistrict(String businessDistrict) {
        this.businessDistrict = businessDistrict;
        return this;
    }

    public float getRate() {
        return rate;
    }

    public BusinessModel setRate(float rate) {
        this.rate = rate;
        return this;
    }

    public boolean isFav() {
        return isFav;
    }

    public BusinessModel setFav(boolean fav) {
        isFav = fav;
        return this;
    }

    public String getBusinessKey() {
        return businessKey;
    }

    public BusinessModel setBusinessKey(String businessKey) {
        this.businessKey = businessKey;
        return this;
    }

    public String getOwnerKey() {
        return ownerKey;
    }

    public BusinessModel setOwnerKey(String ownerKey) {
        this.ownerKey = ownerKey;
        return this;
    }

    public String getUid() {
        return uid;
    }

    public BusinessModel setUid(String uid) {
        this.uid = uid;
        return this;
    }
}

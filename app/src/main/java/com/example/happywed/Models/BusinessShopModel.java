package com.example.happywed.Models;

public class BusinessShopModel {

    private String businessKey;
    private String businessName;
    private String businessCategory;
    private String businessProfilePic;

    public String getBusinessName() {
        return businessName;
    }

    public BusinessShopModel setBusinessName(String businessName) {
        this.businessName = businessName;
        return this;
    }

    public String getBusinessCategory() {
        return businessCategory;
    }

    public BusinessShopModel setBusinessCategory(String businessCategory) {
        this.businessCategory = businessCategory;
        return this;
    }

    public String getBusinessProfilePic() {
        return businessProfilePic;
    }

    public BusinessShopModel setBusinessProfilePic(String businessProfilePic) {
        this.businessProfilePic = businessProfilePic;
        return this;
    }

    public String getBusinessKey() {
        return businessKey;
    }

    public BusinessShopModel setBusinessKey(String businessKey) {
        this.businessKey = businessKey;
        return this;
    }
}

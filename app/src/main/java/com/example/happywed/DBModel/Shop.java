package com.example.happywed.DBModel;

import java.util.ArrayList;

public class Shop {

    private String shopName;
    private String status;
    private String shopId;
    private String ownerId;
    private String profilePic;
    private String confirmation;
    private ArrayList<String> shopCategories;

    public String getShopId() {
        return shopId;
    }

    public Shop setShopId(String shopId) {
        this.shopId = shopId;
        return this;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public Shop setProfilePic(String profilePic) {
        this.profilePic = profilePic;
        return this;
    }

    public String getShopName() {
        return shopName;
    }

    public Shop setShopName(String shopName) {
        this.shopName = shopName;
        return this;
    }

    public ArrayList<String> getShopCategories() {
        return shopCategories;
    }

    public Shop setShopCategories(ArrayList<String> shopCategories) {
        this.shopCategories = shopCategories;
        return this;
    }

    public String getStatus() {
        return status;
    }

    public Shop setStatus(String status) {
        this.status = status;
        return this;
    }

    public String getConfirmation() {
        return confirmation;
    }

    public Shop setConfirmation(String confirmation) {
        this.confirmation = confirmation;
        return this;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public Shop setOwnerId(String ownerId) {
        this.ownerId = ownerId;
        return this;
    }
}

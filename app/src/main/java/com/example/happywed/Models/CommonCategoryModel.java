package com.example.happywed.Models;

import java.io.Serializable;

public class CommonCategoryModel implements Serializable {

    private String ownerKey;
    private String businessKey;
    private String productKey;
    private String uid;
    private String productImage;
    private String productName;
    private boolean isFav;

    public String getProductKey() {
        return productKey;
    }

    public CommonCategoryModel setProductKey(String productKey) {
        this.productKey = productKey;
        return this;
    }

    public String getProductImage() {
        return productImage;
    }

    public CommonCategoryModel setProductImage(String productImage) {
        this.productImage = productImage;
        return this;
    }

    public String getProductName() {
        return productName;
    }

    public CommonCategoryModel setProductName(String productName) {
        this.productName = productName;
        return this;
    }

    public String getOwnerKey() {
        return ownerKey;
    }

    public CommonCategoryModel setOwnerKey(String ownerKey) {
        this.ownerKey = ownerKey;
        return this;
    }

    public String getBusinessKey() {
        return businessKey;
    }

    public CommonCategoryModel setBusinessKey(String businessKey) {
        this.businessKey = businessKey;
        return this;
    }

    public boolean isFav() {
        return isFav;
    }

    public CommonCategoryModel setFav(boolean fav) {
        isFav = fav;
        return this;
    }

    public String getUid() {
        return uid;
    }

    public CommonCategoryModel setUid(String uid) {
        this.uid = uid;
        return this;
    }
}

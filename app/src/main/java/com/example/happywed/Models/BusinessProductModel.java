package com.example.happywed.Models;

import java.io.Serializable;
import java.util.ArrayList;

public class BusinessProductModel implements Serializable {

    private String ownerKey;
    private String businessKey;
    private String uid;
    private String productKey;
    private String productName;
    private String productDescription;
    private String productPrice;
    private String businessDistrict;
    private String businessCity;
    private ArrayList<String> productImages;
    private ArrayList<String> productCategories;
    private String mainProductImage;
    private boolean isFav;

    public String getProductName() {
        return productName;
    }

    public BusinessProductModel setProductName(String productName) {
        this.productName = productName;
        return this;
    }

    public ArrayList<String> getProductImages() {
        return productImages;
    }

    public BusinessProductModel setProductImages(ArrayList<String> productImages) {
        this.productImages = productImages;
        return this;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public BusinessProductModel setProductPrice(String productPrice) {
        this.productPrice = productPrice;
        return this;
    }


    public String getMainProductImage() {
        return mainProductImage;
    }

    public BusinessProductModel setMainProductImage(String mainProductImage) {
        this.mainProductImage = mainProductImage;
        return this;
    }

    public String getProductKey() {
        return productKey;
    }

    public BusinessProductModel setProductKey(String productKey) {
        this.productKey = productKey;
        return this;
    }

    public ArrayList<String> getProductCategories() {
        return productCategories;
    }

    public BusinessProductModel setProductCategories(ArrayList<String> productCategories) {
        this.productCategories = productCategories;
        return this;
    }

    public String getOwnerKey() {
        return ownerKey;
    }

    public BusinessProductModel setOwnerKey(String ownerKey) {
        this.ownerKey = ownerKey;
        return this;
    }

    public String getBusinessKey() {
        return businessKey;
    }

    public BusinessProductModel setBusinessKey(String businessKey) {
        this.businessKey = businessKey;
        return this;
    }

    public String getUid() {
        return uid;
    }

    public BusinessProductModel setUid(String uid) {
        this.uid = uid;
        return this;
    }

    public boolean isFav() {
        return isFav;
    }

    public BusinessProductModel setFav(boolean fav) {
        isFav = fav;
        return this;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public BusinessProductModel setProductDescription(String productDescription) {
        this.productDescription = productDescription;
        return this;
    }

    public String getBusinessDistrict() {
        return businessDistrict;
    }

    public BusinessProductModel setBusinessDistrict(String businessDistrict) {
        this.businessDistrict = businessDistrict;
        return this;
    }

    public String getBusinessCity() {
        return businessCity;
    }

    public BusinessProductModel setBusinessCity(String businessCity) {
        this.businessCity = businessCity;
        return this;
    }
}

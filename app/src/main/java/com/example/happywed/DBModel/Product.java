package com.example.happywed.DBModel;

import java.util.ArrayList;

public class Product {

    private String productName;
    private String productDescription;
    private String productPrice;
    private ArrayList<String> productImages;
    private ArrayList<String> productCategories;

    public String getProductName() {
        return productName;
    }

    public Product setProductName(String productName) {
        this.productName = productName;
        return this;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public Product setProductDescription(String productDescription) {
        this.productDescription = productDescription;
        return this;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public Product setProductPrice(String productPrice) {
        this.productPrice = productPrice;
        return this;
    }

    public ArrayList<String> getProductImages() {
        return productImages;
    }

    public Product setProductImages(ArrayList<String> productImages) {
        this.productImages = productImages;
        return this;
    }

    public ArrayList<String> getProductCategories() {
        return productCategories;
    }

    public Product setProductCategories(ArrayList<String> productCategories) {
        this.productCategories = productCategories;
        return this;
    }
}

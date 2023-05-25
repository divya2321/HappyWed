package com.example.happywed.Models;

public class BusinessProductCardModel {

    private String productImage;
    private String productName;
    private String productPrice;
    private boolean productIsFav;

    public String getProductImage() {
        return productImage;
    }

    public BusinessProductCardModel setProductImage(String productImage) {
        this.productImage = productImage;
        return this;
    }

    public String getProductName() {
        return productName;
    }

    public BusinessProductCardModel setProductName(String productName) {
        this.productName = productName;
        return this;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public BusinessProductCardModel setProductPrice(String productPrice) {
        this.productPrice = productPrice;
        return this;
    }

    public boolean isProductIsFav() {
        return productIsFav;
    }

    public BusinessProductCardModel setProductIsFav(boolean productIsFav) {
        this.productIsFav = productIsFav;
        return this;
    }
}

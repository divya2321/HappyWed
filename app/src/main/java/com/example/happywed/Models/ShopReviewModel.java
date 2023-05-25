package com.example.happywed.Models;

public class ShopReviewModel {


    private String userId;
    private String userName;
    private String userProfilePic;
    private float rate;
    private String reviewedDate;
    private String review;



    public float getRate() {
        return rate;
    }

    public ShopReviewModel setRate(float rate) {
        this.rate = rate;
        return this;
    }

    public String getReviewedDate() {
        return reviewedDate;
    }

    public ShopReviewModel setReviewedDate(String reviewedDate) {
        this.reviewedDate = reviewedDate;
        return this;
    }

    public String getReview() {
        return review;
    }

    public ShopReviewModel setReview(String review) {
        this.review = review;
        return this;
    }

    public String getUserId() {
        return userId;
    }

    public ShopReviewModel setUserId(String userId) {
        this.userId = userId;
        return this;
    }

    public String getUserName() {
        return userName;
    }

    public ShopReviewModel setUserName(String userName) {
        this.userName = userName;
        return this;
    }

    public String getUserProfilePic() {
        return userProfilePic;
    }

    public ShopReviewModel setUserProfilePic(String userProfilePic) {
        this.userProfilePic = userProfilePic;
        return this;
    }
}
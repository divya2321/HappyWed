package com.example.happywed.DBModel;

public class Review {

    private String uid;
    private String rate;
    private String review;
    private String reviewedDate;

    public String getUid() {
        return uid;
    }

    public Review setUid(String uid) {
        this.uid = uid;
        return this;
    }

    public String getRate() {
        return rate;
    }

    public Review setRate(String rate) {
        this.rate = rate;
        return this;
    }

    public String getReview() {
        return review;
    }

    public Review setReview(String review) {
        this.review = review;
        return this;
    }

    public String getReviewedDate() {
        return reviewedDate;
    }

    public Review setReviewedDate(String reviewedDate) {
        this.reviewedDate = reviewedDate;
        return this;
    }
}

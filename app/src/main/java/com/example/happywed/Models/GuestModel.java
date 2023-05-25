package com.example.happywed.Models;

public class GuestModel {

    private int guestId;
    private String familyName;
    private int adultCount;
    private int childCount;
    private int allCount;

    public int getGuestId() {
        return guestId;
    }

    public GuestModel setGuestId(int guestId) {
        this.guestId = guestId;
        return this;
    }

    public String getFamilyName() {
        return familyName;
    }

    public GuestModel setFamilyName(String familyName) {
        this.familyName = familyName;
        return this;
    }

    public int getAdultCount() {
        return adultCount;
    }

    public GuestModel setAdultCount(int adultCount) {
        this.adultCount = adultCount;
        return this;
    }

    public int getChildCount() {
        return childCount;
    }

    public GuestModel setChildCount(int childCount) {
        this.childCount = childCount;
        return this;
    }

    public int getAllCount() {
        return allCount;
    }

    public GuestModel setAllCount(int allCount) {
        allCount = getAdultCount()+getChildCount();
        this.allCount = allCount;
        return this;
    }
}

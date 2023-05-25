package com.example.happywed.Models;

import android.util.Log;

import com.facebook.internal.LockOnGetVariable;

public class ChecklistModel {

    private int id;
    private String title;
    private String checkDate;
    private int status;
    private String statusText;


    public int getId() {
        return id;
    }

    public ChecklistModel setId(int id) {
        this.id = id;
        Log.d("ID SETTED :", String.valueOf(id));
        return this;
    }

    public String getTitle() {
        return title;
    }

    public ChecklistModel setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getCheckDate() {
        return checkDate;
    }

    public ChecklistModel setCheckDate(String checkDate) {
        this.checkDate = checkDate;
        return this;
    }

    public int getStatus() {
        return status;
    }

    public ChecklistModel setStatus(int status) {
        this.status = status;
        return this;
    }

    public String getStatusText() {
        return statusText;
    }

    public ChecklistModel setStatusText(String statusText) {
        this.statusText = statusText;
        return this;
    }


}

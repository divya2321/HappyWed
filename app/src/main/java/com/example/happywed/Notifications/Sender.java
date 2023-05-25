package com.example.happywed.Notifications;

public class Sender {

    private Data data;
    private String to;

    public Sender(Data data, String to){
        this.setData(data);
        this.setTo(to);
    }

    public Sender(){}

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }
}
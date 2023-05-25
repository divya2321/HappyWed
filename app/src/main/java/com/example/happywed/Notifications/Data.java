package com.example.happywed.Notifications;

public class Data {
    private String user;
    private Integer icon;
    private String body;
    private String title;
    private String sent;

    public Data(String user, int icon, String body, String title, String sent) {
        this.user = user;
        this.icon = icon;
        this.body = body;
        this.title = title;
        this.sent = sent;
    }

    public Data() { }

    public String getUser() {
        return user;
    }

    public Data setUser(String user) {
        this.user = user;
        return this;
    }

    public Integer getIcon() {
        return icon;
    }

    public Data setIcon(Integer icon) {
        this.icon = icon;
        return this;
    }

    public String getBody() {
        return body;
    }

    public Data setBody(String body) {
        this.body = body;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public Data setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getSent() {
        return sent;
    }


    public Data setSent(String sent) {
        this.sent = sent;
        return this;
    }
}
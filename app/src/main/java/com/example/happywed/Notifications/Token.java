package com.example.happywed.Notifications;

public class Token {
    private String token;


    public String getToken() {
        return token;
    }

    public Token setToken(String token) {
        this.token = token;
        return this;
    }

    public Token(){}

    public Token(String token){
        this.token = token;
    }
}
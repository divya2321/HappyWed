package com.example.happywed.DBModel;

public class ChatMessageModel {

    private String recieverName;
    private String recieverId;
    private String recieverProfPic;
    private String messageText;
    private String messageStatus;
    private String messageDate;
    private String senderId;
    private String ownerId;

    public String getOwnerId() {
        return ownerId;
    }

    public ChatMessageModel setOwnerId(String ownerId) {
        this.ownerId = ownerId;
        return this;
    }

    public String getSenderId() {
        return senderId;
    }

    public ChatMessageModel setSenderId(String senderId) {
        this.senderId = senderId;
        return this;
    }

    public String getRecieverName() {
        return recieverName;
    }

    public ChatMessageModel setRecieverName(String recieverName) {
        this.recieverName = recieverName;
        return this;
    }

    public String getRecieverId() {
        return recieverId;
    }

    public ChatMessageModel setRecieverId(String recieverId) {
        this.recieverId = recieverId;
        return this;
    }

    public String getRecieverProfPic() {
        return recieverProfPic;
    }

    public ChatMessageModel setRecieverProfPic(String recieverProfPic) {
        this.recieverProfPic = recieverProfPic;
        return this;
    }

    public String getMessageText() {
        return messageText;
    }

    public ChatMessageModel setMessageText(String messageText) {
        this.messageText = messageText;
        return this;
    }

    public String getMessageStatus() {
        return messageStatus;
    }

    public ChatMessageModel setMessageStatus(String messageStatus) {
        this.messageStatus = messageStatus;
        return this;
    }

    public String getMessageDate() {
        return messageDate;
    }

    public ChatMessageModel setMessageDate(String messageDate) {
        this.messageDate = messageDate;
        return this;
    }
}

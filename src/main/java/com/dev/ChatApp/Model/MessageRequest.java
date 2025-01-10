package com.dev.ChatApp.Model;

public class MessageRequest {
    private String receiverUsername;  // Username of the message recipient
    private String senderUsername;   // Username of the sender
    private String content;          // Content of the message

    // Getters and setters
    public String getReceiverUsername() {
        return receiverUsername;
    }

    public void setReceiverUsername(String receiverUsername) {
        this.receiverUsername = receiverUsername;
    }

    public String getSenderUsername() {
        return senderUsername;
    }

    public void setSenderUsername(String senderUsername) {
        this.senderUsername = senderUsername;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}

package com.rahul.child.Model;

import java.time.LocalDateTime;

public class Message {
    private int Id;
    private String Description;
    private String Timestamp;
    private String Sender;
    private String Receiver;

    public Message() {
    }

    public Message(String description, String timestamp, String sender, String receiver) {
        Description = description;
        Timestamp = timestamp;
        Sender = sender;
        Receiver = receiver;
    }

    public Message(int id, String description, String timestamp, String sender, String receiver) {
        Id = id;
        Description = description;
        Timestamp = timestamp;
        Sender = sender;
        Receiver = receiver;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getTimestamp() {
        return Timestamp;
    }

    public void setTimestamp(String timestamp) {
        Timestamp = timestamp;
    }

    public String getSender() {
        return Sender;
    }

    public void setSender(String sender) {
        Sender = sender;
    }

    public String getReceiver() {
        return Receiver;
    }

    public void setReceiver(String receiver) {
        Receiver = receiver;
    }
}

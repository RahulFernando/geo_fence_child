package com.rahul.child.Model;

public class Parent {
    private int Id;
    private String UserName;
    private String Email;
    private String PhoneNumber;
    private String Password;
    private String Salt;
    private String Serial;

    public Parent() {
    }

    public Parent(String userName, String password) {
        UserName = userName;
        Password = password;
    }

    public Parent(String userName, String email, String phoneNumber, String password, String salt, String serial) {
        UserName = userName;
        Email = email;
        PhoneNumber = phoneNumber;
        Password = password;
        Salt = salt;
        Serial = serial;
    }

    public int getId() {
        return Id;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getPhoneNumber() {
        return PhoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        PhoneNumber = phoneNumber;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getSalt() {
        return Salt;
    }

    public void setSalt(String salt) {
        Salt = salt;
    }

    public String getSerial() {
        return Serial;
    }

    public void setSerial(String serial) {
        Serial = serial;
    }
}

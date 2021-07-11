package com.rahul.child.Model;

public class Child {
    private int Id;
    private String UserName;
    private String Email;
    private String PhoneNumber;
    private String Password;
    private String Salt;
    private double Lat;
    private double Lng;
    private int ParentId;

    public Child() {
    }

    public Child(String userName, String password, String salt) {
        UserName = userName;
        Password = password;
        Salt = salt;
    }

    public Child(int id, String userName, String email, String phoneNumber, String password, String salt, double lat, double lng, int parentId) {
        Id = id;
        UserName = userName;
        Email = email;
        PhoneNumber = phoneNumber;
        Password = password;
        Salt = salt;
        Lat = lat;
        Lng = lng;
        ParentId = parentId;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
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

    public double getLat() {
        return Lat;
    }

    public void setLat(double lat) {
        Lat = lat;
    }

    public double getLng() {
        return Lng;
    }

    public void setLng(double lng) {
        Lng = lng;
    }

    public int getParentId() {
        return ParentId;
    }

    public void setParentId(int parentId) {
        ParentId = parentId;
    }
}

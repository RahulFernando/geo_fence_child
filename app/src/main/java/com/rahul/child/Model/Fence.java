package com.rahul.child.Model;

public class Fence {
    private int Id;
    private String FenceName;
    private double Lat;
    private double Lng;
    private int Radius;
    private int ChildId;

    public Fence() {
    }

    public Fence(String fenceName, double lat, double lng, int radius, int childId) {
        FenceName = fenceName;
        Lat = lat;
        Lng = lng;
        Radius = radius;
        ChildId = childId;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getFenceName() {
        return FenceName;
    }

    public void setFenceName(String fenceName) {
        FenceName = fenceName;
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

    public int getRadius() {
        return Radius;
    }

    public void setRadius(int radius) {
        Radius = radius;
    }

    public int getChildId() {
        return ChildId;
    }

    public void setChildId(int childId) {
        ChildId = childId;
    }
}

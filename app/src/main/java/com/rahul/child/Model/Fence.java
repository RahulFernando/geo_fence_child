package com.rahul.child.Model;

public class Fence {
    private int Id;
    private String FenceName;
    private double Lat;
    private double Lng;
    private int Radius;
    private int ChildId;
    private String Status;
    private String Type;

    public Fence() {
    }

    public Fence(int id, String fenceName, double lat, double lng, int radius, int childId, String status, String type) {
        Id = id;
        FenceName = fenceName;
        Lat = lat;
        Lng = lng;
        Radius = radius;
        ChildId = childId;
        Status = status;
        Type = type;
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

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    @Override
    public String toString() {
        return "Fence{" +
                "Id=" + Id +
                ", FenceName='" + FenceName + '\'' +
                ", Lat=" + Lat +
                ", Lng=" + Lng +
                ", Radius=" + Radius +
                ", ChildId=" + ChildId +
                ", Status='" + Status + '\'' +
                '}';
    }
}

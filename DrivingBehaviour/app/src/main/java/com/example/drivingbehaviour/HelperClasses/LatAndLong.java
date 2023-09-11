package com.example.drivingbehaviour.HelperClasses;

public class LatAndLong {

    private double latitude;
    private double longitude;
    private String type;

    public LatAndLong(double latitude, double longitude, String type) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.type = type;
    }

    public LatAndLong(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }


    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getType() {
        return type;
    }
}

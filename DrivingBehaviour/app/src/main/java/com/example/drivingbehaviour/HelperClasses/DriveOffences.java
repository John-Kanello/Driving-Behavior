package com.example.drivingbehaviour.HelperClasses;

public class DriveOffences {

    private int idleTime;
    private int highAcceleration;
    private int highDeceleration;
    private int highVibration;

    public DriveOffences(int idleTime, int highAcceleration, int highDeceleration, int highVibration) {
        this.idleTime = idleTime;
        this.highAcceleration = highAcceleration;
        this.highDeceleration = highDeceleration;
        this.highVibration = highVibration;
    }

    public int getIdleTime() {
        return idleTime;
    }

    public void setIdleTime(int idleTime) {
        this.idleTime = idleTime;
    }

    public int getHighAcceleration() {
        return highAcceleration;
    }

    public void setHighAcceleration(int highAcceleration) {
        this.highAcceleration = highAcceleration;
    }

    public int getHighDeceleration() {
        return highDeceleration;
    }

    public void setHighDeceleration(int highDeceleration) {
        this.highDeceleration = highDeceleration;
    }

    public int getHighVibration() {
        return highVibration;
    }

    public void setHighVibration(int highVibration) {
        this.highVibration = highVibration;
    }
}

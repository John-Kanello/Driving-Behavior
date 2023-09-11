package com.example.drivingbehaviour.Classes;

public class DriveResults {

    private Long userId;
    private int maxSpeed;
    private int avgSpeed;
    private String startHour;
    private String endHour;
    private String date;
    private String duration;
    private int distance;
    private int maxAcceleration;
    private int minAcceleration;
    private String idleTime;
    private int suddenBreaks;
    private int suddenAccelerations;
    private int suddenTurns;

    public DriveResults()
    {

    }

    public DriveResults(Long userId, int maxSpeed, int avgSpeed, String startHour, String endHour, String date, String duration, int distance,
                        int maxAcceleration, int minAcceleration, String idleTime,int suddenBreaks, int suddenAccelerations,
                        int suddenTurns)
    {
        this.userId = userId;
        this.maxSpeed = maxSpeed;
        this.avgSpeed = avgSpeed;
        this.startHour = startHour;
        this.endHour = endHour;
        this.date = date;
        this.duration = duration;
        this.distance = distance;
        this.maxAcceleration = maxAcceleration;
        this.minAcceleration = minAcceleration;
        this.idleTime = idleTime;
        this.suddenBreaks = suddenBreaks;
        this.suddenAccelerations = suddenAccelerations;
        this.suddenTurns = suddenTurns;
    }

    public DriveResults(int maxSpeed, int avgSpeed, String startHour, String endHour, String date, String duration, int distance,
                        int maxAcceleration, int minAcceleration, String idleTime, int suddenBreaks, int suddenAccelerations,
                        int suddenTurns)
    {
        this.maxSpeed = maxSpeed;
        this.avgSpeed = avgSpeed;
        this.startHour = startHour;
        this.endHour = endHour;
        this.date = date;
        this.duration = duration;
        this.distance = distance;
        this.maxAcceleration = maxAcceleration;
        this.minAcceleration = minAcceleration;
        this.idleTime = idleTime;
        this.suddenBreaks = suddenBreaks;
        this.suddenAccelerations = suddenAccelerations;
        this.suddenTurns = suddenTurns;
    }


    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public int getMaxSpeed() {
        return maxSpeed;
    }

    public void setMaxSpeed(int maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    public int getAvgSpeed() {
        return avgSpeed;
    }

    public void setAvgSpeed(int avgSpeed) {
        this.avgSpeed = avgSpeed;
    }

    public String getStartHour() {
        return startHour;
    }

    public void setStartHour(String startHour) {
        this.startHour = startHour;
    }

    public String getEndHour() {
        return endHour;
    }

    public void setEndHour(String endHour) {
        this.endHour = endHour;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public int getMaxAcceleration() {
        return maxAcceleration;
    }

    public void setMaxAcceleration(int maxAcceleration) {
        this.maxAcceleration = maxAcceleration;
    }

    public int getMinAcceleration() {
        return minAcceleration;
    }

    public void setMinAcceleration(int minAcceleration) {
        this.minAcceleration = minAcceleration;
    }

    public String getIdleTime() {
        return idleTime;
    }

    public void setIdleTime(String idleTime) {
        this.idleTime = idleTime;
    }

    public int getSuddenBreaks() {
        return suddenBreaks;
    }

    public void setSuddenBreaks(int suddenBreaks) {
        this.suddenBreaks = suddenBreaks;
    }

    public int getSuddenAccelerations() {
        return suddenAccelerations;
    }

    public void setSuddenAccelerations(int suddenAccelerations) {
        this.suddenAccelerations = suddenAccelerations;
    }

    public int getSuddenTurns() {
        return suddenTurns;
    }

    public void setSuddenTurns(int suddenTurns) {
        this.suddenTurns = suddenTurns;
    }
}

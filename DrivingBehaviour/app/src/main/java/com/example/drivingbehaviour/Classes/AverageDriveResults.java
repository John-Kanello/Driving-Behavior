package com.example.drivingbehaviour.Classes;

public class AverageDriveResults {

    private Long userId;
    private int maxSpeed;
    private int avgSpeed;
    private int maxAcceleration;
    private int minAcceleration;
    private int idleTime;
    private int numOfSessions;
    private int totalDuration;
    private int totalDistance;
    private int suddenBreaks;
    private int suddenAccelerations;
    private int suddenTurns;
    private int avgDistance;
    private int avgDuration;
    private int avgIdleTime;
    private double avgSuddenBreaks;
    private double avgSuddenAcceleration;
    private double avgSuddenTurns;

    public AverageDriveResults()
    {

    }

    public AverageDriveResults(Long userId, int maxSpeed, int avgSpeed, int maxAcceleration, int minAcceleration,
                               int idleTime, int numOfSessions, int totalDuration, int totalDistance, int suddenBreaks,
                               int suddenAccelerations, int suddenTurns, int avgDistance,int avgDuration, int avgIdleTime, double avgSuddenBreaks,
                               double avgSuddenAcceleration, double avgSuddenTurns) {
        this.userId = userId;
        this.maxSpeed = maxSpeed;
        this.avgSpeed = avgSpeed;
        this.maxAcceleration = maxAcceleration;
        this.minAcceleration = minAcceleration;
        this.idleTime = idleTime;
        this.numOfSessions = numOfSessions;
        this.totalDuration = totalDuration;
        this.totalDistance = totalDistance;
        this.suddenBreaks = suddenBreaks;
        this.suddenAccelerations = suddenAccelerations;
        this.suddenTurns = suddenTurns;
        this.avgDistance = avgDistance;
        this.avgDuration = avgDuration;
        this.avgIdleTime = avgIdleTime;
        this.avgSuddenBreaks = avgSuddenBreaks;
        this.avgSuddenAcceleration = avgSuddenAcceleration;
        this.avgSuddenTurns = avgSuddenTurns;
    }

    public AverageDriveResults(int avgSpeed, int avgDistance, int avgDuration,
                                     int avgIdleTime, double avgSuddenBreaks, double avgSuddenAcceleration, double avgSuddenTurns) {
        this.avgSpeed = avgSpeed;
        this.avgDistance = avgDistance;
        this.avgDuration = avgDuration;
        this.avgIdleTime = avgIdleTime;
        this.avgSuddenBreaks = avgSuddenBreaks;
        this.avgSuddenAcceleration = avgSuddenAcceleration;
        this.avgSuddenTurns = avgSuddenTurns;
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

    public int getIdleTime() {
        return idleTime;
    }

    public void setIdleTime(int idleTime) {
        this.idleTime = idleTime;
    }

    public int getNumOfSessions() {
        return numOfSessions;
    }

    public void setNumOfSessions(int numOfSessions) {
        this.numOfSessions = numOfSessions;
    }

    public int getTotalDuration() {
        return totalDuration;
    }

    public void setTotalDuration(int totalDuration) {
        this.totalDuration = totalDuration;
    }

    public int getTotalDistance() {
        return totalDistance;
    }

    public void setTotalDistance(int totalDistance) {
        this.totalDistance = totalDistance;
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

    public int getAvgDistance() {
        return avgDistance;
    }

    public void setAvgDistance(int avgDistance) {
        this.avgDistance = avgDistance;
    }

    public int getAvgDuration() {
        return avgDuration;
    }

    public void setAvgDuration(int avgDuration) {
        this.avgDuration = avgDuration;
    }

    public int getAvgIdleTime() {
        return avgIdleTime;
    }

    public void setAvgIdleTime(int avgIdleTime) {
        this.avgIdleTime = avgIdleTime;
    }

    public double getAvgSuddenBreaks() {
        return avgSuddenBreaks;
    }

    public void setAvgSuddenBreaks(double avgSuddenBreaks) {
        this.avgSuddenBreaks = avgSuddenBreaks;
    }

    public double getAvgSuddenAcceleration() {
        return avgSuddenAcceleration;
    }

    public void setAvgSuddenAcceleration(double avgSuddenAcceleration) {
        this.avgSuddenAcceleration = avgSuddenAcceleration;
    }

    public double getAvgSuddenTurns() {
        return avgSuddenTurns;
    }

    public void setAvgSuddenTurns(double avgSuddenTurns) {
        this.avgSuddenTurns = avgSuddenTurns;
    }
}

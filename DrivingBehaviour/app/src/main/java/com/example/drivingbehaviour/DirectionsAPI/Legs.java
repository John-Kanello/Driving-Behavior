package com.example.drivingbehaviour.DirectionsAPI;

import com.example.drivingbehaviour.DistanceMatrixAPI.Distance;
import com.example.drivingbehaviour.DistanceMatrixAPI.Duration;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Legs {

    @JsonProperty("distance")
    private Distance distance;

    @JsonProperty("duration")
    private Duration duration;

    @JsonProperty("end_address")
    private String end_address;

    @JsonProperty("end_location")
    private EndLocation end_location;

    @JsonProperty("start_address")
    private String start_address;

    @JsonProperty("start_location")
    private StartLocation start_location;

    @JsonProperty("steps")
    private List<Steps> steps;

    @JsonProperty("distance")
    public Distance getDistance() {
        return distance;
    }

    @JsonProperty("duration")
    public Duration getDuration() {
        return duration;
    }

    @JsonProperty("end_address")
    public String getEnd_address() {
        return end_address;
    }

    @JsonProperty("end_location")
    public EndLocation getEndLocation() {
        return end_location;
    }

    @JsonProperty("start_address")
    public String getStart_address() {
        return start_address;
    }

    @JsonProperty("start_location")
    public StartLocation getStartLocation() {
        return start_location;
    }

    @JsonProperty("steps")
    public List<Steps> getStepsList() {
        return steps;
    }
}

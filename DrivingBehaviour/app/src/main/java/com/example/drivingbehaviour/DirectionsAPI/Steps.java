package com.example.drivingbehaviour.DirectionsAPI;

import com.example.drivingbehaviour.DistanceMatrixAPI.Distance;
import com.example.drivingbehaviour.DistanceMatrixAPI.Duration;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Steps {

    @JsonProperty("distance")
    private Distance distance;

    @JsonProperty("duration")
    private Duration duration;

    @JsonProperty("end_location")
    private EndLocation end_location;

    @JsonProperty("html_instructions")
    private String html_instructions;

    @JsonProperty("maneuver")
    private String maneuver;

    @JsonProperty("polyline")
    private Polylines polyline;

    @JsonProperty("start_location")
    private StartLocation start_location;

    @JsonProperty("travel_mode")
    private String travel_mode;

    @JsonProperty("distance")
    public Distance getDistance() {
        return distance;
    }

    @JsonProperty("duration")
    public Duration getDuration() {
        return duration;
    }

    @JsonProperty("end_location")
    public EndLocation getEndLocation() {
        return end_location;
    }

    @JsonProperty("html_instructions")
    public String getHtmlInstructions() {
        return html_instructions;
    }

    @JsonProperty("maneuver")
    public String getManeuver() {
        return maneuver;
    }

    @JsonProperty("polyline")
    public Polylines getPolylines() {
        return polyline;
    }

    @JsonProperty("start_location")
    public StartLocation getStartLocation() {
        return start_location;
    }

    @JsonProperty("travel_mode")
    public String getTravel_mode() {
        return travel_mode;
    }
}

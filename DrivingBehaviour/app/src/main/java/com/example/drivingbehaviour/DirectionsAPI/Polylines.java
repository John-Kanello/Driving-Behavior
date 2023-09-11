package com.example.drivingbehaviour.DirectionsAPI;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Polylines {

    @JsonProperty("points")
    private String points;

    @JsonProperty("points")
    public String getPoints() {
        return points;
    }
}

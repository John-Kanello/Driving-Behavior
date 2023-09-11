package com.example.drivingbehaviour.DirectionsAPI;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class StartLocation {

    @JsonProperty("lat")
    private double lat;
    @JsonProperty("lng")
    private double lng;

    @JsonProperty("lat")
    public double getLat() {
        return lat;
    }
    @JsonProperty("lng")
    public double getLng() {
        return lng;
    }

}

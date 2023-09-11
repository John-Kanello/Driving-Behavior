package com.example.drivingbehaviour.ElevationAPI;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class LocationResults {

    @JsonProperty("elevation")
    private double elevation;

    @JsonProperty("elevation")
    public double getElevation(){
        return elevation;
    }

}

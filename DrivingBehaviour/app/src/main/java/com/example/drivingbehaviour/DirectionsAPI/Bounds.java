package com.example.drivingbehaviour.DirectionsAPI;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Bounds {

    @JsonProperty("northeast")
    private NorthEast northeast;

    @JsonProperty("southwest")
    private SouthWest southwest;

    @JsonProperty("northeast")
    public NorthEast getNorthEast() {
        return northeast;
    }

    @JsonProperty("southwest")
    public SouthWest getSouthWest() {
        return southwest;
    }
}

package com.example.drivingbehaviour.DirectionsAPI;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Routes {

    @JsonProperty("bounds")
    private Bounds bounds;

    @JsonProperty("legs")
    private List<Legs> legs;

    @JsonProperty("copyrights")
    private String copyrights;

    @JsonProperty("bounds")
    public Bounds getBounds() {
        return bounds;
    }

    @JsonProperty("copyrights")
    public String getCopyrights() {
        return copyrights;
    }

    @JsonProperty("legs")
    public List<Legs> getLegs() {
        return legs;
    }
}

package com.example.drivingbehaviour.DirectionsAPI;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class DirectionResponse {

    @JsonProperty("geocoded_waypoints")
    private List<Geocoded_Waypoints> geocoded_waypoints;

    @JsonProperty("routes")
    private List<Routes> routes;

    @JsonProperty("status")
    private String status;

    @JsonProperty("routes")
    public List<Routes> getRoutes() {
        return routes;
    }

    @JsonProperty("geocoded_waypoints")
    public List<Geocoded_Waypoints> getGeocoded_waypoints() {
        return geocoded_waypoints;
    }

    @JsonProperty("status")
    public String getStatus() {
        return status;
    }
}

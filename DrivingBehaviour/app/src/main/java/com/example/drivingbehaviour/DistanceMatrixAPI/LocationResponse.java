package com.example.drivingbehaviour.DistanceMatrixAPI;

import com.example.drivingbehaviour.ElevationAPI.LocationResults;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)

public class LocationResponse {

    @JsonProperty("results")
    private List<LocationResults> results = null;

    @JsonProperty("results")
    public List<LocationResults> getResults() {
        return results;
    }
}

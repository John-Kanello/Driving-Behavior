package com.example.drivingbehaviour.DirectionsAPI;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Geocoded_Waypoints {

    @JsonProperty("geocoder_status")
    private String geocoder_status;

    @JsonProperty("place_id")
    private String place_id;

    @JsonProperty("types")
    private List<String> types;

    @JsonProperty("geocoder_status")
    public String getGeocoder_status() {
        return geocoder_status;
    }

    @JsonProperty("place_id")
    public String getPlace_id() {
        return place_id;
    }

    @JsonProperty("types")
    public List<String> getTypes() {
        return types;
    }
}

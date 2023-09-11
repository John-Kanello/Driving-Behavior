package com.example.drivingbehaviour.DistanceMatrixAPI;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)

public class Element {
    @JsonProperty("distance")
    private Distance distance;
    @JsonProperty("duration")
    private Duration duration;
    @JsonProperty("duration_in_traffic")
    private DurationInTraffic duration_in_traffic;
    @JsonProperty("status")
    private String status;

    public Element(Distance distance, Duration duration, DurationInTraffic duration_in_traffic, String status) {
        this.distance = distance;
        this.duration = duration;
        this.duration_in_traffic = duration_in_traffic;
        this.status = status;
    }

    public Element() {
    }

    @JsonProperty("distance")
    public Distance getDistance() {
        return distance;
    }

    @JsonProperty("duration")
    public Duration getDuration() {
        return duration;
    }

    @JsonProperty("duration_in_traffic")
    public DurationInTraffic getDurationInTraffic() {
        return duration_in_traffic;
    }

    @JsonProperty("status")
    public String getStatus() {
        return status;
    }
}
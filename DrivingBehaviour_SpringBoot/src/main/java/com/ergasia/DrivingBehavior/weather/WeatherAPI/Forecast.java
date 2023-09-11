package com.ergasia.DrivingBehavior.weather.WeatherAPI;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Forecast {

    @JsonProperty("forecastday")
    private List<ForecastDay> forecastday;

    public List<ForecastDay> getForecastday() {
        return forecastday;
    }

    public void setForecastday(List<ForecastDay> forecastday) {
        this.forecastday = forecastday;
    }
}


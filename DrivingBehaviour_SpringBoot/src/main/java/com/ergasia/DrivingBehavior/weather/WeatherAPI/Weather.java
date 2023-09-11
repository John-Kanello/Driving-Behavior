package com.ergasia.DrivingBehavior.weather.WeatherAPI;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Weather {

    @JsonProperty("location")
    private Place place;
    @JsonProperty("current")
    private Current current;
    @JsonProperty("forecast")
    private Forecast forecast;

    public Weather() {
    }

    public Weather(Place place, Current current, Forecast forecast)
    {
        this.place = place;
        this.current = current;
        this.forecast = forecast;
    }

    public Place getPlace() {
        return place;
    }

    public Current getCurrent() {
        return current;
    }

    public Forecast getForecast() {
        return forecast;
    }
}

package com.ergasia.DrivingBehavior.weather.WeatherAPI;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Day {

    private double maxtemp_c;
    private double maxtemp_f;
    private double mintemp_d;
    private double mintemp_f;
    private double avgtemp_c;
    private double avgtemp_f;
    private double maxwind_mph;
    private double maxwind_kph;
    private double totalprecip_mm;
    private double totalprecip_in;
    private double avgvis_km;
    private double avgvis_miles;
    private double avghumidity;
    private int daily_will_it_rain;
    private int daily_chance_of_rain;
    private int daily_will_it_snow;
    private int daily_chance_of_snow;
    private List<String> condition;
    private double uv ;

}

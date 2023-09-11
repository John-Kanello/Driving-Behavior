package com.ergasia.DrivingBehavior.weather.WeatherAPI;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Astro {

     private String sunset;
     private String moonrise;
     private String moonset;
     private String moon_phase;
     private int moon_illumination;

}

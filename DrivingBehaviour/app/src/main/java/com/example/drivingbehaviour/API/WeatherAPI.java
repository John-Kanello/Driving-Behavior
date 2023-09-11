package com.example.drivingbehaviour.API;

import com.example.drivingbehaviour.HelperClasses.LatAndLong;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface WeatherAPI {


    @POST("weather")
    Call<Double> getWeather(

            @Body LatAndLong latAndLong
    );
}

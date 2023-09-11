package com.example.drivingbehaviour.Retrofit;

import com.example.drivingbehaviour.API.UserAPI;
import com.example.drivingbehaviour.API.WeatherAPI;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static final String BASE_URL = "http://192.168.1.73:8080/"; // IP address of device that runs Spring Boot

    private static RetrofitClient mInstance;
    private Retrofit retrofit;

    private RetrofitClient()
    {
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static synchronized RetrofitClient getInstance()
    {
        if(mInstance == null)
        {
            mInstance = new RetrofitClient();
        }

        return mInstance;
    }

    public UserAPI getUserAPI()
    {
        return retrofit.create(UserAPI.class);
    }

    public WeatherAPI getWeatherAPI(){return retrofit.create(WeatherAPI.class);}


}

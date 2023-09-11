package com.example.drivingbehaviour.API;

import com.example.drivingbehaviour.DistanceMatrixAPI.LocationResponse;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

// https://maps.googleapis.com/maps/api/elevation/json?locations=39.7391536%2C-104.9847034&key=YOUR_API_KEY

public interface AltitudeApiClient {

    @GET("maps/api/elevation/json")
    Call<LocationResponse> getAltitude(
            @QueryMap Map<String, String> parameters
    );
}

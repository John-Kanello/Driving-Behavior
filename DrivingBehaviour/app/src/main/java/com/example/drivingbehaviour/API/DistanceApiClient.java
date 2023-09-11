package com.example.drivingbehaviour.API;

import com.example.drivingbehaviour.DistanceMatrixAPI.DistanceResponse;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

// http://maps.googleapis.com/maps/api/distancematrix/json?destinations=Atlanta,GA|New+York,NY&origins=Orlando,FL&units=imperial

public interface DistanceApiClient {
    @GET("maps/api/distancematrix/json")
    Call<DistanceResponse> getDistanceInfo(
            @QueryMap Map<String, String> parameters
    );
}
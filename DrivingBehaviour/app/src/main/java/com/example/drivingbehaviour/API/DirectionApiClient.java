package com.example.drivingbehaviour.API;

import com.example.drivingbehaviour.DirectionsAPI.DirectionResponse;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

public interface DirectionApiClient {
    @GET("maps/api/directions/json")
    Call<DirectionResponse> getDirectionInfo(
            @QueryMap Map<String, String> parameters
            );
}

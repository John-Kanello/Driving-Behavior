package com.example.drivingbehaviour.API;

import com.example.drivingbehaviour.Classes.AverageDriveResults;
import com.example.drivingbehaviour.Classes.DriveResults;
import com.example.drivingbehaviour.HelperClasses.LatAndLong;
import com.example.drivingbehaviour.Classes.User;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;

public interface UserAPI
{

    @POST("users")
    Call<User> createUser(
            @Body User user
    );

    @POST("users/drive")
    Call<ResponseBody> addDriveResults(
            @Body DriveResults driveResults
    );

    @POST("users/averageDriveResults")
    Call<ResponseBody> updateAverageResults(
            @Body AverageDriveResults averageDriveResults
    );

    @POST("users/login")
    Call<User> checkUser(
            @Body User user
    );

    @PUT("users")
    Call<ResponseBody> updateUser(
            @Body User user
    );

    @POST("users/getInformation")
    Call<List<DriveResults>> getUserInformation(
            @Body Long id
            );

    @POST("users/getAverageInformation")
    Call<AverageDriveResults> getUserAverageInformation(
            @Body Long id
    );


    @GET("users/getGlobalAverageInformation")
    Call<AverageDriveResults> getGlobalAverageInformation(

    );

    @POST("dangerousLocations")
    Call<ResponseBody> saveDangerousLocation(
            @Body LatAndLong latAndLong
    );

}

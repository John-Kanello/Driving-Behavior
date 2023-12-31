package com.example.drivingbehaviour.Retrofit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RestUtil {
    private static RestUtil self;
    private String API_BASE_URL = "https://maps.googleapis.com/";
    private Retrofit retrofit;

    private RestUtil() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient httpClient = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        Retrofit.Builder builder =
                new Retrofit.Builder()
                        .baseUrl(API_BASE_URL)
//                        .addConverterFactory(JacksonConverterFactory.create());
                        .addConverterFactory(GsonConverterFactory.create());
        retrofit = builder.client(httpClient).build();
    }

    public static RestUtil getInstance() {
        if (self == null) {
            synchronized(RestUtil.class) {
                if (self == null) {
                    self = new RestUtil();
                }
            }
        }
        return self;
    }

    public Retrofit getRetrofit() {
        return retrofit;
    }

}
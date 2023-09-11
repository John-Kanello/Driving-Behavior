package com.ergasia.DrivingBehavior.weather.Controller;

import com.ergasia.DrivingBehavior.user.HelperClasses.LatAndLong;
import com.ergasia.DrivingBehavior.weather.WeatherAPI.Weather;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@RestController
public class WeatherController {

    @PostMapping("/weather")
    public double getWeather(@Valid @RequestBody LatAndLong latAndLong) {
        String url = "http://api.weatherapi.com/v1/forecast.json?key=6ab7944a41754c46b90105507221903&q="+
                latAndLong.getLatitude()+","+latAndLong.getLongitude()+"&days=1&aqi=no&alerts=no";

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();

        HttpResponse<String> response = null;
        try {
            response = client.send(request,
                    HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        ObjectMapper mapper = new ObjectMapper();
        Weather weather = null;
        try {
            if (response != null) {
                weather = mapper.readValue(response.body(), Weather.class);
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        assert weather != null;
        return weather.getCurrent().getTemp_c();
    }

}

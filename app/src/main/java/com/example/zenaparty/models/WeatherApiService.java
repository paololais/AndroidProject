package com.example.zenaparty.models;

import retrofit2.Call;
import retrofit2.http.GET;

public interface WeatherApiService {
    @GET("current?access_key=9a6fb4601b803613be5365e4f973d1fa&query=genoa")
    Call<WeatherResponse> getWeatherData();
}

package com.example.kornel.alphaui.weather;

public interface WeatherInfoListener {
    void gotWeatherInfo(WeatherInfo weatherInfo, Weather.ErrorType errorType);
}

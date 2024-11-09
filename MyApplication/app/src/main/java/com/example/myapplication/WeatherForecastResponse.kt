package com.example.myapplication


data class WeatherForecastResponse(
    val list: List<Forecast>
)

data class Forecast(
    val dt_txt: String, // 예보 시간
    val main: Main,
    val weather: List<Weather>
)

data class Main(
    val temp: Double
)

data class Weather(
    val description: String
)

package com.abs.huerto_hogar_appmovil.data.remote.dto

data class WeatherResponseDto(
    val name: String,
    val main: Main,
    val weather: List<WeatherDesc>
)

data class Main(
    val temp: Double
)

data class WeatherDesc(
    val description: String
)
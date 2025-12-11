package com.abs.huerto_hogar_appmovil.data.repository

import com.abs.huerto_hogar_appmovil.data.remote.api.WeatherApi
import com.abs.huerto_hogar_appmovil.data.remote.dto.WeatherResponseDto

class WeatherRepository(
    private val api: WeatherApi
) {

    // Por ahora lo dejas fijo. Más adelante, si quieres, lo sacamos a BuildConfig.
    private val apiKey = "add4b53f08ab891e9a458c1e8bbe6d10"

    suspend fun getWeatherByCoordinates(
        lat: Double,
        lon: Double
    ): WeatherResponseDto {
        val response = api.getWeatherByCoordinates(
            lat = lat,
            lon = lon,
            apiKey = apiKey
        )

        if (!response.isSuccessful || response.body() == null) {
            throw Exception("Error al obtener clima: ${response.code()}")
        }

        return response.body()!!
    }
}

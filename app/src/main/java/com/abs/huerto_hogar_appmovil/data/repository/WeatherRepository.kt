package com.abs.huerto_hogar_appmovil.data.repository

import com.abs.huerto_hogar_appmovil.data.remote.api.WeatherApi
import com.abs.huerto_hogar_appmovil.data.remote.dto.WeatherResponseDto

class WeatherRepository(
    private val api: WeatherApi
) {

    // Por ahora lo dejas fijo. Más adelante, si quieres, lo sacamos a BuildConfig.
    private val apiKey = "add4b53f08ab891e9a458c1e8bbe6d10"

    // 👉 Por coordenadas (si algún día usas mapa o GPS)
    suspend fun getWeatherByCoordinates(
        lat: Double,
        lon: Double
    ): WeatherResponseDto {
        val response = api.getWeatherByCoordinates(
            lat = lat,
            lon = lon,
            apiKey = apiKey  // el nombre del parámetro debe coincidir con el del @Query en WeatherApi
        )

        if (!response.isSuccessful || response.body() == null) {
            throw Exception("Error al obtener clima: ${response.code()}")
        }

        return response.body()!!
    }

    // 👉 Por ciudad/comuna
    // OJO: aquí YA NO agregamos ",CL", porque eso lo está haciendo el ViewModel
    // (le llega algo como "Maipu,CL" o "Valparaíso,CL")
    suspend fun obtenerClimaPorCiudad(city: String): WeatherResponseDto {
        val response = api.getCurrentWeather(
            cityName = city,   // ya viene con ",CL" desde el ViewModel
            apiKey = apiKey
        )

        if (!response.isSuccessful || response.body() == null) {
            throw Exception("Error al obtener clima: ${response.code()}")
        }

        return response.body()!!
    }
}

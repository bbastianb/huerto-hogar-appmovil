package com.abs.huerto_hogar_appmovil.data.repository

import com.abs.huerto_hogar_appmovil.data.remote.api.WeatherApi
import com.abs.huerto_hogar_appmovil.data.remote.dto.Main
import com.abs.huerto_hogar_appmovil.data.remote.dto.WeatherDesc
import com.abs.huerto_hogar_appmovil.data.remote.dto.WeatherResponseDto
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import retrofit2.Response

class WeatherRepositoryTest {

    @MockK
    private lateinit var mockWeatherApi: WeatherApi

    private lateinit var weatherRepository: WeatherRepository

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        weatherRepository = WeatherRepository(mockWeatherApi)
    }

    @Test
    fun getWeatherByCoordinatesretornadatoscorrectamente() = runTest {
        // Arrange
        val lat = -33.45694
        val lon = -70.64827
        val expectedWeather = WeatherResponseDto(
            name = "Santiago",
            main = Main(temp = 22.5),
            weather = listOf(WeatherDesc(description = "cielo despejado"))
        )

        coEvery {
            mockWeatherApi.getWeatherByCoordinates(
                lat = lat,
                lon = lon,
                apiKey = "add4b53f08ab891e9a458c1e8bbe6d10",
                units = "metric",
                lang = "es"
            )
        } returns Response.success(expectedWeather)

        // Act
        val result = weatherRepository.getWeatherByCoordinates(lat, lon)

        // Assert
        assertNotNull(result)
        assertEquals("Santiago", result.name)
        assertEquals(22.5, result.main.temp, 0.001)
        assertEquals("cielo despejado", result.weather.first().description)

        // Verificar que se llamó al método correctamente
        coVerify {
            mockWeatherApi.getWeatherByCoordinates(
                lat = lat,
                lon = lon,
                apiKey = "add4b53f08ab891e9a458c1e8bbe6d10",
                units = "metric",
                lang = "es"
            )
        }
    }

    @Test(expected = Exception::class)
    fun getWeatherByCoordinatesconerrorlanzaexcepcion() = runTest {
        // Arrange
        val lat = -33.45694
        val lon = -70.64827

        coEvery {
            mockWeatherApi.getWeatherByCoordinates(
                lat = lat,
                lon = lon,
                apiKey = any(),
                units = any(),
                lang = any()
            )
        } returns Response.error(500, okhttp3.ResponseBody.create(null, ""))

        // Act & Assert (debería lanzar excepción)
        weatherRepository.getWeatherByCoordinates(lat, lon)
    }
}
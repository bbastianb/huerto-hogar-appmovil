package com.abs.huerto_hogar_appmovil.ui.viewmodels

import com.abs.huerto_hogar_appmovil.data.repository.*
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class CheckoutViewModelTest {

    @MockK
    private lateinit var mockPedidoRepository: PedidoRepository

    @MockK
    private lateinit var mockCarritoRepository: CarritoRepository

    @MockK
    private lateinit var mockProductoRepository: ProductoRepository

    @MockK
    private lateinit var mockWeatherRepository: WeatherRepository

    private lateinit var viewModel: CheckoutViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(testDispatcher)

        // Configurar mocks básicos
        every { mockCarritoRepository.obtenerCarrito() } returns flowOf(emptyList())

        viewModel = CheckoutViewModel(
            pedidoRepository = mockPedidoRepository,
            carritoRepository = mockCarritoRepository,
            productoRepository = mockProductoRepository,
            weatherRepository = mockWeatherRepository
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun actualizarUbicacionactualizacoordenadascorrectamente() = runTest {
        // Arrange
        val lat = -33.45694
        val lon = -70.64827
        val direccion = "Calle Falsa 123"
        val comuna = "Santiago"
        val region = "Metropolitana"

        // Act
        viewModel.actualizarUbicacion(lat, lon, direccion, comuna, region)

        // Assert
        val info = viewModel.checkoutInfo.value
        assertEquals(lat, info.latitud)
        assertEquals(lon, info.longitud)
        assertEquals(direccion, info.direccion)
        assertEquals(comuna, info.comuna)
        assertEquals(region, info.region)
    }

    @Test
    fun validarCheckoutcondatoscompletospermiteprocesarpedido() = runTest {
        // Arrange
        val infoCompleto = CheckoutInfo(
            nombre = "Juan Pérez",
            email = "juan@email.com",
            telefono = "912345678",
            direccion = "Calle 123",
            comuna = "Santiago",
            region = "Metropolitana"
        )

        // Act
        viewModel.actualizarCheckoutInfo(infoCompleto)

        // Assert
        assertTrue(viewModel.puedeProcesarPedido.value)
        assertTrue(viewModel.erroresValidacion.value.isEmpty())
    }

    @Test
    fun validarCheckoutcondatosincompletosmuestraerrores() = runTest {
        // Arrange
        val infoIncompleto = CheckoutInfo(
            nombre = "Juan Pérez",
            email = "", // Email vacío
            telefono = "", // Teléfono vacío
            direccion = "Calle 123",
            comuna = "Santiago",
            region = "" // Región vacía
        )

        // Act
        viewModel.actualizarCheckoutInfo(infoIncompleto)

        // Assert
        assertFalse(viewModel.puedeProcesarPedido.value)
        val errores = viewModel.erroresValidacion.value
        assertTrue(errores.contains("Email"))
        assertTrue(errores.contains("Teléfono"))
        assertTrue(errores.contains("Región"))
    }
}
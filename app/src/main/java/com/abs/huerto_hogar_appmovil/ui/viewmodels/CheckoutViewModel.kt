package com.abs.huerto_hogar_appmovil.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abs.huerto_hogar_appmovil.data.model.Pedido
import com.abs.huerto_hogar_appmovil.data.model.PedidoItem
import com.abs.huerto_hogar_appmovil.data.remote.dto.WeatherResponseDto
import com.abs.huerto_hogar_appmovil.data.repository.CarritoRepository
import com.abs.huerto_hogar_appmovil.data.repository.PedidoRepository
import com.abs.huerto_hogar_appmovil.data.repository.ProductoRepository
import com.abs.huerto_hogar_appmovil.data.repository.UsuarioRepository
import com.abs.huerto_hogar_appmovil.data.repository.WeatherRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class CheckoutInfo(
    val nombre: String = "",
    val email: String = "",
    val telefono: String = "",
    val direccion: String = "",
    val comuna: String = "",
    val region: String = ""
)

class CheckoutViewModel(
    private val pedidoRepository: PedidoRepository,
    private val carritoRepository: CarritoRepository,
    private val productoRepository: ProductoRepository,
    private val weatherRepository: WeatherRepository
) : ViewModel() {

    // Datos de envío
    private val _checkoutInfo = MutableStateFlow(CheckoutInfo())
    val checkoutInfo: StateFlow<CheckoutInfo> = _checkoutInfo.asStateFlow()

    // Método de pago
    private val _metodoPago = MutableStateFlow("TARJETA_CREDITO")
    val metodoPago: StateFlow<String> = _metodoPago.asStateFlow()

    // Estados generales
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _mensaje = MutableStateFlow("")
    val mensaje: StateFlow<String> = _mensaje.asStateFlow()

    private val _ordenCreada = MutableStateFlow(false)
    val ordenCreada: StateFlow<Boolean> = _ordenCreada.asStateFlow()

    // Carrito
    val carritoItems = carritoRepository.obtenerCarrito()

    private val _subtotal = MutableStateFlow(0.0)
    val subtotal: StateFlow<Double> = _subtotal.asStateFlow()

    private val _totalPedido = MutableStateFlow(0.0)
    val totalPedido: StateFlow<Double> = _totalPedido.asStateFlow()

    // Validación
    private val _puedeProcesarPedido = MutableStateFlow(false)
    val puedeProcesarPedido: StateFlow<Boolean> = _puedeProcesarPedido.asStateFlow()

    private val _erroresValidacion = MutableStateFlow<List<String>>(emptyList())
    val erroresValidacion: StateFlow<List<String>> = _erroresValidacion.asStateFlow()

    // Clima
    private val _weatherInfo = MutableStateFlow<WeatherResponseDto?>(null)
    val weatherInfo: StateFlow<WeatherResponseDto?> = _weatherInfo.asStateFlow()

    private val _weatherError = MutableStateFlow<String?>(null)
    val weatherError: StateFlow<String?> = _weatherError.asStateFlow()

    private val _isWeatherLoading = MutableStateFlow(false)
    val isWeatherLoading: StateFlow<Boolean> = _isWeatherLoading.asStateFlow()

    init {
        // Escuchar cambios del carrito para recalcular subtotal/total
        viewModelScope.launch {
            carritoRepository.obtenerCarrito().collect { items ->
                val nuevoSubtotal = items.sumOf { it.producto.precio * it.carrito.cantidad }
                _subtotal.value = nuevoSubtotal
                _totalPedido.value = nuevoSubtotal // envío gratis por ahora
            }
        }

        // Validar al inicio
        validarCheckout(_checkoutInfo.value)
    }

    // ------------------------
    // UPDATE DE CAMPOS
    // ------------------------

    fun actualizarCheckoutInfo(nuevoInfo: CheckoutInfo) {
        _checkoutInfo.value = nuevoInfo
        validarCheckout(nuevoInfo)

        // Si hay comuna o región, intenta cargar clima
        if (nuevoInfo.comuna.isNotBlank() || nuevoInfo.region.isNotBlank()) {
            cargarClimaParaCheckout()
        }
    }

    fun actualizarMetodoPago(metodo: String) {
        _metodoPago.value = metodo
    }

    private fun validarCheckout(info: CheckoutInfo) {
        val errores = mutableListOf<String>()

        if (info.nombre.isBlank()) errores.add("Nombre")
        if (info.email.isBlank()) errores.add("Email")
        if (info.telefono.isBlank()) errores.add("Teléfono")
        if (info.direccion.isBlank()) errores.add("Dirección")
        if (info.comuna.isBlank()) errores.add("Comuna")
        if (info.region.isBlank()) errores.add("Región")

        _erroresValidacion.value = errores
        _puedeProcesarPedido.value = errores.isEmpty()
    }

    // ------------------------
    // CLIMA
    // ------------------------

    fun cargarClimaParaCheckout() {
        viewModelScope.launch {
            val info = _checkoutInfo.value

            if (info.comuna.isBlank() && info.region.isBlank()) {
                _weatherError.value = "Completa comuna o región para ver el clima"
                _weatherInfo.value = null
                return@launch
            }

            val ciudadQuery = if (info.comuna.isNotBlank()) {
                "${info.comuna},CL"
            } else {
                "${info.region},CL"
            }

            _isWeatherLoading.value = true
            try {
                val respuesta = weatherRepository.obtenerClimaPorCiudad(ciudadQuery)
                if (respuesta != null) {
                    _weatherInfo.value = respuesta
                    _weatherError.value = null
                } else {
                    _weatherError.value = "No se pudo obtener el clima"
                    _weatherInfo.value = null
                }
            } catch (e: Exception) {
                _weatherError.value = "Error al obtener el clima: ${e.message}"
                _weatherInfo.value = null
            } finally {
                _isWeatherLoading.value = false
            }
        }
    }

    // ------------------------
    // PEDIDO
    // ------------------------

    fun procesarPedido() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Revalidar antes de enviar
                validarCheckout(_checkoutInfo.value)
                if (!_puedeProcesarPedido.value) {
                    _mensaje.value = "Por favor completa: " +
                            _erroresValidacion.value.joinToString(", ")
                    return@launch
                }

                val items = carritoRepository.obtenerCarrito().first()
                if (items.isEmpty()) {
                    _mensaje.value = "El carrito está vacío"
                    return@launch
                }

                val info = _checkoutInfo.value

                // Crear pedido
                val pedido = Pedido(
                    total = _totalPedido.value,
                    metodoPago = _metodoPago.value,
                    direccionEnvio = info.direccion,
                    comunaEnvio = info.comuna,
                    regionEnvio = info.region,
                    telefonoContacto = info.telefono,
                    correoContacto = info.email
                )

                val pedidoItems = items.map { carritoItem ->
                    PedidoItem(
                        pedidoId = pedido.id, // si Room lo genera, solo lo usas para relación local
                        productoId = carritoItem.producto.id,
                        nombreProducto = carritoItem.producto.nombre,
                        precioUnitario = carritoItem.producto.precio,
                        cantidad = carritoItem.carrito.cantidad,
                        subtotal = carritoItem.producto.precio * carritoItem.carrito.cantidad
                    )
                }

                // Usuario autenticado
                val usuarioId = UsuarioRepository.idActual ?: 0L

                // Crear pedido en backend y guardar local
                pedidoRepository.crearPedidoEnBackendYLocal(
                    pedido = pedido,
                    items = pedidoItems,
                    usuarioId = usuarioId
                )

                // Actualizar stock local
                items.forEach { item ->
                    val nuevoStock = item.producto.stock - item.carrito.cantidad
                    if (nuevoStock >= 0) {
                        productoRepository.actualizarStock(item.producto.id, nuevoStock)
                    }
                }

                // Vaciar carrito
                carritoRepository.vaciarCarrito()

                _mensaje.value = "Pedido creado exitosamente"
                _ordenCreada.value = true

            } catch (e: Exception) {
                _mensaje.value =
                    "Error al crear el pedido: ${e.localizedMessage ?: "desconocido"}"
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun limpiarMensaje() {
        _mensaje.value = ""
    }
}

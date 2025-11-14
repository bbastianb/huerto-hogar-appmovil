package com.abs.huerto_hogar_appmovil.ui.viewmodels


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abs.huerto_hogar_appmovil.data.local.dao.CarritoDao
import com.abs.huerto_hogar_appmovil.data.repository.CarritoRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CartViewModel(private val repository: CarritoRepository) : ViewModel() {


    private val _carritoItems = MutableStateFlow<List<CarritoDao.CarritoConProducto>>(emptyList())
    val carritoItems: StateFlow<List<CarritoDao.CarritoConProducto>> = _carritoItems.asStateFlow()

    private val _totalItems = MutableStateFlow(0)
    val totalItems: StateFlow<Int> = _totalItems.asStateFlow()

    private val _totalPrecio = MutableStateFlow(0.0)
    val totalPrecio: StateFlow<Double> = _totalPrecio.asStateFlow()

    private val _mensaje = MutableStateFlow("")
    val mensaje: StateFlow<String> = _mensaje.asStateFlow()

    // INICIALIZACIÓN
    init {
        observarCarrito()
        observarTotales()
    }

    private fun observarCarrito() {
        viewModelScope.launch {
            repository.obtenerCarrito().collect { items ->
                _carritoItems.value = items
            }
        }
    }

    private fun observarTotales() {
        viewModelScope.launch {
            repository.obtenerTotalItems().collect { total ->
                _totalItems.value = total
            }
        }

        viewModelScope.launch {
            repository.calcularTotalCarrito().collect { total ->
                _totalPrecio.value = total
            }
        }
    }
    fun agregarAlCarrito(productoId: String, cantidad: Int) {
        viewModelScope.launch {
            println("🛒 Agregando producto $productoId al carrito, cantidad: $cantidad")
            // Por ahora, agregamos múltiples veces
            var agregadosExitosos = 0
            repeat(cantidad) {
                val exito = repository.agregarAlCarrito(productoId, 1)
                if (exito) agregadosExitosos++
            }

            if (agregadosExitosos == cantidad) {
                _mensaje.value = "✅ Producto agregado al carrito (x$cantidad)"
            } else if (agregadosExitosos > 0) {
                _mensaje.value = "⚠️ Se agregaron $agregadosExitosos de $cantidad unidades"
            } else {
                _mensaje.value = "❌ Error al agregar el producto"
            }

            launch {
                delay(3000)
                _mensaje.value = ""
            }
        }
    }

    fun actualizarCantidad(itemId: Long, nuevaCantidad: Int) {
        viewModelScope.launch {
            repository.actualizarCantidad(itemId, nuevaCantidad)
        }
    }

    fun eliminarDelCarrito(itemId: Long) {
        viewModelScope.launch {
            repository.eliminarDelCarrito(itemId)
        }
    }

    fun vaciarCarrito() {
        viewModelScope.launch {
            repository.vaciarCarrito()
        }
    }
}
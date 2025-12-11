package com.abs.huerto_hogar_appmovil.ui.viewmodels

import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abs.huerto_hogar_appmovil.data.model.Producto
import com.abs.huerto_hogar_appmovil.data.repository.CarritoRepository
import com.abs.huerto_hogar_appmovil.data.repository.ProductoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CatalogoViewModel(
    private val productoRepository: ProductoRepository,
    private val carritoRepository: CarritoRepository
) : ViewModel() {

    private val _productos = MutableStateFlow<List<Producto>>(emptyList())
    val productos: StateFlow<List<Producto>> = _productos.asStateFlow()

    private val _categoria = MutableStateFlow("Todas")
    val categoria: StateFlow<String> = _categoria.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        // Usa tus propias funciones
        cargarProductosIniciales()
        observarProductos()
    }

    fun agregarAlCarrito(productoId: String) {
        viewModelScope.launch {
            val exito = carritoRepository.agregarAlCarrito(productoId, 1)
            if (exito) {
                println("Producto agregado al carrito")
            } else {
                println("Stock insuficiente")
            }
        }
    }

    fun onCategoriaChange(nuevaCategoria: String) {
        _categoria.value = nuevaCategoria
        cargarProductosPorCategoria(nuevaCategoria)
    }

    private fun cargarProductosIniciales() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                productoRepository.cargarProductosIniciales()
            } catch (e: Exception) {
                println("Error al cargar productos iniciales: ${e.message}")
                // igual dejamos que observarProductos ponga isLoading en false
            }
        }
    }

    private fun observarProductos() {
        viewModelScope.launch {
            try {
                productoRepository.obtenerTodos().collect { listaProductos ->
                    _productos.value = listaProductos
                    _isLoading.value = false   // ✅ cuando ya tenemos datos, apagamos el loading
                }
            } catch (e: Exception) {
                println("Error al cargar productos: ${e.message}")
                _isLoading.value = false     // para que no se quede pegado en loading
            }
        }
    }

    private fun cargarProductosPorCategoria(categoria: String) {
        viewModelScope.launch {
            try {
                if (categoria == "Todas") {
                    productoRepository.obtenerTodos().collect { _productos.value = it }
                } else {
                    productoRepository.obtenerPorCategoria(categoria).collect { _productos.value = it }
                }
            } catch (e: Exception) {
                println("Error al cargar productos por categoría: ${e.message}")
            }
        }
    }

    fun onBuscarChange(query: String) {
        viewModelScope.launch {
            try {
                if (query.length >= 2) {
                    productoRepository.buscarPorNombre(query).collect { _productos.value = it }
                } else if (query.isEmpty()) {
                    cargarProductosPorCategoria(_categoria.value)
                }
            } catch (e: Exception) {
                println("Error al buscar productos: ${e.message}")
            }
        }
    }
}

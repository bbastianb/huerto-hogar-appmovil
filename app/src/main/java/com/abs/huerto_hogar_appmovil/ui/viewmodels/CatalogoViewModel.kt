package com.abs.huerto_hogar_appmovil.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abs.huerto_hogar_appmovil.data.model.Producto
import com.abs.huerto_hogar_appmovil.data.repository.CarritoRepository
import com.abs.huerto_hogar_appmovil.data.repository.ProductoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CatalogoViewModel(private val repository: ProductoRepository,private val carritoRepository: CarritoRepository) : ViewModel() {

    private val _productos = MutableStateFlow<List<Producto>>(emptyList())
    val productos: StateFlow<List<Producto>> = _productos.asStateFlow()

    private val _categoria= MutableStateFlow("Todas")
    val categoria: StateFlow<String> = _categoria.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun agregarAlCarrito(productoId: String) {
        viewModelScope.launch {
            println("🛒 Agregando producto $productoId al carrito")
            val exito = carritoRepository.agregarAlCarrito(productoId, 1)
            if (exito) {
                println(" Producto agregado al carrito")
            } else {
                println(" Error al agregar al carrito - Stock insuficiente")
            }
        }
    }

    fun onCategoriaChange(nuevaCategoria: String) {
        _categoria.value = nuevaCategoria
        cargarProductosPorCategoria(nuevaCategoria)
    }

    init {
        cargarProductosIniciales()
        observarProductos()
    }

    private fun cargarProductosIniciales() {
        viewModelScope.launch {
            repository.cargarProductosIniciales()
        }
    }

    private fun observarProductos() {
        viewModelScope.launch {
            repository.obtenerTodos().collect { listaProductos ->
                _productos.value = listaProductos
                _isLoading.value = false
            }
        }
    }

    private fun cargarProductosPorCategoria(categoria: String) {
        viewModelScope.launch {
            if (categoria == "Todas") {
                repository.obtenerTodos().collect {
                    _productos.value = it
                }
            } else {
                repository.obtenerPorCategoria(categoria).collect {
                    _productos.value = it
                }
            }
        }
    }

    fun onBuscarChange(query: String) {
        viewModelScope.launch {
            if (query.length >= 2) {
                repository.buscarPorNombre(query).collect {
                    _productos.value = it
                }
            } else if (query.isEmpty()) {
                cargarProductosPorCategoria(_categoria.value)
            }
        }
    }
}

// DetalleProductoViewModel.kt
package com.abs.huerto_hogar_appmovil.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abs.huerto_hogar_appmovil.data.model.Producto
import com.abs.huerto_hogar_appmovil.data.repository.ProductoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DetalleProductoViewModel(
    private val productoRepository: ProductoRepository
) : ViewModel() {

    private val _producto = MutableStateFlow<Producto?>(null)
    val producto: StateFlow<Producto?> = _producto.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _mensaje = MutableStateFlow("")
    val mensaje: StateFlow<String> = _mensaje.asStateFlow()

    fun cargarProducto(productoId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val productoEncontrado = productoRepository.obtenerPorId(productoId)
                _producto.value = productoEncontrado
            } catch (e: Exception) {
                _mensaje.value = "Error al cargar el producto"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
package com.abs.huerto_hogar_appmovil.ui.viewmodels.adminVM

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abs.huerto_hogar_appmovil.data.model.Producto
import com.abs.huerto_hogar_appmovil.data.repository.ProductoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AdminViewModel(private val repository: ProductoRepository) : ViewModel() {

    private val _productos = MutableStateFlow<List<Producto>>(emptyList())
    val productos: StateFlow<List<Producto>> = _productos

    init {
        viewModelScope.launch {
            repository.obtenerTodos().collect { lista ->
                _productos.value = lista
            }
        }
    }

    // Ejemplo de actualización
    fun actualizarStock(id: String, cantidad: Int) {
        viewModelScope.launch {
            repository.actualizarStock(id, cantidad)
        }
    }
}
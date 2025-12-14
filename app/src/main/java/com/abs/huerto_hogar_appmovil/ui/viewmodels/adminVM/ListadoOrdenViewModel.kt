package com.abs.huerto_hogar_appmovil.ui.viewmodels.adminVM

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abs.huerto_hogar_appmovil.data.remote.dto.OrdenResponseDto
import com.abs.huerto_hogar_appmovil.data.repository.OrdenAdminRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ListadoOrdenViewModel(
    private val repo: OrdenAdminRepository = OrdenAdminRepository()
) : ViewModel() {

    private val _ordenes = MutableStateFlow<List<OrdenResponseDto>>(emptyList())
    val ordenes: StateFlow<List<OrdenResponseDto>> = _ordenes

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _textoBusqueda = MutableStateFlow("")
    val textoBusqueda: StateFlow<String> = _textoBusqueda

    private val _mostrarDialogo = MutableStateFlow(false)
    val mostrarDialogo: StateFlow<Boolean> = _mostrarDialogo

    private val _ordenParaEliminar = MutableStateFlow<OrdenResponseDto?>(null)
    val ordenParaEliminar: StateFlow<OrdenResponseDto?> = _ordenParaEliminar

    init { cargarOrdenes() }

    fun actualizarTextoBusqueda(t: String) { _textoBusqueda.value = t }
    fun clearError() { _errorMessage.value = null }

    fun cargarOrdenes() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                _ordenes.value = repo.listarOrdenes()
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Error cargando órdenes"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun abrirDialogoEliminar(orden: OrdenResponseDto) {
        _ordenParaEliminar.value = orden
        _mostrarDialogo.value = true
    }

    fun cerrarDialogo() {
        _mostrarDialogo.value = false
        _ordenParaEliminar.value = null
    }

    fun confirmarEliminacion() {
        val orden = _ordenParaEliminar.value ?: return
        val id = orden.idOrden

        viewModelScope.launch {
            try {
                repo.eliminarOrden(id)
                _ordenes.update { it.filterNot { o -> o.idOrden == id } }
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Error eliminando orden"
            } finally {
                cerrarDialogo()
            }
        }
    }

    fun cambiarEstado(orden: OrdenResponseDto, nuevoEstado: String) {
        val id = orden.idOrden
        viewModelScope.launch {
            try {
                val updated = repo.actualizarEstado(id, nuevoEstado)
                _ordenes.update { list -> list.map { if (it.idOrden == id) updated else it } }
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Error cambiando estado"
            }
        }
    }
}

package com.abs.huerto_hogar_appmovil.ui.viewmodels.adminVM

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abs.huerto_hogar_appmovil.data.repository.PedidoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AdminViewModel(
    private val pedidoRepository: PedidoRepository
) : ViewModel(){
    private val _uiState = MutableStateFlow(AdminUiState())
    val uiState: StateFlow<AdminUiState> = _uiState

    init {
        cargarOrdenesBackend()
    }

    private fun cargarOrdenesBackend(){
        viewModelScope.launch {
            try {
                val ordenes = pedidoRepository.obtenerOrdenesDesdeBackend()
                _uiState.value = _uiState.value.copy(
                    ordenesBackend = ordenes,
                    totalOrdenes = ordenes.size,
                    isLoading = false,
                    error = null
                )
            }catch (e: Exception){
                _uiState.value=_uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }
}

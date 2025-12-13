package com.abs.huerto_hogar_appmovil.ui.viewmodels.adminVM

import com.abs.huerto_hogar_appmovil.data.remote.dto.OrdenResponseDto

data class AdminUiState(
    val totalOrdenes: Int = 0,
    val ordenesBackend: List<OrdenResponseDto> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

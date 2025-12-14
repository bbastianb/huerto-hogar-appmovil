package com.abs.huerto_hogar_appmovil.data.repository

import com.abs.huerto_hogar_appmovil.data.remote.dto.OrdenResponseDto

interface AdminOrdenesSource {
    suspend fun obtenerOrdenesDesdeBackend(): List<OrdenResponseDto>

}
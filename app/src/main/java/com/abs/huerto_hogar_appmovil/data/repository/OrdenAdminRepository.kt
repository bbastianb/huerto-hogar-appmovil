package com.abs.huerto_hogar_appmovil.data.repository

import com.abs.huerto_hogar_appmovil.data.remote.RetrofitOrden
import com.abs.huerto_hogar_appmovil.data.remote.dto.EstadoEnvioBody
import com.abs.huerto_hogar_appmovil.data.remote.dto.OrdenResponseDto

class OrdenAdminRepository {

    private val api = RetrofitOrden.apiOrden

    private fun bearer(): String {
        val token = UsuarioRepository.tokenActual ?: throw Exception("No autenticado (sin token)")
        return "Bearer $token"
    }

    suspend fun listarOrdenes(): List<OrdenResponseDto> {
        val resp = api.listarOrdenes(bearer())
        if (!resp.isSuccessful) throw Exception("HTTP ${resp.code()} - ${resp.errorBody()?.string()}")
        return resp.body() ?: emptyList()
    }

    suspend fun actualizarEstado(idOrden: Long, nuevoEstado: String): OrdenResponseDto {
        val resp = api.actualizarEstadoEnvio(bearer(), idOrden, EstadoEnvioBody(nuevoEstado))
        if (!resp.isSuccessful) throw Exception("HTTP ${resp.code()} - ${resp.errorBody()?.string()}")
        return resp.body() ?: throw Exception("Respuesta vacía")
    }

    suspend fun eliminarOrden(idOrden: Long) {
        val resp = api.eliminarOrden(bearer(), idOrden)
        if (!resp.isSuccessful) throw Exception("HTTP ${resp.code()} - ${resp.errorBody()?.string()}")
    }
}

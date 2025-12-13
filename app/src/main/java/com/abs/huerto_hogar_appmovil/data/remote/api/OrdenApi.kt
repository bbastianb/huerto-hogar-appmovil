package com.abs.huerto_hogar_appmovil.data.remote.api

import com.abs.huerto_hogar_appmovil.data.remote.dto.EstadoEnvioBody
import com.abs.huerto_hogar_appmovil.data.remote.dto.OrdenRequestDto
import com.abs.huerto_hogar_appmovil.data.remote.dto.OrdenResponseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface OrdenApi {

    @POST("api/orden/guardar")
    suspend fun crearOrden(
        @Header("Authorization") authHeader: String,
        @Body orden: OrdenRequestDto
    ): Response<OrdenResponseDto>

    @GET("api/orden")
    suspend fun listarOrdenes(
        @Header("Authorization") authHeader: String
    ): Response<List<OrdenResponseDto>>

    @GET("api/orden/buscar/{id}")
    suspend fun buscarOrden(
        @Header("Authorization") authHeader: String,
        @Path("id") id: Long
    ): Response<OrdenResponseDto>


    @PUT("api/orden/actualizar-estado/{idOrden}")
    suspend fun actualizarEstadoEnvio(
        @Header("Authorization") authHeader: String,
        @Path("idOrden") idOrden: Long,
        @Body body: EstadoEnvioBody
    ): Response<OrdenResponseDto>

    @DELETE("api/orden/eliminar/{idOrden}")
    suspend fun eliminarOrden(
        @Header("Authorization") authHeader: String,
        @Path("idOrden") idOrden: Long
    ): Response<OrdenResponseDto>
}

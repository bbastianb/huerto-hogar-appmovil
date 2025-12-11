package com.abs.huerto_hogar_appmovil.data.remote.api

import com.abs.huerto_hogar_appmovil.data.remote.dto.OrdenRequestDto
import com.abs.huerto_hogar_appmovil.data.remote.dto.OrdenResponseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface OrdenApi {

    @POST("api/orden/guardar")
    suspend fun crearOrden(
        @Header("Authorization") authHeader: String,
        @Body orden: OrdenRequestDto
    ): Response<OrdenResponseDto>

    @GET("api/orden")
    suspend fun listarOrdenes(): Response<List<OrdenResponseDto>>

    @GET("api/orden/buscar/{id}")
    suspend fun buscarOrden(
        @Path("id") id: Long
    ): Response<OrdenResponseDto>
}

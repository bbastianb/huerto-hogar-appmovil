package com.abs.huerto_hogar_appmovil.data.remote.api

import com.abs.huerto_hogar_appmovil.data.remote.dto.ProductoDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ProductoApi{
    @GET("api/productos")
    suspend fun listarProductos(): Response<List<ProductoDto>>

    @GET("api/productos/buscar/{id}")
    suspend fun buscarProductoPorId(
        @Path("id") id: String): Response<ProductoDto>
}
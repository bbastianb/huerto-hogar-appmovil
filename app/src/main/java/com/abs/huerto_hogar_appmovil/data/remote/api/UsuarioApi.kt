package com.abs.huerto_hogar_appmovil.data.remote.api

import com.abs.huerto_hogar_appmovil.data.model.Usuario
import com.abs.huerto_hogar_appmovil.data.remote.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface UsuarioApi {

    @POST("api/usuario/login")
    suspend fun login(
        @Body solicitudLogin: SolicitudLogin
    ): Response<RespuestaLogin>

    @POST("api/usuario/guardar")
    suspend fun registrar(
        @Body usuario: Usuario
    ): Response<Usuario>



    @GET("api/usuario")
    suspend fun listarUsuarios(
        @Header("Authorization") authHeader: String
    ): Response<List<Usuario>>

    @DELETE("api/usuario/eliminar/{id}")
    suspend fun eliminarUsuario(
        @Header("Authorization") authHeader: String,
        @Path("id") id: Long
    ): Response<Unit>
}

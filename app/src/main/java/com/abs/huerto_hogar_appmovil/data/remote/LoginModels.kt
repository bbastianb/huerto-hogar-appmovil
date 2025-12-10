package com.abs.huerto_hogar_appmovil.data.remote

import com.abs.huerto_hogar_appmovil.data.model.Usuario

// email + contraseña
data class SolicitudLogin(
    val email: String,
    val contrasenna: String
)

// token + usuario
data class RespuestaLogin(
    val token: String,
    val usuario: Usuario
)

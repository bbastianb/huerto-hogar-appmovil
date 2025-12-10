package com.abs.huerto_hogar_appmovil.data.repository

import com.abs.huerto_hogar_appmovil.data.model.Usuario
import com.abs.huerto_hogar_appmovil.data.remote.RetrofitClient
import com.abs.huerto_hogar_appmovil.data.remote.SolicitudLogin

class UsuarioRepository {

    private val api = RetrofitClient.usuarioApi

    companion object {
        var tokenActual: String? = null
            private set

        var rolActual: String? = null
            private set

        var emailActual: String? = null
            private set

        private var ultimoErrorRegistro: String? = null

    }

    suspend fun login(email: String, contrasenna: String): Usuario? {
        val solicitud = SolicitudLogin(
            email = email,
            contrasenna = contrasenna
        )

        val response = api.login(solicitud)

        if (!response.isSuccessful) {
            val errorBody = response.errorBody()?.string()
            throw Exception("HTTP ${response.code()} - ${errorBody ?: "sin cuerpo de error"}")
        }

        val body = response.body()
            ?: throw Exception("Respuesta vacía del servidor")

        tokenActual = body.token
        rolActual = body.usuario.rol
        emailActual = body.usuario.email

        return body.usuario
    }

    suspend fun registrar(usuario: Usuario): Boolean {
        return try {
            val response = api.registrar(usuario)

            if (response.isSuccessful) {
                ultimoErrorRegistro = null
                true
            } else {
                val bodyError = response.errorBody()?.string()
                ultimoErrorRegistro = "Error ${response.code()}: ${bodyError ?: "sin detalle"}"
                false
            }
        } catch (e: Exception) {
            ultimoErrorRegistro = "Excepción: ${e.message}"
            false
        }
    }

    fun obtenerUltimoErrorRegistro(): String? = ultimoErrorRegistro



    suspend fun obtenerUsuariosAdmin(): List<Usuario> {
        val token = tokenActual ?: return emptyList()

        return try {
            val response = api.listarUsuarios("Bearer $token")
            if (response.isSuccessful) {
                response.body() ?: emptyList()
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun eliminarUsuarioAdmin(idUsuario: Long): Boolean {
        val token = tokenActual ?: return false

        return try {
            val response = api.eliminarUsuario("Bearer $token", idUsuario)
            response.isSuccessful
        } catch (e: Exception) {
            false
        }
    }

    fun obtenerRolActual(): String? = rolActual
}

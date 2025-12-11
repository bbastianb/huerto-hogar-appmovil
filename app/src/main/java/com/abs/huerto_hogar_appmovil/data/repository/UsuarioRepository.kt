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

        var idActual: Long? = null
            private set

        var usuarioActual: Usuario? = null
            private set
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
        idActual = body.usuario.id
        usuarioActual = body.usuario

        return body.usuario
    }

    suspend fun registrar(usuario: Usuario): Boolean {
        return try {
            val response = api.registrar(usuario)
            response.isSuccessful
        } catch (e: Exception) {
            false
        }
    }

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
    suspend fun actualizarPerfilUsuario(usuarioActualizado: Usuario): Usuario{
        val token = tokenActual ?: throw Exception("Usuario no autenticado")
        val id = idActual ?: throw Exception ("ID de usuario no disponible")

        val response = api.actualizarUsuario(
            authHeader = "Bearer $token",
            id = id,
            usuarioActualizado = usuarioActualizado
        )
        if (!response.isSuccessful) {
            val errorBody = response.errorBody()?.string()
            throw Exception("HTTP ${response.code()} - ${errorBody ?: "error al actualizar usuario"}")
        }
        val body = response.body() ?: throw Exception("Respuesta vacia del servidor")

        emailActual= body.email
        rolActual = body.rol
        idActual = body.id
        usuarioActual =body
        return body
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

    fun cerrarSesion(){
        tokenActual = null
        rolActual =null
        emailActual = null
        idActual =null
        usuarioActual = null
    }

    fun obtenerRolActual(): String? = rolActual
    fun obtenerUsuarioActual(): Usuario? = usuarioActual



}

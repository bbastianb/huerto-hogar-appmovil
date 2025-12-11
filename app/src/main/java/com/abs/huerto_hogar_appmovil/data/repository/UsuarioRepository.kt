package com.abs.huerto_hogar_appmovil.data.repository

import com.abs.huerto_hogar_appmovil.data.model.Usuario
import com.abs.huerto_hogar_appmovil.data.remote.RetrofitClient
import com.abs.huerto_hogar_appmovil.data.remote.SolicitudLogin
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.HttpException
import java.io.File

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
        idActual = body.usuario.id
        usuarioActual = body.usuario

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

    suspend fun registrarConFotoOpcional(
        usuario: Usuario,
        fotoFile: File
    ): Boolean {
        return try {
            ultimoErrorRegistro = null

            val responseRegistro = api.registrar(usuario)

            if (!responseRegistro.isSuccessful || responseRegistro.body() == null) {
                val errorBody = responseRegistro.errorBody()?.string()
                ultimoErrorRegistro =
                    errorBody ?: "Error al registrar: código ${responseRegistro.code()}"
                return false
            }

            val usuarioCreado = responseRegistro.body()!!
            val idUsuario = usuarioCreado.id
                ?: run {
                    ultimoErrorRegistro = "El usuario creado no devolvió un ID"
                    return false
                }

            val requestFile = fotoFile
                .asRequestBody("image/*".toMediaTypeOrNull())

            val multipartBody = MultipartBody.Part.createFormData(
                name = "foto",
                filename = fotoFile.name,
                body = requestFile
            )

            val responseFoto = api.actualizarFotoPerfil(idUsuario, multipartBody)

            if (!responseFoto.isSuccessful || responseFoto.body() == null) {
                val errorBodyFoto = responseFoto.errorBody()?.string()
                ultimoErrorRegistro =
                    errorBodyFoto ?: "Error al subir foto: código ${responseFoto.code()}"
                return false
            }

            true

        } catch (e: HttpException) {
            ultimoErrorRegistro = "Error HTTP: ${e.code()} ${e.message()}"
            false
        } catch (e: Exception) {
            ultimoErrorRegistro = e.message
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

    suspend fun actualizarPerfilUsuario(usuarioActualizado: Usuario): Usuario {
        val token = tokenActual ?: throw Exception("Usuario no autenticado")
        val id = idActual ?: throw Exception("ID de usuario no disponible")

        val response = api.actualizarUsuario(
            authHeader = "Bearer $token",
            id = id,
            usuarioActualizado = usuarioActualizado
        )

        if (!response.isSuccessful) {
            val errorBody = response.errorBody()?.string()
            throw Exception("HTTP ${response.code()} - ${errorBody ?: "error al actualizar usuario"}")
        }

        val body = response.body() ?: throw Exception("Respuesta vacía del servidor")

        emailActual = body.email
        rolActual = body.rol
        idActual = body.id
        usuarioActual = body

        return body
    }

    suspend fun actualizarFotoPerfilActual(fotoFile: File): Usuario {
        val id = idActual ?: throw Exception("ID de usuario no disponible")

        val requestFile = fotoFile
            .asRequestBody("image/*".toMediaTypeOrNull())

        val multipartBody = MultipartBody.Part.createFormData(
            name = "foto",
            filename = fotoFile.name,
            body = requestFile
        )

        val response = api.actualizarFotoPerfil(id, multipartBody)

        if (!response.isSuccessful) {
            val errorBody = response.errorBody()?.string()
            throw Exception("HTTP ${response.code()} - ${errorBody ?: "error al actualizar foto de perfil"}")
        }

        val body = response.body() ?: throw Exception("Respuesta vacía del servidor")

        usuarioActual = body

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

    fun cerrarSesion() {
        tokenActual = null
        rolActual = null
        emailActual = null
        idActual = null
        usuarioActual = null
    }

    fun obtenerRolActual(): String? = rolActual
    fun obtenerUsuarioActual(): Usuario? = usuarioActual
}

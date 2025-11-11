package com.abs.huerto_hogar_appmovil.data.repository

import com.abs.huerto_hogar_appmovil.data.model.Usuario

class UsuarioRepository {

    /* Mi lista con usuarios simulados */
    private val usuariosRegistrados = mutableListOf<Usuario>()

    init {
        usuariosRegistrados.add(
            Usuario(
                nombre = "Juan",
                apellido = "Pérez",
                correo = "juan@duoc.cl",
                contrasenna = "1234",
                fono = 912345678,
                direccion = "Av. Principal 123",
                comuna = "Santiago",
                region = "Metropolitana",
                rol = "usuario"
            )
        )
        usuariosRegistrados.add(
            Usuario(
                nombre = "María",
                apellido = "González",
                correo = "maria@gmail.com",
                contrasenna = "abcd",
                fono = 987654321,
                direccion = "Calle Secundaria 456",
                comuna = "Providencia",
                region = "Metropolitana",
                rol = "usuario"
            )
        )
    }

    suspend fun registrarUsuario(usuario: Usuario): Boolean {
        return try {
            /* Busca un usuario y setea al usuarioExistente */
            val usuarioExistente = usuariosRegistrados.find { it.correo == usuario.correo }
            /* Este valida si el usuario no retorno null existe/no existe */
            if (usuarioExistente != null) {
                return false /* Si retorna false el email esta registrado */
            } else {
                /* Se agrega a mi lista */
                usuariosRegistrados.add(usuario)
                return true
            }

        } catch (e: Exception){
            false
        }
    }

    suspend fun login(correo : String , contrasenna : String ) : Usuario?{
        return usuariosRegistrados.find {
            /* Si no cumple retorna null,  si cumple  me retorna el email y la contra */
            it.correo== correo && it.contrasenna == contrasenna
        }
    }

    suspend fun obtenerTodosLosUsuarios(): List<Usuario> {
        return usuariosRegistrados.toList()
    }


    suspend fun eliminarUsuario(correo: String): Boolean {
        return try {
            val usuarioExistente = usuariosRegistrados.find { it.correo == correo }
            if (usuarioExistente != null) {
                usuariosRegistrados.remove(usuarioExistente)
                true
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }


    fun obtenerTotalUsuarios(): Int {
        return usuariosRegistrados.size
    }

}
package com.abs.huerto_hogar_appmovil.data.repository

import com.abs.huerto_hogar_appmovil.model.Usuario

class UsuarioRepository {

    /* Mi lista con usuarios simulados */
    private val usuariosRegistrados = mutableListOf<Usuario>()

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

    /* Obtiene el tamaño de la lista */
    fun obtenerTotalUsuarios(): Int {
        return usuariosRegistrados.size
    }

}
package com.abs.huerto_hogar_appmovil.data.repository

import com.abs.huerto_hogar_appmovil.data.model.MensajeContacto


object ContactoRepository {
    fun enviarMensaje(mensaje: MensajeContacto): Boolean {
        println("Mensaje enviado: ${mensaje.nombre} - ${mensaje.email}")
        return true
    }
}

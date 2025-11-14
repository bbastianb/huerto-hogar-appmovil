package com.abs.huerto_hogar_appmovil.ui.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.abs.huerto_hogar_appmovil.data.model.MensajeContacto
import com.abs.huerto_hogar_appmovil.data.repository.ContactoRepository


class ContactoViewModel : ViewModel() {
    var enviado = mutableStateOf(false)
        private set

    fun enviar(nombre: String, email: String, mensaje: String) {
        val msg = MensajeContacto(nombre, email, mensaje)
        enviado.value = ContactoRepository.enviarMensaje(msg)
    }
}

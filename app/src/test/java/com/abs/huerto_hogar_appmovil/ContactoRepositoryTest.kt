package com.abs.huerto_hogar_appmovil

import com.abs.huerto_hogar_appmovil.data.model.MensajeContacto
import com.abs.huerto_hogar_appmovil.data.repository.ContactoRepository
import org.junit.Assert.assertTrue
import org.junit.Test

class ContactoRepositoryTest {

    @Test
    fun `enviarMensaje retorna true`() {
        val mensaje = MensajeContacto(
            nombre = "Aurora",
            email = "aurora@test.cl",
            mensaje = "Hola, este es un mensaje de prueba"
        )

        val result = ContactoRepository.enviarMensaje(mensaje)

        assertTrue(result)
    }
}

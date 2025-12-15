package com.abs.huerto_hogar_appmovil.ui.viewmodels

import org.junit.Assert.*
import org.junit.Test

class CheckoutInfoTest {

    @Test
    fun CheckoutInfoconvalorespordefecto() {
        // Arrange & Act
        val checkoutInfo = CheckoutInfo()

        // Assert
        assertEquals("", checkoutInfo.nombre)
        assertEquals("", checkoutInfo.email)
        assertEquals("", checkoutInfo.telefono)
        assertEquals("", checkoutInfo.direccion)
        assertEquals("", checkoutInfo.comuna)
        assertEquals("", checkoutInfo.region)
        assertNull(checkoutInfo.latitud)
        assertNull(checkoutInfo.longitud)
    }

    @Test
    fun CheckoutInfocopymodificasolocamposespecificados() {
        // Arrange
        val original = CheckoutInfo(
            nombre = "Juan",
            email = "juan@email.com",
            telefono = "912345678",
            direccion = "Calle 123",
            comuna = "Santiago",
            region = "Metropolitana",
            latitud = -33.0,
            longitud = -70.0
        )

        // Act
        val copiado = original.copy(
            direccion = "Calle Nueva 456",
            comuna = "Providencia",
            latitud = -34.0
        )

        // Assert - Campos que NO cambiaron
        assertEquals("Juan", copiado.nombre)
        assertEquals("juan@email.com", copiado.email)
        assertEquals("912345678", copiado.telefono)
        assertEquals("Metropolitana", copiado.region)
        assertEquals(-70.0, copiado.longitud)

        // Assert - Campos que SÍ cambiaron
        assertEquals("Calle Nueva 456", copiado.direccion)
        assertEquals("Providencia", copiado.comuna)
        assertEquals(-34.0, copiado.latitud)
    }

    @Test
    fun CheckoutInfoconcoordenadas() {
        // Arrange & Act
        val checkoutInfo = CheckoutInfo(
            latitud = -33.45694,
            longitud = -70.64827
        )

        // Assert
        assertEquals(-33.45694, checkoutInfo.latitud)
        assertEquals(-70.64827, checkoutInfo.longitud)
    }

    @Test
    fun CheckoutInfoconcamposnovacíos() {
        // Arrange & Act
        val checkoutInfo = CheckoutInfo(
            nombre = "María González",
            email = "maria.gonzalez@email.com",
            telefono = "+56912345678",
            direccion = "Av. Principal 789",
            comuna = "Las Condes",
            region = "Región Metropolitana"
        )

        // Assert
        assertEquals("María González", checkoutInfo.nombre)
        assertEquals("maria.gonzalez@email.com", checkoutInfo.email)
        assertEquals("+56912345678", checkoutInfo.telefono)
        assertEquals("Av. Principal 789", checkoutInfo.direccion)
        assertEquals("Las Condes", checkoutInfo.comuna)
        assertEquals("Región Metropolitana", checkoutInfo.region)
        assertNull(checkoutInfo.latitud)
        assertNull(checkoutInfo.longitud)
    }
}
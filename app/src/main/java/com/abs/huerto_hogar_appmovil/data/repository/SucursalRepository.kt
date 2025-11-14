package com.abs.huerto_hogar_appmovil.data.repository


import com.abs.huerto_hogar_appmovil.data.model.Sucursal
import com.google.android.gms.maps.model.LatLng

object SucursalRepository {

    fun obtenerSucursales(): List<Sucursal> {
        return listOf(
            Sucursal(
                nombre = "Sucursal Santiago",
                direccion = "Av. Providencia 1050, Santiago",
                telefono = "+56 9 3456 7890",
                ubicacion = LatLng(-33.4489, -70.6693)
            ),
            Sucursal(
                nombre = "Sucursal Viña del Mar",
                direccion = "Av. Libertad 900, Viña del Mar",
                telefono = "+56 9 2222 3333",
                ubicacion = LatLng(-33.0153, -71.5500)
            ),
            Sucursal(
                nombre = "Sucursal Valparaíso",
                direccion = "Av. Argentina 500, Valparaíso",
                telefono = "+56 9 4444 5555",
                ubicacion = LatLng(-33.0472, -71.6127)
            ),
            Sucursal(
                nombre = "Sucursal Concepción",
                direccion = "O’Higgins 800, Concepción",
                telefono = "+56 9 6666 7777",
                ubicacion = LatLng(-36.8260, -73.0498)
            )
        )
    }
}

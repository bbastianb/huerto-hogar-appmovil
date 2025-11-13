package com.abs.huerto_hogar_appmovil.data.model

import com.google.android.gms.maps.model.LatLng


data class Sucursal(
    val nombre: String,
    val direccion: String,
    val telefono: String,
    val ubicacion: LatLng
)

package com.abs.huerto_hogar_appmovil.data.model

data class CarritoItem (
    val productoId: String,
    val nombre: String,
    val precio: Double,
    val cantidad: Int,
    val imagen: Int
) {
    val subtotal: Double get() = precio * cantidad
}
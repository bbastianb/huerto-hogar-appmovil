package com.abs.huerto_hogar_appmovil.ui.navegation

object Destinations {
    const val CATALOGO = "catalogo"
    const val CARRITO = "carrito"
    const val DETALLE_PRODUCTO = "detalle_producto/{productoId}"
    const val CHECKOUT = "checkout"

    // Función helper para rutas con parámetros
    fun detalleProductoRoute(productoId: String) = "detalle_producto/$productoId"
}
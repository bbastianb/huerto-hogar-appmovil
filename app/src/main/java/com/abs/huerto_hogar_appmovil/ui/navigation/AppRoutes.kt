package com.abs.huerto_hogar_appmovil.ui.navigation

sealed class Routes(val route: String) {
    // Auth
    data object Login : Routes("login_screen")
    data object Registro : Routes("registro_screen")

    // Tienda
    data object Catalogo : Routes("catalogo")
    data object Carrito : Routes("carrito")
    data object Checkout : Routes("checkout")
    // Home
    data object Home : Routes("home")
    data object Nosotros : Routes("nosotros")
    data object Contacto : Routes("contacto")



    data object EditarPerfil : Routes("editar_perfil")



    // Admin
    data object AdminScreen : Routes("admin_screen")
    data object ListadoUsers : Routes("listadoUsers")

    // Detalle con parámetro
    data object DetalleProducto : Routes("detalle_producto/{productoId}") {
        fun build(productoId: String) = "detalle_producto/$productoId"
    }
}

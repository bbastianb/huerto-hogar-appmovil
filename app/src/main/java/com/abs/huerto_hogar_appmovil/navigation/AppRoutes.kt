package com.abs.huerto_hogar_appmovil.navigation

// Clase sellada para definir las rutas
// Agregar toda nueva ruta
// Si la pantalla necesita parámetros, crea un helper build(...)
sealed class AppRoutes(val route: String){
    object Login : AppRoutes("login_screen")
    object Registro : AppRoutes("registro_screen")
}
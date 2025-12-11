// ui/components/HuertoBottomBar.kt
package com.abs.huerto_hogar_appmovil.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Badge
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.List
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material.icons.outlined.Person
import androidx.compose.ui.graphics.Color
import com.abs.huerto_hogar_appmovil.ui.navigation.Routes

@Composable
fun HuertoBottomBar(
    selectedRoute: String?,
    onNavigate: (String) -> Unit,
    cartCount: Int
) {
    val itemColors = NavigationBarItemDefaults.colors(
        selectedIconColor = MaterialTheme.colorScheme.primary,
        selectedTextColor = MaterialTheme.colorScheme.primary,
        indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
        unselectedIconColor = MaterialTheme.colorScheme.tertiary,
        unselectedTextColor =  MaterialTheme.colorScheme.tertiary,

        )

    NavigationBar {
        // INICIO
        NavigationBarItem(
            selected = selectedRoute == Routes.Home.route,
            onClick = { onNavigate(Routes.Home.route) },
            icon = { Icon(Icons.Outlined.Home, contentDescription = "Inicio") },
            label = { Text("Inicio") },
            colors = itemColors
        )

        // CATÁLOGO
        NavigationBarItem(
            selected = selectedRoute == Routes.Catalogo.route,
            onClick = { onNavigate(Routes.Catalogo.route) },
            icon = { Icon(Icons.Outlined.List, contentDescription = "Catálogo") },
            label = { Text("Catálogo") },
            colors = itemColors
        )

        // CARRITO
        NavigationBarItem(
            selected = selectedRoute == Routes.Carrito.route,
            onClick = { onNavigate(Routes.Carrito.route) },
            icon = {
                if (cartCount > 0) {
                    BadgedBox(badge = { Badge { Text("$cartCount") } }) {
                        Icon(Icons.Outlined.ShoppingCart, contentDescription = "Carrito")
                    }
                } else {
                    Icon(Icons.Outlined.ShoppingCart, contentDescription = "Carrito")
                }
            },
            label = { Text("Carrito") },
            colors = itemColors
        )

        NavigationBarItem(
            selected = selectedRoute == Routes.EditarPerfil.route,
            onClick = {
                onNavigate(Routes.EditarPerfil.route)
            },
            icon = { Icon(Icons.Outlined.Person, contentDescription = "Perfil") },
            label = { Text("Perfil") },
            colors = itemColors
        )
    }
}

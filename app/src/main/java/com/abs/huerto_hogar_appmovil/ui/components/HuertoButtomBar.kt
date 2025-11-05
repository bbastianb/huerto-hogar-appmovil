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

@Composable
fun HuertoBottomBar(
    selectedRoute: String?,
    onNavigate: (String) -> Unit,
    cartCount: Int
) {
    NavigationBar {
        NavigationBarItem(
            selected = selectedRoute == "catalogo",
            onClick = { onNavigate("catalogo") },
            icon = { Icon(Icons.Outlined.List, contentDescription = "Catálogo") },
            label = { Text("Catálogo") }
        )
        NavigationBarItem(
            selected = selectedRoute == "carrito",
            onClick = { onNavigate("carrito") },
            icon = {
                if (cartCount > 0) {
                    BadgedBox(badge = { Badge { Text("$cartCount") } }) {
                        Icon(Icons.Outlined.ShoppingCart, contentDescription = "Carrito")
                    }
                } else {
                    Icon(Icons.Outlined.ShoppingCart, contentDescription = "Carrito")
                }
            },
            label = { Text("Carrito") }
        )
        NavigationBarItem(
            selected = selectedRoute == "home",
            onClick = { onNavigate("home") },
            icon = { Icon(Icons.Outlined.Home, contentDescription = "Inicio") },
            label = { Text("Inicio") }
        )
        NavigationBarItem(
            selected = selectedRoute == "perfil",
            onClick = { onNavigate("perfil") },
            icon = { Icon(Icons.Outlined.Person, contentDescription = "Perfil") },
            label = { Text("Perfil") }
        )
    }
}

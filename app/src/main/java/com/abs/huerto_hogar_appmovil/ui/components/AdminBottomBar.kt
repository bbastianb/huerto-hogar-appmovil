package com.abs.huerto_hogar_appmovil.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Inventory
import androidx.compose.material.icons.outlined.Person
import com.abs.huerto_hogar_appmovil.ui.navigation.Routes
import androidx.compose.material.icons.outlined.ReceiptLong

@Composable
fun AdminBottomBar(
    selectedRoute: String?,
    onNavigate: (String) -> Unit
) {
    val itemColors = NavigationBarItemDefaults.colors(
        selectedIconColor = MaterialTheme.colorScheme.primary,
        selectedTextColor = MaterialTheme.colorScheme.primary,
        indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
    )

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface
    ) {
        // Ordenes (AdminScreen)
        NavigationBarItem(
            selected = selectedRoute == Routes.AdminScreen.route,
            onClick = { onNavigate(Routes.AdminScreen.route) },
            icon = {
                Icon(
                    Icons.Outlined.Inventory,
                    contentDescription = "Pedidos"
                )
            },
            label = { Text("Pedidos") },
            colors = itemColors
        )

        // USUARIOS (ListadoUsers)
        NavigationBarItem(
            selected = selectedRoute == Routes.ListadoUsers.route,
            onClick = { onNavigate(Routes.ListadoUsers.route) },
            icon = {
                Icon(
                    Icons.Outlined.Groups,
                    contentDescription = "Usuarios"
                )
            },
            label = { Text("Usuarios") },
            colors = itemColors
        )
        NavigationBarItem(
            selected = selectedRoute == Routes.ListadoOrdenes.route,
            onClick = { onNavigate(Routes.ListadoOrdenes.route) },
            icon = {
                Icon(
                    Icons.Outlined.ReceiptLong,
                    contentDescription = "Órdenes"
                )
            },
            label = { Text("Órdenes") },
            colors = itemColors
        )
        NavigationBarItem(
            selected = selectedRoute == Routes.PerfilAdmin.route,
            onClick = { onNavigate(Routes.PerfilAdmin.route) },
            icon = { Icon(Icons.Outlined.Person, contentDescription = "Mi perfil") },
            label = { Text("Perfil") },
            colors = itemColors
        )
    }
}
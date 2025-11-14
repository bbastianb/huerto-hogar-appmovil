package com.abs.huerto_hogar_appmovil.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Inventory
import com.abs.huerto_hogar_appmovil.ui.navigation.Routes

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
        // PRODUCTOS (AdminScreen)
        NavigationBarItem(
            selected = selectedRoute == Routes.AdminScreen.route,
            onClick = { onNavigate(Routes.AdminScreen.route) },
            icon = {
                Icon(
                    Icons.Outlined.Inventory,
                    contentDescription = "Productos"
                )
            },
            label = { Text("Productos") },
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
    }
}
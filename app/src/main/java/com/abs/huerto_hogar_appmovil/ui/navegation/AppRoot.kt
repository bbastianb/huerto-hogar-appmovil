// ui/AppRoot.kt
package com.abs.huerto_hogar_appmovil.ui.navegation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.abs.huerto_hogar_appmovil.data.repository.ProductoRepository
import com.abs.huerto_hogar_appmovil.ui.viewmodels.CartViewModel
import androidx.lifecycle.ViewModelProvider
import com.abs.huerto_hogar_appmovil.ui.components.HuertoBottomBar

@Composable
fun AppRoot(
    catalogoViewModelFactory: ViewModelProvider.Factory,
    cartViewModelFactory: ViewModelProvider.Factory,
    productoRepository: ProductoRepository,
    cartViewModel: CartViewModel
) {
    val navController = rememberNavController()
    val cartCount by cartViewModel.totalItems.collectAsState()

    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    val hideBottomBar =
        (currentRoute?.startsWith("detalle/") == true) || currentRoute == Destinations.CHECKOUT
                

    Scaffold(
        bottomBar = {
            if (!hideBottomBar) {
                HuertoBottomBar(
                    selectedRoute = currentRoute,
                    onNavigate = { route ->
                        navController.navigate(route) {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    cartCount = cartCount
                )
            }
        }
    ) { innerPadding ->
        HuertoHogarNavGraph(
            navController = navController,
            catalogoViewModelFactory = catalogoViewModelFactory,
            cartViewModelFactory = cartViewModelFactory,
            productoRepository = productoRepository,
            cartViewModel = cartViewModel,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

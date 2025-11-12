package com.abs.huerto_hogar_appmovil.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.ViewModelProvider

import com.abs.huerto_hogar_appmovil.data.repository.ProductoRepository
import com.abs.huerto_hogar_appmovil.data.repository.UsuarioRepository
import com.abs.huerto_hogar_appmovil.ui.components.HuertoBottomBar
import com.abs.huerto_hogar_appmovil.ui.viewmodels.CartViewModel

@Composable
fun AppRoot(
    usuarioRepository: UsuarioRepository,
    productoRepository: ProductoRepository,
    catalogoViewModelFactory: ViewModelProvider.Factory,
    cartViewModelFactory: ViewModelProvider.Factory,
    cartViewModel: CartViewModel
) {
    val navController = rememberNavController()
    val cartCount by cartViewModel.totalItems.collectAsState()

    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    val hideBottomBar =
        (currentRoute?.startsWith("detalle_producto/") == true) ||
                currentRoute == Routes.Checkout.route ||
                currentRoute == Routes.Login.route ||
                currentRoute == Routes.Registro.route

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
    ) { inner ->
        AppNavGraph(
            navController = navController,
            usuarioRepository = usuarioRepository,
            productoRepository = productoRepository,
            catalogoViewModelFactory = catalogoViewModelFactory,
            cartViewModelFactory = cartViewModelFactory,
            cartViewModel = cartViewModel,
            modifier = Modifier.padding(inner),
            startDestination = Routes.Login.route // o Routes.Catalogo.route si ya hay sesión
        )
    }
}

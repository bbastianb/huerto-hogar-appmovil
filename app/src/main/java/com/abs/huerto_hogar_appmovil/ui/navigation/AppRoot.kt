package com.abs.huerto_hogar_appmovil.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.ViewModelProvider
import com.abs.huerto_hogar_appmovil.data.repository.PedidoRepository

import com.abs.huerto_hogar_appmovil.data.repository.ProductoRepository
import com.abs.huerto_hogar_appmovil.data.repository.UsuarioRepository
import com.abs.huerto_hogar_appmovil.ui.components.AdminBottomBar
import com.abs.huerto_hogar_appmovil.ui.components.HuertoBottomBar
import com.abs.huerto_hogar_appmovil.ui.viewmodels.CartViewModel

@Composable
fun AppRoot(
    usuarioRepository: UsuarioRepository,
    productoRepository: ProductoRepository,
    pedidoRepository: PedidoRepository,
    catalogoViewModelFactory: ViewModelProvider.Factory,
    cartViewModelFactory: ViewModelProvider.Factory,
    cartViewModel: CartViewModel,
    checkoutViewModelFactory: ViewModelProvider.Factory
) {
    val navController = rememberNavController()
    val cartCount by cartViewModel.totalItems.collectAsState()


    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    val isAdminRoute = currentRoute in listOf(
        Routes.AdminScreen.route,
        Routes.ListadoUsers.route,
        Routes.PerfilAdmin.route
    )

    val hideBottomBar =
        (currentRoute?.startsWith("detalle_producto/") == true) ||
                currentRoute == Routes.Checkout.route ||
                currentRoute == Routes.Login.route ||
                currentRoute == Routes.Registro.route ||
                isAdminRoute
    val showAdminBottomBar = isAdminRoute

    Scaffold(
        bottomBar = {
            if (!hideBottomBar) {
                HuertoBottomBar(
                    selectedRoute = currentRoute,
                    onNavigate = { route ->
                        // Navegación SIMPLE y segura
                        navController.navigate(route) {
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    cartCount = cartCount
                )
            }else if (showAdminBottomBar) {
                AdminBottomBar(
                    selectedRoute = currentRoute,
                    onNavigate = { route ->
                        navController.navigate(route){
                            launchSingleTop = true
                            restoreState = true
                        }

                    }

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
            pedidoRepository = pedidoRepository,
            checkoutViewModelFactory= checkoutViewModelFactory,
            modifier = Modifier.padding(inner),
            startDestination = Routes.Login.route // o Routes.Catalogo.route si ya hay sesión,

        )
    }
}

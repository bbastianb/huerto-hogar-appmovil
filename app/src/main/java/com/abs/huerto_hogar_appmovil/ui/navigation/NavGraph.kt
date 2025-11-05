package com.abs.huerto_hogar_appmovil.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.compose.ui.Modifier

import com.abs.huerto_hogar_appmovil.data.repository.UsuarioRepository
import com.abs.huerto_hogar_appmovil.data.repository.ProductoRepository

import com.abs.huerto_hogar_appmovil.ui.screens.LoginScreen
import com.abs.huerto_hogar_appmovil.ui.screens.registro.RegistroScreen
import com.abs.huerto_hogar_appmovil.ui.screens.CatalogoScreen
import com.abs.huerto_hogar_appmovil.ui.screens.CarritoScreen
import com.abs.huerto_hogar_appmovil.ui.screens.DetalleProductoScreen

import com.abs.huerto_hogar_appmovil.viewmodels.RegistroViewModel
import com.abs.huerto_hogar_appmovil.ui.viewmodels.CartViewModel
import com.abs.huerto_hogar_appmovil.ui.viewmodels.DetalleProductoViewModel
import com.abs.huerto_hogar_appmovil.ui.viewmodels.DetalleProductoViewModelFactory

@Composable
fun AppNavGraph(
    navController: NavHostController,
    usuarioRepository: UsuarioRepository,

    catalogoViewModelFactory: ViewModelProvider.Factory,
    cartViewModelFactory: ViewModelProvider.Factory,
    productoRepository: ProductoRepository,
    cartViewModel: CartViewModel,
    modifier: Modifier = Modifier,
    startDestination: String = Routes.Login.route // cambia a Routes.Catalogo.route si ya hay sesión
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(Routes.Login.route) {
            LoginScreen(
                onRegistroClick = { navController.navigate(Routes.Registro.route) },
                onLoginOk = {
                    // post-login: ve al catálogo
                    navController.navigate(Routes.Catalogo.route) {
                        popUpTo(Routes.Login.route) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(Routes.Registro.route) {
            val registroViewModel: RegistroViewModel = viewModel(
                factory = object : ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        @Suppress("UNCHECKED_CAST")
                        return RegistroViewModel(usuarioRepository) as T
                    }
                }
            )
            RegistroScreen(
                viewModel = registroViewModel,
                onRegistroExitoso = {
                    navController.navigate(Routes.Login.route) {
                        popUpTo(Routes.Registro.route) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onIrALogin = { navController.popBackStack() }
            )
        }

        composable(Routes.Catalogo.route) {
            CatalogoScreen(
                viewModel = viewModel(factory = catalogoViewModelFactory),
                onCartClick = { navController.navigate(Routes.Carrito.route) },
                onProductClick = { productoId ->
                    navController.navigate(Routes.DetalleProducto.build(productoId))
                }
            )
        }

        composable(Routes.Carrito.route) {
            CarritoScreen(
                viewModel = viewModel(factory = cartViewModelFactory),
                onBackClick = { navController.popBackStack() },
                onCheckoutClick = { navController.navigate(Routes.Checkout.route) }
            )
        }

        composable(
            route = Routes.DetalleProducto.route,
            arguments = listOf(navArgument("productoId") { type = NavType.StringType })
        ) { backStackEntry ->
            val productoId = backStackEntry.arguments?.getString("productoId") ?: ""
            val detalleVm: DetalleProductoViewModel = viewModel(
                factory = DetalleProductoViewModelFactory(productoRepository)
            )
            DetalleProductoScreen(
                productoId = productoId,
                onBackClick = { navController.popBackStack() },
                onCartClick = { navController.navigate(Routes.Carrito.route) },
                onAddToCart = { id, cantidad -> cartViewModel.agregarAlCarrito(id, cantidad) },
                viewModel = detalleVm
            )
        }

        composable(Routes.Checkout.route) {
            // puedes reutilizar tu pantalla futura de checkout
            // o una placeholder acá
        }
    }
}

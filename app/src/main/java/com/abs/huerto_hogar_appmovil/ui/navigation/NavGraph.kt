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

import com.abs.huerto_hogar_appmovil.data.repository.ProductoRepository
import com.abs.huerto_hogar_appmovil.data.repository.UsuarioRepository
import com.abs.huerto_hogar_appmovil.ui.screens.adminScreens.AdminScreen
import com.abs.huerto_hogar_appmovil.ui.screens.CarritoScreen
import com.abs.huerto_hogar_appmovil.ui.screens.CatalogoScreen
import com.abs.huerto_hogar_appmovil.ui.screens.ContactoScreen
import com.abs.huerto_hogar_appmovil.ui.screens.DetalleProductoScreen
import com.abs.huerto_hogar_appmovil.ui.screens.HomeScreen
import com.abs.huerto_hogar_appmovil.ui.screens.authScreens.LoginScreen
import com.abs.huerto_hogar_appmovil.ui.screens.NosotrosScreen
import com.abs.huerto_hogar_appmovil.ui.screens.registro.RegistroScreen
import com.abs.huerto_hogar_appmovil.ui.screens.CheckoutScreen
import com.abs.huerto_hogar_appmovil.ui.screens.adminScreens.ListadoUsuariosScreen

import com.abs.huerto_hogar_appmovil.ui.viewmodels.authVM.RegistroViewModel
import com.abs.huerto_hogar_appmovil.ui.viewmodels.CartViewModel
import com.abs.huerto_hogar_appmovil.ui.viewmodels.CheckoutViewModel
import com.abs.huerto_hogar_appmovil.ui.viewmodels.DetalleProductoViewModel
import com.abs.huerto_hogar_appmovil.ui.viewmodels.DetalleProductoViewModelFactory
import com.abs.huerto_hogar_appmovil.ui.viewmodels.adminVM.ListadoUsersViewModel
import com.abs.huerto_hogar_appmovil.ui.viewmodels.authVM.LoginViewModel

@Composable
fun AppNavGraph(
    navController: NavHostController,
    usuarioRepository: UsuarioRepository,
    checkoutViewModelFactory: ViewModelProvider.Factory,
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
            val loginViewModel: LoginViewModel = viewModel(
                factory = object : ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        @Suppress("UNCHECKED_CAST")
                        return LoginViewModel(usuarioRepository) as T
                    }
                }
            )

            LoginScreen(
                viewModel = loginViewModel,
                onRegistroClick = { navController.navigate(Routes.Registro.route) },
                onLoginExitosoUsuario = {
                    navController.navigate(Routes.Home.route) {
                        popUpTo(Routes.Login.route) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onLoginExitosoAdmin = {
                    navController.navigate(Routes.AdminScreen.route){
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
        composable(Routes.Home.route) {
            HomeScreen(
                onIrCatalogo = { navController.navigate(Routes.Catalogo.route) },
                onIrNosotros = { navController.navigate(Routes.Nosotros.route) },
                onIrContacto = { navController.navigate(Routes.Contacto.route) },
                onIrAdmin = { navController.navigate(Routes.AdminScreen.route) }
            )
        }
        composable(Routes.Nosotros.route) {
            NosotrosScreen(
                onBack = { navController.popBackStack() }
            )
        }
        composable(Routes.Contacto.route) {
            ContactoScreen(
                onBack = { navController.popBackStack() }
            )
        }
        composable(Routes.AdminScreen.route) {
            AdminScreen(
                productoRepository = productoRepository,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.Catalogo.route) {
            CatalogoScreen(
                viewModel = viewModel(factory = catalogoViewModelFactory),
                onCartClick = { navController.navigate(Routes.Carrito.route) },
                onProductClick = { productoId ->
                    navController.navigate(Routes.DetalleProducto.build(productoId))
                }, onBackClick = {navController.popBackStack()},
                onAddToCart = { id, cantidad ->
                    cartViewModel.agregarAlCarrito(id, cantidad)
                }

            )
        }

        composable(Routes.ListadoUsers.route) {
            val listadoUsersViewModel: ListadoUsersViewModel = viewModel(
                factory = object : ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        @Suppress("UNCHECKED_CAST")
                        return ListadoUsersViewModel(usuarioRepository) as T
                    }
                }
            )

            ListadoUsuariosScreen( // ← Usa el nombre correcto de tu composable
                viewModel = listadoUsersViewModel
            )
        }

        composable(Routes.Carrito.route) {
            CarritoScreen(
                viewModel = viewModel(factory = cartViewModelFactory),
                onBackClick = {
                    navController.popBackStack()
                },
                onCheckoutClick = {
                    navController.navigate(Routes.Checkout.route)
                }

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
            val checkoutViewModel: CheckoutViewModel =
                viewModel(factory = checkoutViewModelFactory)

            CheckoutScreen(
                onBackClick = { navController.popBackStack() },
                onOrderComplete = {
                    navController.navigate(Routes.Catalogo.route) {
                        popUpTo(Routes.Catalogo.route) { inclusive = true }
                    }
                },
                viewModel = checkoutViewModel
            )
        }
    }
}

@Composable
fun ListadoUsersScreen(usuarioRepository: UsuarioRepository, onBack: () -> Boolean) {
    TODO("Not yet implemented")
}


package com.abs.huerto_hogar_appmovil.ui.navegation

import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.ViewModelProvider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.abs.huerto_hogar_appmovil.ui.screens.CatalogoScreen
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import com.abs.huerto_hogar_appmovil.data.repository.ProductoRepository
import com.abs.huerto_hogar_appmovil.ui.screens.CarritoScreen
import com.abs.huerto_hogar_appmovil.ui.screens.DetalleProductoScreen
import com.abs.huerto_hogar_appmovil.ui.viewmodels.CartViewModel
import com.abs.huerto_hogar_appmovil.ui.viewmodels.DetalleProductoViewModel
import com.abs.huerto_hogar_appmovil.ui.viewmodels.DetalleProductoViewModelFactory


@Composable
fun HuertoHogarNavGraph(
    navController: NavHostController,
    catalogoViewModelFactory: ViewModelProvider.Factory,
    cartViewModelFactory: ViewModelProvider.Factory,
    modifier: Modifier = Modifier,
    productoRepository: ProductoRepository,
    cartViewModel: CartViewModel
) {
    NavHost(
        navController = navController,
        startDestination = Destinations.CATALOGO,
        modifier = modifier
    ) {
        // PANTALLA DE CATÁLOGO
        composable(Destinations.CATALOGO) {
            CatalogoScreen(
                viewModel = viewModel(factory = catalogoViewModelFactory),
                onCartClick = {
                    navController.navigate(Destinations.CARRITO)
                },onProductClick = { productoId ->
                    navController.navigate(Destinations.detalleProductoRoute(productoId))
                },
                // 👇 Nuevo: lo usa el Quick View para agregar al carrito
                onAddToCart = { id, cantidad ->
                    cartViewModel.agregarAlCarrito(id, cantidad)
                }
            )
        }

        // PANTALLA DE CARRITO
        composable(Destinations.CARRITO) {
            CarritoScreen(
                viewModel = viewModel(factory = cartViewModelFactory),
                onBackClick = {
                    navController.popBackStack()
                },
                onCheckoutClick = {
                    navController.navigate(Destinations.CHECKOUT)
                }

            )
        }

        // PANTALLA DE DETALLE DE PRODUCTO
        composable(
            route = Destinations.DETALLE_PRODUCTO,
            arguments = listOf(navArgument("productoId") { type = NavType.StringType })
        ) { backStackEntry ->
            val productoId = backStackEntry.arguments?.getString("productoId") ?: ""

            val detalleVm: DetalleProductoViewModel = viewModel(
                factory = DetalleProductoViewModelFactory(productoRepository)
            )

            DetalleProductoScreen(
                productoId = productoId,
                onBackClick = { navController.popBackStack() },
                onCartClick = { navController.navigate(Destinations.CARRITO) },
                onAddToCart = { id, cantidad -> cartViewModel.agregarAlCarrito(id, cantidad) },
                viewModel = detalleVm
            )
        }

        // PANTALLA DE CHECKOUT (OPCIONAL - PARA EL FUTURO)
        composable(Destinations.CHECKOUT) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("💰 Checkout", style = MaterialTheme.typography.headlineMedium)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Procesando compra...")
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = {
                        navController.popBackStack(Destinations.CATALOGO, false)
                    }) {
                        Text("Volver al Catálogo")
                    }
                }
            }
        }
    }
}


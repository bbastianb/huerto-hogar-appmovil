package com.abs.huerto_hogar_appmovil

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Surface
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.room.Room
import com.abs.huerto_hogar_appmovil.data.local.AppDatabase
import com.abs.huerto_hogar_appmovil.data.remote.RetrofitOrden
import com.abs.huerto_hogar_appmovil.data.remote.RetrofitProducto
import com.abs.huerto_hogar_appmovil.data.remote.RetrofitWeather
import com.abs.huerto_hogar_appmovil.data.repository.CarritoRepository
import com.abs.huerto_hogar_appmovil.data.repository.PedidoRepository
import com.abs.huerto_hogar_appmovil.data.repository.ProductoRepository
import com.abs.huerto_hogar_appmovil.data.repository.UsuarioRepository
import com.abs.huerto_hogar_appmovil.data.repository.WeatherRepository
import com.abs.huerto_hogar_appmovil.ui.navigation.AppRoot
import com.abs.huerto_hogar_appmovil.ui.theme.HuertohogarappmovilTheme
import com.abs.huerto_hogar_appmovil.ui.viewmodels.CartViewModel
import com.abs.huerto_hogar_appmovil.ui.viewmodels.CatalogoViewModel
import com.abs.huerto_hogar_appmovil.ui.viewmodels.CheckoutViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Base de datos local
        val database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "huerto_hogar_database"
        )
            .fallbackToDestructiveMigration()
            .build()

        // Repositorios principales
        val productoRepository =
            ProductoRepository(database.productoDao(), RetrofitProducto.apiProducto)

        val carritoRepository =
            CarritoRepository(database.carritoDao(), productoRepository)

        val usuarioRepository = UsuarioRepository()

        val pedidoRepository =
            PedidoRepository(database.pedidoDao(), RetrofitOrden.apiOrden)

        val weatherRepository = WeatherRepository(
            api = RetrofitWeather.api
        )


        // Factories
        val catalogoViewModelFactory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return CatalogoViewModel(
                    productoRepository = productoRepository,
                    carritoRepository = carritoRepository
                ) as T
            }
        }

        val cartViewModelFactory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return CartViewModel(carritoRepository) as T
            }
        }

        val checkoutViewModelFactory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return CheckoutViewModel(
                    pedidoRepository = pedidoRepository,
                    carritoRepository = carritoRepository,
                    productoRepository = productoRepository,
                    weatherRepository = weatherRepository   // 👈 aquí lo inyectas
                ) as T
            }
        }

        setContent {
            HuertohogarappmovilTheme {
                Surface {
                    val cartViewModel: CartViewModel = viewModel(factory = cartViewModelFactory)

                    AppRoot(
                        usuarioRepository = usuarioRepository,
                        productoRepository = productoRepository,
                        catalogoViewModelFactory = catalogoViewModelFactory,
                        cartViewModelFactory = cartViewModelFactory,
                        cartViewModel = cartViewModel,
                        checkoutViewModelFactory = checkoutViewModelFactory,
                        pedidoRepository = pedidoRepository
                    )
                }
            }
        }
    }
}

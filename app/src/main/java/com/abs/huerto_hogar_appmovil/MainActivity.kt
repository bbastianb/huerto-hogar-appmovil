package com.abs.huerto_hogar_appmovil

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.room.Room
import com.abs.huerto_hogar_appmovil.data.local.AppDatabase
import com.abs.huerto_hogar_appmovil.data.repository.CarritoRepository
import com.abs.huerto_hogar_appmovil.data.repository.ProductoRepository
import com.abs.huerto_hogar_appmovil.data.repository.UsuarioRepository
import com.abs.huerto_hogar_appmovil.ui.navigation.AppRoot
import androidx.lifecycle.viewmodel.compose.viewModel
import com.abs.huerto_hogar_appmovil.ui.navigation.AppRoot
import com.abs.huerto_hogar_appmovil.ui.theme.HuertohogarappmovilTheme
import com.abs.huerto_hogar_appmovil.ui.viewmodels.CartViewModel
import com.abs.huerto_hogar_appmovil.ui.viewmodels.CatalogoViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "huerto_hogar_database"
        ).build()

        val productoRepository = ProductoRepository(database.productoDao())
        val carritoRepository  = CarritoRepository(database.carritoDao(), productoRepository)
        val usuarioRepository  = UsuarioRepository()


        val catalogoViewModelFactory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return CatalogoViewModel(productoRepository, carritoRepository) as T
            }
        }
        val cartViewModelFactory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return CartViewModel(carritoRepository) as T
            }
        }
        setContent {
            HuertohogarappmovilTheme {
                Surface {
                    val cartViewModel: CartViewModel = viewModel(factory = cartViewModelFactory)
                    AppRoot(
                        // Auth
                        usuarioRepository = usuarioRepository,
                        // Tienda
                        productoRepository = productoRepository,
                        catalogoViewModelFactory = catalogoViewModelFactory,
                        cartViewModelFactory = cartViewModelFactory,
                        cartViewModel = cartViewModel
                    )
                }
            }
        }
    }
}
package com.abs.huerto_hogar_appmovil

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Surface
import androidx.lifecycle.ViewModel
import androidx.room.Room
import com.abs.huerto_hogar_appmovil.data.AppDatabase
import com.abs.huerto_hogar_appmovil.data.repository.CarritoRepository
import com.abs.huerto_hogar_appmovil.data.repository.ProductoRepository
import androidx.lifecycle.viewmodel.compose.viewModel
import com.abs.huerto_hogar_appmovil.ui.navegation.AppRoot
import com.abs.huerto_hogar_appmovil.ui.theme.HuertohogarappmovilTheme
import com.abs.huerto_hogar_appmovil.ui.viewmodels.CartViewModel
import com.abs.huerto_hogar_appmovil.ui.viewmodels.CatalogoViewModel


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val database= Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "huerto_hogar_database"
        ).build()

        val productoRepository= ProductoRepository(database.productoDao())
        val carritoRepository= CarritoRepository(database.carritoDao(),productoRepository)

        val catalogoViewModelFactory= object : androidx.lifecycle.ViewModelProvider.Factory{
            override fun <T : ViewModel> create(modelClass: Class<T>):T{
                return CatalogoViewModel(productoRepository,carritoRepository) as T
            }
        }
        val cartViewModelFactory= object :androidx.lifecycle.ViewModelProvider.Factory{
            override fun <T : ViewModel> create(modelClass: Class<T>):T{
                return CartViewModel(carritoRepository) as T
            }
        }
        setContent {
            HuertohogarappmovilTheme {
                Surface {
                    val cartViewModel: CartViewModel=viewModel(factory=cartViewModelFactory)
                    AppRoot(
                        catalogoViewModelFactory = catalogoViewModelFactory,
                        cartViewModelFactory = cartViewModelFactory,
                        productoRepository = productoRepository,
                        cartViewModel = cartViewModel
                    )
                }
            }
        }
    }
}
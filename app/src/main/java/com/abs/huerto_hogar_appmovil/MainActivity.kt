package com.abs.huerto_hogar_appmovil

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.remember
import androidx.navigation.compose.rememberNavController
import com.abs.huerto_hogar_appmovil.data.repository.UsuarioRepository
import com.abs.huerto_hogar_appmovil.navigation.AppNavGraph

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val navController = rememberNavController()
            val usuarioRepository = remember { UsuarioRepository() }

            AppNavGraph(
                navController = navController,
                usuarioRepository = usuarioRepository
            )
        }
    }
}
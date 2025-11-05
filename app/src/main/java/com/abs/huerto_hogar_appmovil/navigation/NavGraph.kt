package com.abs.huerto_hogar_appmovil.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.abs.huerto_hogar_appmovil.data.repository.UsuarioRepository
import com.abs.huerto_hogar_appmovil.ui.screens.LoginScreen
import com.abs.huerto_hogar_appmovil.ui.screens.registro.RegistroScreen
import com.abs.huerto_hogar_appmovil.viewmodels.RegistroViewModel

@Composable
fun AppNavGraph(
    navController: NavHostController,
    usuarioRepository: UsuarioRepository
) {
    NavHost(
        navController = navController,
        startDestination = AppRoutes.Login.route
    ) {

        composable(AppRoutes.Login.route) {
            LoginScreen(
                onRegistroClick = {
                    navController.navigate(AppRoutes.Registro.route)
                }
            )
        }


        composable(AppRoutes.Registro.route) {
            val registroViewModel: RegistroViewModel = viewModel(
                factory = object : androidx.lifecycle.ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        return RegistroViewModel(usuarioRepository) as T
                    }
                }
            )

            RegistroScreen(
                onRegistroExitoso = {
                    navController.navigate(AppRoutes.Login.route) {
                        popUpTo(AppRoutes.Registro.route) { inclusive = true }
                    }
                },
                onIrALogin = {
                    navController.popBackStack()
                },
                viewModel = registroViewModel
            )
        }
    }
}
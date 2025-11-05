package com.abs.huerto_hogar_appmovil.ui.screens.registro

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.abs.huerto_hogar_appmovil.viewmodels.RegistroViewModel

@Composable
fun RegistroScreen(
    onRegistroExitoso: () -> Unit,
    onIrALogin: () -> Unit,
    viewModel: RegistroViewModel
) {

    val nombre by viewModel.nombre.collectAsState()
    val apellido by viewModel.apellido.collectAsState()
    val correo by viewModel.correo.collectAsState()
    val contrasenna by viewModel.contrasenna.collectAsState()
    val confirmarContrasenna by viewModel.confirmarContrasenna.collectAsState()
    val fono by viewModel.fono.collectAsState()
    val direccion by viewModel.direccion.collectAsState()
    val comuna by viewModel.comuna.collectAsState()
    val region by viewModel.region.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val isSuccess by viewModel.isSuccess.collectAsState()
    val isCargando by viewModel.isLoading.collectAsState()

    // Si el registro fue exitoso, navegar
    if (isSuccess) {
        LaunchedEffect(Unit) {
            onRegistroExitoso()
            viewModel.limpiarFormulario()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        // Título
        Text(
            text = "Registro",
            style = MaterialTheme.typography.headlineLarge
        )

        // Campos del formulario
        OutlinedTextField(
            value = nombre,
            onValueChange = viewModel::onNombreChange,
            label = { Text("Nombre") },
            leadingIcon = { Icon(Icons.Default.Person, "Nombre") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = apellido,
            onValueChange = viewModel::onApellidoChange,
            label = { Text("Apellido") },
            leadingIcon = { Icon(Icons.Default.Person, "Apellido") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = correo,
            onValueChange = viewModel::onCorreoChange,
            label = { Text("Correo") },
            leadingIcon = { Icon(Icons.Default.Email, "Correo") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = contrasenna,
            onValueChange = viewModel::onContrasennaChange,
            label = { Text("Contraseña") },
            leadingIcon = { Icon(Icons.Default.Lock, "Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = confirmarContrasenna,
            onValueChange = viewModel::onConfirmarContrasennaChange,
            label = { Text("Confirmar Contraseña") },
            leadingIcon = { Icon(Icons.Default.Lock, "Confirmar contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = fono,
            onValueChange = viewModel::onFonoChange,
            label = { Text("Teléfono") },
            leadingIcon = { Icon(Icons.Default.Phone, "Teléfono") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = direccion,
            onValueChange = viewModel::onDireccionChange,
            label = { Text("Dirección") },
            leadingIcon = { Icon(Icons.Default.Home, "Dirección") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = comuna,
            onValueChange = viewModel::onComunaChange,
            label = { Text("Comuna") },
            leadingIcon = { Icon(Icons.Default.LocationOn, "Comuna") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = region,
            onValueChange = viewModel::onRegionChange,
            label = { Text("Región") },
            leadingIcon = { Icon(Icons.Default.Map, "Región") },
            modifier = Modifier.fillMaxWidth()
        )

        // Mensaje de error
        errorMessage?.let { error ->
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botón de registro
        Button(
            onClick = viewModel::registrar,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text("Crear cuenta")
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Botón para ir al login
        TextButton(
            onClick = onIrALogin,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("¿Ya tienes cuenta? Inicia Sesión")
        }
    }
}
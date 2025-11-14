package com.abs.huerto_hogar_appmovil.ui.screens.registro

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.abs.huerto_hogar_appmovil.ui.viewmodels.RegistroViewModel

@Composable
fun RegistroScreen(
    onRegistroExitoso: () -> Unit = {},
    onIrALogin: () -> Unit = {},
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

    if (isSuccess) {
        LaunchedEffect(Unit) {
            onRegistroExitoso()
            viewModel.limpiarFormulario()
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Text(
                text = "Crea tu cuenta",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary
            )

            Divider(
                color = MaterialTheme.colorScheme.primary,
                thickness = 3.dp,
                modifier = Modifier.width(64.dp)
            )

            Text(
                text = "Únete a Huerto Hogar y recibe frescura del campo en tu mesa.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground
            )




            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    HuertoTextField(
                        value = nombre,
                        onValue = viewModel::onNombreChange,
                        label = "Nombre",
                        leading = Icons.Filled.Person
                    )

                    HuertoTextField(
                        value = apellido,
                        onValue = viewModel::onApellidoChange,
                        label = "Apellido",
                        leading = Icons.Filled.Person
                    )

                    HuertoTextField(
                        value = correo,
                        onValue = viewModel::onCorreoChange,
                        label = "Correo",
                        leading = Icons.Filled.Email
                    )

                    HuertoTextField(
                        value = contrasenna,
                        onValue = viewModel::onContrasennaChange,
                        label = "Contraseña",
                        leading = Icons.Filled.Lock,
                        isPassword = true
                    )

                    HuertoTextField(
                        value = confirmarContrasenna,
                        onValue = viewModel::onConfirmarContrasennaChange,
                        label = "Confirmar contraseña",
                        leading = Icons.Filled.Lock,
                        isPassword = true
                    )

                    HuertoTextField(
                        value = fono,
                        onValue = viewModel::onFonoChange,
                        label = "Teléfono",
                        leading = Icons.Filled.Phone
                    )

                    HuertoTextField(
                        value = direccion,
                        onValue = viewModel::onDireccionChange,
                        label = "Dirección",
                        leading = Icons.Filled.Home
                    )

                    HuertoTextField(
                        value = comuna,
                        onValue = viewModel::onComunaChange,
                        label = "Comuna",
                        leading = Icons.Filled.LocationOn
                    )

                    HuertoTextField(
                        value = region,
                        onValue = viewModel::onRegionChange,
                        label = "Región",
                        leading = Icons.Filled.Map
                    )

                    errorMessage?.let { error ->
                        Text(
                            text = error,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    Spacer(Modifier.height(8.dp))

                    Button(
                        onClick = viewModel::registrar,
                        enabled = !isCargando,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(if (isCargando) "Creando..." else "Crear cuenta")
                    }


                    TextButton(
                        onClick = onIrALogin,
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text("¿Ya tienes cuenta? Inicia sesión")
                    }
                }
            }
        }
    }
}

@Composable
private fun HuertoTextField(
    value: String,
    onValue: (String) -> Unit,
    label: String,
    leading: androidx.compose.ui.graphics.vector.ImageVector,
    isPassword: Boolean = false
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValue,
        label = { Text(label) },
        leadingIcon = {
            Icon(
                imageVector = leading,
                contentDescription = label
            )
        },
        visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth(),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            focusedLabelColor = MaterialTheme.colorScheme.primary,
            cursorColor = MaterialTheme.colorScheme.primary,
            focusedLeadingIconColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
            unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
            unfocusedLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface
        )
    )
}

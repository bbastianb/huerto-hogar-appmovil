package com.abs.huerto_hogar_appmovil.ui.screens.adminScreens

import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import com.abs.huerto_hogar_appmovil.ui.viewmodels.adminVM.PerfilAdminViewModel
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilAdminScreen(viewModel: PerfilAdminViewModel,onVolverClick: () -> Unit,onCerrarSesion: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var fotoPreview by remember { mutableStateOf<Bitmap?>(null) }
    var errorFoto by remember { mutableStateOf<String?>(null) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        try {
            if (bitmap != null) {
                fotoPreview = bitmap
                errorFoto = null

                val tempFile = File.createTempFile("foto_admin_", ".jpg", context.cacheDir)
                FileOutputStream(tempFile).use { out ->
                    if (!bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)) {
                        throw Exception("No se pudo comprimir la imagen")
                    }
                }
                viewModel.onNuevaFotoSeleccionada(tempFile)
            } else {
                errorFoto = "No se pudo obtener la imagen de la cámara"
            }
        } catch (e: Exception) {
            e.printStackTrace()
            errorFoto = "Error al procesar la foto: ${e.message}"
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Mi perfil (Admin)",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onVolverClick) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onCerrarSesion) {
                        Icon(
                            imageVector = Icons.Filled.ExitToApp,
                            contentDescription = "Cerrar sesión"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                Text(
                    text = "Actualiza tus datos como administrador",
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

                        if (fotoPreview != null) {
                            Image(
                                bitmap = fotoPreview!!.asImageBitmap(),
                                contentDescription = "Foto de perfil admin",
                                modifier = Modifier
                                    .size(120.dp)
                                    .align(Alignment.CenterHorizontally)
                            )
                        }

                        Button(
                            onClick = {
                                viewModel.limpiarMensajes()
                                cameraLauncher.launch(null)
                            },
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.CameraAlt,
                                contentDescription = "Cambiar foto",
                                modifier = Modifier.padding(end = 8.dp)
                            )
                            Text("Cambiar foto")
                        }

                        errorFoto?.let { msg ->
                            Text(
                                text = msg,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            )
                        }

                        PerfilAdminTextField(
                            value = uiState.nombre,
                            onValue = viewModel::onNombreChange,
                            label = "Nombre",
                            leading = Icons.Filled.Person
                        )

                        PerfilAdminTextField(
                            value = uiState.apellido,
                            onValue = viewModel::onApellidoChange,
                            label = "Apellido",
                            leading = Icons.Filled.Person
                        )

                        PerfilAdminTextField(
                            value = uiState.email,
                            onValue = {},
                            label = "Correo (no editable)",
                            leading = Icons.Filled.Person,
                            enabled = false
                        )

                        PerfilAdminTextField(
                            value = uiState.fono,
                            onValue = viewModel::onFonoChange,
                            label = "Teléfono",
                            leading = Icons.Filled.Phone
                        )

                        PerfilAdminTextField(
                            value = uiState.direccion,
                            onValue = viewModel::onDireccionChange,
                            label = "Dirección",
                            leading = Icons.Filled.Home
                        )

                        PerfilAdminTextField(
                            value = uiState.comuna,
                            onValue = viewModel::onComunaChange,
                            label = "Comuna",
                            leading = Icons.Filled.Place
                        )

                        PerfilAdminTextField(
                            value = uiState.region,
                            onValue = viewModel::onRegionChange,
                            label = "Región",
                            leading = Icons.Filled.Place
                        )

                        Divider(modifier = Modifier.padding(vertical = 8.dp))

                        Text(
                            text = "Cambiar contraseña (opcional)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )

                        PerfilAdminTextField(
                            value = uiState.nuevaContrasenna,
                            onValue = viewModel::onNuevaContrasennaChange,
                            label = "Nueva contraseña",
                            leading = Icons.Filled.Lock,
                            isPassword = true
                        )

                        PerfilAdminTextField(
                            value = uiState.confirmarContrasenna,
                            onValue = viewModel::onConfirmarContrasennaChange,
                            label = "Confirmar contraseña",
                            leading = Icons.Filled.Lock,
                            isPassword = true
                        )

                        uiState.error?.let { error ->
                            Text(
                                text = error,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }

                        uiState.mensaje?.let { msg ->
                            Text(
                                text = msg,
                                color = MaterialTheme.colorScheme.primary,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }

                        Spacer(Modifier.height(8.dp))

                        Button(
                            onClick = {
                                scope.launch {
                                    viewModel.guardarCambios()
                                }
                            },
                            enabled = !uiState.isSaving,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(if (uiState.isSaving) "Guardando..." else "Guardar cambios")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PerfilAdminTextField(
    value: String,
    onValue: (String) -> Unit,
    label: String,
    leading: androidx.compose.ui.graphics.vector.ImageVector,
    isPassword: Boolean = false,
    enabled: Boolean = true
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
        enabled = enabled,
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

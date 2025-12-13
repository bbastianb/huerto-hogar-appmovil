package com.abs.huerto_hogar_appmovil.ui.screens.adminScreens

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.abs.huerto_hogar_appmovil.ui.components.CampoRegionDropdown
import com.abs.huerto_hogar_appmovil.ui.viewmodels.adminVM.PerfilAdminViewModel
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilAdminScreen(
    viewModel: PerfilAdminViewModel,
    onVolverClick: () -> Unit,
    onCerrarSesion: () -> Unit
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

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = RequestPermission()
    ) { granted ->
        if (granted) cameraLauncher.launch(null)
        else errorFoto = "Permiso de cámara denegado"
    }

    val fotoParaMostrar = fotoPreview ?: uiState.fotoRemota

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Mi perfil",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onVolverClick) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = onCerrarSesion) {
                        Icon(Icons.Filled.ExitToApp, contentDescription = "Cerrar sesión")
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
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 18.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(110.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            if (fotoParaMostrar != null) {
                                Image(
                                    bitmap = fotoParaMostrar.asImageBitmap(),
                                    contentDescription = "Foto de perfil admin",
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(CircleShape)
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Filled.Person,
                                    contentDescription = "Sin foto",
                                    modifier = Modifier.size(48.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Text(
                            text = "${uiState.nombre} ${uiState.apellido}".trim().ifBlank { "Administrador" },
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )

                        Text(
                            text = uiState.email.ifBlank { "—" },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        OutlinedButton(
                            onClick = {
                                viewModel.limpiarMensajes()
                                errorFoto = null

                                val hasPermission = ContextCompat.checkSelfPermission(
                                    context,
                                    Manifest.permission.CAMERA
                                ) == PackageManager.PERMISSION_GRANTED

                                if (hasPermission) cameraLauncher.launch(null)
                                else permissionLauncher.launch(Manifest.permission.CAMERA)
                            },
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.CameraAlt,
                                contentDescription = "Cambiar foto",
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text("Cambiar foto")
                        }

                        errorFoto?.let { msg ->
                            AssistChip(
                                onClick = {},
                                label = { Text(msg) },
                                colors = AssistChipDefaults.assistChipColors(
                                    containerColor = MaterialTheme.colorScheme.errorContainer,
                                    labelColor = MaterialTheme.colorScheme.onErrorContainer
                                )
                            )
                        }
                    }
                }

                uiState.error?.let { error ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text(
                            text = error,
                            modifier = Modifier.padding(14.dp),
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                uiState.mensaje?.let { msg ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text(
                            text = msg,
                            modifier = Modifier.padding(14.dp),
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                SectionTitle(title = "Datos personales")

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {

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

                        CampoRegionDropdown(
                            regionCodigoSeleccionado = uiState.region.ifBlank { null },
                            onRegionCodigoChange = viewModel::onRegionChange
                        )
                    }
                }

                SectionTitle(title = "Seguridad")

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Cambiar contraseña (opcional)",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
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
                    }
                }

                Spacer(Modifier.height(6.dp))

                Button(
                    onClick = {
                        scope.launch {
                            viewModel.guardarCambios()
                            fotoPreview = null
                        }
                    },
                    enabled = !uiState.isSaving,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text(if (uiState.isSaving) "Guardando..." else "Guardar cambios")
                }

                Spacer(Modifier.height(12.dp))
            }
        }
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onBackground,
        modifier = Modifier.padding(horizontal = 4.dp)
    )
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
        leadingIcon = { Icon(imageVector = leading, contentDescription = label) },
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

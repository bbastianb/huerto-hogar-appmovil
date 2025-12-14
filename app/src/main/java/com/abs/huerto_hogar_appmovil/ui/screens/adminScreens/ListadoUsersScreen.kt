package com.abs.huerto_hogar_appmovil.ui.screens.adminScreens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.abs.huerto_hogar_appmovil.data.repository.UsuarioRepository
import com.abs.huerto_hogar_appmovil.ui.components.UsuarioCard
import com.abs.huerto_hogar_appmovil.ui.viewmodels.adminVM.ListadoUsersViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListadoUsuariosScreen(
    viewModel: ListadoUsersViewModel = ListadoUsersViewModel(UsuarioRepository())
) {
    val usuarios by viewModel.usuarios.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()
    val mostrarDialogo by viewModel.mostrarDialogo.collectAsStateWithLifecycle()
    val usuarioParaEliminar by viewModel.usuarioParaEliminar.collectAsStateWithLifecycle()
    val textoBusqueda by viewModel.textoBusqueda.collectAsStateWithLifecycle()

    if (mostrarDialogo) {
        AlertDialog(
            onDismissRequest = { viewModel.cerrarDialogo() },
            title = {
                Text(
                    "Confirmar eliminación",
                    fontWeight = FontWeight.SemiBold
                )
            },
            text = {
                Text(
                    "¿Estás seguro de que quieres eliminar a ${usuarioParaEliminar?.nombre} ${usuarioParaEliminar?.apellido}? Esta acción no se puede deshacer.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            confirmButton = {
                TextButton(onClick = { viewModel.confirmarEliminacion() }) {
                    Text("ELIMINAR", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.cerrarDialogo() }) {
                    Text("CANCELAR")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Usuarios", fontWeight = FontWeight.SemiBold) },
                actions = {
                    TextButton(onClick = { viewModel.limpiarFiltros() }) {
                        Text("Limpiar")
                    }
                }
            )
        }
    ) { padding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 18.dp, vertical = 14.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "Gestión de usuarios",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Total: ${usuarios.size}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            AssistChip(
                                onClick = {},
                                label = {
                                    Text(
                                        text = if (textoBusqueda.isBlank()) "Sin filtro" else "Filtrando",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            )
                        }

                        OutlinedTextField(
                            value = textoBusqueda,
                            onValueChange = { viewModel.actualizarTextoBusqueda(it) },
                            label = { Text("Buscar") },
                            placeholder = { Text("Nombre, apellido, email, región") },
                            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp)
                        )
                    }
                }

                //contenido
                Box(modifier = Modifier.fillMaxSize()) {

                    if (isLoading) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(48.dp),
                                color = MaterialTheme.colorScheme.primary,
                                strokeWidth = 4.dp
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Cargando usuarios...",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        return@Box
                    }

                    errorMessage?.let { error ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Error",
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                                Spacer(Modifier.height(6.dp))
                                Text(
                                    text = error,
                                    color = MaterialTheme.colorScheme.onErrorContainer,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(Modifier.height(12.dp))
                                Button(
                                    onClick = { viewModel.clearError() },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.primary,
                                        contentColor = MaterialTheme.colorScheme.onPrimary
                                    )
                                ) {
                                    Text("Reintentar")
                                }
                            }
                        }
                        return@Box
                    }

                    if (usuarios.isEmpty()) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = if (textoBusqueda.isNotBlank())
                                    "No se encontraron usuarios con '$textoBusqueda'"
                                else
                                    "No hay usuarios registrados",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )

                            if (textoBusqueda.isNotBlank()) {
                                Spacer(Modifier.height(12.dp))
                                OutlinedButton(onClick = { viewModel.actualizarTextoBusqueda("") }) {
                                    Text("Limpiar búsqueda")
                                }
                            }
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(bottom = 12.dp)
                        ) {
                            items(usuarios) { usuario ->
                                UsuarioCard(
                                    usuario = usuario,
                                    onEliminarClick = { viewModel.abrirDialogo(usuario) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

package com.abs.huerto_hogar_appmovil.ui.screens.adminScreens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
    val ordenarPor by viewModel.ordenarPor.collectAsStateWithLifecycle()

    if (mostrarDialogo) {
        AlertDialog(
            onDismissRequest = { viewModel.cerrarDialogo() },
            title = {
                Text(
                    "Confirmar Eliminación",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            text = {
                Text(
                    "¿Estás seguro de que quieres eliminar a ${usuarioParaEliminar?.nombre} ${usuarioParaEliminar?.apellido}?",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            confirmButton = {
                TextButton(
                    onClick = { viewModel.confirmarEliminacion() }
                ) {
                    Text(
                        "ELIMINAR",
                        color = Color.Red
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { viewModel.cerrarDialogo() }
                ) {
                    Text("CANCELAR")
                }
            }
        )
    }

    Scaffold(
    ) { paddingValues ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            color = MaterialTheme.colorScheme.background
        ) {
            Column (modifier = Modifier.fillMaxSize()){
                OutlinedTextField(
                value = textoBusqueda,
                    onValueChange = { viewModel.actualizarTextoBusqueda(it) },
                    label = { Text("Buscar usuarios...") },
                    placeholder = { Text("Nombre, apellido, email, región") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    singleLine = true
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
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
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "Cargando usuarios...",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                            }
                            return@Box
                        }

                        errorMessage?.let { error ->
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Card(
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.errorContainer
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = "Error",
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onErrorContainer
                                        )
                                        Text(
                                            text = error,
                                            color = MaterialTheme.colorScheme.onErrorContainer,
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.padding(top = 8.dp)
                                        )
                                        Button(
                                            onClick = { viewModel.clearError() },
                                            modifier = Modifier.padding(top = 16.dp),
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = MaterialTheme.colorScheme.primary,
                                                contentColor = MaterialTheme.colorScheme.onPrimary
                                            )
                                        ) {
                                            Text("Reintentar")
                                        }
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
                                    text = if (textoBusqueda.isNotBlank()){
                                        "No se encontraron usuarios con '$textoBusqueda'"
                                    }else  {
                                        "No hay usuarios registrados"
                                    },
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                    Button(
                                        onClick = { viewModel.limpiarFiltros() },
                                        modifier = Modifier.padding(top = 16.dp)
                                    ) {
                                        Text("Limpiar Búsqueda")
                                    }
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
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
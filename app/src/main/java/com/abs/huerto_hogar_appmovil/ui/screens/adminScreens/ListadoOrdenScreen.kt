package com.abs.huerto_hogar_appmovil.ui.screens.adminScreens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.abs.huerto_hogar_appmovil.data.remote.dto.OrdenResponseDto
import com.abs.huerto_hogar_appmovil.ui.viewmodels.adminVM.ListadoOrdenViewModel

private val ESTADOS_ENVIO = listOf(
    "PENDIENTE",
    "EN_PREPARACION",
    "EN_CAMINO",
    "ENTREGADA",
    "CANCELADA"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListadoOrdensScreen(
    viewModel: ListadoOrdenViewModel = remember { ListadoOrdenViewModel() },
    onVolver: (() -> Unit)? = null
) {
    val ordenes by viewModel.ordenes.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()
    val mostrarDialogo by viewModel.mostrarDialogo.collectAsStateWithLifecycle()
    val ordenParaEliminar by viewModel.ordenParaEliminar.collectAsStateWithLifecycle()
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
                Text("¿Eliminar la orden #${ordenParaEliminar?.idOrden}? Esta acción no se puede deshacer.")
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

    // filtro
    val q = textoBusqueda.trim().lowercase()
    val ordenesFiltradas = remember(ordenes, q) {
        if (q.isBlank()) ordenes
        else ordenes.filter { o ->
            val estado = o.detalleEnvio?.estadoEnvio ?: ""
            val region = o.detalleEnvio?.region ?: ""
            val texto = "${o.idOrden} ${o.fechaOrden} ${o.total} $estado $region".lowercase()
            texto.contains(q)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Órdenes", fontWeight = FontWeight.SemiBold)
                },
                navigationIcon = {
                    if (onVolver != null) {
                        IconButton(onClick = onVolver) {
                            Icon(Icons.Filled.ArrowBack, contentDescription = "Volver")
                        }
                    }
                },
                actions = {
                    TextButton(onClick = { viewModel.cargarOrdenes() }) {
                        Text("Actualizar")
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

                // Header "resumenn"
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
                            text = "Gestión de órdenes",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Total: ${ordenes.size}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            AssistChip(
                                onClick = {},
                                label = {
                                    Text(
                                        text = if (q.isBlank()) "Sin filtro" else "Filtrando",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            )
                        }

                        OutlinedTextField(
                            value = textoBusqueda,
                            onValueChange = { viewModel.actualizarTextoBusqueda(it) },
                            label = { Text("Buscar") },
                            placeholder = { Text("Id, fecha, total, estado, región") },
                            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp)
                        )
                    }
                }

                //Contenido
                Box(modifier = Modifier.fillMaxSize()) {

                    if (isLoading) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            CircularProgressIndicator()
                            Spacer(Modifier.height(12.dp))
                            Text("Cargando órdenes...", color = MaterialTheme.colorScheme.onSurfaceVariant)
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
                                Text("Error", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onErrorContainer)
                                Spacer(Modifier.height(6.dp))
                                Text(
                                    text = error,
                                    textAlign = TextAlign.Center,
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                                Spacer(Modifier.height(12.dp))
                                Button(onClick = { viewModel.cargarOrdenes() }) {
                                    Text("Reintentar")
                                }
                            }
                        }
                        return@Box
                    }

                    if (ordenesFiltradas.isEmpty()) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = if (q.isBlank()) "No hay órdenes registradas"
                                else "No se encontraron órdenes con '$textoBusqueda'",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )

                            if (q.isNotBlank()) {
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
                            items(ordenesFiltradas, key = { it.idOrden }) { orden ->
                                OrdenAdminCard(
                                    orden = orden,
                                    onEliminarClick = { viewModel.abrirDialogoEliminar(orden) },
                                    onCambiarEstado = { nuevo ->
                                        viewModel.cambiarEstado(orden, nuevo)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun OrdenAdminCard(
    orden: OrdenResponseDto,
    onEliminarClick: () -> Unit,
    onCambiarEstado: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    val estadoActual = orden.detalleEnvio?.estadoEnvio ?: "SIN_ESTADO"
    val region = orden.detalleEnvio?.region ?: "-"
    val comuna = orden.detalleEnvio?.comuna ?: "-"
    val direccion = orden.detalleEnvio?.direccion ?: "-"

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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Orden #${orden.idOrden}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Fecha: ${orden.fechaOrden}  •  Total: ${orden.total}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                AssistChip(
                    onClick = {},
                    label = { Text(estadoActual) }
                )
            }

            Divider()

            // info en dos columnas
            Row(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Región", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(region, style = MaterialTheme.typography.bodyMedium)

                    Spacer(Modifier.height(6.dp))

                    Text("Comuna", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(comuna, style = MaterialTheme.typography.bodyMedium)
                }

                Spacer(Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Dirección", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(direccion, style = MaterialTheme.typography.bodyMedium)
                }
            }

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = estadoActual,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Cambiar estado") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp)
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    ESTADOS_ENVIO.forEach { est ->
                        DropdownMenuItem(
                            text = { Text(est) },
                            onClick = {
                                expanded = false
                                if (est != estadoActual) onCambiarEstado(est)
                            }
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                OutlinedButton(
                    onClick = onEliminarClick,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Icon(Icons.Filled.DeleteOutline, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Eliminar")
                }
            }
        }
    }
}

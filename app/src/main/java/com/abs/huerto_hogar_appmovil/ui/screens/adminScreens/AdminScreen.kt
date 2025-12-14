package com.abs.huerto_hogar_appmovil.ui.screens.adminScreens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.abs.huerto_hogar_appmovil.ui.viewmodels.adminVM.AdminUiState
import com.abs.huerto_hogar_appmovil.ui.viewmodels.adminVM.AdminViewModel

// ✅ Wrapper que usa ViewModel (esto usa tu app normal)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(
    viewModel: AdminViewModel,
    onBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    AdminContent(state = state, onBack = onBack)
}

// ✅ Composable "puro" (esto es lo que testeamos en UI tests)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminContent(
    state: AdminUiState,
    onBack: () -> Unit
) {
    val ultimas = state.ordenesBackend.take(5)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Panel administrador") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { inner ->
        Column(
            modifier = Modifier
                .padding(inner)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Text("Bienvenido, Administrador", style = MaterialTheme.typography.titleMedium)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SmallStatCard(
                    title = "Ordenes totales",
                    value = state.totalOrdenes.toString(),
                    modifier = Modifier.weight(1f)
                )
                SmallStatCard(
                    title = "Últimas",
                    value = ultimas.size.toString(),
                    modifier = Modifier.weight(1f)
                )
            }

            if (state.isLoading) {
                CircularProgressIndicator()
                return@Column
            }

            state.error?.let { msg ->
                Text(msg, color = MaterialTheme.colorScheme.error)
                return@Column
            }

            Text("Últimas ordenes", style = MaterialTheme.typography.titleSmall)

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(ultimas) { orden ->
                    Card {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                "Orden #${orden.idOrden}",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text("Fecha: ${orden.fechaOrden}", style = MaterialTheme.typography.bodySmall)
                            Text("Total: $${orden.total}", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SmallStatCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(title, style = MaterialTheme.typography.labelMedium)
            Text(
                value,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}

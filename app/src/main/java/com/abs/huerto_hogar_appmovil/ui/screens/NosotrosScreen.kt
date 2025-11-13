package com.abs.huerto_hogar_appmovil.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlin.collections.forEach

data class Sucursal(
    val nombre: String,
    val direccion: String,
    val telefono: String,
    val ubicacion: LatLng
)

@Composable
fun NosotrosScreen(onBack: () -> Unit) {
    val sucursales = listOf(
        Sucursal(
            "Sucursal Santiago",
            "Av. Providencia 1050, Santiago",
            "+56 9 3456 7890",
            LatLng(-33.4489, -70.6693)
        ),
        Sucursal(
            "Sucursal Viña del Mar",
            "Av. Libertad 900, Viña del Mar",
            "+56 9 2222 3333",
            LatLng(-33.0153, -71.5500)
        ),
        Sucursal(
            "Sucursal Valparaíso",
            "Av. Argentina 500, Valparaíso",
            "+56 9 4444 5555",
            LatLng(-33.0472, -71.6127)
        ),
        Sucursal(
            "Sucursal Concepción",
            "O’Higgins 800, Concepción",
            "+56 9 6666 7777",
            LatLng(-36.8260, -73.0498)
        ),
        Sucursal(
            "Sucursal Villarrica",
            "Camino Licanray 1200, Villarrica",
            "+56 9 8888 9999",
            LatLng(-39.2850, -72.2279)
        ),
        Sucursal(
            "Sucursal Nacimiento",
            "Av. Lautaro 345, Nacimiento",
            "+56 9 1111 2222",
            LatLng(-37.5021, -72.6661)
        ),
        Sucursal(
            "Sucursal Puerto Montt",
            "Costanera 123, Puerto Montt",
            "+56 9 1234 5678",
            LatLng(-41.4717, -72.9369)
        )
    )

    val chile = LatLng(-35.6751, -71.5430)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(chile, 4.5f)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 🔙 Botón superior para volver atrás
        OutlinedButton(
            onClick = onBack,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        ) {
            Text("← Volver atrás")
        }

        Text(
            "Nosotros",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Text(
            "Encuentra nuestras sucursales en todo Chile",
            style = MaterialTheme.typography.bodyMedium
        )

        // 🗺️ Mapa
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp),
            contentAlignment = Alignment.Center
        ) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState
            ) {
                sucursales.forEach { suc ->
                    Marker(
                        state = MarkerState(position = suc.ubicacion),
                        title = suc.nombre,
                        snippet = suc.direccion
                    )
                }
            }

            // Texto overlay si el mapa no carga aún
            Text(
                text = "Cargando mapa...",
                color = Color.Gray,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        // 📋 Lista de direcciones
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            sucursales.forEach { suc ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = suc.nombre,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(suc.direccion, style = MaterialTheme.typography.bodyMedium)
                        Text("Tel: ${suc.telefono}", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}

package com.abs.huerto_hogar_appmovil.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState

data class Sucursal(
    val nombre: String,
    val direccion: String,
    val telefono: String,
    val lat: Double = 0.0,
    val lng: Double = 0.0
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NosotrosScreen(onBack: () -> Unit) {
    val sucursales = listOf(
        Sucursal(
            "Sucursal Santiago",
            "Av. Providencia 1050, Santiago",
            "+56 9 3456 7890",
            lat = -33.42628,
            lng = -70.61859
        ),
        Sucursal(
            "Sucursal Viña del Mar",
            "Av. Libertad 900, Viña del Mar",
            "+56 9 2222 3333"
        ),
        Sucursal(
            "Sucursal Valparaíso",
            "Av. Argentina 500, Valparaíso",
            "+56 9 4444 5555"
        ),
        Sucursal(
            "Sucursal Concepción",
            "O'Higgins 800, Concepción",
            "+56 9 6666 7777"
        ),
        Sucursal(
            "Sucursal Villarrica",
            "Camino Licanray 1200, Villarrica",
            "+56 9 8888 9999"
        )
    )
    //usamos la primera sucursal como principal para el mapa
    val sucursalPrincipal = sucursales.first()

    //Coordenadas en formato latlng para maps
    val posicionPrincipal =LatLng(sucursalPrincipal.lat,sucursalPrincipal.lng)

    //Estado de la cámara del mapa
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(posicionPrincipal,15f)
    }
    //Estado del marcador que se dibuja en el mapa
    val markerState = rememberMarkerState(position = posicionPrincipal)


    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Nosotros") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            // Header informativo
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        "Huerto Hogar",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Tu tienda de confianza para productos de jardinería y hogar",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "Encuentra nuestras sucursales en todo Chile",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Text(
                "Ubicación sucursal principal",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
            Card (
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
                    .padding(horizontal = 16.dp),
                    elevation = CardDefaults.cardElevation(4.dp)

            ){
                GoogleMap (
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState
                ){
                    Marker(
                        state = markerState,
                        title = sucursalPrincipal.nombre,
                        snippet = sucursalPrincipal.direccion
                    )
                }
            }

            // Lista de sucursales
            Text(
                "Nuestras Sucursales",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                sucursales.forEach { sucursal ->
                    SucursalCard(sucursal = sucursal)
                }
            }

            // Información adicional
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        "Horarios de Atención",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Lunes a Viernes: 9:00 - 20:00 hrs")
                    Text("Sábados: 10:00 - 18:00 hrs")
                    Text("Domingos: 11:00 - 15:00 hrs")
                }
            }
        }
    }
}

@Composable
fun SucursalCard(sucursal: Sucursal) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = sucursal.nombre,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = sucursal.direccion,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Tel: ${sucursal.telefono}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
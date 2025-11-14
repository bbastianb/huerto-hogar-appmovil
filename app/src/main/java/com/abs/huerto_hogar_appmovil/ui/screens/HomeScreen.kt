package com.abs.huerto_hogar_appmovil.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.abs.huerto_hogar_appmovil.R
import kotlin.collections.forEach

@Composable
fun HomeScreen(
    onIrCatalogo: () -> Unit,
    onIrNosotros: () -> Unit,
    onIrContacto: () -> Unit,
    onIrAdmin: () -> Unit
) {
    // imágenes del carrusel (usa las que están en tu res/drawable)
    val imagenesCarrusel = listOf(
        R.drawable.manzanas,
        R.drawable.naranja,
        R.drawable.zanahoria,
        R.drawable.platanos,
        R.drawable.espinaca
    )

    // mismos productos para mostrarlos abajo como cards
    val productos = listOf(
        ProductoUi("Manzanas Fuji", "1.200", R.drawable.manzanas),
        ProductoUi("Naranjas Valencia", "1.000", R.drawable.naranja),
        ProductoUi("Zanahorias", "900", R.drawable.zanahoria),
        ProductoUi("Plátanos", "800", R.drawable.platanos),
        ProductoUi("Espinaca", "700", R.drawable.espinaca)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        // título
        Column {
            Text(
                text = "Huerto Hogar",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
            )
            Text(
                text = "Catálogo y administración",
                style = MaterialTheme.typography.bodyMedium
            )
        }

        // 1) Carrusel de solo imágenes
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
        ) {
            items(imagenesCarrusel) { imgRes ->
                Card(
                    modifier = Modifier
                        .width(220.dp)
                        .fillMaxHeight(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                ) {
                    Image(
                        painter = painterResource(id = imgRes),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }

        // 2) Botones de navegación
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = onIrCatalogo,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Ver catálogo")
            }
            OutlinedButton(
                onClick = onIrNosotros,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Nosotros (mapa y teléfonos)")
            }
            OutlinedButton(
                onClick = onIrContacto,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Contacto")
            }
            OutlinedButton(
                onClick = onIrAdmin,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Admin")
            }
        }

        // 3) Cards de productos abajo de los botones
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            productos.forEach { p ->
                ProductoCard(
                    nombre = p.nombre,
                    precio = p.precio,
                    imagenRes = p.imagenRes,
                    onVer = onIrCatalogo
                )
            }
        }
    }
}

private data class ProductoUi(
    val nombre: String,
    val precio: String,
    val imagenRes: Int
)

@Composable
private fun ProductoCard(
    nombre: String,
    precio: String,
    imagenRes: Int,
    onVer: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Image(
                painter = painterResource(id = imagenRes),
                contentDescription = nombre,
                modifier = Modifier
                    .size(70.dp),
                contentScale = ContentScale.Crop
            )
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(nombre, fontWeight = FontWeight.SemiBold)
                Text("$$precio", style = MaterialTheme.typography.bodySmall)
            }
            Button(onClick = onVer) {
                Text("Ver")
            }
        }
    }
}

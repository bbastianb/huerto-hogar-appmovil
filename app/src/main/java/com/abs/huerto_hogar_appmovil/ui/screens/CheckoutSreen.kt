package com.abs.huerto_hogar_appmovil.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.abs.huerto_hogar_appmovil.ui.viewmodels.CheckoutInfo
import com.abs.huerto_hogar_appmovil.ui.viewmodels.CheckoutViewModel
import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import android.annotation.SuppressLint
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import android.location.Geocoder
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    onBackClick: () -> Unit,
    onOrderComplete: () -> Unit,
    viewModel: CheckoutViewModel
) {
    val checkoutInfo by viewModel.checkoutInfo.collectAsState()
    val metodoPago by viewModel.metodoPago.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val mensaje by viewModel.mensaje.collectAsState()
    val ordenCreada by viewModel.ordenCreada.collectAsState()
    val erroresValidacion by viewModel.erroresValidacion.collectAsState()
    val puedeProcesarPedido by viewModel.puedeProcesarPedido.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // ====== BLOQUE UBICACIÓN (al estilo del ejemplo de tu profe) ======

    // Cliente de ubicación
    val fusedLocationClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }

    var locationMessage by remember { mutableStateOf("Buscando ubicación...") }

    @SuppressLint("MissingPermission")
    suspend fun recuperarCurrentLocation() {
        try {
            val location = fusedLocationClient
                .getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                .await()

            if (location != null) {
                locationMessage = "Ubicación recuperada"

                // Usamos Geocoder para obtener dirección/comuna/región
                val geocoder = Geocoder(context, Locale.getDefault())
                val addresses = geocoder.getFromLocation(
                    location.latitude,
                    location.longitude,
                    1
                )

                if (!addresses.isNullOrEmpty()) {
                    val addr = addresses[0]

                    val direccion = listOfNotNull(
                        addr.thoroughfare,      // calle
                        addr.subThoroughfare    // número
                    ).joinToString(" ")

                    val comuna = addr.locality ?: ""
                    val region = addr.adminArea ?: ""

                    val actual = viewModel.checkoutInfo.value
                    val nuevaInfo = actual.copy(
                        direccion = if (direccion.isNotBlank()) direccion else actual.direccion,
                        comuna = if (comuna.isNotBlank()) comuna else actual.comuna,
                        region = if (region.isNotBlank()) region else actual.region
                    )

                    viewModel.actualizarCheckoutInfo(nuevaInfo)
                }

            } else {
                locationMessage = "Problemas con la recuperación de ubicación"
            }
        } catch (e: Exception) {
            locationMessage = "Error: ${e.message}"
        }
    }

    // Launcher para pedir permisos MÚLTIPLES (igual que en el ejemplo del profe)
    val locationPermissionLaunch = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fine = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
        val coarse = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true

        if (fine || coarse) {
            locationMessage = "Permiso concedido"
            scope.launch {
                recuperarCurrentLocation()
            }
        } else {
            locationMessage = "Permiso denegado :("
        }
    }

    // Función que se llamará desde el botón "Usar mi ubicación actual"
    fun onUsarUbicacion() {
        val hasFine = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val hasCoarse = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (hasFine || hasCoarse) {
            // Ya tiene permiso → directamente recuperamos la ubicación
            scope.launch {
                recuperarCurrentLocation()
            }
        } else {
            // Pedimos permisos (igual que en el botón del ejemplo)
            locationPermissionLaunch.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    // ====== FIN BLOQUE UBICACIÓN ======
    // Navegar cuando la orden esté creada
    LaunchedEffect(ordenCreada) {
        if (ordenCreada) {
            delay(2000)
            onOrderComplete()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                ),
                title = {
                    Text(
                        "Finalizar Compra",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver al carrito"
                        )
                    }
                }
            )
        },
        bottomBar = {
            if (!isLoading) {
                val totalPedido by viewModel.totalPedido.collectAsState(initial = 0.0)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Total a pagar:", style = MaterialTheme.typography.titleMedium)
                            Text(
                                "$${"%.2f".format(totalPedido)}",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        val puedeProcesarPedido by viewModel.puedeProcesarPedido.collectAsState()
                        if (erroresValidacion.isNotEmpty()) {
                            Text(
                                text = "Falta completar: " + erroresValidacion.joinToString(", "),
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 4.dp)
                            )
                        }
                        Button(
                            onClick = {
                                viewModel.procesarPedido()
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            enabled = puedeProcesarPedido
                        ) {
                            Icon(
                                imageVector = Icons.Default.ShoppingCart,
                                contentDescription = "Confirmar pedido",
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.size(8.dp))
                            Text("Confirmar Pedido")
                        }

                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            // Mensajes
            if (mensaje.isNotEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = mensaje,
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (mensaje.startsWith("")) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.error
                    )
                }
            }

            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Procesando tu pedido...")
                    }
                }
            } else {
                // Información personal
                SeccionInformacionPersonal(
                    checkoutInfo = checkoutInfo,
                    onCheckoutInfoChange = viewModel::actualizarCheckoutInfo,
                    onUsarUbicacionClick = { onUsarUbicacion() }
                )

                Spacer(modifier = Modifier.height(16.dp))

                SeccionMetodoPago(
                    metodoSeleccionado = metodoPago,
                    onMetodoPagoChange = viewModel::actualizarMetodoPago
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Resumen del pedido
                SeccionResumenPedido(viewModel = viewModel)

                Spacer(modifier = Modifier.height(100.dp)) // Espacio para el bottom bar
            }
        }
    }
}

@Composable
fun SeccionInformacionPersonal(
    checkoutInfo: CheckoutInfo,
    onCheckoutInfoChange: (CheckoutInfo) -> Unit,
    onUsarUbicacionClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                "Información de Envío",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            // ✅ CORREGIDO - Sintaxis correcta para Material 3
            OutlinedTextField(
                value = checkoutInfo.nombre,
                onValueChange = { nuevoNombre ->
                    onCheckoutInfoChange(checkoutInfo.copy(nombre = nuevoNombre))
                },
                label = { Text("Nombre completo") },
                leadingIcon = {
                    Icon(Icons.Default.Person, contentDescription = "Nombre")
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            // ✅ CORREGIDO
            OutlinedTextField(
                value = checkoutInfo.email,
                onValueChange = { nuevoEmail ->
                    onCheckoutInfoChange(checkoutInfo.copy(email = nuevoEmail))
                },
                label = { Text("Email") },
                leadingIcon = {
                    Icon(Icons.Default.Email, contentDescription = "Email")
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email) // ✅ Cambiado
            )

            Spacer(modifier = Modifier.height(12.dp))

            // ✅ CORREGIDO
            OutlinedTextField(
                value = checkoutInfo.telefono,
                onValueChange = { nuevoTelefono ->
                    onCheckoutInfoChange(checkoutInfo.copy(telefono = nuevoTelefono))
                },
                label = { Text("Teléfono") },
                leadingIcon = {
                    Icon(Icons.Default.Phone, contentDescription = "Teléfono")
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone) // ✅ Cambiado
            )

            Spacer(modifier = Modifier.height(12.dp))

            // ✅ CORREGIDO
            OutlinedTextField(
                value = checkoutInfo.direccion,
                onValueChange = { nuevaDireccion ->
                    onCheckoutInfoChange(checkoutInfo.copy(direccion = nuevaDireccion))
                },
                label = { Text("Dirección") },
                leadingIcon = {
                    Icon(Icons.Default.Home, contentDescription = "Dirección")
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = false,
                maxLines = 3 // ✅ En lugar de minLines
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                // ✅ CORREGIDO
                OutlinedTextField(
                    value = checkoutInfo.comuna,
                    onValueChange = { nuevaComuna ->
                        onCheckoutInfoChange(checkoutInfo.copy(comuna = nuevaComuna))
                    },
                    label = { Text("Comuna") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )

                // ✅ CORREGIDO
                OutlinedTextField(
                    value = checkoutInfo.region,
                    onValueChange = { nuevaRegion ->
                        onCheckoutInfoChange(checkoutInfo.copy(region = nuevaRegion))
                    },
                    label = { Text("Región") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onUsarUbicacionClick,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.LocationOn, contentDescription = "Ubicación")
                Spacer(modifier = Modifier.size(8.dp))
                Text("Usar mi ubicación actual")
            }
        }
    }
}

@Composable
fun SeccionMetodoPago(
    metodoSeleccionado: String,
    onMetodoPagoChange: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                "Método de Pago",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Tarjeta de Crédito
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = metodoSeleccionado == "TARJETA_CREDITO",
                    onClick = { onMetodoPagoChange("TARJETA_CREDITO") }
                )
                Spacer(modifier = Modifier.size(8.dp))
                Icon(Icons.Default.Check, contentDescription = "Tarjeta Crédito")
                Spacer(modifier = Modifier.size(8.dp))
                Text("Tarjeta de Crédito", style = MaterialTheme.typography.bodyLarge)
            }

            // Débito
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = metodoSeleccionado == "DEBITO",
                    onClick = { onMetodoPagoChange("DEBITO") }
                )
                Spacer(modifier = Modifier.size(8.dp))
                Icon(Icons.Default.Check, contentDescription = "Tarjeta Débito")
                Spacer(modifier = Modifier.size(8.dp))
                Text("Tarjeta de Débito", style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}

@Composable
fun SeccionResumenPedido(viewModel: CheckoutViewModel) {
    val carritoItems by viewModel.carritoItems.collectAsState(initial = emptyList())
    val subtotal by viewModel.subtotal.collectAsState(initial = 0.0)
    val totalPedido by viewModel.totalPedido.collectAsState(initial = 0.0)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                "Resumen del Pedido",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Lista de productos
            if (carritoItems.isEmpty()) {
                Text(
                    "No hay productos en el carrito",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                carritoItems.forEach { item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(Modifier.weight(1f)) {
                            Text(
                                item.producto.nombre,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                "Cantidad: ${item.carrito.cantidad} • $${item.producto.precio} c/u",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Text(
                            "$${"%.2f".format(item.producto.precio * item.carrito.cantidad)}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Totales
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Subtotal:", style = MaterialTheme.typography.bodyLarge)
                Text("$${"%.2f".format(subtotal)}")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Envío:", style = MaterialTheme.typography.bodyLarge)
                Text("$0.00") // Envío gratis
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Total:", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(
                    "$${"%.2f".format(totalPedido)}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
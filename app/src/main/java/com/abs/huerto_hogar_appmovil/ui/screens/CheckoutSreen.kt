package com.abs.huerto_hogar_appmovil.ui.screens

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Geocoder
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.abs.huerto_hogar_appmovil.data.remote.dto.WeatherResponseDto
import com.abs.huerto_hogar_appmovil.ui.viewmodels.CheckoutInfo
import com.abs.huerto_hogar_appmovil.ui.viewmodels.CheckoutViewModel
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    onBackClick: () -> Unit,
    onOrderComplete: () -> Unit,
    viewModel: CheckoutViewModel
) {
    // -------- STATE DEL VIEWMODEL --------
    val checkoutInfo by viewModel.checkoutInfo.collectAsState()
    val metodoPago by viewModel.metodoPago.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val mensaje by viewModel.mensaje.collectAsState()
    val ordenCreada by viewModel.ordenCreada.collectAsState()
    val erroresValidacion by viewModel.erroresValidacion.collectAsState()
    val puedeProcesarPedido by viewModel.puedeProcesarPedido.collectAsState()

    // Clima
    val weatherInfo by viewModel.weatherInfo.collectAsState()
    val weatherError by viewModel.weatherError.collectAsState()
    val isWeatherLoading by viewModel.isWeatherLoading.collectAsState()

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // -------- LOCATION CLIENT --------
    val fusedLocationClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }

    var locationMessage by remember { mutableStateOf("") }

    @SuppressLint("MissingPermission")
    suspend fun recuperarCurrentLocation() {
        try {
            val location = fusedLocationClient
                .getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                .await()

            if (location != null) {
                locationMessage = "Ubicación recuperada correctamente"

                val geocoder = Geocoder(context, Locale.getDefault())
                val addresses = geocoder.getFromLocation(
                    location.latitude,
                    location.longitude,
                    1
                )

                var direccion = ""
                var comuna = ""
                var region = ""

                if (!addresses.isNullOrEmpty()) {
                    val addr = addresses[0]

                    direccion = listOfNotNull(
                        addr.thoroughfare,
                        addr.subThoroughfare
                    ).joinToString(" ")

                    comuna = addr.locality ?: ""
                    region = addr.adminArea ?: ""
                }

                // ✅ AHORA usamos el nuevo método con lat/lon
                viewModel.actualizarUbicacion(
                    lat = location.latitude,
                    lon = location.longitude,
                    direccion = direccion,
                    comuna = comuna,
                    region = region
                )

            } else {
                locationMessage = "No se pudo obtener la ubicación actual"
            }
        } catch (e: Exception) {
            locationMessage = "Error al obtener ubicación: ${e.message}"
        }
    }


    // Launcher permisos
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
            locationMessage = "Permiso de ubicación denegado"
        }
    }

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
            scope.launch { recuperarCurrentLocation() }
        } else {
            locationPermissionLaunch.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

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
                    Column(modifier = Modifier.padding(16.dp)) {
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
                            onClick = { viewModel.procesarPedido() },
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
            // Mensaje general
            if (mensaje.isNotEmpty()) {
                val esError = mensaje.contains("error", ignoreCase = true)
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
                        color = if (esError)
                            MaterialTheme.colorScheme.error
                        else
                            MaterialTheme.colorScheme.primary
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
                // Datos de envío
                SeccionInformacionPersonal(
                    checkoutInfo = checkoutInfo,
                    onCheckoutInfoChange = viewModel::actualizarCheckoutInfo,
                    onUsarUbicacionClick = { onUsarUbicacion() }
                )

                if (locationMessage.isNotBlank()) {
                    Text(
                        text = locationMessage,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Clima
                WeatherCard(
                    weatherInfo = weatherInfo,
                    isLoading = isWeatherLoading,
                    error = weatherError,
                    onRetry = { viewModel.recargarClima() }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Método de pago
                SeccionMetodoPago(
                    metodoSeleccionado = metodoPago,
                    onMetodoPagoChange = viewModel::actualizarMetodoPago
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Resumen
                SeccionResumenPedido(viewModel = viewModel)

                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }
}

@Composable
fun WeatherCard(
    weatherInfo: WeatherResponseDto?,
    isLoading: Boolean,
    error: String?,
    onRetry: () -> Unit
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
                text = "Clima en tu zona",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            when {
                isLoading -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Cargando clima...")
                    }
                }

                !error.isNullOrBlank() -> {
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = onRetry,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Reintentar")
                    }
                }

                weatherInfo != null -> {
                    val temp = weatherInfo.main.temp
                    val desc = weatherInfo.weather.firstOrNull()?.description ?: ""

                    Text(
                        text = weatherInfo.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${"%.1f".format(temp)} °C",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = desc.replaceFirstChar { it.uppercase() },
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                else -> {
                    Text(
                        text = "Usa 'Mi ubicación actual' para ver el clima.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
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
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "Información de Envío",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

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
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )

            Spacer(modifier = Modifier.height(12.dp))

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
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
            )

            Spacer(modifier = Modifier.height(12.dp))

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
                maxLines = 3
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = checkoutInfo.comuna,
                    onValueChange = { nuevaComuna ->
                        onCheckoutInfoChange(checkoutInfo.copy(comuna = nuevaComuna))
                    },
                    label = { Text("Comuna") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )

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
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "Método de Pago",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

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
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "Resumen del Pedido",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

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
                Text("$0.00")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "Total:",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
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
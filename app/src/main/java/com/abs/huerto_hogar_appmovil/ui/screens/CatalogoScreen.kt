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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.abs.huerto_hogar_appmovil.data.model.Producto
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import com.abs.huerto_hogar_appmovil.ui.viewmodels.CatalogoViewModel
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogoScreen(
    viewModel: CatalogoViewModel = viewModel(),
    onCartClick: () -> Unit = {},
    onProductClick: (String) -> Unit = {},
    onBackClick: () -> Unit = {},
    onAddToCart: (String, Int) -> Unit
) {
    val productos by viewModel.productos.collectAsState(emptyList())
    val isLoading by viewModel.isLoading.collectAsState()
    val categoriaSeleccionada by viewModel.categoria.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var isSearching by remember { mutableStateOf(false) }
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    var showQuickView by remember { mutableStateOf(false) }
    var selected by remember { mutableStateOf<Producto?>(null) }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        contentWindowInsets = WindowInsets(0.dp,0.dp,0.dp,0.dp),
        topBar = {
            if (isSearching) {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    ),
                    title = {
                        TextField(
                            value = searchQuery,
                            onValueChange = {
                                searchQuery = it
                                viewModel.onBuscarChange(it)
                            },
                            placeholder = { Text("Buscar...") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                disabledContainerColor = Color.Transparent,
                                focusedIndicatorColor = MaterialTheme.colorScheme.onPrimary,
                                unfocusedIndicatorColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.5f),
                                cursorColor = MaterialTheme.colorScheme.onPrimary,
                                focusedTextColor = MaterialTheme.colorScheme.onPrimary,
                                unfocusedTextColor = MaterialTheme.colorScheme.onPrimary
                            ),
                            singleLine = true,
                            textStyle = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onPrimary)
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            isSearching = false
                            searchQuery = ""
                            viewModel.onBuscarChange("")
                        }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Cerrar búsqueda"
                            )
                        }
                    },
                    actions = {

                    }
                )
            } else {
                CenterAlignedTopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    ),
                    title = {
                        Text(
                            "Catálogo",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Volver"
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = { isSearching = true }) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Buscar",
                                tint = MaterialTheme.colorScheme.onSecondary
                            )
                        }
//                        IconButton(onClick = onCartClick) {
//                            Icon(
//                                imageVector = Icons.Default.ShoppingCart,
//                                contentDescription = "Carrito",
//                                tint = MaterialTheme.colorScheme.onSecondary
//                            )
//                        }
                    },
                    scrollBehavior = scrollBehavior,

                )
            }
        }
//        floatingActionButton = {
//            if (!isSearching) {
//                FloatingActionButton(
//                    onClick = onCartClick,
//                    containerColor = MaterialTheme.colorScheme.primary
//                ) {
//                    Icon(
//                        Icons.Default.ShoppingCart,
//                        "Carrito",
//                        tint = MaterialTheme.colorScheme.onPrimary
//                    )
//                }
//            }
//        }
    ) { innerPadding ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                if (!isSearching) {
//                    Spacer(modifier = Modifier.height(8.dp))
//                    Text(
//                        "Catálogo de Productos",
//                        style = MaterialTheme.typography.headlineSmall,
//                        fontWeight = FontWeight.Normal
//                    )

                    CategoriaFiltro(
                        categorias = listOf("Todas", "Frutas", "Verduras", "Orgánicos", "Lácteos"),
                        categoriaSeleccionada = categoriaSeleccionada,
                        onCategoriaSelected = viewModel::onCategoriaChange
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
                // PRODUCTOS
                LazyColumn {
                    items(productos) { p ->
                        ProductoCard(
                            producto = p,
                            onProductClick = { id ->
                                // si quieres que el "tocar card" abra el quick view:
                                selected = p
                                showQuickView = true

                                // si en vez de quick view quieres navegar:
                                // onProductClick(id)
                            },
                            onAddToCart = { id ->
                                onAddToCart(id, 1)
                            }
                        )
                        Spacer(Modifier.height(8.dp))
                    }
                }
                if (showQuickView && selected != null) {
                    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
                    ModalBottomSheet(
                        onDismissRequest = { showQuickView = false },
                        sheetState = sheetState
                    ) {
                        // Contenido del quick view:
                        QuickViewProducto(
                            producto = selected!!,
                            onAdd = {
                                onAddToCart(selected!!.id, 1)
                                showQuickView = false
                            },
                            onVerMas = {
                                showQuickView = false
                                onProductClick(selected!!.id) // navegar al detalle completo si quieres
                            }
                        )
                    }
                }

            }
        }
    }
}
@Composable
fun QuickViewProducto(
    producto: Producto,
    onAdd: () -> Unit,
    onVerMas: () -> Unit
) {
    Column(Modifier.padding(16.dp)) {
        Image(
            painter = painterResource(id = producto.imagen),
            contentDescription = producto.nombre,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(8.dp, topEnd = 8.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentScale = ContentScale.Crop
        )
        Spacer(Modifier.height(6.dp))
        Text(producto.nombre, style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(6.dp))
        Text("$${producto.precio} / ${producto.medida}")
        Spacer(Modifier.height(8.dp))
        Text(
            text = producto.descripcion,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "Stock: ${producto.stock}",
            style = MaterialTheme.typography.bodySmall,
            color = if (producto.stock > 0) MaterialTheme.colorScheme.onSurfaceVariant
            else MaterialTheme.colorScheme.error
        )
        Row {
            botonAddCart {
                onAdd()
            }
            Spacer(Modifier.width(8.dp))
            OutlinedButton(onClick = onVerMas, modifier = Modifier.weight(1f)) {
                Text("Ver detalle")
            }
        }
        Spacer(Modifier.height(12.dp))
    }
}
@Composable
fun botonAddCart(onClick: () -> Unit) {
    Button(onClick = { onClick() }) {
        Icon(
            imageVector = Icons.Default.ShoppingCart,
            contentDescription = "Añadir al carrito",
            modifier = Modifier.size(ButtonDefaults.IconSize)
        )
        Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
        Text("Añadir al carrito")
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriaFiltro(
    categorias: List<String>,
    categoriaSeleccionada: String,
    onCategoriaSelected: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        categorias.forEach { categoria ->
            FilterChip(
                onClick = { onCategoriaSelected(categoria) },
                label = {
                    Text(
                        text = categoria,
                        style = MaterialTheme.typography.labelMedium,
                        maxLines = 1
                    )
                },
                selected = categoria == categoriaSeleccionada,
                leadingIcon = if (categoria == categoriaSeleccionada) {
                    {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Seleccionado",
                            modifier = Modifier.size(FilterChipDefaults.IconSize)
                        )
                    }
                } else {
                    null
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                    selectedLeadingIconColor = MaterialTheme.colorScheme.onPrimary,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}

@Composable
fun ProductoCard(
    producto: Producto,
    onProductClick: (String) -> Unit,
    onAddToCart: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showMessage by remember { mutableStateOf(false) }
    LaunchedEffect(showMessage) {
        if (showMessage) {
            delay(2000)
            showMessage = false
        }
    }
    Card(
        onClick = { onProductClick(producto.id) },
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {

                    Image(
                        painter = painterResource(id = producto.imagen),
                        contentDescription = producto.nombre,
                        modifier = Modifier
                            .size(80.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = producto.nombre,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(4.dp))

//                    Text(
//                        text = producto.descripcion,
//                        style = MaterialTheme.typography.bodySmall,
//                        color = MaterialTheme.colorScheme.onSurfaceVariant,
//                        maxLines = 2,
//                        overflow = TextOverflow.Ellipsis
//                    )
//
//                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "$${producto.precio}",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = "• ${producto.medida}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.width(8.dp))
//
//                        Text(
//                            text = "Stock: ${producto.stock}",
//                            style = MaterialTheme.typography.bodySmall,
//                            color = if (producto.stock > 0) MaterialTheme.colorScheme.onSurfaceVariant
//                            else MaterialTheme.colorScheme.error
//                        )

                    }
                    if (showMessage) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "✅ Agregado al carrito",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSecondary,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    MaterialTheme.colorScheme.primaryContainer,
                                    RoundedCornerShape(8.dp)
                                )
                                .padding(vertical = 4.dp, horizontal = 8.dp),
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                IconButton(
                    onClick = {
                        onAddToCart(producto.id)
                        showMessage = true
                    },
                    modifier = Modifier.size(48.dp),
                    enabled = producto.stock > 0
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Agregar al carrito",
                        tint = if (producto.stock > 0) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.38f)
                    )
                }
            }

        }
    }
}
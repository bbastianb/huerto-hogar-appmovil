package com.abs.huerto_hogar_appmovil.data.repository

import com.abs.huerto_hogar_appmovil.R
import com.abs.huerto_hogar_appmovil.data.model.Producto
import com.abs.huerto_hogar_appmovil.data.ProductoDao
import kotlinx.coroutines.flow.Flow

class ProductoRepository(private val productoDao: ProductoDao) {
    fun obtenerTodos(): Flow<List<Producto>> = productoDao.obtenerTodos()

    fun obtenerPorCategoria(categoria: String): Flow<List<Producto>> =
        productoDao.obtenerPorCategoria(categoria)

    fun buscarPorNombre(query: String): Flow<List<Producto>> =
        productoDao.buscarPorNombre(query)

    suspend fun obtenerPorId(productoId: String): Producto? =
        productoDao.obtenerPorId(productoId)

    suspend fun actualizarStock(productoId: String, cantidad: Int) {
        productoDao.actualizarStock(productoId, cantidad)
    }

    suspend fun cargarProductosIniciales() {
        if (productoDao.contarProductos() == 0) {
            productoDao.agregarProducto(productosIniciales)
        }

    }
    private val productosIniciales = listOf(
        Producto(
            id = "FR001", nombre = "Manzanas Fuji", precio = 1200.0,
            categoria = "Frutas",
            stock = 150,
            descripcion = "Manzanas Fuji crujientes y dulces...",
            imagen = R.drawable.manzanas, medida = "kilo", origen = "Valle del Maule"
        ),
        Producto(
            id = "FR002", nombre = "Naranjas Valencia", precio = 1000.0,
            categoria = "Frutas",
            stock = 200,
            descripcion = "Jugosas y ricas en vitamina C...",
            imagen = R.drawable.naranja, medida = "kilo", origen = "Región de Valparaíso"
        ),
        Producto(
            id = "VR001", nombre = "Zanahorias Orgánicas", precio = 900.0,
            categoria = "Verduras",
            stock = 100,
            descripcion = "Zanahorias crujientes cultivadas sin pesticidas...",
            imagen = R.drawable.zanahoria, medida = "kilo", origen = "Región de O'Higgins"
        ),
        Producto(
            id = "PO001", nombre = "Miel Orgánica", precio = 5000.0,
            categoria = "Orgánicos",
            stock = 50,
            descripcion = "Miel pura y orgánica producida por apicultores locales.",
            imagen = R.drawable.miel5, medida = "frasco", origen = "Región de La Araucanía"
        ),
        Producto(
            id = "PL001", nombre = "Leche Entera", precio = 1200.0,
            categoria = "Lácteos",
            stock = 60,
            descripcion = "Leche entera fresca de vacas criadas en granjas locales.",
            imagen = R.drawable.leche, medida = "litro", origen = "Región de Los Lagos"
        ),
        Producto(
            id = "FR003", nombre = "Plátanos", precio = 800.0,
            categoria = "Frutas", stock = 250,
            descripcion = "Plátanos maduros y dulces...",
            imagen = R.drawable.platanos, medida = "kilo", origen = "Región de Valparaíso"
        ),
        Producto(
            id = "VR002", nombre = "Espinacas Frescas", precio = 700.0,
            categoria = "Verduras", stock = 80,
            descripcion = "Espinacas frescas y nutritivas...",
            imagen = R.drawable.espinaca, medida = "bolsa", origen = "Región Metropolitana"

        )
    )
}
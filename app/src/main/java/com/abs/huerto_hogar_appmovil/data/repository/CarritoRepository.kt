package com.abs.huerto_hogar_appmovil.data.repository

import com.abs.huerto_hogar_appmovil.data.model.Carrito
import com.abs.huerto_hogar_appmovil.data.CarritoDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CarritoRepository(private val carritoDao: CarritoDao,private val productoRepository: ProductoRepository) {
    fun obtenerCarrito(): Flow<List<CarritoDao.CarritoConProducto>> =
        carritoDao.obtenerCarritoConProductos()

    suspend fun agregarAlCarrito(productoId: String, cantidad: Int): Boolean {
        try {
            val producto = productoRepository.obtenerPorId(productoId)
            if (producto == null || !producto.tieneStock() || producto.stock < cantidad) {
                return false // Stock insuficiente o producto no existe
            }

            val itemExistente = carritoDao.obtenerItemPorProductoId(productoId)

            if (itemExistente != null) {
                val nuevaCantidad = itemExistente.cantidad + cantidad
                if (producto.stock >= nuevaCantidad) {
                    carritoDao.actualizarCantidad(itemExistente.id, nuevaCantidad)
                } else {
                    return false
                }
            } else {
                val nuevoItem = Carrito(productoId = productoId, cantidad = cantidad)
                carritoDao.agregarItem(nuevoItem)
            }
            return true
        } catch (e: Exception) {
            return false
        }
    }

    suspend fun actualizarCantidad(itemId: Long, cantidad: Int) {
        if (cantidad <= 0) {
            eliminarDelCarrito(itemId)
        } else {
            carritoDao.actualizarCantidad(itemId, cantidad)
        }
    }

    suspend fun eliminarDelCarrito(itemId: Long) {
        val item = Carrito(id = itemId, productoId = "", cantidad = 0)
        carritoDao.eliminarItem(item)
    }

    suspend fun vaciarCarrito() = carritoDao.vaciarCarrito()

    fun obtenerTotalItems(): Flow<Int> = carritoDao.contarItemsTotales()

    fun calcularTotalCarrito(): Flow<Double> {
        return obtenerCarrito().map { carritoItems ->
            carritoItems.sumOf { it.producto.precio * it.carrito.cantidad }
        }
    }
}
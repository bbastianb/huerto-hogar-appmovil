package com.abs.huerto_hogar_appmovil.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "pedidos")
data class Pedido(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val usuarioId: Long? = null, // Relación con usuario si está logueado
    val total: Double,
    val fecha: Long = System.currentTimeMillis(),
    val estado: String = "Pendiente", // Pendiente, Confirmada, Enviada, Entregada
    val metodoPago: String,
    val direccionEnvio: String,
    val comunaEnvio: String,
    val regionEnvio: String,
    val telefonoContacto: String,
    val correoContacto: String
)

// OrderItem.kt
@Entity(tableName = "pedido_items")
data class PedidoItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val pedidoId: String,
    val productoId: String,
    val nombreProducto: String,
    val precioUnitario: Double,
    val cantidad: Int,
    val subtotal: Double
)
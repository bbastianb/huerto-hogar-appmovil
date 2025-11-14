package com.abs.huerto_hogar_appmovil.data.local.dao

import androidx.room.*
import com.abs.huerto_hogar_appmovil.data.model.Pedido
import com.abs.huerto_hogar_appmovil.data.model.PedidoItem
import kotlinx.coroutines.flow.Flow

@Dao
interface PedidoDao {

    @Insert
    suspend fun insertarOrden(pedido: Pedido)

    @Insert
    suspend fun insertarOrderItems(items: List<PedidoItem>)

    @Query("SELECT * FROM pedidos ORDER BY fecha DESC")
    fun obtenerTodasLasOrdenes(): Flow<List<Pedido>>

    @Query("SELECT * FROM pedidos WHERE usuarioId = :usuarioId ORDER BY fecha DESC")
    fun obtenerOrdenesPorUsuario(usuarioId: Long): Flow<List<Pedido>>

    @Query("SELECT * FROM pedido_items WHERE pedidoId = :pedidoId")
    suspend fun obtenerItemsPorOrden(pedidoId: String): List<PedidoItem>

    @Query("SELECT * FROM pedidos WHERE id = :pedidoId")
    suspend fun obtenerOrdenPorId(pedidoId: String): Pedido?

    @Update
    suspend fun actualizarOrden(pedido: Pedido)

    data class PedidoConItems(
        val pedido: Pedido,
        val items: List<PedidoItem>
    )
}

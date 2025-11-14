// PedidoRepository.kt
package com.abs.huerto_hogar_appmovil.data.repository

import com.abs.huerto_hogar_appmovil.data.local.dao.PedidoDao.PedidoConItems
import com.abs.huerto_hogar_appmovil.data.model.Pedido
import com.abs.huerto_hogar_appmovil.data.model.PedidoItem
import kotlinx.coroutines.flow.Flow
import com.abs.huerto_hogar_appmovil.data.local.dao.PedidoDao
class PedidoRepository(
    private val pedidoDao: PedidoDao
) {
    suspend fun crearPedido(
        pedido: Pedido,
        items: List<PedidoItem>
    ) {
        pedidoDao.insertarOrden(pedido)
        pedidoDao.insertarOrderItems(items)
    }

    fun obtenerTodasLasOrdenes(): Flow<List<Pedido>> =
        pedidoDao.obtenerTodasLasOrdenes()

    fun obtenerOrdenesPorUsuario(usuarioId: Long): Flow<List<Pedido>> =
        pedidoDao.obtenerOrdenesPorUsuario(usuarioId)

    suspend fun obtenerPedidoConItems(pedidoId: String): PedidoConItems? {
        val pedido = pedidoDao.obtenerOrdenPorId(pedidoId)
        return pedido?.let {
            PedidoConItems(
                pedido = it,  // ✅ CAMBIADO: "order" por "pedido"
                items = pedidoDao.obtenerItemsPorOrden(pedidoId)  // ✅ CAMBIADO: "orderId" por "pedidoId"
            )
        }
    }
}
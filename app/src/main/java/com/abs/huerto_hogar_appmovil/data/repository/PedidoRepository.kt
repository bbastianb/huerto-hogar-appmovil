package com.abs.huerto_hogar_appmovil.data.repository

import com.abs.huerto_hogar_appmovil.data.local.dao.PedidoDao
import com.abs.huerto_hogar_appmovil.data.local.dao.PedidoDao.PedidoConItems
import com.abs.huerto_hogar_appmovil.data.model.Pedido
import com.abs.huerto_hogar_appmovil.data.model.PedidoItem
import com.abs.huerto_hogar_appmovil.data.remote.api.OrdenApi
import com.abs.huerto_hogar_appmovil.data.remote.dto.*
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PedidoRepository(
    private val pedidoDao: PedidoDao,
    private val ordenApi: OrdenApi
) {
    suspend fun crearPedidoEnBackendYLocal(
        pedido: Pedido,
        items: List<PedidoItem>,
        usuarioId: Long
    ) {
        // 1) construir los detalles para el backend
        val detallesDto = items.map { item ->
            DetalleOrdenDto(
                idProducto = item.productoId,
                nombreProducto = item.nombreProducto,
                precioUnitario = item.precioUnitario,
                cantidad = item.cantidad
            )
        }

        // 2) construir el detalle de envío para el backend
        // (mapeamos desde tu Pedido local)
        val detalleEnvioDto = DetalleEnvioDto(
            direccion = pedido.direccionEnvio,
            comuna = pedido.comunaEnvio,
            region = pedido.regionEnvio,
            metodoEnvio = "DOMICILIO",      // o "RETIRO_TIENDAS" según lo que quieras
            estadoEnvio = "Pendiente"
        )

        // 3) formatear la fecha_orden como String (yyyy-MM-dd)
        val fechaStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            .format(Date(pedido.fecha))

        val ordenRequest = OrdenRequestDto(
            fechaOrden = fechaStr,
            total = pedido.total,
            usuario = UsuarioOrdenDto(id = usuarioId),
            detalles = detallesDto,
            detalleEnvio = detalleEnvioDto
        )

        // 4) llamar al backend
        val token = UsuarioRepository.tokenActual
            ?: throw Exception("Usuario no autenticado")

        val response = ordenApi.crearOrden("Bearer $token", ordenRequest)

        if (!response.isSuccessful) {
            throw Exception("Error al crear orden en backend: ${response.code()}")
        }

        // 5) si todo OK, guardar en Room como hasta ahora
        pedidoDao.insertarOrden(pedido)
        pedidoDao.insertarOrderItems(items)
    }

    // ¡ELIMINA ESTO! Ya tienes otra WeatherRepository en otro archivo
    // class WeatherRepository(
    //     private val api: WeatherApi
    // ) {
    //     suspend fun getWeatherForCity(cityName: String, apiKey: String): WeatherResponseDto? {
    //         val response = api.getCurrentWeather(
    //             cityName = cityName,
    //             apiKey = apiKey
    //         )
    //         return if (response.isSuccessful) response.body() else null
    //     }
    // }

    //    suspend fun crearPedido(
    //        pedido: Pedido,
    //        items: List<PedidoItem>
    //    ) {
    //        pedidoDao.insertarOrden(pedido)
    //        pedidoDao.insertarOrderItems(items)
    //    }

    fun obtenerTodasLasOrdenes(): Flow<List<Pedido>> =
        pedidoDao.obtenerTodasLasOrdenes()

    fun obtenerOrdenesPorUsuario(usuarioId: Long): Flow<List<Pedido>> =
        pedidoDao.obtenerOrdenesPorUsuario(usuarioId)

    suspend fun obtenerPedidoConItems(pedidoId: String): PedidoConItems? {
        val pedido = pedidoDao.obtenerOrdenPorId(pedidoId)
        return pedido?.let {
            PedidoConItems(
                pedido = it,
                items = pedidoDao.obtenerItemsPorOrden(pedidoId)
            )
        }
    }
    suspend fun obtenerOrdenesDesdeBackend(): List<OrdenResponseDto> {
        val token = UsuarioRepository.tokenActual
            ?: throw Exception("Usuario no autenticado")

        val response = ordenApi.listarOrdenes("Bearer $token")

        if (!response.isSuccessful) {
            throw Exception("Error al obtener órdenes: ${response.code()}")
        }

        return response.body() ?: emptyList()
    }


}
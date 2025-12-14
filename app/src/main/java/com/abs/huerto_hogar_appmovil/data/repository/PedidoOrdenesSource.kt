package com.abs.huerto_hogar_appmovil.data.repository

class PedidoOrdenesSource (
    private val pedidoRepository: PedidoRepository
): AdminOrdenesSource{
    override suspend fun obtenerOrdenesDesdeBackend()=
        pedidoRepository.obtenerOrdenesDesdeBackend()
}
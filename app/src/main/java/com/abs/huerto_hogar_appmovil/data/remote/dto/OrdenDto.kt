package com.abs.huerto_hogar_appmovil.data.remote.dto

import com.google.gson.annotations.SerializedName

data class UsuarioOrdenDto(
    @SerializedName("id") val id: Long
)

data class DetalleOrdenDto(
    @SerializedName("idProducto") val idProducto: String,
    @SerializedName("nombreProducto") val nombreProducto: String,
    @SerializedName("precioUnitario") val precioUnitario: Double,
    @SerializedName("cantidad") val cantidad: Int
)

data class DetalleEnvioDto(
    @SerializedName("direccion") val direccion: String,
    @SerializedName("comuna") val comuna: String,
    @SerializedName("region") val region: String,
    @SerializedName("metodo_envio") val metodoEnvio: String,
    @SerializedName("estado_envio") val estadoEnvio: String? = null
)

data class OrdenRequestDto(
    @SerializedName("fecha_orden") val fechaOrden: String,
    @SerializedName("total") val total: Double,
    @SerializedName("usuario") val usuario: UsuarioOrdenDto,
    @SerializedName("detalles") val detalles: List<DetalleOrdenDto>,
    @SerializedName("detalleEnvio") val detalleEnvio: DetalleEnvioDto
)

data class OrdenResponseDto(
    @SerializedName("id_orden") val idOrden: Long,
    @SerializedName("fecha_orden") val fechaOrden: String,
    @SerializedName("total") val total: Double,
    @SerializedName("detalleEnvio") val detalleEnvio: DetalleEnvioDto? = null,
    @SerializedName("usuario") val usuario: UsuarioOrdenDto? = null
)

data class EstadoEnvioBody(val estado: String)

package com.abs.huerto_hogar_appmovil.data.model

import androidx.annotation.DrawableRes
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "productos")
data class Producto (
    @PrimaryKey
    val id: String,
    val nombre: String,
    val precio: Double,
    val categoria: String,
    val stock: Int,
    val descripcion: String,
    @ColumnInfo(name = "imagen_res")@DrawableRes val imagen: Int,
    val medida: String,
    val origen: String=""
)
{
    fun tieneStock(): Boolean = stock > 0
    fun stockSuficiente(cantidad: Int) = stock >= cantidad

}
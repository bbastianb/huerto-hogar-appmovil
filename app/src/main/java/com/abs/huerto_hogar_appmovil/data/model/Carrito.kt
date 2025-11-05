package com.abs.huerto_hogar_appmovil.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "carrito", foreignKeys = [
    ForeignKey(
        entity = Producto::class,
        parentColumns = ["id"],
        childColumns = ["productoId"],
        onDelete = ForeignKey.Companion.CASCADE
    )
])
data class Carrito (
    @PrimaryKey(autoGenerate = true)
    val id: Long=0,
    val productoId: String,
    val cantidad: Int,
    val fechaAgregado: Long = System.currentTimeMillis()
)
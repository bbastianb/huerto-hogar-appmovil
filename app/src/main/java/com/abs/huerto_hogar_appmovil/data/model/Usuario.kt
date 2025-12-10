package com.abs.huerto_hogar_appmovil.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "usuarios",
    indices = [Index(value = ["email"], unique = true)]
)
data class Usuario(
    @PrimaryKey
    val id: Long = 0L,

    val nombre: String,
    val apellido: String,
    val email: String,
    val contrasenna: String,
    val telefono: String,
    val direccion: String,
    val comuna: String,
    val region: String,
    // "admin" / "usuario"
    val rol: String,
)

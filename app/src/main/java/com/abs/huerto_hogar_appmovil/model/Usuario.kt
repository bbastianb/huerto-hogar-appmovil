package com.abs.huerto_hogar_appmovil.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "usuarios" ,  indices = [Index(value = ["correo"], unique = true)])
data class Usuario(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val nombre : String,
    val apellido : String,
    val correo : String,
    val contrasenna : String,
    val fono : Int,
    val direccion : String,
    val comuna : String,
    val region : String,
    val rol : String
)
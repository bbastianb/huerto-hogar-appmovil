package com.abs.huerto_hogar_appmovil.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.abs.huerto_hogar_appmovil.data.local.dao.CarritoDao
import com.abs.huerto_hogar_appmovil.data.local.dao.ProductoDao
import com.abs.huerto_hogar_appmovil.data.model.Carrito
import com.abs.huerto_hogar_appmovil.data.model.Producto

@Database(entities = [Producto::class, Carrito::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase(){

    abstract fun productoDao(): ProductoDao
    abstract fun carritoDao(): CarritoDao
}
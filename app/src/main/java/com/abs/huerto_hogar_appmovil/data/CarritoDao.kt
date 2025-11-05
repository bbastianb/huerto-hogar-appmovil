package com.abs.huerto_hogar_appmovil.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Embedded
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Relation
import androidx.room.Transaction
import com.abs.huerto_hogar_appmovil.data.model.Carrito
import com.abs.huerto_hogar_appmovil.data.model.Producto
import kotlinx.coroutines.flow.Flow

@Dao
interface CarritoDao {
    //clase para el join de las tablas
    data class CarritoConProducto(
        @Embedded
        val carrito: Carrito,
        @Relation(
            parentColumn = "productoId",
            entityColumn = "id"
        )
        val producto: Producto
    )

    //Carrito con informacion de productos
    @Transaction
    @Query("SELECT * FROM carrito ORDER BY fechaAgregado DESC")
    fun obtenerCarritoConProductos(): Flow<List<CarritoConProducto>>

    // Agrega los productos al carrito
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun agregarItem(item: Carrito)

    // Obtiene todos los productos del carrito
    @Query("SELECT * FROM carrito ORDER BY fechaAgregado DESC")
    fun obtenerItems(): Flow<List<Carrito>>

    // Obtiene los productos por su id
    @Query("SELECT * FROM carrito WHERE productoId = :productoId")
    suspend fun obtenerItemPorProductoId(productoId: String): Carrito?

    // Actualiza las cantidades
    @Query("UPDATE carrito SET cantidad = :cantidad WHERE id = :cartId")
    suspend fun actualizarCantidad(cartId: Long, cantidad: Int)

    // Elimina producto del carrito
    @Delete
    suspend fun eliminarItem(item: Carrito)

    // Vacua el carrito
    @Query("DELETE FROM carrito")
    suspend fun vaciarCarrito()

    // Cuenta el total de los productos
    @Query("SELECT SUM(cantidad) FROM carrito")
    fun contarItemsTotales(): Flow<Int>
}
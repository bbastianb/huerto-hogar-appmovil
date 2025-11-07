package com.abs.huerto_hogar_appmovil.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.abs.huerto_hogar_appmovil.data.model.Producto
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductoDao {
    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun agregarProducto(productos: List<Producto>)
    @Update
    suspend fun actualizarProducto(productos: Producto)
    @Delete
    suspend fun eliminarProducto(productos: Producto)
    //Obtiene todos los Productos
    @Query("SELECT * FROM productos ORDER BY nombre ASC")
    fun obtenerTodos(): Flow<List<Producto>>
    //Obtitne por categoria
    @Query("SELECT * FROM productos WHERE categoria LIKE '%' || :categoria || '%' ORDER BY nombre ASC")
    fun obtenerPorCategoria(categoria: String): Flow<List<Producto>>
    //Obtiene por id
    @Query("SELECT * FROM productos WHERE id = :productoId")
    suspend fun obtenerPorId(productoId: String): Producto?
    //Busca por nombre
    @Query("SELECT * FROM productos WHERE nombre LIKE '%'|| :query ||'%'")
    fun buscarPorNombre(query: String): Flow<List<Producto>>
    //Actualiza el stock
    @Query("Update productos SET stock = stock - :cantidad WHERE id = :productoId")
    suspend fun actualizarStock(productoId: String,cantidad: Int)
    //Cuenta los producto iniciales
    @Query("SELECT COUNT(*)FROM productos")
    suspend fun contarProductos():Int
}
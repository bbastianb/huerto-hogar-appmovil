package com.abs.huerto_hogar_appmovil.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.abs.huerto_hogar_appmovil.model.Usuario
import kotlinx.coroutines.flow.Flow

@Dao
interface UsuarioDao {

    /* Busca y Compara */
    @Query("SELECT * FROM usuarios WHERE correo = :email AND contrasenna = :contrasenna")
    suspend fun login(email: String, contrasenna: String): Usuario?

    /* Insertar Usuarios */
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertarUsuario(usuario: Usuario): Long

    /* Buscar Por EMAIL */
    @Query("SELECT * FROM usuarios WHERE correo = :email")
    suspend fun getUsuarioPorEmail(email: String): Usuario?

    /* Buscar Por ID */
    @Query("SELECT * FROM usuarios WHERE id = :id")
    fun getUsuarioPorId(id: Long): Flow<Usuario?>

    /* Actualizar */
    @Update
    suspend fun actualizarUsuario(usuario: Usuario)

    /* Eliminar Por ID */
    @Query("DELETE FROM usuarios WHERE id = :id")
    suspend fun eliminarUsuario(id: Long)

}
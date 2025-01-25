package com.example.gemaroom2.data

import androidx.room.*

@Dao
interface UsuarioDAO {
    @Query("SELECT * FROM Usuario WHERE correo = :correo")
    suspend fun getUsuarioByCorreo(correo: String): Usuario?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addUsuario(usuario: Usuario)

    @Update
    suspend fun updateUsuario(usuario: Usuario)
}
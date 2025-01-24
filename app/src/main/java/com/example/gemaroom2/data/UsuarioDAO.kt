package com.example.gemaroom2.data

import androidx.room.*

/*Clase para acceso a datos*/
//Añadir un usuario
/*
Anotación @Insert:Indica que este método insertará una nueva fila en la tabla correspondiente a la entidad Usuario.
Parámetro onConflict:Especifica la estrategia de resolución de conflictos en caso de que ya exista un registro con la misma clave primaria.
OnConflictStrategy.REPLACE sobrescribirá el registro existente con el nuevo.
suspend:Marca el método como una función de suspensión, lo que significa que debe ejecutarse en un contexto de una coroutine.
Esto asegura que las operaciones de la base de datos no bloqueen el hilo principal.
 */
@Dao
interface UsuarioDAO {
    @Query("SELECT * FROM Usuario WHERE correo = :correo")
    suspend fun getUsuarioByCorreo(correo: String): Usuario?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addUsuario(usuario: Usuario)

    @Update
    suspend fun updateUsuario(usuario: Usuario)
}
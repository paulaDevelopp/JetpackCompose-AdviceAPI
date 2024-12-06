package com.example.gemaroom

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/*
Este código define y configura una base de datos Room para una aplicación Android. Aquí hay una explicación detallada de las partes clave:
 */

@Database(entities=[Usuario::class], version =1)
abstract class UsuarioDB:RoomDatabase() {
    //Métodos que devuelven los DAO que hemos implementado y con los
    //cuales accedemos a la BD
    abstract fun usuarioDao():UsuarioDAO
}

/*
Es un objeto Singleton que garantiza que solo exista una instancia de la base de datos en la aplicación.
 */
object DatabaseBuilder{
    //Almacena la instancia única de la base de datos. Inicialmente es null.
    private var INSTANCE:UsuarioDB?=null

    /*
    Comprueba si INSTANCE es null.
    Si es null, sincroniza el acceso para garantizar que solo un hilo inicialice la base de datos (esto previene problemas en aplicaciones multi-hilo).
    Llama al método buildRoomDB para construir la base de datos.
     */
    fun getInstance(context: Context): UsuarioDB{
        if(INSTANCE == null){
            synchronized((UsuarioDB::class)){
                INSTANCE = buildRoomDB(context)
            }
        }
        return INSTANCE!!
    }

    //Constructor privado
    /*
    Utiliza el método estático Room.databaseBuilder para construir la base de datos.
    Especifica:
    El contexto de la aplicación.
    La clase que representa la base de datos (UsuarioDB::class.java).
    El nombre de la base de datos física en el almacenamiento ("usuario-db").
     */
    private fun buildRoomDB(contexto: Context) =
        Room.databaseBuilder(
            contexto.applicationContext, UsuarioDB::class.java,
            "usuario-db"
        ).build()
}
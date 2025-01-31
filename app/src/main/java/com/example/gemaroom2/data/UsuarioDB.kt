package com.example.gemaroom2.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/*
Este código define y configura una base de datos Room para una aplicación Android. Aquí hay una explicación detallada de las partes clave:
 */

@Database(entities = [Usuario::class], version = 2)
abstract class UsuarioDB : RoomDatabase() {
    abstract fun usuarioDao(): UsuarioDAO
    companion object {
        @Volatile
        private var INSTANCE: UsuarioDB? = null
        fun getInstance(context: Context): UsuarioDB {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    UsuarioDB::class.java,
                    "usuario_database"
                ).fallbackToDestructiveMigration().build().also { INSTANCE = it }
            }
        }
    }
}
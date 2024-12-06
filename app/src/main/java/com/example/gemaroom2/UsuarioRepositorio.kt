package com.example.gemaroom

import android.content.Context
import androidx.lifecycle.LiveData
//import kotlinx.coroutines.flow.internal.NoOpContinuation.context
//import kotlin.coroutines.jvm.internal.CompletedContinuation.context


/*
La clase UsuarioRepositorio actúa como un intermediario entre la base de datos (manejada por Room) y
el resto de la aplicación. Implementa el patrón de diseño Repositorio, que abstrae el acceso a los datos y
permite a otras partes de la aplicación interactuar con la base de datos sin conocer los detalles de su implementación.
 */
class UsuarioRepositorio private constructor(private var context:Context) {
    companion object {

        private var INSTANCE: UsuarioRepositorio? = null
        fun getInstance(contex: Context): UsuarioRepositorio {
            if (INSTANCE == null) {
                INSTANCE = UsuarioRepositorio(contex)
            }
            return INSTANCE!!
        }

    }
    //Metodos que ofrece el Repositorio

    fun getUsuario():LiveData<List<Usuario>>{
        return DatabaseBuilder.getInstance(context).usuarioDao().getAll()
    }

    suspend fun removeUsuario(usu: Usuario):Int =
        DatabaseBuilder.getInstance(context).usuarioDao().deleteUsuario(usu)

    suspend fun updateUsuario(usu: Usuario) =
        DatabaseBuilder.getInstance(context).usuarioDao().updateUsuario(usu)

    suspend fun addUsuario(usu: Usuario) =
        DatabaseBuilder.getInstance(context).usuarioDao().addUsuario(usu)

}

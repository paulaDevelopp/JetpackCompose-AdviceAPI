package com.example.gemaroom2.data
import android.content.Context

class UsuarioRepositorio private constructor(context: Context) {
    private val usuarioDao = UsuarioDB.getInstance(context).usuarioDao()

    suspend fun getUsuarioByCorreo(correo: String): Usuario? {
        return usuarioDao.getUsuarioByCorreo(correo)
    }

    suspend fun addUsuario(usuario: Usuario) {
        usuarioDao.addUsuario(usuario)
    }

    suspend fun updateUsuario(usuario: Usuario) {
        usuarioDao.updateUsuario(usuario)
    }

    companion object {
        @Volatile
        private var INSTANCE: UsuarioRepositorio? = null
        fun getInstance(context: Context): UsuarioRepositorio {
            return INSTANCE ?: synchronized(this) {
                UsuarioRepositorio(context).also { INSTANCE = it }
            }
        }
    }
}
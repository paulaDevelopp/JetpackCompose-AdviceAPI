package com.example.gemaroom2.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable
import androidx.room.TypeConverters
import java.util.Date

@Entity
@TypeConverters(Converter::class) // Asegura que se utilice el conversor para la fecha
data class Usuario(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,
    val nombre: String,
    val correo: String,
    var contadorAccesos: Int = 0,
    var fechaUltimoAcceso: Date
) : Serializable

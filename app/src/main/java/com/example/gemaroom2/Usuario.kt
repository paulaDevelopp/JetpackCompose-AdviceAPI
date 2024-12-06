package com.example.gemaroom

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class Usuario (
    @PrimaryKey(autoGenerate = true)
    var id:Long = 0,
    var name:String,
    var apellido:String
):Serializable




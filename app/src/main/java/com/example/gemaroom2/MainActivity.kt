package com.example.gemaroom2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DatePicker
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import com.example.gemaroom.DatabaseBuilder
import com.example.gemaroom.Usuario
import com.example.gemaroom.UsuarioDB
import com.example.gemaroom2.ui.theme.GemaRoom2Theme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

       // Obtén la instancia de la base de datos utilizando DatabaseBuilder
        val database = DatabaseBuilder.getInstance(this)
        val usuarioDAO = database.usuarioDao()

        // Inserta un usuario directamente en la base de datos
        lifecycleScope.launch {
            val nuevoUsuario = Usuario(name = "Juan", apellido = "Pérez")
            usuarioDAO.addUsuario(nuevoUsuario)
        }

        enableEdgeToEdge()
        setContent {
            GemaRoom2Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    GemaRoom2Theme {
        Greeting("Android")
    }
}
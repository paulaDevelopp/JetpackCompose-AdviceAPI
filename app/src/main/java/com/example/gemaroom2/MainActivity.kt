package com.example.gemaroom2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.example.gemaroom.Usuario
import com.example.gemaroom.UsuarioRepositorio
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Instancia del repositorio
        val repositorio = UsuarioRepositorio.getInstance(application.applicationContext)


        setContent {
            MaterialTheme {
                UsuarioForm { nombre, apellido ->
                    // Guardar el usuario en la base de datos utilizando una coroutine
                    lifecycleScope.launch {
                        repositorio.addUsuario(
                            Usuario(
                                name = nombre,
                                apellido = apellido
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun UsuarioForm(onSubmit: (String, String) -> Unit) {
    var nombre by remember { mutableStateOf("") }
    var apellido by remember { mutableStateOf("") }
    var mensaje by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Registrar Usuario",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Campo para el nombre
        BasicTextField(
            value = nombre,
            onValueChange = { nombre = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .border(1.dp, MaterialTheme.colorScheme.primary)
                .padding(8.dp),
            singleLine = true,
            decorationBox = { innerTextField ->
                if (nombre.isEmpty()) {
                    Text("Nombre", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                innerTextField()
            }
        )

        // Campo para el apellido
        BasicTextField(
            value = apellido,
            onValueChange = { apellido = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .border(1.dp, MaterialTheme.colorScheme.primary)
                .padding(8.dp),
            singleLine = true,
            decorationBox = { innerTextField ->
                if (apellido.isEmpty()) {
                    Text("Apellido", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                innerTextField()
            }
        )

        // Botón para guardar
        Button(
            onClick = {
                if (nombre.isNotBlank() && apellido.isNotBlank()) {
                    onSubmit(nombre, apellido)
                    mensaje = "Usuario guardado correctamente"
                    nombre = ""
                    apellido = ""
                } else {
                    mensaje = "Por favor, complete ambos campos."
                }
            },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text(text = "Guardar")
        }

        // Mensaje de retroalimentación
        if (mensaje.isNotEmpty()) {
            Text(
                text = mensaje,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }
}

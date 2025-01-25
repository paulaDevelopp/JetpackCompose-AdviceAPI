package com.example.gemaroom2

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.gemaroom2.api.User
import com.example.gemaroom2.api.UserViewModel
import com.example.gemaroom2.data.Usuario
import com.example.gemaroom2.data.UsuarioRepositorio
import kotlinx.coroutines.launch
import java.util.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()

            // NavHost con diferentes pantallas
            NavHost(navController, startDestination = "login") {
                composable("login") { LoginScreen(navController) }
                composable("register") { RegisterScreen(navController) }
                composable("welcome") { WelcomeScreen(navController) } // Nueva pantalla de bienvenida
                composable("home") { HomeScreen(UserViewModel(), navController ) }
            }
        }
    }
}

@Composable
fun LoginScreen(navController: NavHostController) {
    val context = LocalContext.current
    val usuarioRepo = remember { UsuarioRepositorio.getInstance(context) }
    val correo = remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Ingrese su correo:", style = MaterialTheme.typography.headlineMedium)
        BasicTextField(value = correo.value, onValueChange = { correo.value = it }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            coroutineScope.launch {
                val user = usuarioRepo.getUsuarioByCorreo(correo.value)
                if (user == null) {
                    navController.navigate("register")
                } else {
                    user.contadorAccesos++
                    user.fechaUltimoAcceso = Date()
                    usuarioRepo.updateUsuario(user)
                    navController.navigate("welcome") // Navegar a la pantalla de bienvenida
                }
            }
        }, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)) {
            Text("Ingresar")
        }
    }
}

@Composable
fun RegisterScreen(navController: NavHostController) {
    val context = LocalContext.current
    val usuarioRepo = remember { UsuarioRepositorio.getInstance(context) }
    val correo = remember { mutableStateOf("") }
    val nombre = remember { mutableStateOf("") }
    val contadorAc = remember { mutableStateOf(0) }
    val coroutineScope = rememberCoroutineScope()

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Ingrese su nombre:", style = MaterialTheme.typography.headlineMedium)
        BasicTextField(value = nombre.value, onValueChange = { nombre.value = it }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        Text("Ingrese su correo:", style = MaterialTheme.typography.headlineMedium)
        BasicTextField(value = correo.value, onValueChange = { correo.value = it }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            if (nombre.value.isNotEmpty() && correo.value.isNotEmpty()) {
                coroutineScope.launch {
                    val nuevoContador = contadorAc.value + 1
                    contadorAc.value = nuevoContador

                    usuarioRepo.addUsuario(
                        Usuario(
                            nombre = nombre.value,
                            correo = correo.value,
                            fechaUltimoAcceso = Date(),
                            contadorAccesos = nuevoContador
                        )
                    )
                    navController.navigate("welcome") // Navegar a la pantalla de bienvenida
                }
            } else {
                Toast.makeText(context, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show()
            }
        }, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)) {
            Text("Registrar")
        }
    }
}

@Composable
fun WelcomeScreen(navController: NavHostController) {
    Scaffold { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Bienvenido! ¿Qué quieres hacer?",
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { navController.navigate("home") }) {
                Text("Obtener lista")
            }
        }
    }
}
@Composable
fun UserList(users: List<User>) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(users) { user ->
            UserItem(user)
        }
    }
}

@Composable
fun UserItem(user: User) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = user.name, style = MaterialTheme.typography.titleMedium)
            Text(text = user.email, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(userViewModel: UserViewModel = viewModel(), navController: NavHostController) {
    val users by userViewModel.users.collectAsState()
    val context = LocalContext.current // Obtén el contexto de la aplicación
    var expanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Lista de Usuarios") },
                actions = {
                    IconButton(onClick = { expanded = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Más opciones")
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Volver Atrás") },
                            onClick = {
                                navController.popBackStack()
                                expanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Cerrar la App") },
                            onClick = {
                                (context as Activity).finish()
                                expanded = false
                            }
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            UserList(users)
        }
    }
}

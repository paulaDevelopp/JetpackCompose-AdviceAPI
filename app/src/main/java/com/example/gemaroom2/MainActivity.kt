package com.example.gemaroom2

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.gemaroom2.api.AdviceViewModel
import com.example.gemaroom2.data.Usuario
import com.example.gemaroom2.data.UsuarioRepositorio
import kotlinx.coroutines.launch
import java.util.*
import android.os.Handler
import android.os.Looper
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.TopAppBar
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import com.example.gemaroom2.api.Advice
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.BasicTextField
import java.text.SimpleDateFormat
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            val usuario = remember { mutableStateOf(Usuario(
                    correo = "user@domain.com",
                fechaUltimoAcceso = Date(), // You can get this from the current time
                nombre = "User Name")) } // Default empty Usuario or provide initial data

            // NavHost with different screens
            NavHost(navController, startDestination = "login") {
                composable("login") { LoginScreen(navController) }
                composable("register") { RegisterScreen(navController) }
                composable("welcome") {
                    // Pass 'usuario' to WelcomeScreen and ensure it's not null
                    WelcomeScreen(navController, usuario.value)
                }
                composable("home") { HomeScreen(AdviceViewModel(), navController) }
            }
        }
    }
}

fun isEmailValid(email: String): Boolean {
    val emailRegex = "[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}".toRegex()
    return email.matches(emailRegex)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavHostController) {
    val context = LocalContext.current
    val usuarioRepo = remember { UsuarioRepositorio.getInstance(context) }
    val correo = remember { mutableStateOf("") }
    val nombre = remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    // Cargar la imagen de fondo
    val fondo = painterResource(id = R.drawable.fondo)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("ConsejosVendoQueParaMiNoTengo", style = MaterialTheme.typography.titleLarge.copy(color = Color.White)) },
                colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = Color(0xFF2F6B3C))
            )
        },
        content = { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                // Imagen de fondo
                Image(
                    painter = fondo,
                    contentDescription = "Fondo",
                    modifier = Modifier.fillMaxSize()
                )

                // El contenido encima de la imagen
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .padding(padding),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Cuadro de fondo para el texto de "Nombre de usuario"
                    Box(
                        modifier = Modifier

                            .background(MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(8.dp))
                            .padding(8.dp)
                    ) {
                        Text(
                            "Nombre de usuario:",
                            style = MaterialTheme.typography.bodyLarge.copy(fontSize = 15.sp, color = Color(0xFF00008B))
                        )
                    }
                    Spacer(modifier = Modifier.height(5.dp))

                    // Campo de texto para el nombre
                    BasicTextField(
                        value = nombre.value,
                        onValueChange = { nombre.value = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .border(1.dp, Color.Gray, shape = RoundedCornerShape(8.dp))
                            .background(Color(0xFFFFF8B5), shape = RoundedCornerShape(8.dp)),
                        singleLine = true,
                        decorationBox = { innerTextField ->
                            Box(modifier = Modifier.padding(16.dp)) { innerTextField() }
                        }

                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Cuadro de fondo para el texto de "Correo electrónico"
                    Box(
                        modifier = Modifier

                            .background(MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(8.dp))
                            .padding(8.dp)
                    ) {
                        Text(
                            "Correo electrónico:",
                            style = MaterialTheme.typography.bodyLarge.copy(fontSize = 15.sp, color = Color(0xFF00008B))
                        )
                    }
                    Spacer(modifier = Modifier.height(5.dp))

                    // Campo de texto para el correo
                    BasicTextField(
                        value = correo.value,
                        onValueChange = { correo.value = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .border(1.dp, Color.Gray, shape = RoundedCornerShape(8.dp))
                            .background(Color(0xFFFFF8B5), shape = RoundedCornerShape(8.dp)),
                        singleLine = true,
                        decorationBox = { innerTextField ->
                            Box(modifier = Modifier.padding(16.dp)) { innerTextField() }
                        },
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Botón de "Acceder"
                    Button(
                        onClick = {
                            if (correo.value.isNotEmpty() && nombre.value.isNotEmpty()) {
                                if (correo.value.contains("@")) {
                                    coroutineScope.launch {
                                        val user = usuarioRepo.getUsuarioByCorreo(correo.value)
                                        if (user == null) {
                                            navController.navigate("register")
                                            Toast.makeText(context, "Vaya, parece que no está registrado aún.", Toast.LENGTH_SHORT).show()
                                        } else {
                                            user.contadorAccesos++
                                            user.fechaUltimoAcceso = Date()
                                            usuarioRepo.updateUsuario(user)
                                            navController.navigate("welcome")
                                        }
                                    }
                                } else {
                                    Toast.makeText(context, "Correo inválido", Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                Toast.makeText(context, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFADD8E6)),
                    ) {
                        Text("Acceder", style = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp, color = Color(0xFF00008B)))
                    }

                    Button(
                        onClick = {
                            navController.navigate("register")
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFADD8E6))
                    ) {
                        Text("Registrar", style = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp, color = Color(0xFF00008B)))
                    }

                }
            }
        }
    )
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(navController: NavHostController) {
    val context = LocalContext.current
    val usuarioRepo = remember { UsuarioRepositorio.getInstance(context) }
    val correo = remember { mutableStateOf("") } // Guardar el correo
    val nombre = remember { mutableStateOf("") }
    val contadorAc = remember { mutableStateOf(0) }
    val coroutineScope = rememberCoroutineScope()

    // Cargar la imagen de fondo
    val fondo = painterResource(id = R.drawable.fondo)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registro", style = MaterialTheme.typography.titleLarge.copy(color = Color.White)) },
                colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = Color(0xFF2F6B3C))
            )
        }
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // Imagen de fondo
            Image(
                painter = fondo,
                contentDescription = "Fondo",
                modifier = Modifier.fillMaxSize()
            )

            // El contenido encima de la imagen
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Cuadro de fondo para el texto de "Nombre"
                Box(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(8.dp))
                        .padding(8.dp)
                ) {
                    Text(
                        "Ingrese el nombre de usuario:",
                        style = MaterialTheme.typography.bodyLarge.copy(fontSize = 15.sp, color = Color(0xFF00008B))
                    )
                }
                Spacer(modifier = Modifier.height(5.dp))

                // Campo de texto para el nombre
                BasicTextField(
                    value = nombre.value,
                    onValueChange = { nombre.value = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .border(1.dp, Color.Gray, shape = RoundedCornerShape(8.dp))
                        .background(Color(0xFFFFF8B5), shape = RoundedCornerShape(8.dp)),
                    singleLine = true,
                    decorationBox = { innerTextField -> Box(modifier = Modifier.padding(16.dp)) { innerTextField() } }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Cuadro de fondo para el texto de "Correo"
                Box(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(8.dp))
                        .padding(8.dp)
                ) {
                    Text(
                        "Ingrese el correo electrónico:",
                        style = MaterialTheme.typography.bodyLarge.copy(fontSize = 15.sp, color = Color(0xFF00008B))
                    )
                }
                Spacer(modifier = Modifier.height(5.dp))

                // Campo de texto para el correo - PRE-LLENADO con el correo recibido
                BasicTextField(
                    value = correo.value,
                    onValueChange = { correo.value = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .border(1.dp, Color.Gray, shape = RoundedCornerShape(8.dp))
                        .background(Color(0xFFFFF8B5), shape = RoundedCornerShape(8.dp)),
                    singleLine = true,
                    decorationBox = { innerTextField -> Box(modifier = Modifier.padding(16.dp)) { innerTextField() } }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Botón de "Registrar"
                Button(
                    onClick = {
                        if (nombre.value.isNotEmpty() && correo.value.isNotEmpty()) {
                            if (isEmailValid(correo.value)) {
                                coroutineScope.launch {
                                    // Convertir el nombre a mayúsculas
                                    val nombreMayusculas = nombre.value.toUpperCase(Locale.ROOT)

                                    val nuevoContador = contadorAc.value + 1
                                    contadorAc.value = nuevoContador

                                    usuarioRepo.addUsuario(
                                        Usuario(
                                            nombre = nombreMayusculas,
                                            correo = correo.value,
                                            fechaUltimoAcceso = Date(),
                                            contadorAccesos = nuevoContador
                                        )
                                    )
                                    navController.navigate("welcome") // Navegar a la pantalla de bienvenida
                                }
                            } else {
                                Toast.makeText(context, "Por favor, ingrese un correo válido", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(context, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFADD8E6)),
                ) {
                    Text("Registrar", style = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp, color = Color(0xFF00008B)))
                }
            }
        }
    }
}@Composable
fun WelcomeScreen(navController: NavHostController, usuario: Usuario?) {
    val context = LocalContext.current
    val handler = remember { Handler(Looper.getMainLooper()) }
    var isUserListOpened by remember { mutableStateOf(false) }
    var isWorkerRunning by remember { mutableStateOf(false) }

    // Función para mostrar el mensaje (Toast)
    fun showMessage(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    // Función para iniciar el worker que muestra la hora cada minuto
    fun startTimeWorker() {
        if (!isWorkerRunning) {
            isWorkerRunning = true

            handler.post(object : Runnable {
                override fun run() {
                    // Obtener la hora actual
                    val currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
                    showMessage("Son las $currentTime, es hora de consultar la api de consejos!")

                    // Repetir cada minuto
                    handler.postDelayed(this, 6000)
                }
            })
        }
    }

    // Función para detener el worker
    fun stopTimeWorker() {
        if (isWorkerRunning) {
            isWorkerRunning = false
            handler.removeCallbacksAndMessages(null) // Detener todas las tareas pendientes
        }
    }

    // Iniciar el worker al cargar la pantalla
    LaunchedEffect(Unit) {
        startTimeWorker() // Iniciar el worker al entrar en la pantalla
    }

    // Detener el worker al navegar a otra pantalla o al abrir la lista
    LaunchedEffect(isUserListOpened) {
        if (isUserListOpened) {
            stopTimeWorker() // Detener el worker cuando el usuario ha consultado la API
        }
    }

    // Crear un formateador para la fecha en formato "yyyy-MM-dd"
    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

    // Formatear la fecha, asegurando que 'usuario' no sea null
    val formattedDate = usuario?.fechaUltimoAcceso?.let { dateFormat.format(it) } ?: "No disponible"

    // Cargar la imagen de fondo
    val fondo = painterResource(id = R.drawable.fondoadvice)

    // El contenido encima de la imagen
    Scaffold { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            // Imagen de fondo
            Image(
                painter = fondo,
                contentDescription = "Fondo",
                modifier = Modifier
                    .fillMaxSize()
                    .align(Alignment.Center)
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Fecha de último acceso: $formattedDate",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Título de la pantalla
                Box(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Bienvenido!",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontStyle = FontStyle.Italic,
                            color = Color.Black
                        ),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Botón para obtener la lista
                Button(onClick = {
                    stopTimeWorker() // Detener el worker
                    navController.navigate("home") // Navegar a la siguiente pantalla
                    isUserListOpened = true // El usuario ha consultado la API
                }) {
                    Text("Obtener consejos")
                }
            }
        }
    }

    // Detener el worker cuando se salga de la pantalla
    DisposableEffect(Unit) {
        onDispose {
            stopTimeWorker() // Detener el worker al salir de la pantalla
        }
    }
}

@Composable
fun AdviceList(advices: List<Advice>) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(advices) { advice ->
            AdviceItem(advice)
        }
    }
}

@Composable
fun AdviceItem(advice: Advice) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Yellow, shape = RoundedCornerShape(8.dp))
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = advice.advice, style = MaterialTheme.typography.titleLarge)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    adviceViewModel: AdviceViewModel = viewModel(),
    navController: NavHostController
) {
    val advices by adviceViewModel.advices.collectAsState(initial = emptyList()) // Escucha la lista de consejos
    val context = LocalContext.current
    var expanded by remember { mutableStateOf(false) }
    val isLoading = advices.isEmpty() // Determina si los consejos aún se están cargando

    // Asegúrate de que el ViewModel haga la llamada para cargar los consejos si no se han cargado aún
    LaunchedEffect(Unit) {
        if (advices.isEmpty()) {
            adviceViewModel.fetchAdvices(10) // Esto debería ser el método para cargar los consejos
        }
    }



    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Lista de Consejos") },
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
            // Mostrar un mensaje mientras se cargan los consejos
            if (isLoading) {
                Text("Cargando consejos...")
            } else {
                AdviceList(advices) // Muestra la lista de consejos una vez cargados
            }
        }
    }
}

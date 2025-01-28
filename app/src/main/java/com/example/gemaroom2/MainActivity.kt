package com.example.gemaroom2

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
import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.media.Image
import android.os.Build
import android.os.Handler
import android.os.Looper
import androidx.compose.foundation.background
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.TopAppBar
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import com.example.gemaroom2.api.Advice
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.layout.ContentScale
import java.text.SimpleDateFormat


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
                composable("home") { HomeScreen(AdviceViewModel(), navController ) }
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
    val coroutineScope = rememberCoroutineScope()

        // Scaffold para contener la UI
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            "ConsejosVendoQueParaMiNoTengo",
                            style = MaterialTheme.typography.titleLarge.copy(color = Color.White)
                        )
                    },
                    colors = TopAppBarDefaults.smallTopAppBarColors(
                        containerColor = Color(0xFF6200EE) // Color de fondo del TopAppBar
                    ),
                    modifier = Modifier.shadow(4.dp, shape = MaterialTheme.shapes.small)
                )
            },
            content = { padding ->
                // Contenido principal dentro del Scaffold
                Column(
                    modifier = Modifier
                        .fillMaxSize() // Esto asegura que la columna ocupe toda la pantalla
                        .background(color = Color.LightGray)
                        .padding(16.dp)
                        .padding(padding), // Padding de Scaffold
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("Ingrese su correo:", style = MaterialTheme.typography.headlineMedium)
                    Spacer(modifier = Modifier.height(8.dp))

                    // Cuadro con un placeholder para el correo
                    TextField(
                        value = correo.value,
                        onValueChange = { correo.value = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .background(MaterialTheme.colorScheme.surface, shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)),
                        placeholder = { Text("Correo") },
                        singleLine = true,
                        colors = TextFieldDefaults.textFieldColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                            focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                            unfocusedIndicatorColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Botón de ingreso
                    Button(
                        onClick = {
                            if (isEmailValid(correo.value)) {
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
                            } else {
                                Toast.makeText(context, "Por favor, ingrese un correo válido", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text("Ingresar")
                    }
                }
            }
        )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(navController: NavHostController) {
    val context = LocalContext.current
    val usuarioRepo = remember { UsuarioRepositorio.getInstance(context) }
    val correo = remember { mutableStateOf("") }
    val nombre = remember { mutableStateOf("") }
    val contadorAc = remember { mutableStateOf(0) }
    val coroutineScope = rememberCoroutineScope()


    Column(
        modifier = Modifier
            .fillMaxSize() // Ocupa toda la pantalla
            .background(color = Color.LightGray)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally, // Centrado horizontal
        verticalArrangement = Arrangement.Center // Centrado vertical
    ) {
        Text("Ingrese su nombre:", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(8.dp))

        // Cuadro con un placeholder para el nombre
        TextField(
            value = nombre.value,
            onValueChange = { nombre.value = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .background(MaterialTheme.colorScheme.surface, shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)),
            placeholder = { Text("Nombre") }, // Placeholder en el cuadro de texto
            singleLine = true, // Para que sea solo una línea
            colors = TextFieldDefaults.textFieldColors(
                containerColor = MaterialTheme.colorScheme.surface,
                focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                unfocusedIndicatorColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text("Ingrese su correo:", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(8.dp))

        // Cuadro con un placeholder para el correo
        TextField(
            value = correo.value,
            onValueChange = { correo.value = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .background(MaterialTheme.colorScheme.surface, shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)),
            placeholder = { Text("Correo") }, // Placeholder en el cuadro de texto
            singleLine = true, // Para que sea solo una línea
            colors = TextFieldDefaults.textFieldColors(
                containerColor = MaterialTheme.colorScheme.surface,
                focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                unfocusedIndicatorColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

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
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text("Registrar")
        }

    }
}
@Composable
fun WelcomeScreen(navController: NavHostController) {
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
                    handler.postDelayed(this, 60 * 100)
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

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .background(color = Color.LightGray)
                .padding(padding)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Bienvenido!",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontStyle = FontStyle.Italic, // Cursiva
                        color = Color.Black // Color del texto
                    ),
                    textAlign = TextAlign.Center, // Centrado del texto
                    modifier = Modifier.fillMaxWidth() // Asegura que ocupe todo el ancho disponible
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = {
                // Detener el worker cuando se presiona el botón "Obtener lista"
                stopTimeWorker()

                // Navegar a la siguiente pantalla
                navController.navigate("home")
                isUserListOpened = true // El usuario ha consultado la API
            }) {
                Text("Obtener lista")
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
            Text(text = "Consejo", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = advice.advice, style = MaterialTheme.typography.bodyMedium)
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

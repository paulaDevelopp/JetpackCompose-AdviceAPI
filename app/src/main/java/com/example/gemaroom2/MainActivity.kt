package com.example.gemaroom2

import android.annotation.SuppressLint
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
import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.Color


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
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavHostController) {
    val context = LocalContext.current
    val usuarioRepo = remember { UsuarioRepositorio.getInstance(context) }
    val correo = remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize() // Ocupa toda la pantalla
            .background(Color(0xFFADD8E6)) // Fondo azul clarito
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally, // Centrado horizontal
        verticalArrangement = Arrangement.Center // Centrado vertical
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
            },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text("Ingresar")
        }
    }
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
            .background(Color(0xFFADD8E6)) // Fondo azul clarito
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

    // Función para mostrar la notificación
    fun showNotification() {
        val notificationManager = NotificationManagerCompat.from(context)

        val notification = NotificationCompat.Builder(context.applicationContext, "user_reminder_channel")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Recordatorio")
            .setContentText("Puedes consultar la información de la API")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        // Verificar si el permiso para notificaciones está concedido
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            notificationManager.notify(1, notification)
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(
                context as Activity,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                101
            )
        }
    }

    // Mostrar las notificaciones cada medio segundo mientras el usuario esté en la pantalla
    DisposableEffect(Unit) {
        // Crear canal de notificación
        createNotificationChannel(context)

        // Función para enviar notificaciones periódicas
        val runnable = object : Runnable {
            override fun run() {
                if (!isUserListOpened) {
                    showNotification()
                    handler.postDelayed(this, 500) // 500ms = 0.5 segundos
                }
            }
        }

        handler.post(runnable)

        onDispose {
            handler.removeCallbacksAndMessages(null) // Limpiar el handler cuando se salga de la pantalla
        }
    }

    // Detener las notificaciones cuando se haya consultado la API
    LaunchedEffect(isUserListOpened) {
        if (isUserListOpened) {
            handler.removeCallbacksAndMessages(null) // Detener las notificaciones
        }
    }

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
            Button(onClick = {
                navController.navigate("home")
                isUserListOpened = true // El usuario ha consultado la API
            }) {
                Text("Obtener lista")
            }
        }
    }
}

fun createNotificationChannel(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            "user_reminder_channel", // ID del canal
            "User Reminder", // Nombre del canal
            NotificationManager.IMPORTANCE_HIGH // Importancia de las notificaciones
        ).apply {
            description = "Canal para recordar a los usuarios" // Descripción del canal
        }
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
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

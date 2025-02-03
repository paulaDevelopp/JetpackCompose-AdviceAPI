## TRABAJO FINAL MÓVILES- PAULA MARTÍNEZ NOVAS
# 📱 Aplicación de Registro de Usuarios y Consulta de API  

# 📝 Descripción  

Esta aplicación permite a los usuarios registrarse e iniciar sesión con su **nombre y correo electrónico**. Se almacena un historial de accesos en una base de datos local utilizando **Room**, registrando la fecha y el número de accesos.  

Una vez autenticado, el usuario puede consultar datos, que en este caso son consejos de una **API pública** utilizando **Retrofit**. Mientras no está consultando la API, se visualiza una notificación que muestra este mensaje *'Son las $currentTime, es hora de consultar la api de consejos!'*

---

# 🚀 Funcionalidades  

### 🔹 Registro e inicio de sesión  
- Los usuarios ingresan su **nombre y correo electrónico**; si no se encuentra el correo en la base de datos, te redirige a la pantalla de registro. 
- Una vez registrado, el nombre de usuario y el correo se almacenan en **Room** junto con la fecha del último acceso y el contador de accesos.   

### 🔹 Consulta de datos de API (Retrofit)  
- Se conecta a la API pública [API ADVICE](https://api.adviceslip.com/).  
- Se obtiene una lista de **10 consejos**.  
- La consulta se activa al presionar el botón 'Obtener consjeos' en la pantalla principal.  

### 🔹 Notificaciones  
- Se muestra una **notificación al acceder a la app**.  
- Un **hilo Runnable** recuerda al usuario que consulte la API.  
- Las notificaciones periódicas se detienen una vez realizada la consulta pero vuelven a mostrarse si volvemos a la pantalla principal.

### 🔹 Flujo de navegación  
- Se implementa con **NavController** para gestionar transiciones entre pantallas:  
  - **Login** → Si el usuario existe, accede directamente.  
  - **Registro** → Si el usuario no está registrado, se redirige a esta pantalla.  
  - **Pantalla de consulta** → Donde el usuario obtiene los datos de la API.  
  - **Volver o cerrar la app** desde la pantalla de consulta.  

---


# 📊 Análisis de Decisiones en el Desarrollo  

## 🔄 1. Organización del Proyecto  

**`app/`**  
  - 📄 `AndroidManifest.xml` _(Archivo de configuración principal de la app)_  
  - **`com.example.gemaroom2/`**  
    - **`api/`** 🌐 _Módulo para la gestión de API con Retrofit_  
     
      - 📄 `ApiService` _(Interfaz Retrofit para la API)_  
      - 📄 `RetrofitInstance` _(Configuración de Retrofit)_
      - 📄 `Advice` _(Representa el JSON recibido)_
      - 📄 `AdviceViewModel` _(Obtiene los datos y los expone en la UI)_
    - **`data/`** 💾 _Módulo de base de datos con Room_  
      - 📄 `Converter` _(Conversores para Room)_  
      - 📄 `Usuario` _(Modelo de datos del usuario)_  
      - 📄 `UsuarioDAO` _(DAO para operaciones en la base de datos)_  
      - 📄 `UsuarioDB` _(Base de datos Room)_  
      - 📄 `UsuarioRepositorio` _(Repositorio de usuario)_   
  - 📄 `MainActivity.kt` _(Contiene toda la lógica de navegación de la app)_  

---

## 🖥️ 2. Pantallas Implementadas  
---
### 📝 **Inicio de Sesión (`LoginScreen`)**  
- Permite que el usuario ingrese su **nombre** y **correo electrónico**.  
- **Validaciones**:  
  - Se verifica que el nombre y el correo no estén vacíos.
  - Se valida la forma del correo electrónico.
  - Se comprueba que el correo existe en la bbdd, si no, se le redirige a la pantalla de     Registro.  
- Al iniciar sesión:    
  - Se actualiza el **contador de accesos** del usuario y la **fecha del último acceso**.  
  - Se navega a la pantalla de bienvenida (`WelcomeScreen`).  
- **Interfaz**:  
  - Uso de `BasicTextField` con un diseño personalizado.  
  - Botón de acceso.
  - Toast con las validaciones con mensajes como 'Completa todos los campos'.  
  - Botón de registarse para ir directamente a la pantalla de registro.

---
### 📝 **Registro de Usuario (`RegisterScreen`)**  
- Permite que el usuario ingrese su **nombre** y **correo electrónico**.  
- **Validaciones**:  
  - Se verifica que el nombre y el correo no estén vacíos.  
  - Se valida que el correo tenga un formato válido.  
- Al registrarse:  
  - Se almacena el usuario en la base de datos **Room**.   
  - Se actualiza el **contador de accesos** del usuario, en este caso a 1, ya que es la 1ª vez que accede, y la **fecha del último acceso**.  
  - Se navega a la pantalla de bienvenida (`WelcomeScreen`).  
- **Interfaz**:  
  - Uso de `BasicTextField` con un diseño personalizado.  
  - Botón de registro con validaciones previas.  

---

### 🎉 **Pantalla de Bienvenida (`WelcomeScreen`)**  
- Muestra la **fecha y la hora del último acceso** del usuario.  
- Implementa un **hilo Runnable** que, cada 6 segundos, muestra un recordatorio con la hora actual.  
- **Manejo del hilo**:  
  - Inicia automáticamente cuando se carga la pantalla.  
  - Se detiene si el usuario consulta la API de consejos.  
  - Se reanuda si el usuario vuelve a esta página. 

- Botón para **obtener consejos**, que navega a (`HomeScreen`).  

---

### 📜 **Pantalla de Consejos (`HomeScreen`)**  
- Carga una lista de consejos desde una API usando **Retrofit y ViewModel**.  
- **Manejo de datos con ViewModel**:  
  - `fetchAdvices(10)` obtiene 10 consejos desde la API.  
  - Se usa `collectAsState()` para escuchar cambios en la lista de consejos.  
  - Solo se hace la petición si la lista está vacía, evitando recargas innecesarias.  
- **Interfaz**:  
  - Se muestra un mensaje de **"Cargando consejos..."** hasta que se obtienen los datos.  
  - Usa `LazyColumn` para mostrar la lista de consejos.  
  - Cada consejo se muestra dentro de un `Card` con diseño personalizado.  
- **Menú superior** con opciones para:  
  - **Volver atrás** (esto hace que se reanude el hilo)
  - **Cerrar la aplicación**.  

---

## 📡 3. Integración de la API con Retrofit  
- Se decisió usar la API [API ADVICE](https://api.adviceslip.com/).  
- Se implementó un `ViewModel` para manejar los datos de la API de forma reactiva.  
- Se empleó un `Retrofit` porque esácil de usar: define una interfaz con @GET, @POST, etc. y ya puedes hacer llamadas a una API. Convierte JSON a objetos Kotlin/Java automáticamente con GsonConverterFactory. Y soporta llamadas asíncronas con Call y Coroutines.

## 🔔 4. Implementación de Notificaciones  
- Se implementaron notificaciones locales para recordar al usuario que puede consultar los datos de la API.  
- Se utilizó un `Runnable` para enviar recordatorios periódicos.  
- Se agregó una condición para detener las notificaciones una vez que el usuario realiza la consulta a la API.  

## 🚀 6. Decisiones sobre la Experiencia de Usuario (UX)  
- Se implementó una interfaz intuitiva con Material 3 y colores pastel.  
- Se agregaron mensajes `Toast` para dar retroalimentación al usuario sobre validaciones. 
- Se usaron `BasicTextField` con bordes redondeados para mejorar la estética.  
- Se incluyó validación de correo electrónico para evitar entradas inválidas.  

---
---
# 🔧 Pasos para ejecutar el proyecto  
```
1️⃣ Clonar el repositorio 
   git clone https://github.com/paulaDevelopp/JetpackCompose-AdviceAPI.git
   
2️⃣ Abrir en Android Studio

3️⃣ Instalar dependencias en build.gradle

implementation ("androidx.room:room-runtime:2.4.2")
implementation ("com.squareup.retrofit2:retrofit:2.9.0")
implementation ("androidx.navigation:navigation-compose:2.5.3")

4️⃣ Ejecutar la aplicación en un emulador o dispositivo físico.
```
🔍 Búsquedas realizadas
- Room para almacenamiento local. Se investigó cómo crear una base de datos local para almacenar el usuario.
Se utilizó Room por su integración con Jetpack y su facilidad de uso.
-  Retrofit para API REST por su flexibilidad, facilidad de manejo y compatibilidad con Gson.
-  Notificaciones y hilos en segundo plano. Se investigaron métodos para mostrar notificaciones periódicas.
Se implementó un Runnable con Handler para gestionar los recordatorios.
- Navegación con NavController.
Se eligió NavController para gestionar cambios entre pantallas de manera eficiente.
---
---
# 📜 Commits


### 1. Configuración de la BBDD con Room
Este commit establece la configuración de la base de datos local utilizando Room para almacenar los datos de los usuarios. Los aspectos clave incluyen:

- Converter: Un conversor para manejar las fechas, convirtiéndolas entre tipos Date y Long (timestamp).
- Entidad Usuario: Representa la tabla Usuario en la base de datos.
- DAO: Define las operaciones que se pueden realizar sobre la tabla Usuario.
- Base de datos UsuarioDB: Configura la base de datos de Room para almacenar usuarios.
- Repositorio UsuarioRepositorio: Abstrae el acceso a la base de datos y proporciona una capa intermedia para la gestión de datos de usuarios.

---

### 2. Acceso a la API y se muestran los datos recibidos de esta
En este commit se implementa la integración con la API de Advice Slip, permitiendo a la aplicación obtener consejos aleatorios y mostrarlos en la interfaz de usuario. Las clases involucradas son:

- RetrofitInstance: Configura la instancia de Retrofit para hacer las peticiones a la API.
- ApiService: Define los métodos para interactuar con la API.
- AdviceViewModel: Maneja la lógica de negocio y la obtención de consejos desde la API.
- AdviceResponse y Advice: Representan la estructura de los datos que se reciben de la API.

 ### 3. Notificación configurada
En este primer commit se configura la funcionalidad básica para enviar notificaciones periódicas al usuario. Se realiza lo siguiente:
- Notificaciones cada 0.5 segundos en la pantalla de bienvenida para recordar al usuario consultar la API, se envían como un mensaje emergente (push notification) que puede aparecer en la barra de notificaciones del dispositivo, o como una alerta en la pantalla si la notificación se configura para hacerlo.
- Las notificaciones se detienen cuando el usuario navega a la pantalla de lista de usuarios.
- Se verifican y solicitan los permisos necesarios para mostrar notificaciones.
- Se crea un canal de notificación para dispositivos con Android Oreo o superior.

### 4. Cambio de forma de la notificación
En este segundo commit se mejora la forma de la notificación para hacerla más informativa y visualmente atractiva. Los cambios incluyen:
-  Se muestra en mensajes Toast.
- El worker se inicia al cargar la pantalla y se detiene cuando el usuario navega a la pantalla de lista de usuarios.
- La fecha de último acceso del usuario se muestra en la pantalla, formateada en "yyyy-MM-dd HH:mm:ss".
- Se utiliza una imagen de fondo en la pantalla.
- El botón "Obtener consejos" navega a la pantalla "home" y detiene el worker cuando el usuario consulta la API.

### 5. Retoques finales, fondos de pantalla y mostramos la hora y fecha en la pantalla principal.
---
---


# 🏗️ Buenas Prácticas Aplicadas

✔ Uso de Jetpack Compose → Interfaz declarativa moderna y eficiente.

✔ Persistencia con Room → Almacena datos de usuario localmente de forma eficiente.

✔ Llamadas a API con Retrofit → Gestión de datos externos sin bloquear la UI.

✔ Navegación estructurada → Uso de NavController para facilitar la transición entre pantallas.

✔ Manejo eficiente de notificaciones → Se detienen cuando el usuario consulta la API.

---
---
Este proyecto es una forma sencilla de integrar varias funciones en una aplicación Android. Permite registrar usuarios, almacenar datos localmente, conectarse con APIs para obtener información y enviar notificaciones, todo usando herramientas como Jetpack Compose, Room, Retrofit y NavController. Es una buena base para desarrollar aplicaciones móviles que necesiten autenticación, consultar datos desde internet y gestionar accesos.

📌 Autor: Paula Martínez Novas

📌 Repositorio: [GitHub](https://github.com/paulaDevelopp/JetpackCompose-AdviceAPI)

📌 API utilizada: https://api.adviceslip.com/

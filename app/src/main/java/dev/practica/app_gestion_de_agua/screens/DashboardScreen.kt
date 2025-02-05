package dev.practica.app_gestion_de_agua.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import dev.practica.app_gestion_de_agua.Notificacion

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(navController: NavController) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val database = FirebaseDatabase.getInstance().getReference("niveles_agua")

    var nivelAgua by remember { mutableStateOf(0.0) }
    var volumen by remember { mutableStateOf(0.0) }
    var coordenadas by remember { mutableStateOf("") }
    var escala by remember { mutableStateOf("") }
    var alerta by remember { mutableStateOf<String?>(null) }

    // Leer datos desde Firebase
    LaunchedEffect(Unit) {
        database.orderByChild("nodo").equalTo("nodo1").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var ultimaMedicion: DataSnapshot? = null
                for (dato in snapshot.children) {
                    val timestamp = dato.child("timestamp").getValue(String::class.java) ?: ""
                    if (ultimaMedicion == null || timestamp > (ultimaMedicion.child("timestamp").getValue(String::class.java) ?: "")) {
                        ultimaMedicion = dato
                    }
                }
                ultimaMedicion?.let {
                    nivelAgua = it.child("nivel_agua_cm").getValue(Double::class.java) ?: 0.0
                    volumen = it.child("volumen_litros").getValue(Double::class.java) ?: 0.0
                    coordenadas = it.child("coordenadas").getValue(String::class.java) ?: "Desconocido"
                    escala = it.child("escala").getValue(String::class.java) ?: "Desconocido"
                    alerta = it.child("alerta").getValue(String::class.java)

                    alerta?.let { mensaje ->
                        Notificacion.enviarNotificacion(context, "Alerta de Agua", mensaje)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Error al obtener datos: ${error.message}")
            }
        })
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Medida actual") },
                actions = {
                    IconButton(
                        onClick = {
                            auth.signOut()
                            Toast.makeText(context, "Sesión cerrada", Toast.LENGTH_SHORT).show()
                            navController.navigate("login") { popUpTo("login") { inclusive = true } }
                        }
                    ) {
                        Icon(imageVector = Icons.Filled.ExitToApp, contentDescription = "Cerrar sesión")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("AQUAWATCH", fontSize = 24.sp, color = Color.Black)
            Spacer(modifier = Modifier.height(16.dp))

            Text("Nivel de agua: $nivelAgua cm", fontSize = 20.sp, color = Color.Black)
            Spacer(modifier = Modifier.height(8.dp))

            Text("Volumen: $volumen L", fontSize = 20.sp, color = Color.Black)
            Spacer(modifier = Modifier.height(8.dp))

            Text("Coordenadas: $coordenadas", fontSize = 16.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(8.dp))

            Text("Escala: $escala", fontSize = 20.sp, color = when (escala) {
                "Malo" -> Color.Red
                "Normal" -> Color.Yellow
                "Bueno" -> Color.Green
                else -> Color.Gray
            })
        }
    }
}

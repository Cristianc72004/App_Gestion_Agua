package dev.practica.app_gestion_de_agua.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
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
    var coordenadas by remember { mutableStateOf("Desconocido") }
    var escala by remember { mutableStateOf("Desconocido") }
    var alerta by remember { mutableStateOf<String?>(null) }

    // Escuchar cambios en Firebase en tiempo real
    LaunchedEffect(Unit) {
        database.orderByChild("nodo").equalTo("nodo1").addValueEventListener(object : ValueEventListener {
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
                title = {
                    Text(
                        "Estado del Agua",
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        fontFamily = FontFamily.SansSerif
                    )
                },
                actions = {
                    IconButton(
                        onClick = {
                            auth.signOut()
                            Toast.makeText(context, "Sesión cerrada", Toast.LENGTH_SHORT).show()
                            navController.navigate("login") { popUpTo("login") { inclusive = true } }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Cerrar sesión",
                            tint = Color.Red
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        colors = listOf(Color(0xFF0D47A1), Color(0xFF1976D2))
                    )
                )
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                "AQUAWATCH",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontFamily = FontFamily.SansSerif
            )

            // Contenedores de información con colores y diseño moderno
            InfoCard(
                title = "Nivel de Agua",
                value = "$nivelAgua cm",
                icon = Icons.Filled.WaterDrop,
                backgroundColor = Color(0xFF42A5F5)
            )

            InfoCard(
                title = "Volumen de Agua",
                value = "$volumen L",
                icon = Icons.Filled.Notifications,
                backgroundColor = Color(0xFF66BB6A)
            )

            InfoCard(
                title = "Ubicación",
                value = coordenadas,
                icon = Icons.Filled.LocationOn,
                backgroundColor = Color(0xFFFFA726)
            )

            InfoCard(
                title = "Estado del Agua",
                value = escala,
                icon = Icons.Filled.Warning,
                backgroundColor = when (escala) {
                    "Malo" -> Color.Red
                    "Normal" -> Color.Yellow
                    "Bueno" -> Color.Green
                    else -> Color.Gray
                }
            )

            alerta?.let {
                InfoCard(
                    title = "Alerta",
                    value = it,
                    icon = Icons.Filled.Warning,
                    backgroundColor = Color(0xFFD32F2F)
                )
            }
        }
    }
}

// 📌 Componente de Tarjeta de Información
@Composable
fun InfoCard(title: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector, backgroundColor: Color) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(6.dp)
            .shadow(8.dp, RoundedCornerShape(16.dp))
            .animateContentSize(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Row(
            modifier = Modifier
                .padding(18.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    title,
                    fontSize = 20.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.SansSerif
                )
                Text(
                    value,
                    fontSize = 24.sp,
                    color = Color.White,
                    fontFamily = FontFamily.SansSerif
                )
            }
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = Color.White,
                modifier = Modifier.size(36.dp)
            )
        }
    }
}

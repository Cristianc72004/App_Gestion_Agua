package dev.practica.app_gestion_de_agua.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen() {
    var waterLevel by remember { mutableStateOf(50f) } // Nivel del agua en %

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("AQUAWATCH", fontSize = 24.sp, color = Color.Black)
        Spacer(modifier = Modifier.height(16.dp))

        WaterTank()
        Spacer(modifier = Modifier.height(16.dp))

        Text("% restante", fontSize = 20.sp, color = Color.Black)
        Spacer(modifier = Modifier.height(8.dp))

        Text("Litros restantes", fontSize = 20.sp, color = Color.Black)
        Spacer(modifier = Modifier.height(8.dp))

        Text("Conectado", fontSize = 20.sp, color = Color.Green)
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = { /* Acción de conectar */ },
                colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Conectar", color = Color.Black)
            }
            Button(
                onClick = { /* Acción de desconectar */ },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Blue),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Desconectar", color = Color.White)
            }
        }
    }
}

@Composable
fun WaterTank() {
    Canvas(
        modifier = Modifier
            .width(140.dp)
            .height(220.dp)
    ) {
        // Dibujar el cilindro principal
        drawOval(
            color = Color.Black,
            topLeft = Offset(0f, 0f),
            size = Size(size.width, 40f),
            style = Stroke(width = 4f)
        )
        drawLine(
            color = Color.Black,
            start = Offset(0f, 20f),
            end = Offset(0f, size.height - 20f),
            strokeWidth = 4f
        )
        drawLine(
            color = Color.Black,
            start = Offset(size.width, 20f),
            end = Offset(size.width, size.height - 20f),
            strokeWidth = 4f
        )
        drawOval(
            color = Color.Black,
            topLeft = Offset(0f, size.height - 40f),
            size = Size(size.width, 40f),
            style = Stroke(width = 4f)
        )
    }
}

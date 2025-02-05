package dev.practica.app_gestion_de_agua

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import dev.practica.app_gestion_de_agua.screens.DashboardScreen
import dev.practica.app_gestion_de_agua.screens.LoginScreen
import dev.practica.app_gestion_de_agua.screens.RegisterScreen
import dev.practica.app_gestion_de_agua.ui.theme.APP_GESTION_DE_AGUATheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            APP_GESTION_DE_AGUATheme {
                // Solicitar permisos de notificación en Android 13+
                val requestPermissionLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestPermission(),
                    onResult = { isGranted -> }
                )

                LaunchedEffect(Unit) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                }

                // Suscribirse a Firebase Cloud Messaging (FCM)
                LaunchedEffect(Unit) {
                    FirebaseMessaging.getInstance().subscribeToTopic("alertas_agua")
                        .addOnCompleteListener { task ->
                            if (!task.isSuccessful) {
                                println("Error al suscribirse a notificaciones de agua")
                            }
                        }
                }

                val auth = FirebaseAuth.getInstance()
                val startDestination = if (auth.currentUser != null) "dashboard" else "login"
                AppNavigation(startDestination)
            }
        }
    }
}

@Composable
fun AppNavigation(startDestination: String) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = startDestination) {
        composable("login") {
            LoginScreen(
                onNavigateToRegister = { navController.navigate("register") },
                onLoginSuccess = { navController.navigate("dashboard") }
            )
        }
        composable("register") {
            RegisterScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable("dashboard") {
            DashboardScreen(navController = navController)
        }
    }
}

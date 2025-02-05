package dev.practica.app_gestion_de_agua

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dev.practica.app_gestion_de_agua.screens.DashboardScreen
import dev.practica.app_gestion_de_agua.screens.LoginScreen
import dev.practica.app_gestion_de_agua.screens.RegisterScreen
import dev.practica.app_gestion_de_agua.ui.theme.APP_GESTION_DE_AGUATheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            APP_GESTION_DE_AGUATheme {
                AppNavigation()
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController: NavHostController = rememberNavController()
    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(onNavigateToRegister = {
                navController.navigate("register")
            }, onLoginSuccess = {
                navController.navigate("dashboard")
            })
        }
        composable("register") {
            RegisterScreen(onNavigateBack = {
                navController.popBackStack()
            })
        }
        composable("dashboard") {
            DashboardScreen()
        }
    }
}


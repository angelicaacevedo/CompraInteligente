package br.com.angelica.comprainteligente.presentation.navigation

import HomeScreen
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import br.com.angelica.comprainteligente.presentation.view.LoginScreen
import br.com.angelica.comprainteligente.presentation.view.RegisterScreen

@Composable
fun NavGraph() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "Login") {
        composable("login") { LoginScreen(navController) }
        composable("register") { RegisterScreen(navController) }
        composable("home") { HomeScreen(navController) }
        composable("lists") { /* Tela para Listas */ }
        composable("add_product") { /* Tela para adicionar produto */ }
        composable("reports") { /* Tela para relatórios */ }
        composable("profile") { /* Tela de perfil */ }
    }
}
package br.com.angelica.comprainteligente.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import br.com.angelica.comprainteligente.presentation.view.AddProductScreen
import br.com.angelica.comprainteligente.presentation.view.HomeScreen
import br.com.angelica.comprainteligente.presentation.view.LoginScreen
import br.com.angelica.comprainteligente.presentation.view.ProductDetailsScreen
import br.com.angelica.comprainteligente.presentation.view.RegisterScreen
import br.com.angelica.comprainteligente.presentation.view.ShoppingListScreen

@Composable
fun NavGraph() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "login") {
        composable("login") { LoginScreen(navController) }
        composable("register") { RegisterScreen(navController) }
        composable("home") { HomeScreen(navController) }
        composable("lists") {
            ShoppingListScreen(
                onNavigateBack = { navController.popBackStack() },
            )
        }
        composable("add_product") {
            AddProductScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable("products_details/{productId}") { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId") ?: ""
            ProductDetailsScreen(productId)
        }
        composable("reports") { /* Tela para relatórios */ }
        composable("profile") { /* Tela de perfil */ }
    }
}
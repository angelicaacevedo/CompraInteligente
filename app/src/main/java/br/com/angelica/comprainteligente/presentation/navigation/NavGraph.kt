package br.com.angelica.comprainteligente.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import br.com.angelica.comprainteligente.presentation.view.AddProductScreen
import br.com.angelica.comprainteligente.presentation.view.HomeScreen
import br.com.angelica.comprainteligente.presentation.view.LoginScreen
import br.com.angelica.comprainteligente.presentation.view.PersonalizeScreen
import br.com.angelica.comprainteligente.presentation.view.ProductDetailsScreen
import br.com.angelica.comprainteligente.presentation.view.ProfileScreen
import br.com.angelica.comprainteligente.presentation.view.RegisterScreen
import br.com.angelica.comprainteligente.presentation.view.ReportsScreen
import br.com.angelica.comprainteligente.presentation.view.ShoppingListScreen

@Composable
fun NavGraph() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "home") {
        composable("login") { LoginScreen(navController) }
        composable("register") { RegisterScreen(navController) }
        composable("home") { HomeScreen(navController) }
        composable("cart") {
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
        composable("reports") { ReportsScreen(navController) }
        composable("profile") { ProfileScreen(navController) }
        composable("personalize") { PersonalizeScreen(navController) }
    }
}
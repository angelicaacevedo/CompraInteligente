package br.com.angelica.comprainteligente.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import br.com.angelica.comprainteligente.presentation.view.HomeScreen
import br.com.angelica.comprainteligente.presentation.view.ListDetailScreen
import br.com.angelica.comprainteligente.presentation.view.LoginScreen
import br.com.angelica.comprainteligente.presentation.view.NewListScreen
import br.com.angelica.comprainteligente.presentation.view.ProductListScreen
import br.com.angelica.comprainteligente.presentation.view.ProductRegisterScreen
import br.com.angelica.comprainteligente.presentation.view.RegisterScreen

@Composable
fun AppNavigation() {
    val navController: NavHostController = rememberNavController()

    NavHost(navController = navController, startDestination = "home") {
        // Login Screen
        composable("login") {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate("register")
                }
            )
        }

        // Register Screen
        composable("register") {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate("home") {
                        popUpTo("register") { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.navigate("login")
                }
            )
        }

        // Home Screen
        composable("home") {
            HomeScreen(navController = navController)
        }

        // Product Register Screen
        composable("add_product") {
            ProductRegisterScreen(
                onBack = { navController.popBackStack() },
                onProductRegistered = {
                    navController.navigate("home")
                }
            )
        }

        // Product List History Screen
        composable("list_history") {
            ProductListScreen(
                onBack = { navController.popBackStack() },
                onNavigateToCreateList = {
                    navController.navigate("create_list")
                },
                onNavigateToListItems = { productIds ->
                    navController.navigate("list_items/${productIds.joinToString(",")}")
                }
            )
        }

        // Create New List Screen
        composable("create_list") {
            NewListScreen(
                onBack = { navController.popBackStack() },
                onListCreated = {
                    navController.navigate("list_history")
                }
            )
        }

        // Navegação para a tela de itens da lista
        composable(
            "list_items/{productIds}",
            arguments = listOf(navArgument("productIds") { type = NavType.StringType })
        ) { backStackEntry ->
            val productIds =
                backStackEntry.arguments?.getString("productIds")?.split(",") ?: emptyList()
            ListDetailScreen(
                productIds = productIds,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
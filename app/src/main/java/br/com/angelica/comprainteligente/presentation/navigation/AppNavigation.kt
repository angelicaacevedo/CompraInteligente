package br.com.angelica.comprainteligente.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import br.com.angelica.comprainteligente.presentation.view.HomeScreen
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
                onProductRegistered = {
                    navController.navigate("home")
                }
            )
        }

        // Product List History Screen
        composable("list_history") {
            ProductListScreen(
                onNavigateToCreateList = {
                    navController.navigate("create_list")
                }
            )
        }

        // Create New List Screen
        composable("create_list") {
            NewListScreen(
                onListCreated = {
                    navController.navigate("list_history")
                }
            )
        }
    }
}
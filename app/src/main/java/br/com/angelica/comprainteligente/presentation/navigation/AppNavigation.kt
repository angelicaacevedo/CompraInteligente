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
import br.com.angelica.comprainteligente.presentation.view.HistoryListScreen
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
            HistoryListScreen(
                onBack = { navController.navigate("home") },
                onNavigateToCreateList = {
                    navController.navigate("create_list") // Navegar para a rota sem argumentos
                },
                onNavigateToListItems = { listId, listName, productIds -> // Passe o listName também
                    navController.navigate("list_items/${listId}/${listName}/${productIds.joinToString(",")}")
                }
            )
        }

        // Create New List Screen without arguments
        composable("create_list") {
            NewListScreen(
                onBack = { navController.popBackStack() },
                listId = null, // Não há listId porque é uma nova lista
                listNameArg = "",
                productIdsArg = emptyList(),
                onListCreated = {
                    navController.navigate("list_history")
                }
            )
        }


        // Create New List Screen
        composable(
            "create_list/{listId}/{listName}/{productIds}",
            arguments = listOf(
                navArgument("listId") { type = NavType.StringType },
                navArgument("listName") { type = NavType.StringType },
                navArgument("productIds") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val listId = backStackEntry.arguments?.getString("listId") ?: ""
            val listName = backStackEntry.arguments?.getString("listName") ?: ""
            val productIds = backStackEntry.arguments?.getString("productIds")?.split(",") ?: emptyList()

            NewListScreen(
                onBack = { navController.popBackStack() },
                listId = listId,
                listNameArg = listName,
                productIdsArg = productIds,
                onListCreated = {
                    navController.navigate("list_history")
                }
            )
        }

        // Navegação para a tela de itens da lista
        composable(
            "list_items/{listId}/{listName}/{productIds}",
            arguments = listOf(
                navArgument("listId") { type = NavType.StringType },
                navArgument("listName") { type = NavType.StringType },
                navArgument("productIds") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val listId = backStackEntry.arguments?.getString("listId") ?: ""
            val productIds = backStackEntry.arguments?.getString("productIds")?.split(",") ?: emptyList()
            val listName = backStackEntry.arguments?.getString("listName") ?: ""

            ListDetailScreen(
                listId = listId,
                productIds = productIds,
                listName = listName,
                onBack = { navController.popBackStack() },
                onEditList = { id, name, ids ->
                    navController.navigate("create_list/$id/${name}/${ids.joinToString(",")}")
                }
            )
        }
    }
}
package br.com.angelica.comprainteligente.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import br.com.angelica.comprainteligente.presentation.view.HistoryListScreen
import br.com.angelica.comprainteligente.presentation.view.HomeScreen
import br.com.angelica.comprainteligente.presentation.view.ListDetailScreen
import br.com.angelica.comprainteligente.presentation.view.LoginScreen
import br.com.angelica.comprainteligente.presentation.view.NewListScreen
import br.com.angelica.comprainteligente.presentation.view.PriceComparisonScreen
import br.com.angelica.comprainteligente.presentation.view.ProductRegisterScreen
import br.com.angelica.comprainteligente.presentation.view.RegisterScreen

@Composable
fun AppNavigation(userId: String) { // Recebe o userId como argumento
    val navController: NavHostController = rememberNavController()

    NavHost(navController = navController, startDestination = "login") {
        // Login Screen
        composable("login") {
            LoginScreen(
                onLoginSuccess = { loginUserId ->
                    navController.navigate("home/$loginUserId") {
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
                onRegisterSuccess = { registerUserId ->
                    navController.navigate("home/$registerUserId") {
                        popUpTo("register") { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.navigate("login")
                }
            )
        }

        // Home Screen with userId
        composable(
            "home/{userId}",
            arguments = listOf(navArgument("userId") { type = NavType.StringType })
        ) { backStackEntry ->
            val currentUserId = backStackEntry.arguments?.getString("userId") ?: userId
            HomeScreen(navController = navController, currentUserId)
        }


        // Product Register Screen
        composable(
            "add_product/{userId}",
            arguments = listOf(navArgument("userId") { type = NavType.StringType })
        ) { backStackEntry ->
            val currentUserId = backStackEntry.arguments?.getString("userId") ?: userId
            ProductRegisterScreen(
                onBack = { navController.navigate("home/$currentUserId") },
                onProductRegistered = {
                    navController.navigate("home/$currentUserId") {
                        popUpTo("home/$currentUserId") { inclusive = true }
                    }
                },
                userId = currentUserId
            )
        }

        // Price Comparison Screen with userId
        composable(
            "price_comparison/{userId}",
            arguments = listOf(navArgument("userId") { type = NavType.StringType })
        ) { backStackEntry ->
            val currentUserId = backStackEntry.arguments?.getString("userId") ?: userId
            PriceComparisonScreen(
                onBackClick = { navController.navigate("home/$userId") },
                userId = currentUserId
            )
        }

        // Product List History Screen with userId
        composable(
            "list_history/{userId}",
            arguments = listOf(navArgument("userId") { type = NavType.StringType })
        ) { backStackEntry ->
            val currentUserId = backStackEntry.arguments?.getString("userId") ?: userId
            HistoryListScreen(
                onBack = { navController.navigate("home/$currentUserId") },
                onNavigateToCreateList = {
                    navController.navigate("create_list/$currentUserId") // Navega para a criação de lista com o userId
                },
                onNavigateToListItems = { listId, listName, productIds ->
                    navController.navigate(
                        "list_items/$currentUserId/$listId/$listName/${productIds.joinToString(",")}"
                    )
                },
                userId = currentUserId
            )
        }

        // Create New List Screen without arguments, for new list creation
        composable(
            "create_list/{userId}",
            arguments = listOf(navArgument("userId") { type = NavType.StringType })
        ) { backStackEntry ->
            val currentUserId = backStackEntry.arguments?.getString("userId") ?: userId
            NewListScreen(
                onBack = { navController.navigate("list_history/$currentUserId") },
                listId = null,
                listNameArg = "",
                productIdsArg = emptyList(),
                onListCreated = {
                    navController.navigate("list_history/$currentUserId")
                },
                userId = currentUserId
            )
        }

        // Edit Existing List Screen with userId, listId, listName, productIds
        composable(
            "create_list/{userId}/{listId}/{listName}/{productIds}",
            arguments = listOf(
                navArgument("userId") { type = NavType.StringType },
                navArgument("listId") { type = NavType.StringType },
                navArgument("listName") { type = NavType.StringType },
                navArgument("productIds") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val currentUserId = backStackEntry.arguments?.getString("userId") ?: userId
            val listId = backStackEntry.arguments?.getString("listId") ?: ""
            val listName = backStackEntry.arguments?.getString("listName") ?: ""
            val productIds =
                backStackEntry.arguments?.getString("productIds")?.split(",") ?: emptyList()

            NewListScreen(
                onBack = { navController.popBackStack() },
                listId = listId,
                listNameArg = listName,
                productIdsArg = productIds,
                onListCreated = {
                    navController.navigate("list_history/$currentUserId")
                },
                userId = currentUserId
            )
        }

        // List Items Screen with userId, listId, listName, productIds
        composable(
            "list_items/{userId}/{listId}/{listName}/{productIds}",
            arguments = listOf(
                navArgument("userId") { type = NavType.StringType },
                navArgument("listId") { type = NavType.StringType },
                navArgument("listName") { type = NavType.StringType },
                navArgument("productIds") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val currentUserId = backStackEntry.arguments?.getString("userId") ?: userId
            val listId = backStackEntry.arguments?.getString("listId") ?: ""
            val productIds =
                backStackEntry.arguments?.getString("productIds")?.split(",") ?: emptyList()
            val listName = backStackEntry.arguments?.getString("listName") ?: ""

            ListDetailScreen(
                listId = listId,
                productIds = productIds,
                listName = listName,
                onBack = { navController.popBackStack() },
                onEditList = { id, name, ids ->
                    navController.navigate(
                        "create_list/$currentUserId/$id/$name/${
                            ids.joinToString(
                                ","
                            )
                        }"
                    )
                },
                userId = currentUserId
            )
        }
    }
}
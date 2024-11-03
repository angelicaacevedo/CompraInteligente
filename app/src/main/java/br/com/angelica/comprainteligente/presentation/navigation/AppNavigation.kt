package br.com.angelica.comprainteligente.presentation.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import br.com.angelica.comprainteligente.data.SessionManager
import br.com.angelica.comprainteligente.presentation.view.HistoryListScreen
import br.com.angelica.comprainteligente.presentation.view.HomeScreen
import br.com.angelica.comprainteligente.presentation.view.InflationScreen
import br.com.angelica.comprainteligente.presentation.view.ListDetailScreen
import br.com.angelica.comprainteligente.presentation.view.LoginScreen
import br.com.angelica.comprainteligente.presentation.view.NewListScreen
import br.com.angelica.comprainteligente.presentation.view.PriceComparisonScreen
import br.com.angelica.comprainteligente.presentation.view.ProductRegisterScreen
import br.com.angelica.comprainteligente.presentation.view.RegisterScreen
import br.com.angelica.comprainteligente.presentation.view.UserProfileScreen

@Composable
fun AppNavigation(sessionManager: SessionManager) {
    val navController: NavHostController = rememberNavController()
    val isUserLoggedIn = remember { mutableStateOf(sessionManager.userId != null) }


    // Use LaunchedEffect to navigate based on login state
    LaunchedEffect(isUserLoggedIn.value) {
        val startDestination = if (isUserLoggedIn.value) "home/${sessionManager.userId}" else "login"
        navController.navigate(startDestination) {
            popUpTo(navController.graph.startDestinationId) { inclusive = true }
            launchSingleTop = true
        }
    }

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
            val currentUserId = backStackEntry.arguments?.getString("userId") ?: ""
            HomeScreen(navController = navController, currentUserId)
        }


        // Product Register Screen
        composable(
            "add_product/{userId}",
            arguments = listOf(navArgument("userId") { type = NavType.StringType })
        ) { backStackEntry ->
            val currentUserId = backStackEntry.arguments?.getString("userId") ?: ""
            ProductRegisterScreen(
                userId = currentUserId,
                onProductRegistered = {
                    navController.navigate("home/$currentUserId") {
                        popUpTo("home/$currentUserId") { inclusive = true }
                    }
                },
                navController = navController
            )
        }

        // Price Comparison Screen with userId
        composable(
            "price_comparison/{userId}",
            arguments = listOf(navArgument("userId") { type = NavType.StringType })
        ) { backStackEntry ->
            val currentUserId = backStackEntry.arguments?.getString("userId") ?: ""
            PriceComparisonScreen(
                userId = currentUserId,
                navController = navController,
            )
        }

        // Product List History Screen with userId
        composable(
            "list_history/{userId}",
            arguments = listOf(navArgument("userId") { type = NavType.StringType })
        ) { backStackEntry ->
            val currentUserId = backStackEntry.arguments?.getString("userId") ?: ""
            HistoryListScreen(
                navController = navController,
                userId = currentUserId,
                onNavigateToCreateList = {
                    navController.navigate("create_list/$currentUserId") // Navega para a criação de lista com o userId
                },
                onNavigateToListItems = { listId, listName, productIds ->
                    val encodedListName = Uri.encode(listName)
                    val encodedProductIds = Uri.encode(productIds.joinToString(","))
                    navController.navigate("list_items/$currentUserId/$listId/$encodedListName/$encodedProductIds")
                }
            )
        }

        // Create New List Screen without arguments, for new list creation
        composable(
            "create_list/{userId}",
            arguments = listOf(navArgument("userId") { type = NavType.StringType })
        ) { backStackEntry ->
            val currentUserId = backStackEntry.arguments?.getString("userId") ?: ""
            NewListScreen(
                userId = currentUserId,
                onBack = { navController.navigate("list_history/$currentUserId") },
                listId = null,
                listNameArg = "",
                productIdsArg = emptyList(),
                onListCreated = {
                    navController.navigate("list_history/$currentUserId")
                },
                navController = navController
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
            val currentUserId = backStackEntry.arguments?.getString("userId") ?: ""
            val listId = backStackEntry.arguments?.getString("listId") ?: ""
            val listName = backStackEntry.arguments?.getString("listName") ?: ""
            val productIds =
                backStackEntry.arguments?.getString("productIds")?.split(",") ?: emptyList()

            NewListScreen(
                userId = currentUserId,
                onBack = { navController.popBackStack() },
                listId = listId,
                listNameArg = listName,
                productIdsArg = productIds,
                onListCreated = {
                    navController.navigate("list_history/$currentUserId")
                },
                navController = navController
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
            val currentUserId = backStackEntry.arguments?.getString("userId") ?: ""
            val listId = backStackEntry.arguments?.getString("listId") ?: ""
            val productIds =
                backStackEntry.arguments?.getString("productIds")?.split(",") ?: emptyList()
            val listName = backStackEntry.arguments?.getString("listName") ?: ""

            ListDetailScreen(
                userId = currentUserId,
                listId = listId,
                productIds = productIds,
                listName = listName,
                onBack = { navController.popBackStack() },
                onEditList = { id, name, ids ->
                    val encodedListName = Uri.encode(name)
                    val encodedProductIds = Uri.encode(ids.joinToString(","))
                    navController.navigate("create_list/$currentUserId/$id/$encodedListName/$encodedProductIds")
                },
                navController = navController
            )
        }

        // Inflation Screen
        composable(
            "inflation/{userId}",
            arguments = listOf(
                navArgument("userId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val currentUserId = backStackEntry.arguments?.getString("userId") ?: ""
            InflationScreen(
                userId = currentUserId,
                navController = navController
            )
        }

        // Profile Screen
        composable(
            "profile/{userId}",
            arguments = listOf(navArgument("userId") { type = NavType.StringType })
        ) { backStackEntry ->
            val currentUserId = backStackEntry.arguments?.getString("userId") ?: ""
            UserProfileScreen(
                userId = currentUserId,
                onLogoutClick = {
                    sessionManager.logout()
                    isUserLoggedIn.value = false
                },
                navController = navController
            )
        }
    }
}
package br.com.angelica.comprainteligente.presentation.common

import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun CustomBottomNavigation(navController: NavController) {
    BottomNavigation(
        backgroundColor = Color(0xFF4CAF50), // Cor verde clara para o background da bottom navigation
        elevation = 8.dp
    ) {
        val items = listOf(
            BottomNavItem("home", Icons.Default.Home, "Início"),
            BottomNavItem("lists", Icons.AutoMirrored.Filled.List, "Listas"),
            BottomNavItem("add_product", Icons.Default.Add, "Adicionar Produto"), // Ícone de Adicionar Produto no centro
            BottomNavItem("reports", Icons.Default.BarChart, "Relatórios")
        )

        // Obtém a rota atual para indicar a tela selecionada
        val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

        items.forEachIndexed { index, item ->
            BottomNavigationItem(
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title,
                        tint = Color.White
                    )
                },
                selected = currentRoute == item.route, // Verifica se a rota atual corresponde ao item
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                selectedContentColor = Color.Blue, // Cor do ícone/texto quando selecionado
                unselectedContentColor = Color.Gray // Cor do ícone/texto quando não selecionado
            )
        }
    }
}


data class BottomNavItem(
    val route: String,
    val icon: ImageVector,
    val title: String? = null
)
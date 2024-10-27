package br.com.angelica.comprainteligente.presentation.common

import androidx.compose.foundation.layout.Column
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.AddCircleOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun CustomBottomNavigation(navController: NavController, userId: String) {
    BottomNavigation(
        backgroundColor = Color(0xFF4CAF50),
        elevation = 8.dp
    ) {
        val items = listOf(
            BottomNavItem("home", Icons.Default.Home, "Início"),
            BottomNavItem("list_history/$userId", Icons.AutoMirrored.Default.List, "Listas"),
            BottomNavItem("add_product/$userId", Icons.Outlined.AddCircleOutline, "Adicionar"),
            BottomNavItem("price_comparison/$userId", Icons.Default.AttachMoney, "Preços"),
            BottomNavItem("reports", Icons.AutoMirrored.Default.TrendingUp, "Inflação")
        )

        // Obtém a rota atual para indicar a tela selecionada
        val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

        items.forEachIndexed { index, item ->
            BottomNavigationItem(
                icon = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.title,
                            tint = if (currentRoute == item.route) Color.Yellow else Color.White
                        )
                        Text(
                            text = item.title ?: "",
                            color = Color.White,
                            style = MaterialTheme.typography.labelSmall

                        )
                    }
                },
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                selectedContentColor = Color.Blue,
                unselectedContentColor = Color.Gray
            )
        }
    }
}

data class BottomNavItem(
    val route: String,
    val icon: ImageVector,
    val title: String? = null
)
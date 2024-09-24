package br.com.angelica.comprainteligente.presentation.common

import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun GenericBottomNavigationBar(
    navController: NavController
) {
    val items = listOf(
        BottomNavItem(
            route = "home",
            icon = Icons.Default.Home,
            title = "Inicio"
        ),
        BottomNavItem(
            route = "lists",
            icon = Icons.AutoMirrored.Filled.List,
            title = "Listas"
        ),
        BottomNavItem(
            route = "add_product",
            icon = Icons.Default.Add,
            title = "Adicionar"
        ),
        BottomNavItem(
            route = "reports",
            icon = Icons.Default.BarChart,
            title = "Relatórios"
        ),
        BottomNavItem(
            route = "profile",
            icon = Icons.Default.Person,
            title = "Perfil"
        )
    )

    BottomNavigation(
        backgroundColor = Color.White,
        contentColor = Color.Black,
        elevation = 5.dp
    ) {
        val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

        items.forEach { item ->
            BottomNavigationItem(
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = null
                    )
                },
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        // Evita múltiplas cópias da mesma rota na pilha de navegação
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        // Evita recriar o mesmo destino se já estiver selecionado
                        launchSingleTop = true
                        // Restaura o estado quando re-selecionado
                        restoreState = true
                    }
                },
                alwaysShowLabel = false,
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
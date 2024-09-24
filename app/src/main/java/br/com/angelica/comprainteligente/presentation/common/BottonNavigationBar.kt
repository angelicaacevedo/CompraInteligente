package br.com.angelica.comprainteligente.presentation.common

import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun GenericBottomNavigationBar(navController: NavController) {
    val items = listOf(
        BottomNavItem(
            route = "home",
            icon = Icons.Default.Home // Escolha um ícone relacionado
        ),
        BottomNavItem(
            route = "insert_product",
            icon = Icons.Default.Add // Escolha um ícone relacionado
        ),
        BottomNavItem(
            route = "shopping_list",
            icon = Icons.AutoMirrored.Filled.List // Escolha um ícone relacionado
        ),
        BottomNavItem(
            route = "reports",
            icon = Icons.AutoMirrored.Filled.Send // Escolha um ícone relacionado
        )
    )

    BottomNavigation(
        backgroundColor = Color.White, // Personalize a cor de fundo
        contentColor = Color.Black, // Personalize a cor do conteúdo
        elevation = 5.dp // Personalize a elevação
    ) {
        val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

        items.forEach { item ->
            BottomNavigationItem(
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = null // Ícone sem descrição de acessibilidade
                    )
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
                // Remove o texto dos itens de navegação
                alwaysShowLabel = false,
                selectedContentColor = Color.Blue, // Cor do ícone selecionado
                unselectedContentColor = Color.Gray // Cor do ícone não selecionado
            )
        }
    }
}

data class BottomNavItem(
    val route: String,
    val icon: ImageVector
)

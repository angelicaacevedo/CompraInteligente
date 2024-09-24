package br.com.angelica.comprainteligente.presentation.common

import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import br.com.angelica.comprainteligente.presentation.viewmodel.NavigationViewModel

@Composable
fun GenericBottomNavigationBar(
    navController: NavController,
    viewModel: NavigationViewModel
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
                    when (item.route) {
                        "home" -> viewModel.handleIntent(NavigationViewModel.NavigationIntent.NavigateToHome)
                        "lists" -> viewModel.handleIntent(NavigationViewModel.NavigationIntent.NavigateToLists)
                        "add_product" -> viewModel.handleIntent(NavigationViewModel.NavigationIntent.NavigateToAddProduct)
                        "reports" -> viewModel.handleIntent(NavigationViewModel.NavigationIntent.NavigateToReports)
                        "profile" -> viewModel.handleIntent(NavigationViewModel.NavigationIntent.NavigateToProfile)
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

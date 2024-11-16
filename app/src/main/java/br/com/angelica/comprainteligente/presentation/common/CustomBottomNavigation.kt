package br.com.angelica.comprainteligente.presentation.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.AddCircleOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import br.com.angelica.comprainteligente.theme.BackgroundLightGray
import br.com.angelica.comprainteligente.theme.GreenStrong
import br.com.angelica.comprainteligente.theme.LilacStrong
import br.com.angelica.comprainteligente.theme.PrimaryBlue
import br.com.angelica.comprainteligente.theme.TextGray
import br.com.angelica.comprainteligente.theme.TextSecondary

@Composable
fun CustomBottomNavigation(navController: NavController, userId: String) {
    if (userId.isEmpty()) {
        return
    }

    NavigationBar(
        containerColor = BackgroundLightGray,
        tonalElevation = 6.dp
    ) {
        val items = listOf(
            BottomNavItem("home/$userId", Icons.Default.Home, "Início"),
            BottomNavItem("list_history/$userId", Icons.AutoMirrored.Default.List, "Listas"),
            BottomNavItem("add_product/$userId", Icons.Outlined.AddCircleOutline, "Adicionar"),
            BottomNavItem("price_comparison/$userId", Icons.Default.AttachMoney, "Preços"),
            BottomNavItem("inflation/$userId", Icons.AutoMirrored.Default.TrendingUp, "Inflação")
        )

        val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

        items.forEach { item ->
            val isSelected = currentRoute?.contains(item.route.substringBefore("/")) == true
            NavigationBarItem(
                icon = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.title,
                            modifier = Modifier.size(24.dp),
                            tint = if (isSelected) PrimaryBlue else TextSecondary
                        )
                        Text(
                            text = item.title ?: "",
                            color = if (isSelected) PrimaryBlue else TextSecondary,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            ),
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                },
                selected = isSelected,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = BackgroundLightGray,
                    selectedIconColor = PrimaryBlue,
                    unselectedIconColor = TextSecondary,
                    selectedTextColor = PrimaryBlue,
                    unselectedTextColor = TextSecondary
                )
            )
        }
    }
}

data class BottomNavItem(
    val route: String,
    val icon: ImageVector,
    val title: String? = null
)
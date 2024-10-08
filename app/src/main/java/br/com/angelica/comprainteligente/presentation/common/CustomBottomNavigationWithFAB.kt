package br.com.angelica.comprainteligente.presentation.common

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.FloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun CustomBottomNavigationWithFAB(
    navController: NavController,
    onFabClick: () -> Unit
) {
    Box {
        // Desenhando o fundo customizado com recorte para o FAB
        Canvas(modifier = Modifier.fillMaxWidth().height(80.dp)) {
            val width = size.width
            val height = size.height

            // Desenhando o caminho da bottom navigation com um recorte
            drawPath(
                path = Path().apply {
                    moveTo(0f, 0f)
                    lineTo(width * 0.35f, 0f) // Mantendo o início da curva
                    quadraticTo(
                        width * 0.5f, height * 0.75f, // A altura é um pouco mais baixa para uma curva mais fechada
                        width * 0.65f, 0f  // Mantendo a largura da curva
                    )
                    lineTo(width, 0f)
                    lineTo(width, height)
                    lineTo(0f, height)
                    close()
                },
                color = Color(0xFF4CAF50), // Cor verde clara para o background da bottom navigation
                style = Fill
            )
        }

        // Bottom Navigation Itens (sem o FAB no centro)
        BottomNavigation(
            modifier = Modifier.align(Alignment.BottomCenter),
            backgroundColor = Color.Transparent, // Transparecer para não cobrir o Canvas
            elevation = 0.dp
        ) {
            val items = listOf(
                BottomNavItem("home", Icons.Default.Home, "Inicio"),
                BottomNavItem("lists", Icons.AutoMirrored.Filled.List, "Listas"),
                BottomNavItem("reports", Icons.Default.BarChart, "Relatórios"),
                BottomNavItem("profile", Icons.Default.Person, "Perfil")
            )
            val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

            items.forEachIndexed { index, item ->
                BottomNavigationItem(
                    icon = {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.title
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
                    selectedContentColor = Color.Blue,
                    unselectedContentColor = Color.Gray
                )
            }
        }

        // FAB centralizado acima da bottom navigation
        FloatingActionButton(
            onClick = onFabClick,
            containerColor = Color(0xFF00BFA5), // Ajuste a cor conforme necessário
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(y = (-45).dp) // Ajuste de acordo com a altura da BottomNavigation
        ) {
            Icon(Icons.Default.Add, contentDescription = "Adicionar Produto")
        }
    }
}

data class BottomNavItem(
    val route: String,
    val icon: ImageVector,
    val title: String? = null
)
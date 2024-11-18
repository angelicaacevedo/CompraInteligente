package br.com.angelica.comprainteligente.presentation.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import br.com.angelica.comprainteligente.data.SessionManager
import br.com.angelica.comprainteligente.model.OnboardingSlideData
import br.com.angelica.comprainteligente.theme.ButtonGreen
import br.com.angelica.comprainteligente.theme.CarouselGray
import br.com.angelica.comprainteligente.theme.PageBlue
import br.com.angelica.comprainteligente.theme.PageGreen
import br.com.angelica.comprainteligente.theme.PageLilac
import br.com.angelica.comprainteligente.theme.PageRed
import br.com.angelica.comprainteligente.theme.PageYellow
import br.com.angelica.comprainteligente.theme.PrimaryBlue
import br.com.angelica.comprainteligente.theme.TextBlack
import br.com.angelica.comprainteligente.theme.White
import kotlinx.coroutines.launch

@Composable
fun OnboardingScreen(navController: NavController, sessionManager: SessionManager) {
    val slides = listOf(
        OnboardingSlideData(
            title = "Bem-vindo ao Compra Inteligente!",
            description = "Organize suas compras e economize mais.",
            icon = Icons.Default.ShoppingCart,
            backgroundColor = PageBlue
        ),
        OnboardingSlideData(
            title = "Contribua com o App",
            description = "Cadastre produtos com código de barras e ajude a manter os preços atualizados.",
            icon = Icons.Default.AddShoppingCart,
            backgroundColor = PageGreen
        ),
        OnboardingSlideData(
            title = "Gerencie suas Listas",
            description = "Crie listas personalizadas para facilitar suas compras.",
            icon = Icons.AutoMirrored.Default.List,
            backgroundColor = PageYellow
        ),
        OnboardingSlideData(
            title = "Compare Preços",
            description = "Simule o custo da sua lista de compras em diferentes supermercados da sua cidade.",
            icon = Icons.Default.AttachMoney,
            backgroundColor = PageRed
        ),
        OnboardingSlideData(
            title = "Acompanhe os Preços",
            description = "Compare preços e veja a inflação dos produtos ao longo do tempo.",
            icon = Icons.AutoMirrored.Default.TrendingUp,
            backgroundColor = PageLilac
        ),
    )

    val pagerState = rememberPagerState(pageCount = { slides.size })

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Text(
            text = "Introdução",
            style = MaterialTheme.typography.headlineLarge.copy(
                color = TextBlack,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 16.dp)
        )
        Spacer(modifier = Modifier.height(24.dp))

        OnboardingPager(
            slides = slides,
            pagerState = pagerState,
            onFinish = {
                sessionManager.hasSeenOnboarding = true
                navController.navigate("home/${sessionManager.userId}") {
                    popUpTo("login") { inclusive = true }
                }
            }
        )
    }
}

@Composable
fun OnboardingPager(
    slides: List<OnboardingSlideData>,
    pagerState: PagerState,
    onFinish: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .weight(1f)
                .padding(16.dp)
        ) { page ->
            OnboardingSlide(slide = slides[page])
        }

        Spacer(modifier = Modifier.height(24.dp))

        CustomPagerIndicator(
            pagerState = pagerState,
            pageCount = slides.size,
            activeColor = PrimaryBlue,
            inactiveColor = CarouselGray,
            indicatorWidth = 12.dp,
            spacing = 8.dp,
            modifier = Modifier.padding(16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (pagerState.currentPage == slides.size - 1) {
                    onFinish()
                } else {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                    }
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = if (pagerState.currentPage == slides.size - 1) ButtonGreen else PrimaryBlue
            ),
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp)
                .height(50.dp)
        ) {
            Text(
                text = if (pagerState.currentPage == slides.size - 1) "Começar" else "Próximo",
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            )
        }
    }
}

@Composable
fun OnboardingSlide(slide: OnboardingSlideData) {
    Card(
        shape = MaterialTheme.shapes.large,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .background(slide.backgroundColor)
                .padding(32.dp)
        ) {
            Icon(
                imageVector = slide.icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier
                    .size(100.dp)
                    .padding(16.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = slide.title,
                style = MaterialTheme.typography.headlineLarge.copy(
                    color = White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(16.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = slide.description,
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                ),
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}

@Composable
fun CustomPagerIndicator(
    pagerState: PagerState,
    pageCount: Int,
    modifier: Modifier = Modifier,
    activeColor: Color = PrimaryBlue,
    inactiveColor: Color = CarouselGray,
    indicatorWidth: Dp = 8.dp,
    indicatorHeight: Dp = indicatorWidth,
    spacing: Dp = indicatorWidth,
    indicatorShape: Shape = CircleShape
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(spacing),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        repeat(pageCount) { pageIndex ->
            Box(
                modifier = Modifier
                    .size(
                        width = indicatorWidth,
                        height = indicatorHeight
                    )
                    .clip(indicatorShape)
                    .background(if (pagerState.currentPage == pageIndex) activeColor else inactiveColor)
            )
        }
    }
}

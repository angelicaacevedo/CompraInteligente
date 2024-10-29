package br.com.angelica.comprainteligente.presentation.view

import android.graphics.drawable.ColorDrawable
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Chip
import androidx.compose.material.ChipDefaults
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import br.com.angelica.comprainteligente.model.Price
import br.com.angelica.comprainteligente.model.Product
import br.com.angelica.comprainteligente.presentation.common.CustomBottomNavigation
import br.com.angelica.comprainteligente.presentation.viewmodel.InflationViewModel
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import org.koin.androidx.compose.getViewModel

@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
fun InflationScreen(
    userId: String,
    navController: NavController,
    viewModel: InflationViewModel = getViewModel()
) {
    val state by viewModel.state.collectAsState()

    val selectedProduct = remember { mutableStateOf<Product?>(null) }
    val expanded = remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Inflação dos Produtos", modifier = Modifier.fillMaxWidth()) },
                colors = TopAppBarDefaults.topAppBarColors(
                    titleContentColor = Color.Black,
                    containerColor = Color.White,
                )
            )
        },
        bottomBar = {
            CustomBottomNavigation(navController = navController, userId = userId)
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp)
                    .background(MaterialTheme.colorScheme.background)
            ) {
                // Botão para Selecionar Produto
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .background(
                            MaterialTheme.colorScheme.primary,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .clickable { expanded.value = true }
                        .padding(vertical = 12.dp)
                ) {
                    Text(
                        text = selectedProduct.value?.name ?: "Selecione um Produto",
                        color = Color.White,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.align(Alignment.Center)
                    )

                    DropdownMenu(
                        expanded = expanded.value,
                        onDismissRequest = { expanded.value = false }
                    ) {
                        state.products.items.forEach { product ->
                            DropdownMenuItem(
                                text = { Text(text = product.name) },
                                onClick = {
                                    selectedProduct.value = product
                                    expanded.value = false
                                    viewModel.handleIntent(
                                        InflationViewModel.InflationIntent.LoadPriceHistory(
                                            productId = product.id,
                                            period = state.prices.period
                                        )
                                    )
                                }
                            )
                        }
                    }
                }

                // Filtro de Período
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    listOf("7 dias", "1 mês", "6 meses", "1 ano", "5 anos").forEach { period ->
                        Chip(
                            onClick = {
                                viewModel.handleIntent(
                                    InflationViewModel.InflationIntent.UpdatePeriod(
                                        period
                                    )
                                )
                            },
                            colors = ChipDefaults.chipColors(
                                backgroundColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                contentColor = MaterialTheme.colorScheme.primary
                            ),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                            shape = RoundedCornerShape(50),
                            modifier = Modifier.height(40.dp)
                        ) {
                            Text(text = period, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Controle de carregamento, exibição de erros e dados de preços
                when {
                    state.isLoading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }

                    state.error != null -> {
                        Text(
                            text = "Erro: ${state.error}",
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    }

                    state.prices.items.isNotEmpty() -> {
                        InflationChart(prices = state.prices.items)
                    }

                    else -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Nenhum dado disponível",
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun InflationChart(prices: List<Price>) {
    val entries = prices.mapIndexed { index, price ->
        Entry(index.toFloat(), price.price.toFloat())
    }

    val lineDataSet = LineDataSet(entries, "Variação de Preço").apply {
        color = android.graphics.Color.parseColor("#6200EE") // Cor da linha
        valueTextColor = android.graphics.Color.parseColor("#03DAC5") // Cor do valor
        lineWidth = 3f
        setDrawCircles(true)
        setCircleColor(android.graphics.Color.parseColor("#6200EE"))
        setDrawFilled(true)
        fillDrawable =
            ColorDrawable(android.graphics.Color.parseColor("#EDE7F6")) // Cor de preenchimento
        setDrawHighlightIndicators(false)
    }

    val lineData = LineData(lineDataSet)

    AndroidView(
        factory = { context ->
            LineChart(context).apply {
                data = lineData
                description.isEnabled = false
                legend.isEnabled = true
                axisLeft.textColor = android.graphics.Color.BLACK
                axisRight.isEnabled = false
                xAxis.apply {
                    position = XAxis.XAxisPosition.BOTTOM
                    textColor = android.graphics.Color.BLACK
                    setDrawGridLines(false)
                }
                animateX(1000)
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
            .padding(16.dp)
    )
}

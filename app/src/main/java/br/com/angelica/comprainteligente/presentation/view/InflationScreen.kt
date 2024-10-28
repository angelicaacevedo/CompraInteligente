package br.com.angelica.comprainteligente.presentation.view

import android.graphics.drawable.ColorDrawable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.unit.dp
import br.com.angelica.comprainteligente.model.Price
import br.com.angelica.comprainteligente.presentation.viewmodel.InflationViewModel
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import org.koin.androidx.compose.getViewModel

@Composable
fun InflationScreen(
    userId: String,
    productId: String,
    viewModel: InflationViewModel = getViewModel()
) {
    val state by viewModel.state.collectAsState()

    // Carregar dados assim que a tela for iniciada
    LaunchedEffect(productId) {
        viewModel.handleIntent(InflationViewModel.InflationIntent.LoadPriceHistory(productId))
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.LightGray)
    ) {
        when {
            state.isLoading -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(color = Color(0xFF6200EE))
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Carregando dados...", color = Color.Gray)
                }
            }
            state.error != null -> {
                Text(
                    text = "Erro: ${state.error}",
                    color = Color.Red,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            state.prices.isNotEmpty() -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    // Cabeçalho com título e botão de voltar
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Inflação do Produto",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color(0xFF6200EE)
                        )
                        IconButton(onClick = { /* Ação de Voltar */ }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Voltar",
                                tint = Color(0xFF6200EE)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))

                    // Gráfico customizado
                    InflationChart(prices = state.prices)

                    // Exibindo o último preço e a diferença percentual
                    val currentPrice = state.prices.last().price
                    val initialPrice = state.prices.first().price
                    val percentageChange = ((currentPrice - initialPrice) / initialPrice) * 100

                    Column(
                        modifier = Modifier.padding(top = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Preço Atual: R$ ${"%.2f".format(currentPrice)}",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color(0xFF03DAC5)
                        )
                        Text(
                            text = "Variação: ${"%.2f".format(percentageChange)}%",
                            style = MaterialTheme.typography.bodyLarge,
                            color = if (percentageChange >= 0) Color.Green else Color.Red
                        )
                    }
                }
            }
            else -> {
                Text(
                    text = "Nenhum dado disponível",
                    color = Color.Gray,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
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
        fillDrawable = ColorDrawable(android.graphics.Color.parseColor("#EDE7F6")) // Cor de preenchimento
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

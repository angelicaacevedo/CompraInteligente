package br.com.angelica.comprainteligente.presentation.view

import android.graphics.drawable.ColorDrawable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import br.com.angelica.comprainteligente.model.Price
import br.com.angelica.comprainteligente.model.Product
import br.com.angelica.comprainteligente.presentation.common.CustomBottomNavigation
import br.com.angelica.comprainteligente.presentation.common.EmptyStateScreen
import br.com.angelica.comprainteligente.presentation.common.LoadingAnimation
import br.com.angelica.comprainteligente.presentation.viewmodel.InflationViewModel
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import org.koin.androidx.compose.getViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InflationScreen(
    userId: String,
    navController: NavController,
    viewModel: InflationViewModel = getViewModel()
) {
    val state by viewModel.state.collectAsState()
    val selectedProduct = remember { mutableStateOf<Product?>(null) }
    var expanded by remember { mutableStateOf(false) }

    // Nova variável para selecionar o tipo de análise
    val analysisOptions = listOf("Inflação de Produtos", "Histórico de Preços")
    var selectedAnalysis by remember { mutableStateOf(analysisOptions[0]) }
    var analysisMenuExpanded by remember { mutableStateOf(false) }
    val periodOptions = listOf("7 dias", "1 mês", "6 meses", "1 ano", "5 anos") // Opções de período
    var periodMenuExpanded by remember { mutableStateOf(false) } // Controle de expansão do menu


    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Análise de Compras",
                        modifier = Modifier.fillMaxWidth(),
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
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
                    .padding(16.dp)
                    .background(MaterialTheme.colorScheme.background)
            ) {
                // Dropdown para selecionar o tipo de análise
                ExposedDropdownMenuBox(
                    expanded = analysisMenuExpanded,
                    onExpandedChange = { analysisMenuExpanded = !analysisMenuExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedAnalysis,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Tipo de Análise") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = analysisMenuExpanded)
                        },
                        colors = TextFieldDefaults.outlinedTextFieldColors(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surface)
                            .padding(vertical = 4.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = analysisMenuExpanded,
                        onDismissRequest = { analysisMenuExpanded = false }
                    ) {
                        analysisOptions.forEach { analysis ->
                            DropdownMenuItem(
                                text = { Text(analysis) },
                                onClick = {
                                    selectedAnalysis = analysis
                                    analysisMenuExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Dropdown de seleção de produto
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = selectedProduct.value?.name ?: "Selecione um Produto",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Produto") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                        },
                        colors = TextFieldDefaults.outlinedTextFieldColors(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surface)
                            .padding(vertical = 4.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        state.products.items.forEach { product ->
                            DropdownMenuItem(
                                text = { Text(product.name) },
                                onClick = {
                                    selectedProduct.value = product
                                    expanded = false
                                    viewModel.setSelectedProduct(product, userId)
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Filtro de Período (para o gráfico de inflação)
                if (selectedAnalysis == "Inflação de Produtos") {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                    ) {
                        IconButton(onClick = { periodMenuExpanded = !periodMenuExpanded }) {
                            Icon(
                                imageVector = Icons.Filled.FilterList,
                                contentDescription = "Filtrar por período",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                        Text(
                            text = "Período: ${state.prices.period}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        DropdownMenu(
                            expanded = periodMenuExpanded,
                            onDismissRequest = { periodMenuExpanded = false }
                        ) {
                            periodOptions.forEach { period ->
                                DropdownMenuItem(
                                    text = { Text(period) },
                                    onClick = {
                                        periodMenuExpanded = false
                                        viewModel.handleIntent(
                                            InflationViewModel.InflationIntent.UpdatePeriod(
                                                period,
                                                userId
                                            )
                                        )
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Exibição do gráfico conforme a análise selecionada
                when {
                    state.isLoading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            LoadingAnimation(message = "Carregando dados...")
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
                        if (selectedAnalysis == "Inflação de Produtos") {
                            Text(
                                text = "Inflação do Período: ${state.inflationRate}%",
                                style = MaterialTheme.typography.bodyLarge.copy(color = Color.Red),
                                modifier = Modifier
                                    .align(Alignment.CenterHorizontally)
                                    .padding(vertical = 8.dp)
                            )
                            InflationChart(
                                prices = state.prices.items,
                                selectedPeriod = state.prices.period
                            )
                        } else {
                            PriceHistoryChart(
                                prices = state.prices.items,
                                supermarketNames = state.supermarketNames
                            )
                        }
                    }

                    else -> {
                        EmptyStateScreen(
                            title = "Selecione um produto",
                            message = "Ao selecionar um produto você pode verificar sua análise",
                            icon = Icons.AutoMirrored.Default.TrendingUp,
                            contentDescription = "Gráfico de análise",
                        )
                    }
                }
            }
        }
    )
}


@Composable
fun InflationChart(prices: List<Price>, selectedPeriod: String) {
    // Mapeia os preços para entradas do gráfico
    val entries = prices.map { price ->
        val dateValue = price.date.toDate().time.toFloat()
        val priceValue = price.price.toFloat()
        Entry(dateValue, priceValue)
    }

    // Configura o conjunto de dados para o preço
    val lineDataSet = LineDataSet(entries, "price").apply {
        color = android.graphics.Color.parseColor("#6200EE")
        valueTextColor = android.graphics.Color.parseColor("#6200EE")
        lineWidth = 2f
        circleRadius = 4f  // Diminui o tamanho dos círculos para evitar sobreposição
        setDrawFilled(true)
        setCircleColor(android.graphics.Color.parseColor("#6200EE"))
        setDrawValues(true)
        valueTextSize = 8f  // Diminui o tamanho do texto para evitar sobreposição
        fillDrawable = ColorDrawable(android.graphics.Color.parseColor("#EDE7F6"))
        setDrawHighlightIndicators(false)
    }

    // Configura o conjunto de dados para a inflação
    val inflationDataSet = LineDataSet(entries, "inflation").apply {
        color = android.graphics.Color.parseColor("#03DAC5")
        valueTextColor = android.graphics.Color.parseColor("#03DAC5")
        lineWidth = 2.5f
        circleRadius = 4f
        setDrawCircles(true)
        setCircleColor(android.graphics.Color.parseColor("#03DAC5"))
        setDrawFilled(true)
        fillDrawable = ColorDrawable(android.graphics.Color.parseColor("#C8E6C9"))
        setDrawHighlightIndicators(false)
        valueTextSize = 8f
    }

    val lineData = LineData(lineDataSet, inflationDataSet)

    AndroidView(
        factory = { context ->
            LineChart(context).apply {
                data = lineData
                description.isEnabled = false
                legend.isEnabled = true
                legend.textSize = 12f
                setExtraOffsets(10f, 10f, 10f, 10f)  // Espaçamento extra ao redor do gráfico
                axisRight.isEnabled = false
                animateX(1000)

                // Configuração do eixo Y
                axisLeft.apply {
                    textColor = android.graphics.Color.BLACK
                    textSize = 10f
                    axisMinimum = (prices.minOfOrNull { it.price.toFloat() }
                        ?: 0f) - 4f  // Mais espaço abaixo do valor mínimo
                    axisMaximum = (prices.maxOfOrNull { it.price.toFloat() }
                        ?: 10f) + 4f  // Mais espaço acima do valor máximo
                    granularity = 1f
                    labelCount = 6
                    valueFormatter = object : ValueFormatter() {
                        override fun getFormattedValue(value: Float): String {
                            return "R$ ${"%.2f".format(value)}"
                        }
                    }
                }

                // Configuração do eixo X
                xAxis.apply {
                    position = XAxis.XAxisPosition.BOTTOM
                    textColor = android.graphics.Color.BLACK
                    textSize = 10f
                    setDrawGridLines(false)
                    labelRotationAngle = -30f  // Reduz a rotação para melhorar a legibilidade
                    granularity = when (selectedPeriod) {
                        "1 mês" -> 86400000f * 7
                        "6 meses" -> 86400000f * 30
                        "1 ano" -> 86400000f * 60
                        else -> 86400000f * 180
                    }
                    valueFormatter = DateAxisValueFormatter()
                }

                axisRight.isEnabled = false
                animateX(1000)
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)  // Aumenta a altura para mais espaço no gráfico
            .padding(16.dp)
    )
}

@Composable
fun PriceHistoryChart(
    prices: List<Price>,
    supermarketNames: Map<String, String>
) {
    // Agrupar os preços por supermercado
    val entriesBySupermarket = prices.groupBy { it.supermarketId }.mapValues { (_, prices) ->
        prices.map { price ->
            Entry(price.date.toDate().time.toFloat(), price.price.toFloat())
        }
    }

    // Criar um conjunto de dados para cada supermercado, usando o nome do supermercado para a legenda
    val dataSets = entriesBySupermarket.map { (supermarketId, entries) ->
        val supermarketName = supermarketNames[supermarketId] ?: supermarketId
        LineDataSet(entries, supermarketName).apply {
            color = android.graphics.Color.parseColor("#FF9800") // Cor de cada linha
            lineWidth = 2f
            setDrawCircles(true)
            circleRadius = 5f
            setDrawValues(false) // Oculta valores para evitar poluição visual
        }
    }

    val lineData = LineData(dataSets)

    AndroidView(
        factory = { context ->
            LineChart(context).apply {
                data = lineData
                description.isEnabled = false
                legend.apply {
                    isEnabled = true
                    textSize = 10f
                    isWordWrapEnabled = true // Habilita quebra de linha para legendas longas
                    verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
                    horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
                    orientation = Legend.LegendOrientation.HORIZONTAL
                    setDrawInside(false)
                }
                axisRight.isEnabled = false
                animateX(1000)

                // Configurações do eixo Y
                axisLeft.apply {
                    textColor = android.graphics.Color.BLACK
                    textSize = 10f
                    axisMinimum = (prices.minOfOrNull { it.price.toFloat() } ?: 0f) - 2f
                    axisMaximum = (prices.maxOfOrNull { it.price.toFloat() } ?: 10f) + 2f
                    granularity = 1f
                    valueFormatter = object : ValueFormatter() {
                        override fun getFormattedValue(value: Float): String {
                            return "R$ ${"%.2f".format(value)}"
                        }
                    }
                }

                // Configurações do eixo X
                xAxis.apply {
                    position = XAxis.XAxisPosition.BOTTOM
                    textColor = android.graphics.Color.BLACK
                    textSize = 10f
                    setDrawGridLines(false)
                    labelRotationAngle = -30f
                    granularity =
                        86400000f * 30 // Aproximadamente um mês, ajustável conforme o período selecionado
                    valueFormatter = DateAxisValueFormatter()
                }
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .padding(16.dp)
    )
}

// Classe para formatar datas no eixo X
class DateAxisValueFormatter : ValueFormatter() {
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    override fun getFormattedValue(value: Float): String {
        return dateFormat.format(Date(value.toLong()))
    }
}
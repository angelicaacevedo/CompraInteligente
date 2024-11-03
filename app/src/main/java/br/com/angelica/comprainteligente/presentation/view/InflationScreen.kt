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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.CircularProgressIndicator
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
    val periodOptions = listOf("7 dias", "1 mês", "6 meses", "1 ano", "5 anos")
    var selectedPeriod by remember { mutableStateOf("7 dias") }
    var periodMenuExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Inflação dos Produtos",
                        modifier = Modifier.fillMaxWidth()
                    )
                },
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
                // Seletor de Produto usando ExposedDropdownMenuBox para posicionamento correto
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
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            cursorColor = MaterialTheme.colorScheme.primary,
                            focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                            unfocusedIndicatorColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()  // Garantir que o menu expanda abaixo do campo
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
                                    viewModel.setSelectedProduct(product)
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Filtro de Período com Ícone de Filtro
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
                                        InflationViewModel.InflationIntent.UpdatePeriod(period)
                                    )
                                }
                            )
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
                        InflationChart(prices = state.prices.items, selectedPeriod = selectedPeriod)
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
fun InflationChart(prices: List<Price>, selectedPeriod: String) {
    // Transforme os preços em entradas para o gráfico
    val entries = prices.map { price ->
        val dateValue = price.date.toDate().time.toFloat()
        val priceValue = price.price.toFloat()
        Entry(dateValue, priceValue)
    }

    val lineDataSet = LineDataSet(entries, "Variação de Preço").apply {
        color = android.graphics.Color.parseColor("#6200EE")
        valueTextColor = android.graphics.Color.parseColor("#03DAC5")
        lineWidth = 3f
        circleRadius = 6f
        setDrawCircles(true)
        setCircleColor(android.graphics.Color.parseColor("#6200EE"))
        setDrawFilled(true)
        fillDrawable = ColorDrawable(android.graphics.Color.parseColor("#EDE7F6"))
        setDrawHighlightIndicators(false)
        valueTextSize = 12f // Aumenta o tamanho do texto dos valores
    }

    val lineData = LineData(lineDataSet)

    AndroidView(
        factory = { context ->
            LineChart(context).apply {
                data = lineData
                description.isEnabled = false
                legend.isEnabled = true

                val minY = prices.minOfOrNull { it.price.toFloat() } ?: 0f
                val maxY = prices.maxOfOrNull { it.price.toFloat() } ?: 10f

                // Ajustes do eixo Y
                axisLeft.apply {
                    textColor = android.graphics.Color.BLACK
                    textSize = 12f
                    axisMinimum = minY - 1f
                    axisMaximum = maxY + 1f
                    granularity = 0.5f
                    labelCount = 6
                    valueFormatter = object : ValueFormatter() {
                        override fun getFormattedValue(value: Float): String {
                            return "R$ ${"%.2f".format(value)}"
                        }
                    }
                }

                // Ajustes do eixo X
                xAxis.apply {
                    position = XAxis.XAxisPosition.BOTTOM
                    textColor = android.graphics.Color.BLACK
                    textSize = 12f
                    setDrawGridLines(false)
                    labelRotationAngle = -45f
                    granularity = when (selectedPeriod) {
                        "7 dias" -> 86400000f
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
            .height(250.dp)
            .padding(16.dp)
    )
}

class DateAxisValueFormatter : ValueFormatter() {
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    override fun getFormattedValue(value: Float): String {
        return dateFormat.format(Date(value.toLong()))
    }
}

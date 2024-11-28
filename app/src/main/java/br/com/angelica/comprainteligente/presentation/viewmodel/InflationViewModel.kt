package br.com.angelica.comprainteligente.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.angelica.comprainteligente.domain.usecase.GetPriceHistoryUseCase
import br.com.angelica.comprainteligente.domain.usecase.GetSupermarketSuggestionsUseCase
import br.com.angelica.comprainteligente.domain.usecase.ProductOperationsUseCase
import br.com.angelica.comprainteligente.model.Price
import br.com.angelica.comprainteligente.model.Product
import com.github.mikephil.charting.data.Entry
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class InflationViewModel(
    private val getPriceHistoryUseCase: GetPriceHistoryUseCase,
    private val productOperationsUseCase: ProductOperationsUseCase,
    private val getSupermarketSuggestionsUseCase: GetSupermarketSuggestionsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(InflationViewState())
    val state: StateFlow<InflationViewState> = _state.asStateFlow()

    init {
        loadProducts()
        loadSupermarkets()
    }

    fun handleIntent(intent: InflationIntent) {
        when (intent) {
            is InflationIntent.LoadProducts -> loadProducts()
            is InflationIntent.LoadPriceHistory -> loadPriceHistory(
                intent.productId,
                intent.period,
                intent.userId
            )

            is InflationIntent.UpdatePeriod -> updatePeriod(intent.period, intent.userId)
            is InflationIntent.UpdateAnalysisType -> {
                updateAnalysisType(intent.analysisType, intent.userId)
            }
        }
    }

    // Carrega a lista de produtos e atualiza o estado
    private fun loadProducts() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            val result = productOperationsUseCase.getProducts()
            result.onSuccess { productList ->
                _state.value = _state.value.copy(
                    isLoading = false,
                    products = ProductState(items = productList),
                    error = null
                )
            }.onFailure { exception ->
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = exception.message
                )
            }
        }
    }

    private fun loadSupermarkets() {
        viewModelScope.launch {
            val result = getSupermarketSuggestionsUseCase()
            result.onSuccess { supermarketList ->
                val supermarketNames = supermarketList.associateBy(
                    keySelector = { it.id },
                    valueTransform = { it.name.split(" - ")[0] }
                )

                _state.value = _state.value.copy(
                    supermarketNames = supermarketNames
                )
            }.onFailure { exception ->
                _state.value = _state.value.copy(
                    error = exception.message
                )
            }
        }
    }

    fun setSelectedProduct(product: Product, userId: String) {
        _state.value = _state.value.copy(
            products = _state.value.products.copy(selectedProduct = product)
        )
        if (_state.value.analysisType == "Histórico de Preços") {
            loadPriceHistory(
                productId = product.id,
                period = _state.value.prices.period,
                userId = userId
            )
        } else if (_state.value.analysisType == "Inflação de Produtos") {
            loadPriceHistory(
                productId = product.id,
                period = _state.value.prices.period,
                userId = userId
            )
        }
    }

    private fun updatePeriod(period: String, userId: String) {
        _state.value = _state.value.copy(
            prices = _state.value.prices.copy(period = period)
        )

        val selectedProduct = _state.value.products.selectedProduct
        if (selectedProduct != null) {
            loadPriceHistory(productId = selectedProduct.id, period = period, userId = userId)
        }
    }

    private fun updateAnalysisType(analysisType: String, userId: String) {
        _state.value = _state.value.copy(analysisType = analysisType)

        val selectedProduct = _state.value.products.selectedProduct

        if (analysisType == "Histórico de Preços" && selectedProduct != null) {
            loadPriceHistory(
                productId = selectedProduct.id,
                period = _state.value.prices.period,
                userId = userId
            )
        }
    }

    private fun loadPriceHistory(productId: String, period: String, userId: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)

            val result = getPriceHistoryUseCase(productId, period, userId)
            result.onSuccess { priceList ->

                // Calcular a inflação entre o primeiro e o último preço do período
                val inflationRate = calculateInflationRate(priceList)

                // Agrupar os preços por supermercado
                val pricesBySupermarket = priceList.groupBy { it.supermarketId }
                val entriesBySupermarket = pricesBySupermarket.mapValues { (_, prices) ->
                    prices.map { price ->
                        Entry(price.date.toDate().time.toFloat(), price.price.toFloat())
                    }
                }

                _state.value = _state.value.copy(
                    isLoading = false,
                    prices = PriceState(items = priceList, period = period),
                    inflationRate = inflationRate,
                    entriesBySupermarket = entriesBySupermarket,
                    error = null
                )
            }.onFailure { exception ->
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = exception.message
                )
            }
        }
    }


    // Calcula a inflação entre o primeiro e o último preço
    private fun calculateInflationRate(prices: List<Price>): String {
        return if (prices.size >= 2) {
            val initialPrice = prices.first().price
            val finalPrice = prices.last().price
            val rate = ((finalPrice - initialPrice) / initialPrice) * 100
            "%.2f".format(rate)
        } else {
            "Nem uma referência para calcular"
        }
    }

    sealed class InflationIntent {
        object LoadProducts : InflationIntent()
        data class LoadPriceHistory(
            val productId: String,
            val period: String,
            val userId: String
        ) : InflationIntent()

        data class UpdatePeriod(val period: String, val userId: String) : InflationIntent()
        data class UpdateAnalysisType(val analysisType: String, val userId: String) :
            InflationIntent()
    }

    data class InflationViewState(
        val isLoading: Boolean = false,
        val products: ProductState = ProductState(),
        val supermarketNames: Map<String, String> = emptyMap(),
        val prices: PriceState = PriceState(),
        val inflationRate: String = "N/A",
        val entriesBySupermarket: Map<String, List<Entry>> = emptyMap(),
        val error: String? = null,
        val userState: String = "",
        val userCity: String = "",
        val analysisType: String = "Inflação de Produtos"
    )

    data class ProductState(
        val items: List<Product> = emptyList(),
        val selectedProduct: Product? = null
    )

    data class PriceState(
        val items: List<Price> = emptyList(),
        val period: String = "1 mês"
    )
}

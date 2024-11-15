package br.com.angelica.comprainteligente.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.angelica.comprainteligente.domain.usecase.GetPriceHistoryUseCase
import br.com.angelica.comprainteligente.domain.usecase.ProductOperationsUseCase
import br.com.angelica.comprainteligente.model.Price
import br.com.angelica.comprainteligente.model.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class InflationViewModel(
    private val getPriceHistoryUseCase: GetPriceHistoryUseCase,
    private val productOperationsUseCase: ProductOperationsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(InflationViewState())
    val state: StateFlow<InflationViewState> = _state.asStateFlow()

    init {
        loadProducts()
    }

    fun handleIntent(intent: InflationIntent) {
        when (intent) {
            is InflationIntent.LoadProducts -> loadProducts()
            is InflationIntent.LoadPriceHistory -> loadPriceHistory(intent.productId, intent.period)
            is InflationIntent.UpdatePeriod -> updatePeriod(intent.period)
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

    // Define o produto selecionado e carrega o histórico de preços
    fun setSelectedProduct(product: Product) {
        _state.value = _state.value.copy(
            products = _state.value.products.copy(selectedProduct = product)
        )

        // Carrega o histórico de preços para o produto selecionado
        loadPriceHistory(product.id, _state.value.prices.period)
    }

    // Atualiza o período e recarrega o histórico de preços
    private fun updatePeriod(period: String) {
        _state.value = _state.value.copy(
            prices = _state.value.prices.copy(period = period)
        )

        val selectedProduct = _state.value.products.selectedProduct
        if (selectedProduct != null) {
            loadPriceHistory(selectedProduct.id, period)
        }
    }

    // Carrega o histórico de preços para o produto e período especificados
    private fun loadPriceHistory(productId: String, period: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)

            val result = getPriceHistoryUseCase(productId, period)
            result.onSuccess { priceList ->
                val inflationRate = calculateInflationRate(priceList)
                _state.value = _state.value.copy(
                    isLoading = false,
                    prices = PriceState(items = priceList, period = period),
                    inflationRate = inflationRate,
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


    // Definindo as Intents possíveis para a interface de inflação
    sealed class InflationIntent {
        object LoadProducts : InflationIntent()
        data class LoadPriceHistory(val productId: String, val period: String) : InflationIntent()
        data class UpdatePeriod(val period: String) : InflationIntent()
    }

    // Representação do estado da interface para a tela de inflação
    data class InflationViewState(
        val isLoading: Boolean = false,
        val products: ProductState = ProductState(),
        val prices: PriceState = PriceState(),
        val inflationRate: String = "N/A",
        val error: String? = null
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

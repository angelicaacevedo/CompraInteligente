package br.com.angelica.comprainteligente.presentation.viewmodel

import androidx.lifecycle.ViewModel
import br.com.angelica.comprainteligente.model.SupermarketComparisonResult
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ListsViewModel : ViewModel() {
    private val _state = MutableStateFlow<ListState>(ListState.Loading)
    val state: StateFlow<ListState> = _state

    private val _priceAnalysisState = MutableStateFlow<PriceAnalysisState>(PriceAnalysisState.Idle)
    val priceAnalysisState: StateFlow<PriceAnalysisState> = _priceAnalysisState

    private val firestore = FirebaseFirestore.getInstance()
    private val shoppingListCollection = firestore.collection("shoppingLists")

    init {
        loadShoppingList()
    }

    private fun loadShoppingList() {
        shoppingListCollection.document("userShoppingList")
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val items = document.get("items") as? List<String> ?: emptyList()
                    _state.value = ListState.Success(items)
                } else {
                    _state.value = ListState.Success(emptyList())
                }
            }
            .addOnFailureListener { exception ->
                _state.value = ListState.Error(exception.message ?: "Erro ao carregar lista")
            }
    }

    fun addItemToShoppingList(item: String) {
        val currentState = _state.value
        if (currentState is ListState.Success) {
            val updatedItems = currentState.items + item
            saveShoppingList(updatedItems)
        }
    }

    fun removeItemFromShoppingList(item: String) {
        val currentState = _state.value
        if (currentState is ListState.Success) {
            val updatedItems = currentState.items - item
            saveShoppingList(updatedItems)
        }
    }

    private fun saveShoppingList(items: List<String>) {
        shoppingListCollection.document("userShoppingList")
            .set(mapOf("items" to items))
            .addOnSuccessListener {
                _state.value = ListState.Success(items)
            }
            .addOnFailureListener { exception ->
                _state.value = ListState.Error(exception.message ?: "Erro ao salvar lista")
            }
    }

    fun analyzePrices(shoppingList: List<String>) {
        // Implementar a lógica de análise de preços
    }

    sealed class ListState {
        object Loading : ListState()
        data class Success(val items: List<String>) : ListState()
        data class Error(val message: String) : ListState()
    }

    sealed class PriceAnalysisState {
        object Idle : PriceAnalysisState()
        data class Success(val result: List<SupermarketComparisonResult>) : PriceAnalysisState()
        data class Error(val message: String) : PriceAnalysisState()
    }
}
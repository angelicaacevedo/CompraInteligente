package br.com.angelica.comprainteligente.presentation.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AddPurchaseViewModel : ViewModel() {
    private val _name = MutableStateFlow("")
    val name: StateFlow<String> = _name

    private val _quantity = MutableStateFlow("")
    val quantity: StateFlow<String> = _quantity

    private val _price = MutableStateFlow("")
    val price: StateFlow<String> = _price

    private val _category = MutableStateFlow("")
    val category: StateFlow<String> = _category

    private val _state = MutableStateFlow<AddPurchaseState>(AddPurchaseState.Idle)
    val state: StateFlow<AddPurchaseState> = _state

    fun processIntent(intent: AddPurchaseIntent) {
        when (intent) {
            is AddPurchaseIntent.NameChanged -> _name.value = intent.name
            is AddPurchaseIntent.QuantityChanged -> _quantity.value = intent.quantity
            is AddPurchaseIntent.PriceChanged -> _price.value = intent.price
            is AddPurchaseIntent.CategoryChanged -> _category.value = intent.category
            is AddPurchaseIntent.SavePurchase -> savePurchase(intent.name, intent.quantity, intent.price, intent.category)
        }
    }

    private fun savePurchase(name: String, quantity: String, price: String, category: String) {
        _state.value = AddPurchaseState.Loading
        // Simulate saving purchase
        _state.value = AddPurchaseState.Success
    }

    sealed class AddPurchaseIntent {
        data class NameChanged(val name: String) : AddPurchaseIntent()
        data class QuantityChanged(val quantity: String) : AddPurchaseIntent()
        data class PriceChanged(val price: String) : AddPurchaseIntent()
        data class CategoryChanged(val category: String) : AddPurchaseIntent()
        data class SavePurchase(val name: String, val quantity: String, val price: String, val category: String) : AddPurchaseIntent()
    }

    sealed class AddPurchaseState {
        object Idle : AddPurchaseState()
        object Loading : AddPurchaseState()
        object Success : AddPurchaseState()
        data class Error(val message: String) : AddPurchaseState()
    }
}
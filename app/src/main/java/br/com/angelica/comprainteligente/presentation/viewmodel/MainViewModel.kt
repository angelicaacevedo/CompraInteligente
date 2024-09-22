package br.com.angelica.comprainteligente.presentation.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class Purchase(val name: String, val quantity: String, val price: String, val category: String)

class MainViewModel : ViewModel() {
    private val _purchases = MutableStateFlow<List<Purchase>>(emptyList())
    val purchases: StateFlow<List<Purchase>> = _purchases

    init {
        // Mock data
        _purchases.value = listOf(
            Purchase("Arroz", "2kg", "R$ 10,00", "Alimentos"),
            Purchase("Feijão", "1kg", "R$ 8,00", "Alimentos")
        )
    }
}
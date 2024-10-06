package br.com.angelica.comprainteligente.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import br.com.angelica.comprainteligente.model.ShoppingItem

class ShoppingListViewModel : ViewModel() {
    private val _shoppingList = MutableLiveData<List<ShoppingItem>>()
    val shoppingList: LiveData<List<ShoppingItem>> get() = _shoppingList

    init {
        _shoppingList.value = mutableListOf()
    }

    fun addItem(item: ShoppingItem) {
        val currentList = _shoppingList.value?.toMutableList() ?: mutableListOf()
        currentList.add(item)
        _shoppingList.value = currentList
    }

    fun removeItem(item: ShoppingItem) {
        val currentList = _shoppingList.value?.toMutableList() ?: mutableListOf()
        currentList.remove(item)
        _shoppingList.value = currentList
    }
}
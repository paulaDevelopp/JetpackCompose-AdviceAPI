package com.example.gemaroom2.api

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.await

class AdviceViewModel : ViewModel() {
    private val _advices = MutableStateFlow<List<Advice>>(emptyList()) // Lista de consejos
    val advices: StateFlow<List<Advice>> get() = _advices

    init {
        fetchAdvices(10) // Trae 10 consejos al inicializar
    }

    fun fetchAdvices(count: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val adviceList = mutableListOf<Advice>()
                repeat(count) { // Llama al endpoint 'count' veces
                    val response = RetrofitInstance.api.getRandomAdvice().await()
                    adviceList.add(response.slip) // Agrega cada consejo a la lista
                }
                _advices.value = adviceList
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}


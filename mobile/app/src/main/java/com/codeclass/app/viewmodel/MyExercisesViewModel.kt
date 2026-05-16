package com.codeclass.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codeclass.app.data.api.ApiService
import com.codeclass.app.data.model.Exercise
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MyExercisesViewModel(private val api: ApiService) : ViewModel() {

    private val _exercises = MutableStateFlow<List<Exercise>>(emptyList())
    val exercises: StateFlow<List<Exercise>> = _exercises

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _deleting = MutableStateFlow(false)
    val deleting: StateFlow<Boolean> = _deleting

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init { load() }

    fun load() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                _exercises.value = api.getMyExercises().data
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteExercise(id: Int, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _deleting.value = true
            try {
                api.deleteExercise(id)
                _exercises.value = _exercises.value.filter { it.id != id }
                onSuccess()
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _deleting.value = false
            }
        }
    }

    fun clearError() { _error.value = null }
}

package com.codeclass.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codeclass.app.data.api.ApiService
import com.codeclass.app.data.model.ExerciseDetail
import com.codeclass.app.data.model.SubmissionEntry
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SubmissionsViewModel(
    private val api: ApiService,
    val exerciseId: Int,
) : ViewModel() {

    data class State(
        val exercise: ExerciseDetail? = null,
        val mySubmissions: List<SubmissionEntry> = emptyList(),
        val globalSubmissions: List<SubmissionEntry> = emptyList(),
        val isLoadingExercise: Boolean = true,
        val isLoadingMy: Boolean = true,
        val isLoadingGlobal: Boolean = false,
        val myError: String? = null,
        val globalError: String? = null,
        val globalLoaded: Boolean = false,
    )

    private val _state = MutableStateFlow(State())
    val state: StateFlow<State> = _state.asStateFlow()

    init {
        loadExercise()
        loadMySubmissions()
    }

    private fun loadExercise() {
        viewModelScope.launch {
            runCatching { api.getExercise(exerciseId) }
                .onSuccess { r -> _state.update { it.copy(exercise = r.data, isLoadingExercise = false) } }
                .onFailure { _state.update { it.copy(isLoadingExercise = false) } }
        }
    }

    fun loadMySubmissions() {
        viewModelScope.launch {
            _state.update { it.copy(isLoadingMy = true, myError = null) }
            runCatching { api.getMySubmissions(exerciseId) }
                .onSuccess { list -> _state.update { it.copy(mySubmissions = list, isLoadingMy = false) } }
                .onFailure { e -> _state.update { it.copy(isLoadingMy = false, myError = e.message) } }
        }
    }

    fun loadGlobalSubmissions() {
        if (_state.value.globalLoaded) return
        viewModelScope.launch {
            _state.update { it.copy(isLoadingGlobal = true, globalError = null) }
            runCatching { api.getGlobalSubmissions(exerciseId) }
                .onSuccess { list -> _state.update { it.copy(globalSubmissions = list, isLoadingGlobal = false, globalLoaded = true) } }
                .onFailure { e -> _state.update { it.copy(isLoadingGlobal = false, globalError = e.message) } }
        }
    }
}

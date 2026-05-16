package com.codeclass.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codeclass.app.data.api.ApiService
import com.codeclass.app.data.local.TokenDataStore
import com.codeclass.app.data.model.Exercise
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ChallengesViewModel(
    private val api: ApiService,
    tokenStore: TokenDataStore,
) : ViewModel() {

    val isAuthenticated: StateFlow<Boolean> = tokenStore.token
        .map { !it.isNullOrEmpty() }
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    private val _rawExercises = MutableStateFlow<List<Exercise>>(emptyList())

    private val _total = MutableStateFlow(0)
    val total: StateFlow<Int> = _total

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _isLoadingMore = MutableStateFlow(false)
    val isLoadingMore: StateFlow<Boolean> = _isLoadingMore

    private val _currentPage = MutableStateFlow(1)
    private val _lastPage = MutableStateFlow(1)
    val hasMorePages: StateFlow<Boolean> = combine(_currentPage, _lastPage) { cur, last ->
        cur < last
    }.stateIn(viewModelScope, SharingStarted.Eagerly, false)

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _difficulty = MutableStateFlow("all")
    val difficulty: StateFlow<String> = _difficulty

    private val _category = MutableStateFlow("")
    val category: StateFlow<String> = _category

    private val _statusFilter = MutableStateFlow("all")
    val statusFilter: StateFlow<String> = _statusFilter

    val exercises: StateFlow<List<Exercise>> = combine(_rawExercises, _statusFilter) { exs, status ->
        when (status) {
            "solved"   -> exs.filter { it.isSolved }
            "unsolved" -> exs.filter { !it.isSolved }
            else       -> exs
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    private var searchJob: Job? = null

    fun onSearchChange(query: String) {
        _searchQuery.value = query
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(350)
            fetchExercises()
        }
    }

    fun onDifficultyChange(d: String) {
        _difficulty.value = d
        fetchExercises()
    }

    fun onCategoryChange(c: String) {
        _category.value = c
        fetchExercises()
    }

    fun onStatusFilterChange(s: String) {
        _statusFilter.value = s
    }

    fun fetchExercises() {
        viewModelScope.launch {
            _isLoading.value = true
            _currentPage.value = 1
            runCatching {
                api.getExercises(
                    page       = 1,
                    difficulty = _difficulty.value.takeIf { it != "all" },
                    category   = _category.value.takeUnless { it.isEmpty() },
                    search     = _searchQuery.value.trim().takeUnless { it.isBlank() },
                )
            }.onSuccess {
                _rawExercises.value = it.data
                _total.value = it.meta.total
                _lastPage.value = it.meta.lastPage
            }.onFailure {
                _rawExercises.value = emptyList()
                _total.value = 0
                _lastPage.value = 1
            }
            _isLoading.value = false
        }
    }

    fun loadNextPage() {
        val next = _currentPage.value + 1
        if (next > _lastPage.value || _isLoadingMore.value) return
        viewModelScope.launch {
            _isLoadingMore.value = true
            runCatching {
                api.getExercises(
                    page       = next,
                    difficulty = _difficulty.value.takeIf { it != "all" },
                    category   = _category.value.takeUnless { it.isEmpty() },
                    search     = _searchQuery.value.trim().takeUnless { it.isBlank() },
                )
            }.onSuccess {
                _rawExercises.value = _rawExercises.value + it.data
                _currentPage.value = next
                _lastPage.value = it.meta.lastPage
            }
            _isLoadingMore.value = false
        }
    }
}

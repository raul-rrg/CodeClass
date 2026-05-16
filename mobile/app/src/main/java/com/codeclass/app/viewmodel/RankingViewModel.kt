package com.codeclass.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codeclass.app.data.api.ApiService
import com.codeclass.app.data.model.LeaderboardEntry
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RankingViewModel(
    private val api: ApiService,
) : ViewModel() {

    private val _entries = MutableStateFlow<List<LeaderboardEntry>>(emptyList())
    val entries: StateFlow<List<LeaderboardEntry>> = _entries

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _currentUserId = MutableStateFlow<Int?>(null)
    val currentUserId: StateFlow<Int?> = _currentUserId

    init {
        fetch()
    }

    fun fetch() {
        viewModelScope.launch {
            _isLoading.value = true
            val leaderboardJob = async { runCatching { api.getLeaderboard() } }
            val meJob = async { runCatching { api.me() } }
            leaderboardJob.await()
                .onSuccess { _entries.value = it.data }
                .onFailure { _entries.value = emptyList() }
            meJob.await()
                .onSuccess { _currentUserId.value = it.id }
            _isLoading.value = false
        }
    }
}

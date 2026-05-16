package com.codeclass.app.viewmodel

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codeclass.app.data.api.ApiService
import com.codeclass.app.data.local.TokenDataStore
import com.codeclass.app.data.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class ProfileUiState(
    val user: User? = null,
    val isLoading: Boolean = true,
    val nameLoading: Boolean = false,
    val nameSuccess: Boolean = false,
    val nameError: String = "",
    val passwordLoading: Boolean = false,
    val passwordSuccess: Boolean = false,
    val passwordError: String = "",
)

class ProfileViewModel(
    private val api: ApiService,
    private val dataStore: TokenDataStore,
    private val prefs: SharedPreferences,
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileUiState())
    val state: StateFlow<ProfileUiState> = _state

    private val _selectedTheme = MutableStateFlow(prefs.getString("editor_theme", "Dracula") ?: "Dracula")
    val selectedTheme: StateFlow<String> = _selectedTheme

    private val _selectedFont = MutableStateFlow(prefs.getString("editor_font", "JetBrains Mono") ?: "JetBrains Mono")
    val selectedFont: StateFlow<String> = _selectedFont

    init {
        loadUser()
    }

    fun loadUser() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            runCatching { api.me() }
                .onSuccess { _state.value = _state.value.copy(user = it, isLoading = false) }
                .onFailure { _state.value = _state.value.copy(isLoading = false) }
        }
    }

    fun updateName(name: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(nameLoading = true, nameSuccess = false, nameError = "")
            runCatching { api.updateMe(mapOf("name" to name.trim())) }
                .onSuccess { updated ->
                    _state.value = _state.value.copy(user = updated, nameLoading = false, nameSuccess = true)
                }
                .onFailure { e ->
                    _state.value = _state.value.copy(nameLoading = false, nameError = e.message ?: "Error al guardar.")
                }
        }
    }

    fun updatePassword(currentPassword: String, newPassword: String, confirmation: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(passwordLoading = true, passwordSuccess = false, passwordError = "")
            runCatching {
                api.updateMe(mapOf(
                    "current_password"      to currentPassword,
                    "password"              to newPassword,
                    "password_confirmation" to confirmation,
                ))
            }
                .onSuccess { _state.value = _state.value.copy(passwordLoading = false, passwordSuccess = true) }
                .onFailure { e ->
                    _state.value = _state.value.copy(passwordLoading = false, passwordError = e.message ?: "Error al guardar.")
                }
        }
    }

    fun clearNameFeedback() {
        _state.value = _state.value.copy(nameSuccess = false, nameError = "")
    }

    fun clearPasswordFeedback() {
        _state.value = _state.value.copy(passwordSuccess = false, passwordError = "")
    }

    fun selectTheme(theme: String) {
        _selectedTheme.value = theme
        prefs.edit().putString("editor_theme", theme).apply()
    }

    fun selectFont(font: String) {
        _selectedFont.value = font
        prefs.edit().putString("editor_font", font).apply()
    }

    fun logout() {
        viewModelScope.launch {
            runCatching { api.logout() }
            dataStore.clearToken()
        }
    }
}

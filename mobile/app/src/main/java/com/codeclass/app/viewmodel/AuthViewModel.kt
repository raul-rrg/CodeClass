package com.codeclass.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codeclass.app.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// Estado UI compartido por Login y Register
sealed class AuthUiState {
    object Idle : AuthUiState()
    object Loading : AuthUiState()
    object Success : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}

class AuthViewModel(private val repo: AuthRepository) : ViewModel() {

    private val _state = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val state: StateFlow<AuthUiState> = _state

    fun login(email: String, password: String) {
        _state.value = AuthUiState.Loading
        viewModelScope.launch {
            runCatching { repo.login(email, password) }
                .onSuccess { _state.value = AuthUiState.Success }
                .onFailure { _state.value = AuthUiState.Error(it.message ?: "Error al iniciar sesión") }
        }
    }

    fun register(name: String, email: String, password: String, passwordConfirmation: String, role: String) {
        _state.value = AuthUiState.Loading
        viewModelScope.launch {
            runCatching { repo.register(name, email, password, passwordConfirmation, role) }
                .onSuccess { _state.value = AuthUiState.Success }
                .onFailure { _state.value = AuthUiState.Error(it.message ?: "Error al registrarse") }
        }
    }

    // Resetea el estado al volver a la pantalla para no reusar un Success/Error anterior
    fun resetState() { _state.value = AuthUiState.Idle }
}

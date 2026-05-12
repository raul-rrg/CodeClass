package com.codeclass.app.data.repository

import com.codeclass.app.data.api.ApiService
import com.codeclass.app.data.local.TokenDataStore
import com.codeclass.app.data.model.User

// Repositorio de autenticación: conrola login, registro y logout

class AuthRepository(
    private val api: ApiService,
    private val dataStore: TokenDataStore,
) {
    // Llama al backend, guarda el token y devuelve el usuario
    suspend fun login(email: String, password: String): User {
        val response = api.login(mapOf("email" to email, "password" to password))
        dataStore.saveToken(response.token)
        return response.user
    }

    // Registra al usuario, guarda el token y devuelve el usuario
    suspend fun register(name: String, email: String, password: String, passwordConfirmation: String, role: String): User {
        val response = api.register(
            mapOf(
                "name" to name,
                "email" to email,
                "password" to password,
                "password_confirmation" to passwordConfirmation,
                "role" to role,
            )
        )
        dataStore.saveToken(response.token)
        return response.user
    }

    // Revoca el token en el backend y lo borra localmente
    suspend fun logout() {
        runCatching { api.logout() } 
        dataStore.clearToken()
    }
}

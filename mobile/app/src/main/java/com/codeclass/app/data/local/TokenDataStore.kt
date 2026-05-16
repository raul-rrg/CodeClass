package com.codeclass.app.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// DataStore para persistir el token de autenticación (Sanctum) de forma segura y reactiva



// Extensión — crea un DataStore llamado "auth" ligado al contexto de la app
private val Context.dataStore by preferencesDataStore(name = "auth")

class TokenDataStore(private val context: Context) {

    companion object {
        private val TOKEN_KEY = stringPreferencesKey("token")
        private val ROLE_KEY  = stringPreferencesKey("role")
    }

    val token: Flow<String?> = context.dataStore.data.map { it[TOKEN_KEY] }
    val role: Flow<String?>  = context.dataStore.data.map { it[ROLE_KEY] }

    suspend fun saveToken(token: String) {
        context.dataStore.edit { it[TOKEN_KEY] = token }
    }

    suspend fun saveRole(role: String) {
        context.dataStore.edit { it[ROLE_KEY] = role }
    }

    suspend fun clearToken() {
        context.dataStore.edit { it.remove(TOKEN_KEY); it.remove(ROLE_KEY) }
    }
}

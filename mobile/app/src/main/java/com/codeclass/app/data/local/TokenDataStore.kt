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
        private val TOKEN_KEY = stringPreferencesKey("token") // clave para el token Sanctum
    }

    // Flow reactivo — emite el token actual o null si no hay sesión
    val token: Flow<String?> = context.dataStore.data.map { it[TOKEN_KEY] }

    // Persiste el token tras login/register
    suspend fun saveToken(token: String) {
        context.dataStore.edit { it[TOKEN_KEY] = token }
    }

    // Borra el token al hacer logout
    suspend fun clearToken() {
        context.dataStore.edit { it.remove(TOKEN_KEY) }
    }
}

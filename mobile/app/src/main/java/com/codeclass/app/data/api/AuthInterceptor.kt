package com.codeclass.app.data.api

import com.codeclass.app.data.local.TokenDataStore
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

// Sirve para añadir el token de autenticación a cada request que se hace al backend Laravel,
// usando el formato que Laravel Sanct


// Interceptor OkHttp — añade "Authorization: Bearer <token>" a todas las requests
class AuthInterceptor(private val dataStore: TokenDataStore) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        // runBlocking necesario porque OkHttp no es suspendible
        val token = runBlocking { dataStore.token.firstOrNull() }
        val request = chain.request().newBuilder().apply {
            if (token != null) addHeader("Authorization", "Bearer $token")
            addHeader("Accept", "application/json") 
        }.build()
        return chain.proceed(request)
    }
}

package com.codeclass.app.data.api

import com.codeclass.app.data.local.TokenDataStore
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


// 10.0.2.2 es la IP especial del emulador Android que apunta a localhost del host
private const val BASE_URL = "http://10.0.2.2:8000/api/v1/"

object RetrofitClient {

    fun create(dataStore: TokenDataStore): ApiService {
        // Log de requests/responses completo — útil en desarrollo
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(dataStore)) // añade token a cada request
            .addInterceptor(logging)
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create()) // serializa/deserializa JSON con Gson
            .build()
            .create(ApiService::class.java)
    }
}

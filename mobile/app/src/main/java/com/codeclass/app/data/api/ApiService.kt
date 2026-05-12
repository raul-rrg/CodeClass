package com.codeclass.app.data.api

import com.codeclass.app.data.model.*
import retrofit2.http.*

// Conexión con el backend Laravel: define las rutas y 
// los datos que se envían/reciben para cada operación 

interface ApiService {

    // --- Auth ---

    @POST("login")
    suspend fun login(@Body body: Map<String, String>): AuthResponse

    @POST("register")
    suspend fun register(@Body body: Map<String, String>): AuthResponse

    @POST("logout")
    suspend fun logout()

    @GET("users/me")
    suspend fun me(): User

    // --- Exercises ---

    @GET("exercises")
    suspend fun getExercises(
        @Query("page") page: Int = 1,
        @Query("difficulty") difficulty: String? = null,
        @Query("search") search: String? = null,
    ): PaginatedResponse<Exercise>

    @GET("exercises/{id}")
    suspend fun getExercise(@Path("id") id: Int): ExerciseDetail

    // --- Submit ---

    @POST("exercises/{id}/submissions")
    suspend fun submit(@Path("id") id: Int, @Body body: SubmitRequest): Submission

    // --- Leaderboard ---
    
    @GET("leaderboard")
    suspend fun getLeaderboard(): List<LeaderboardEntry>
}

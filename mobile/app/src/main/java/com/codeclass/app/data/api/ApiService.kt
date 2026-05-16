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

    @PUT("users/me")
    suspend fun updateMe(@Body body: Map<String, String>): User

    // --- Exercises ---

    @GET("exercises")
    suspend fun getExercises(
        @Query("page") page: Int = 1,
        @Query("difficulty") difficulty: String? = null,
        @Query("category") category: String? = null,
        @Query("search") search: String? = null,
    ): PaginatedResponse<Exercise>

    @GET("exercises/{id}")
    suspend fun getExercise(@Path("id") id: Int): ResourceResponse<ExerciseDetail>

    // --- Run / Submit ---

    @POST("exercises/{id}/run")
    suspend fun run(@Path("id") id: Int, @Body body: RunRequest): RunResult

    @POST("exercises/{id}/submissions")
    suspend fun submit(@Path("id") id: Int, @Body body: SubmitRequest): Submission

    // --- Submissions ---

    @GET("exercises/{id}/submissions")
    suspend fun getMySubmissions(@Path("id") id: Int): List<SubmissionEntry>

    @GET("exercises/{id}/submissions")
    suspend fun getGlobalSubmissions(@Path("id") id: Int, @Query("global") global: Boolean = true): List<SubmissionEntry>

    // --- Leaderboard ---

    @GET("submissions/{id}")
    suspend fun getSubmission(@Path("id") id: Int): SubmissionDetail

    @GET("leaderboard")
    suspend fun getLeaderboard(): LeaderboardResponse

    // --- My Exercises (CRUD) ---

    @GET("users/me/exercises")
    suspend fun getMyExercises(): CollectionResponse<Exercise>

    @POST("exercises")
    suspend fun createExercise(@Body body: ExerciseCreateRequest): ResourceResponse<ExerciseDetail>

    @PUT("exercises/{id}")
    suspend fun updateExercise(@Path("id") id: Int, @Body body: ExerciseCreateRequest): ResourceResponse<ExerciseDetail>

    @DELETE("exercises/{id}")
    suspend fun deleteExercise(@Path("id") id: Int)
}

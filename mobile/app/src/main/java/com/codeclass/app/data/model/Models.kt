package com.codeclass.app.data.model

import com.google.gson.annotations.SerializedName

// Modelos de datos que representan las entidades principales de la app: usuario, ejercicio, submission, etc.

// Usuario autenticado
data class User(
    val id: Int,
    val name: String,
    val email: String,
    val role: String, // Student o Teacher
    @SerializedName("avatar_url") val avatarUrl: String?,
)


// Respuesta de login y register :  Contiene usuario + token Sanctum
data class AuthResponse(
    val user: User,
    val token: String,
)

 
// Ejercicio en listado (version resumida para listados) — no incluye templates ni test cases
data class Exercise(
    val id: Int,
    val title: String,
    val description: String?,
    @SerializedName("short_description") val shortDescription: String?,
    val difficulty: String, // "easy" | "medium" | "hard" | "insane"
    val category: String?,
    @SerializedName("is_verified") val isVerified: Boolean,
    @SerializedName("is_published") val isPublished: Boolean,
    val author: String?,
    @SerializedName("author_avatar") val authorAvatar: String?,
    @SerializedName("is_solved") val isSolved: Boolean, 
    @SerializedName("created_at") val createdAt: String?,
)



// Caso de prueba de un ejercicio — incluye input, output esperado y si es oculto o no
data class TestCase(
    val id: Int,
    @SerializedName("is_hidden") val isHidden: Boolean, 
    val input: String?,
    @SerializedName("expected_output") val expectedOutput: String?,
)

// Detalle completo de un ejercicio — incluye templates de código y test cases
data class ExerciseDetail(
    val id: Int,
    val title: String,
    val description: String?,
    @SerializedName("short_description") val shortDescription: String?,
    val difficulty: String,
    val category: String?,
    @SerializedName("is_published") val isPublished: Boolean,
    @SerializedName("is_verified") val isVerified: Boolean,
    @SerializedName("function_name") val functionName: String?, 
    val parameters: List<Map<String, String>>?, 
    @SerializedName("return_type") val returnType: String?,
    val templates: Map<String, String>?, 
    val author: String?,
    @SerializedName("is_solved") val isSolved: Boolean,
    @SerializedName("test_cases") val testCases: List<TestCase>,
)


// Submission enviada por el usuario
data class Submission(
    val id: Int,
    val status: String, // "accepted" | "wrong_answer" | "runtime_error" | "pending"
    val language: String,
    val code: String?,
    val score: Int?,
    @SerializedName("created_at") val createdAt: String?,
)

// Body para enviar una submission
data class SubmitRequest(
    val language: String,
    val code: String,
)

// Body para ejecutar código sin guardar (solo test cases visibles)
data class RunRequest(
    val language: String,
    val code: String,
)

// Resultado de ejecutar código
data class RunResult(
    val status: String,
    val output: String?,
    val error: String?,
)

// Entrada del ranking global
data class LeaderboardEntry(
    val rank: Int,
    val name: String,
    @SerializedName("avatar_url") val avatarUrl: String?,
    val score: Int,
    @SerializedName("solved_count") val solvedCount: Int,
)

// Wrapper de paginación de Laravel — envuelve cualquier lista paginada
data class PaginatedResponse<T>(
    val data: List<T>,
    val current_page: Int,
    val last_page: Int,
    val total: Int,
)

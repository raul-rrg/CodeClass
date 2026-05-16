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
    val points: Int = 0,
    @SerializedName("solved_count") val solvedCount: Int = 0,
    @SerializedName("created_at") val createdAt: String? = null,
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
    val input: List<Any>?,
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
    @SerializedName("test_cases") val testCases: List<TestCase> = emptyList(),
)


// Submission enviada por el usuario
data class Submission(
    val id: Int,
    val status: String, // "accepted" | "wrong_answer" | "runtime_error" | "pending"
    val language: String,
    val code: String?,
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

// Resultado individual de un test case al ejecutar código
data class RunTestResult(
    @SerializedName("test_case_id") val testCaseId: Int,
    val passed: Boolean,
    val output: String?,
    val error: String?,
    val status: String,
    val time: Double?,
    @SerializedName("expected_output") val expectedOutput: String?,
)

// Resultado de ejecutar código contra test cases visibles
data class RunResult(
    @SerializedName("passed_count") val passedCount: Int = 0,
    @SerializedName("total_count") val totalCount: Int = 0,
    @SerializedName("compile_output") val compileOutput: String?,
    @SerializedName("error_type") val errorType: String?,
    val results: List<RunTestResult> = emptyList(),
)

// Entrada del ranking global
data class LeaderboardEntry(
    val position: Int,
    @SerializedName("user_id") val userId: Int,
    val name: String,
    @SerializedName("avatar_url") val avatarUrl: String?,
    val solved: Int,
    @SerializedName("total_points") val totalPoints: Int,
)

data class LeaderboardResponse(val data: List<LeaderboardEntry>)

data class SubResult(val passed: Boolean)

data class SubUser(val id: Int, val name: String)

data class SubmissionEntry(
    val id: Int,
    val status: String,
    val language: String,
    @SerializedName("created_at")         val createdAt: String?,
    @SerializedName("max_execution_time") val maxExecutionTime: Double?,
    @SerializedName("max_memory")         val maxMemory: Int?,
    @SerializedName("submission_results") val submissionResults: List<SubResult>?,
    val user: SubUser?,
) {
    val passedCount: Int?      get() = submissionResults?.count { it.passed }
    val totalCount: Int?       get() = submissionResults?.size
    val userName: String?      get() = user?.name
    val executionTime: String? get() = maxExecutionTime?.let { "%.3fs".format(it) }
    val memoryUsed: String?    get() = maxMemory?.let {
        if (it >= 1024) "%.1f MB".format(it / 1024.0) else "$it KB"
    }
}

// Wrapper para Laravel Resource single — { data: { ...objeto... } }
data class ResourceResponse<T>(val data: T)

// Wrapper de paginación de Laravel Resource Collection
// La API devuelve { data: [...], links: {...}, meta: { current_page, last_page, total, ... } }
data class PaginatedResponse<T>(
    val data: List<T>,
    val meta: PaginationMeta,
)

data class PaginationMeta(
    @SerializedName("current_page") val currentPage: Int,
    @SerializedName("last_page") val lastPage: Int,
    val total: Int,
)

data class CollectionResponse<T>(val data: List<T>)

data class SubmissionTestCase(
    val id: Int,
    @SerializedName("is_hidden") val isHidden: Boolean,
    val input: List<Any>?,
    @SerializedName("expected_output") val expectedOutput: String?,
)

data class SubmissionResultDetail(
    @SerializedName("test_case_id") val testCaseId: Int,
    val passed: Boolean,
    val output: String?,
    val error: String?,
    val status: String,
    @SerializedName("execution_time") val executionTime: Double?,
    val memory: Int?,
    @SerializedName("test_case") val testCase: SubmissionTestCase?,
)

data class SubmissionDetail(
    val id: Int,
    val status: String,
    val language: String,
    @SerializedName("created_at") val createdAt: String?,
    @SerializedName("compile_output") val compileOutput: String?,
    @SerializedName("submission_results") val submissionResults: List<SubmissionResultDetail> = emptyList(),
)

data class ParameterCreate(
    val name: String,
    val type: String,
)

data class TestCaseCreate(
    val input: List<Any>,
    @SerializedName("expected_output") val expectedOutput: String,
    @SerializedName("is_hidden") val isHidden: Boolean = false,
)

data class ExerciseCreateRequest(
    val title: String,
    @SerializedName("short_description") val shortDescription: String,
    val description: String,
    val difficulty: String,
    val category: String,
    @SerializedName("function_name") val functionName: String,
    val parameters: List<ParameterCreate>,
    @SerializedName("return_type") val returnType: String,
    @SerializedName("test_cases") val testCases: List<TestCaseCreate>,
    @SerializedName("is_published") val isPublished: Boolean,
    @SerializedName("solution_code") val solutionCode: String? = null,
    @SerializedName("solution_language") val solutionLanguage: String? = null,
)

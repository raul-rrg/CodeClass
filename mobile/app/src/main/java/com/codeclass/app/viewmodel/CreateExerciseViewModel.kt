package com.codeclass.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codeclass.app.data.api.ApiService
import com.codeclass.app.data.model.ExerciseCreateRequest
import com.codeclass.app.data.model.ParameterCreate
import com.codeclass.app.data.model.TestCaseCreate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class ParamForm(
    val name: String = "",
    val type: String = "int",
)

data class TestCaseForm(
    val inputs: List<String> = emptyList(),
    val expectedOutput: String = "",
    val isHidden: Boolean = false,
)

data class ExerciseForm(
    val title: String = "",
    val shortDescription: String = "",
    val description: String = "",
    val difficulty: String = "easy",
    val category: String = "",
    val functionName: String = "",
    val parameters: List<ParamForm> = listOf(ParamForm()),
    val returnType: String = "int",
    val testCases: List<TestCaseForm> = listOf(
        TestCaseForm(isHidden = false),
        TestCaseForm(isHidden = true),
    ),
    val isPublished: Boolean = false,
    val solutionCode: String = "",
    val solutionLanguage: String = "python",
)

val PARAM_TYPES    = listOf("int", "float", "string", "bool", "array")
val EXERCISE_CATEGORIES = listOf(
    "math", "strings", "arrays", "trees",
    "graphs", "sorting", "dynamic programming", "recursion",
)
val EXERCISE_DIFFICULTIES = listOf("easy", "medium", "hard", "insane")
val SOLUTION_LANGUAGES = listOf("python", "javascript", "java")

class CreateExerciseViewModel(
    private val api: ApiService,
    private val exerciseId: Int? = null,
) : ViewModel() {

    private val _form = MutableStateFlow(ExerciseForm())
    val form: StateFlow<ExerciseForm> = _form

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _success = MutableStateFlow(false)
    val success: StateFlow<Boolean> = _success

    val isEditMode = exerciseId != null

    init {
        if (exerciseId != null) loadExercise(exerciseId)
    }

    private fun loadExercise(id: Int) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val ex = api.getExercise(id).data
                _form.value = ExerciseForm(
                    title            = ex.title,
                    shortDescription = ex.shortDescription ?: "",
                    description      = ex.description ?: "",
                    difficulty       = ex.difficulty,
                    category         = ex.category ?: "",
                    functionName     = ex.functionName ?: "",
                    parameters       = ex.parameters?.map {
                        ParamForm(name = it["name"] ?: "", type = it["type"] ?: "int")
                    } ?: listOf(ParamForm()),
                    returnType       = ex.returnType ?: "int",
                    testCases        = ex.testCases.map { tc ->
                        val ins = (tc.input ?: emptyList()).map { it.toString() }
                        TestCaseForm(inputs = ins, expectedOutput = tc.expectedOutput ?: "", isHidden = tc.isHidden)
                    }.ifEmpty { listOf(TestCaseForm(), TestCaseForm(isHidden = true)) },
                    isPublished      = ex.isPublished,
                )
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }

    // ── Form field updaters ──

    fun updateTitle(v: String)            { _form.value = _form.value.copy(title = v) }
    fun updateShortDesc(v: String)        { _form.value = _form.value.copy(shortDescription = v) }
    fun updateDescription(v: String)      { _form.value = _form.value.copy(description = v) }
    fun updateDifficulty(v: String)       { _form.value = _form.value.copy(difficulty = v) }
    fun updateCategory(v: String)         { _form.value = _form.value.copy(category = v) }
    fun updateFunctionName(v: String)     { _form.value = _form.value.copy(functionName = v) }
    fun updateReturnType(v: String)       { _form.value = _form.value.copy(returnType = v) }
    fun updateIsPublished(v: Boolean)     { _form.value = _form.value.copy(isPublished = v) }
    fun updateSolutionCode(v: String)     { _form.value = _form.value.copy(solutionCode = v) }
    fun updateSolutionLanguage(v: String) { _form.value = _form.value.copy(solutionLanguage = v) }

    // ── Parameters ──

    fun updateParam(index: Int, name: String? = null, type: String? = null) {
        val params = _form.value.parameters.toMutableList()
        val old = params[index]
        params[index] = old.copy(name = name ?: old.name, type = type ?: old.type)
        _form.value = _form.value.copy(parameters = params)
    }

    fun addParam() {
        if (_form.value.parameters.size >= 5) return
        _form.value = _form.value.copy(parameters = _form.value.parameters + ParamForm())
    }

    fun removeParam(index: Int) {
        if (_form.value.parameters.size <= 1) return
        val params = _form.value.parameters.toMutableList()
        params.removeAt(index)
        _form.value = _form.value.copy(parameters = params)
        // Trim test case inputs to match new param count
        val paramCount = params.size
        val tcs = _form.value.testCases.map { tc ->
            tc.copy(inputs = tc.inputs.take(paramCount))
        }
        _form.value = _form.value.copy(testCases = tcs)
    }

    // ── Test cases ──

    fun updateTestInput(tcIndex: Int, paramIndex: Int, value: String) {
        val tcs = _form.value.testCases.toMutableList()
        val tc = tcs[tcIndex]
        val ins = tc.inputs.toMutableList()
        while (ins.size <= paramIndex) ins.add("")
        ins[paramIndex] = value
        tcs[tcIndex] = tc.copy(inputs = ins)
        _form.value = _form.value.copy(testCases = tcs)
    }

    fun updateTestExpected(index: Int, value: String) {
        val tcs = _form.value.testCases.toMutableList()
        tcs[index] = tcs[index].copy(expectedOutput = value)
        _form.value = _form.value.copy(testCases = tcs)
    }

    fun toggleTestHidden(index: Int) {
        val tcs = _form.value.testCases.toMutableList()
        tcs[index] = tcs[index].copy(isHidden = !tcs[index].isHidden)
        _form.value = _form.value.copy(testCases = tcs)
    }

    fun addTestCase() {
        if (_form.value.testCases.size >= 15) return
        _form.value = _form.value.copy(testCases = _form.value.testCases + TestCaseForm(isHidden = true))
    }

    fun removeTestCase(index: Int) {
        if (_form.value.testCases.size <= 2) return
        val tcs = _form.value.testCases.toMutableList()
        tcs.removeAt(index)
        _form.value = _form.value.copy(testCases = tcs)
    }

    // ── Validation ──

    fun isStep1Valid(): Boolean {
        val f = _form.value
        return f.title.trim().length >= 6 && f.difficulty.isNotEmpty() && f.category.isNotEmpty()
    }

    fun isStep2Valid(): Boolean {
        val f = _form.value
        return f.functionName.trim().length >= 2 && f.parameters.all { it.name.trim().isNotEmpty() }
    }

    fun isStep3Valid(): Boolean {
        val f = _form.value
        return f.testCases.size >= 2 && f.testCases.all { tc ->
            tc.expectedOutput.isNotBlank() &&
                f.parameters.indices.all { i -> tc.inputs.getOrNull(i)?.isNotBlank() == true }
        }
    }

    fun isStepValid(step: Int) = when (step) {
        1 -> isStep1Valid()
        2 -> isStep2Valid()
        3 -> isStep3Valid()
        else -> true
    }

    // ── Submit ──

    fun submit() {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                val f = _form.value
                val request = ExerciseCreateRequest(
                    title            = f.title.trim(),
                    shortDescription = f.shortDescription.trim(),
                    description      = f.description.trim(),
                    difficulty       = f.difficulty,
                    category         = f.category,
                    functionName     = f.functionName.trim(),
                    parameters       = f.parameters.map { ParameterCreate(it.name.trim(), it.type) },
                    returnType       = f.returnType,
                    testCases        = f.testCases.map { tc ->
                        TestCaseCreate(
                            input          = f.parameters.indices.map { i ->
                                parseInputValue(tc.inputs.getOrElse(i) { "" }, f.parameters[i].type)
                            },
                            expectedOutput = tc.expectedOutput,
                            isHidden       = tc.isHidden,
                        )
                    },
                    isPublished      = f.isPublished,
                    solutionCode     = f.solutionCode.ifBlank { null },
                    solutionLanguage = if (f.solutionCode.isNotBlank()) f.solutionLanguage else null,
                )
                if (isEditMode && exerciseId != null) {
                    api.updateExercise(exerciseId, request)
                } else {
                    api.createExercise(request)
                }
                _success.value = true
            } catch (e: Exception) {
                _error.value = e.message ?: "Error desconocido"
            } finally {
                _loading.value = false
            }
        }
    }

    fun clearError() { _error.value = null }

    private fun parseInputValue(raw: String, type: String): Any {
        val trimmed = raw.trim()
        return when (type) {
            "int"   -> trimmed.toIntOrNull() ?: trimmed
            "float" -> trimmed.toDoubleOrNull() ?: trimmed
            "bool"  -> trimmed.lowercase() == "true"
            "array" -> try {
                com.google.gson.Gson().fromJson(trimmed, List::class.java) ?: trimmed
            } catch (e: Exception) { trimmed }
            else    -> trimmed
        }
    }
}

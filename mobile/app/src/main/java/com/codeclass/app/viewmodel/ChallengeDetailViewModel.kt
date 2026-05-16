package com.codeclass.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codeclass.app.data.api.ApiService
import com.codeclass.app.data.model.ExerciseDetail
import com.codeclass.app.data.model.RunRequest
import com.codeclass.app.data.model.RunResult
import com.codeclass.app.data.model.SubmissionDetail
import com.codeclass.app.data.model.SubmitRequest
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ChallengeDetailViewModel(
    private val api: ApiService,
    private val exerciseId: Int,
) : ViewModel() {

    private val _exercise = MutableStateFlow<ExerciseDetail?>(null)
    val exercise: StateFlow<ExerciseDetail?> = _exercise

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _runResult = MutableStateFlow<RunResult?>(null)
    val runResult: StateFlow<RunResult?> = _runResult

    private val _isRunning = MutableStateFlow(false)
    val isRunning: StateFlow<Boolean> = _isRunning

    private val _submissionDetail = MutableStateFlow<SubmissionDetail?>(null)
    val submissionDetail: StateFlow<SubmissionDetail?> = _submissionDetail

    private val _isSubmitting = MutableStateFlow(false)
    val isSubmitting: StateFlow<Boolean> = _isSubmitting

    private val _actionError = MutableStateFlow<String?>(null)
    val actionError: StateFlow<String?> = _actionError

    private var submitJob: Job? = null

    init { load() }

    fun load() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            runCatching { api.getExercise(exerciseId).data }
                .onSuccess { _exercise.value = it }
                .onFailure { _error.value = it.message }
            _isLoading.value = false
        }
    }

    fun runCode(language: String, code: String) {
        viewModelScope.launch {
            _isRunning.value = true
            _actionError.value = null
            _runResult.value = null
            _submissionDetail.value = null
            runCatching { api.run(exerciseId, RunRequest(language, code)) }
                .onSuccess { _runResult.value = it }
                .onFailure { _actionError.value = it.message ?: "Error al ejecutar" }
            _isRunning.value = false
        }
    }

    fun submitCode(language: String, code: String) {
        submitJob?.cancel()
        submitJob = viewModelScope.launch {
            _isSubmitting.value = true
            _actionError.value = null
            _runResult.value = null
            _submissionDetail.value = null

            val submissionId = runCatching {
                api.submit(exerciseId, SubmitRequest(language, code)).id
            }.getOrElse {
                _actionError.value = it.message ?: "Error al enviar"
                _isSubmitting.value = false
                return@launch
            }

            // Poll until evaluation completes (max 60 s)
            repeat(30) {
                delay(2000)
                val detail = runCatching { api.getSubmission(submissionId) }.getOrNull()
                if (detail != null && detail.status != "pending") {
                    _submissionDetail.value = detail
                    _isSubmitting.value = false
                    return@launch
                }
            }

            _actionError.value = "Tiempo de espera agotado. Revisa tus submissions."
            _isSubmitting.value = false
        }
    }

    fun clearResults() {
        submitJob?.cancel()
        _runResult.value = null
        _submissionDetail.value = null
        _actionError.value = null
        _isSubmitting.value = false
    }
}

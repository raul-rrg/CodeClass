import { ref } from 'vue'
import { useI18n } from 'vue-i18n'
import { api } from '@/services/api'
import { validators } from '@/validators/codeValidators'

function playSound(type) {
    const src = type === 'accepted' ? '/sounds/resolve_challenge.mp3' : '/sounds/faied_challange.mp3'
    new Audio(src).play().catch(() => {})
}

export function useSubmission(challenge) {
    const { t } = useI18n()

    // Estado que comparte este composable con la interfaz de usuario.
    const submissionResult = ref({ status: 'pending', details: null })
    const isLoading = ref(false)
    const isResultModalOpen = ref(false)
    const submissionHistory = ref([])
    const globalSubmissions = ref([])

    // Carga el historial de envios del ejercicio actual.
    async function fetchSubmissions() {
        if (!challenge.value?.id) return

        try {
            const submissionList = await api.get(`/exercises/${challenge.value.id}/submissions`)
            submissionHistory.value = Array.isArray(submissionList) ? submissionList : []
        } catch {
            // Se ignora el error para no bloquear la pantalla principal.
        }
    }

    // Carga todos los envios del ejercicio (global).
    async function fetchGlobalSubmissions() {
        if (!challenge.value?.id) return

        try {
            const list = await api.get(`/exercises/${challenge.value.id}/submissions?global=1`)
            globalSubmissions.value = Array.isArray(list) ? list : []
        } catch {
            // Se ignora el error para no bloquear la pantalla principal.
        }
    }

    // Valida el codigo contra los validadores definidos antes de ejecutar o enviar.
    function validate(code, template, language) {
        for (const validatorFn of validators) {
            const result = validatorFn(code, template, language, challenge.value)

            if (result) {
                submissionResult.value = {
                    status: 'template_error',
                    details: { error_message: t(result.key, result.params ?? {}) },
                }
                return false
            }
        }

        return true
    }

    // Ejecuta el codigo sin guardar una submission en la base de datos.
    async function runCode(language, code, template) {
        if (!validate(code, template, language)) return

        isLoading.value = true
        submissionResult.value = { status: 'loading', details: null }

        try {
            const runResult = await api.post(`/exercises/${challenge.value.id}/run`, {
                language,
                code,
            })
            const allTestsPassed = runResult.passed_count === runResult.total_count && runResult.total_count > 0
            submissionResult.value = {
                status: allTestsPassed ? 'accepted' : 'rejected',
                details: runResult,
            }
        } catch (error) {
            submissionResult.value = {
                status: 'error',
                details: { error_message: error?.message ?? t('challenge.validation.unknown_error') },
            }
        } finally {
            isLoading.value = false
        }
    }

    // Adapta la respuesta del backend al formato que consume el modal/resultados.
    function mapSubmission(submission) {
        const backendResults = submission.submission_results ?? []
        const normalizedResults = backendResults.map(testResult => ({
            test_case_id: testResult.test_case_id,
            passed: testResult.passed,
            output: testResult.output,
            error: testResult.error,
            status: testResult.status,
            time: testResult.execution_time ?? null,
        }))

        let detectedErrorType = null

        if (normalizedResults.some(testResult => testResult.status === 'time_limit_exceeded')) {
            detectedErrorType = 'time_limit_exceeded'
        } else if (normalizedResults.some(testResult => testResult.status === 'compile_error')) {
            detectedErrorType = 'compile_error'
        } else if (normalizedResults.some(testResult => testResult.status === 'runtime_error')) {
            detectedErrorType = 'runtime_error'
        }

        return {
            passed_count: normalizedResults.filter(testResult => testResult.passed).length,
            total_count: normalizedResults.length,
            results: normalizedResults,
            compile_output: submission.compile_output,
            error_type: detectedErrorType,
        }
    }

    // Hace polling mientras la submission siga pendiente.
    async function waitForSubmissionCompletion(id, maxAttempts = 30, interval = 1500) {
        for (let attempt = 0; attempt < maxAttempts; attempt++) {
            await new Promise(resolve => setTimeout(resolve, interval))
            const submission = await api.get(`/submissions/${id}`)

            if (submission.status !== 'pending') return submission
        }

        throw new Error(t('challenge.validation.timeout'))
    }

    // Envía la solucion final, normaliza la respuesta y abre el modal.
    async function submitSolution(language, code, template) {
        if (!validate(code, template, language)) return

        isLoading.value = true
        submissionResult.value = { status: 'loading', details: null }

        try {
            let submission = await api.post(`/exercises/${challenge.value.id}/submissions`, {
                language,
                code,
            })

            if (submission.status === 'pending') {
                submission = await waitForSubmissionCompletion(submission.id)
            }

            const details = mapSubmission(submission)
            submissionResult.value = {
                status: submission.status,
                details,
            }

            playSound(submission.status)
            isResultModalOpen.value = true
            fetchSubmissions()
        } catch (error) {
            submissionResult.value = {
                status: 'error',
                details: { error_message: error?.message ?? t('challenge.validation.unknown_error') },
            }
        } finally {
            isLoading.value = false
        }
    }

    return {
        result: submissionResult,
        isLoading,
        showModal: isResultModalOpen,
        submissions: submissionHistory,
        globalSubmissions,
        fetchSubmissions,
        fetchGlobalSubmissions,
        runCode,
        submitSolution,
    }
}

import { ref, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { marked } from 'marked'
import DOMPurify from 'dompurify'
import { api } from '@/services/api'
import { LANGUAGES } from '@/constants/languages'

export const PARAM_TYPES = ['int', 'float', 'string', 'bool', 'array']
export const CATEGORIES  = ['arrays', 'strings', 'math', 'recursion', 'sorting', 'other']

// ── Limits ──
export const LIMITS = {
    title:      { min: 5,  max: 80 },
    shortDesc:  { max: 160 },
    desc:       { min: 30, max: 5000 },
    fnName:     { min: 2,  max: 50 },
    solution:   { max: 10_000 },
    testCases:  { max: 20 },
    imageWidth: { max: 1200 },
}

// ── Reserved words (JS / Python / Java) ──
const RESERVED_WORDS = new Set([
    'var','let','const','function','class','return','import','export',
    'if','else','for','while','do','switch','case','break','continue',
    'new','delete','typeof','instanceof','void','this','super','extends',
    'try','catch','finally','throw','async','await','yield',
    'def','lambda','pass','with','as','raise','from','global','nonlocal',
    'assert','del','in','is','not','and','or','True','False','None','elif',
    'public','private','protected','static','final','abstract','interface',
    'implements','package','throws','boolean','double','long','short','byte',
    'char','null','true','false',
])

const IDENTIFIER_REGEX = /^[a-zA-Z_][a-zA-Z0-9_-]*$/

// ── Field validators (return error string or '') ──

export function validateTitle(v) {
    const s = v?.trim() ?? ''
    if (s.length < LIMITS.title.min) return `Mínimo ${LIMITS.title.min} caracteres.`
    return ''
}

export function validateDescription(v) {
    const s = v?.trim() ?? ''
    if (s.length < LIMITS.desc.min)  return `Mínimo ${LIMITS.desc.min} caracteres.`
    if (s.length > LIMITS.desc.max) return `Máximo ${LIMITS.desc.max} caracteres.`
    return ''
}

export function validateFunctionName(v) {
    const s = v?.trim() ?? ''
    if (/\s/.test(s))                  return 'Sin espacios.'
    if (s.length < LIMITS.fnName.min)  return `Mínimo ${LIMITS.fnName.min} caracteres.`
    if (!IDENTIFIER_REGEX.test(s))     return 'Solo letras, números, _ o -. No empieces con número.'
    if (RESERVED_WORDS.has(s))         return `"${s}" es palabra reservada.`
    return ''
}

export function validateParameterName(name, allParams, index) {
    const s = name?.trim() ?? ''
    if (/\s/.test(s))                  return 'Sin espacios.'
    if (!IDENTIFIER_REGEX.test(s))     return 'Solo letras, números, _ o -.'
    if (RESERVED_WORDS.has(s))         return `"${s}" es palabra reservada.`
    if (allParams.some((p, i) => i !== index && (p.name?.trim() ?? '') === s)) return 'Nombre duplicado.'
    return ''
}

export function validateInputForType(value, type) {
    const v = value?.trim() ?? ''
    if (v === '') return 'Requerido.'
    if (type === 'int')   return /^-?\d+$/.test(v)                          ? '' : 'Entero (ej. 42).'
    if (type === 'float') return /^-?\d+(\.\d+)?([eE][+-]?\d+)?$/.test(v)   ? '' : 'Decimal (ej. 3.14).'
    if (type === 'bool')  return ['true', 'false'].includes(v.toLowerCase()) ? '' : 'true o false.'
    if (type === 'array') {
        try   { return Array.isArray(JSON.parse(v)) ? '' : 'Array JSON (ej. [1,2,3]).' }
        catch { return 'Array JSON válido (ej. [1,2,3]).' }
    }
    return '' // string: any non-empty text OK
}

// ── Internal helpers ──

function makeTestCase(paramCount) {
    return { input: Array(paramCount).fill(''), expected_output: '', is_hidden: false }
}

function isTestCaseComplete(tc, params, returnType) {
    if (validateInputForType(tc.expected_output, returnType)) return false
    return tc.input.every((v, i) => !validateInputForType(v, params[i]?.type ?? 'string'))
}

// ── Composable ──

export function useCreateExercise(exerciseId = null) {
    const router     = useRouter()
    const route      = useRoute()
    const loading    = ref(false)
    const error      = ref(null)
    const isEditMode = !!exerciseId

    const form = ref({
        title:             '',
        short_description: '',
        description:       '',
        difficulty:        'easy',
        category:          '',
        function_name:     '',
        parameters:        [{ name: '', type: 'int' }],
        return_type:       'int',
        solution_language: 'python',
        solution_code:     '',
        test_cases:        [makeTestCase(1)],
        is_published:      false,
    })

    // ── Form mutations ──

    function addParameter() {
        form.value.parameters.push({ name: '', type: 'int' })
        form.value.test_cases.forEach(tc => tc.input.push(''))
    }

    function removeParameter(i) {
        if (form.value.parameters.length === 1) return
        form.value.parameters.splice(i, 1)
        form.value.test_cases.forEach(tc => tc.input.splice(i, 1))
    }

    function addTestCase() {
        if (form.value.test_cases.length >= LIMITS.testCases.max) return
        form.value.test_cases.push(makeTestCase(form.value.parameters.length))
    }

    function removeTestCase(i) {
        if (form.value.test_cases.length === 1) return
        form.value.test_cases.splice(i, 1)
    }

    // ── Step validation ──

    function hasValidTestCaseDistribution() {
        const tcs = form.value.test_cases
        return tcs.length >= 2 && tcs.some(tc => tc.is_hidden) && tcs.some(tc => !tc.is_hidden)
    }

    function isStep1Valid() {
        return !validateTitle(form.value.title) && !validateDescription(form.value.description)
    }

    function isStep2Valid() {
        const { function_name, parameters } = form.value
        if (validateFunctionName(function_name)) return false
        return parameters.every((p, i) => !validateParameterName(p.name, parameters, i))
    }

    function isStep3Valid() {
        if (!hasValidTestCaseDistribution()) return false
        return form.value.test_cases.every(tc => isTestCaseComplete(tc, form.value.parameters, form.value.return_type))
    }

    function isStepValid(step) {
        if (step === 1) return isStep1Valid()
        if (step === 2) return isStep2Valid()
        if (step === 3) return isStep3Valid()
        return true
    }

    function stepCompleted(step) {
        if (step === 4) return false
        return isStepValid(step)
    }

    const canPublish = computed(() =>
        hasValidTestCaseDistribution()
        && form.value.test_cases.every(tc => isTestCaseComplete(tc, form.value.parameters, form.value.return_type))
    )

    // ── Init: guarantee min distribution ──

    ;(function ensureMinimumTestCases() {
        while (form.value.test_cases.length < 2) {
            form.value.test_cases.push(makeTestCase(form.value.parameters.length))
        }
        const tcs = form.value.test_cases
        if (!tcs.some(tc =>  tc.is_hidden)) tcs[tcs.length - 1].is_hidden = true
        if (!tcs.some(tc => !tc.is_hidden)) tcs[0].is_hidden = false
    })()

    // ── Load (edit mode) ──

    async function loadExercise(id) {
        loading.value = true
        error.value   = null
        try {
            const res = await api.get(`/exercises/${id}`)
            const ex  = res.data

            form.value.title             = ex.title             ?? ''
            form.value.short_description = ex.short_description ?? ''
            form.value.description       = ex.description       ?? ''
            form.value.difficulty        = ex.difficulty        ?? 'easy'
            form.value.category          = ex.category          ?? ''
            form.value.function_name     = ex.function_name     ?? ''
            form.value.parameters        = ex.parameters        ?? [{ name: '', type: 'int' }]
            form.value.return_type       = ex.return_type       ?? 'int'
            form.value.is_published      = ex.is_published      ?? false
            form.value.test_cases        = ex.test_cases.map(tc => ({
                is_hidden:       tc.is_hidden,
                expected_output: tc.expected_output ?? '',
                input:           (tc.input ?? []).map(v =>
                    typeof v === 'string' ? v : JSON.stringify(v)
                ),
            }))
        } catch {
            error.value = 'Error al cargar el ejercicio.'
        } finally {
            loading.value = false
        }
    }

    // ── Submit ──

    async function submit() {
        loading.value = true
        error.value   = null
        try {
            const payload = {
                ...form.value,
                test_cases: form.value.test_cases.map(tc => ({
                    ...tc,
                    input: tc.input.map(v => {
                        try { return JSON.parse(v) } catch { return v }
                    }),
                })),
            }
            if (isEditMode) {
                await api.put(`/exercises/${exerciseId}`, payload)
                router.push({ name: 'my-exercises' })
            } else {
                await api.post('/exercises', payload)
                const from = route.query.from
                router.push({ name: from === 'tournament' ? 'tournament-create' : 'challenges' })
            }
        } catch (err) {
            error.value = err?.errors
                ? Object.values(err.errors).flat().join('. ')
                : (err?.message ?? 'Error al guardar el ejercicio')
        } finally {
            loading.value = false
        }
    }

    // ── Computed ──

    const descriptionHtml = computed(() =>
        DOMPurify.sanitize(marked.parse(form.value.description || ''))
    )

    const publicTestCases = computed(() =>
        form.value.test_cases.filter(tc => !tc.is_hidden)
    )

    const functionSignature = computed(() => {
        const params = form.value.parameters.map(p => `${p.name || '?'}: ${p.type}`).join(', ')
        return `${form.value.function_name || 'fn'}(${params}) → ${form.value.return_type}`
    })

    return {
        loading, error, form, isEditMode,
        LANGUAGES,
        addParameter, removeParameter,
        addTestCase, removeTestCase,
        submit, loadExercise,
        isStepValid, stepCompleted, canPublish,
        descriptionHtml, publicTestCases, functionSignature,
    }
}

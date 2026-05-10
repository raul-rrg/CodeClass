const MAX_LENGTH      = 10_000
const INVALID_CHARS   = /[\x00-\x08\x0b\x0c\x0e-\x1f\x7f]/

export const validators = [
    validateNotEmpty,
    validateMaxLength,
    validateNoInvalidChars,
    validateFunctionPresent,
    validateSignatureIntact,
    validateHasReturn,
]

function validateNotEmpty(code) {
    if (!code.trim())
        return { key: 'challenge.validation.empty_code' }
}

function validateMaxLength(code) {
    if (code.length > MAX_LENGTH)
        return { key: 'challenge.validation.max_length', params: { max: MAX_LENGTH.toLocaleString() } }
}

function validateNoInvalidChars(code) {
    if (INVALID_CHARS.test(code))
        return { key: 'challenge.validation.invalid_chars' }
}

function validateFunctionPresent(code, _template, _language, challenge) {
    const name = challenge?.function_name
    if (!name) return
    if (!code.includes(name))
        return { key: 'challenge.validation.missing_function', params: { name } }
}

function validateSignatureIntact(code, template, _language, challenge) {
    const name = challenge?.function_name
    if (!name || !template) return
    const sigLine = template.split('\n').find(l => l.includes(name))
    if (!sigLine) return
    const intact = code.split('\n').some(l => l.trim() === sigLine.trim())
    if (!intact)
        return { key: 'challenge.validation.signature_intact', params: { sig: sigLine.trim() } }
}

function validateHasReturn(code) {
    const hasReturn = code.split('\n').some(l => l.trimStart().startsWith('return'))
    if (!hasReturn)
        return { key: 'challenge.validation.no_return' }
}

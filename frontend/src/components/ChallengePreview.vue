<template>
    <section class="px-8 pt-10 pb-20">
        <div class="max-w-7xl mx-auto">

            <div class="text-center mb-8">
                <h2 class="font-medium text-4xl md:text-5xl text-title tracking-tight">{{ $t('challenge_preview.title') }}</h2>
            </div>

            <div class="bg-[#1a1d29] border border-white/20 rounded-xl overflow-hidden shadow-2xl h-[650px] flex">

                <!-- Panel izquierdo: descripción del reto -->
                <div class="w-1/2 border-r border-white/10 bg-[#0d1117] overflow-hidden">
                    <Transition name="challenge-fade" mode="out-in">
                        <div :key="challengeIndex" class="p-5 h-full flex flex-col">

                            <h1 class="font-bold text-2xl text-title mb-2">{{ current.title }}</h1>

                            <div class="flex flex-wrap gap-2 mb-3">
                                <span :class="['px-3 py-1 rounded text-sm font-semibold', difficultyClass]">
                                    {{ current.difficulty }}
                                </span>
                                <span class="px-3 py-1 rounded text-sm font-medium border border-white/20 text-subtitle capitalize">
                                    {{ current.category }}
                                </span>
                                <span class="flex items-center gap-1 px-3 py-1 text-sm font-medium text-yellow-400">
                                    🏆 {{ current.points }} {{ $t('challenge_preview.points_label') }}
                                </span>
                            </div>

                            <div class="mb-3">
                                <h3 class="flex items-center gap-2 font-bold text-title mb-2">
                                    <span class="text-blue-400">📄</span> {{ $t('challenge_preview.description_label') }}
                                </h3>
                                <p class="text-subtitle leading-relaxed text-sm">{{ current.description }}</p>
                            </div>

                            <div>
                                <h3 class="flex items-center gap-2 font-bold text-title mb-2">
                                    <span class="text-cyan-400 font-mono">&gt;_</span> {{ $t('challenge_preview.test_cases_header') }}
                                </h3>
                                <div
                                    v-for="(tc, i) in current.testCases" :key="i"
                                    class="mb-2 bg-white/3 border border-white/10 rounded-lg p-3"
                                >
                                    <p class="text-sm text-title font-medium mb-1">{{ $t('challenge_preview.case_label', { n: i + 1 }) }} {{ tc.label }}</p>
                                    <p class="text-xs text-subtitle mb-0.5">
                                        {{ $t('challenge_preview.input_label') }} <span class="font-mono text-cyan-400">{{ tc.input }}</span>
                                    </p>
                                    <p class="text-xs text-subtitle">
                                        {{ $t('challenge_preview.expected_output_label') }} <span class="font-mono text-green-400">{{ tc.expected }}</span>
                                    </p>
                                </div>
                            </div>

                        </div>
                    </Transition>
                </div>

                <!-- Panel derecho: editor de código -->
                <div class="w-1/2 flex flex-col">

                    <div class="bg-[#1a1d29] border-b border-white/10 px-4 py-3 flex items-center gap-3">
                        <span class="text-cyan-400 font-mono font-bold text-sm">&lt;/&gt;</span>
                        <span class="font-bold text-title text-sm">{{ $t('challenge_preview.editor_title') }}</span>
                        <div class="flex gap-2 ml-auto">
                            <button
                                v-for="lang in LANGUAGES"
                                :key="lang.id"
                                @click="switchLanguage(lang.id)"
                                :class="[
                                    'px-3 py-1 text-xs font-medium border rounded-sm transition-all',
                                    selectedLang === lang.id
                                        ? 'border-blue-500 text-white bg-blue-500/10'
                                        : 'border-white/20 text-subtitle hover:border-white/40 hover:text-title'
                                ]"
                            >
                                {{ lang.label }}
                            </button>
                        </div>
                    </div>

                    <div class="flex-1 overflow-auto bg-[#282c34] font-mono text-[13px] leading-[1.65]">
                        <div class="flex min-h-full py-5">
                            <!-- Números de línea -->
                            <div class="select-none text-right px-3 text-white/25 border-r border-white/6 min-w-11">
                                <div v-for="n in lineCount" :key="n">{{ n }}</div>
                            </div>
                            <!-- Código -->
                            <div class="flex-1 px-5">
                                <pre class="m-0 whitespace-pre"><code :class="`language-${selectedLang}`" v-html="visibleHtml" /><span class="cursor">█</span></pre>
                            </div>
                        </div>
                    </div>

                </div>
            </div>
        </div>
    </section>
</template>

<script setup>
import { ref, computed, onMounted, onBeforeUnmount } from 'vue'
import Prism from 'prismjs'
import 'prismjs/components/prism-javascript'
import 'prismjs/components/prism-python'
import 'prismjs/components/prism-java'
import 'prism-themes/themes/prism-one-dark.css'

const LANGUAGES = [
    { id: 'javascript', label: 'JavaScript' },
    { id: 'python',     label: 'Python'     },
    { id: 'java',       label: 'Java'       },
]

const CHALLENGES = [
    {
        title:       'Suma de Dos Números',
        difficulty:  'Fácil',
        category:    'Fundamentos',
        points:      10,
        description: 'Escribe una función que reciba dos números y retorne su suma.',
        testCases: [
            { label: 'Suma básica de positivos', input: '2, 3',  expected: '5' },
            { label: 'Suma con número negativo', input: '-1, 1', expected: '0' },
            { label: 'Suma de ceros',            input: '0, 0',  expected: '0' },
        ],
        snippets: {
            javascript: `function suma(a, b) {\n  return a + b;\n}`,
            python:     `def suma(a, b):\n    return a + b`,
            java:       `public int suma(int a, int b) {\n    return a + b;\n}`,
        },
    },
    {
        title:       'Número de Fibonacci',
        difficulty:  'Fácil',
        category:    'Algoritmos',
        points:      20,
        description: 'Implementa una función que calcule el n-ésimo número de la secuencia de Fibonacci. La secuencia empieza con 0 y 1.',
        testCases: [
            { label: 'Caso base cero',  input: '0',  expected: '0'  },
            { label: 'Caso base uno',   input: '1',  expected: '1'  },
            { label: 'Décimo elemento', input: '10', expected: '55' },
        ],
        snippets: {
            javascript: `function fibonacci(n) {\n  if (n <= 1) return n;\n\n  let a = 0, b = 1;\n  for (let i = 2; i <= n; i++) {\n    let temp = a + b;\n    a = b;\n    b = temp;\n  }\n  return b;\n}`,
            python:     `def fibonacci(n):\n    if n <= 1:\n        return n\n    a, b = 0, 1\n    for _ in range(2, n + 1):\n        a, b = b, a + b\n    return b`,
            java:       `public int fibonacci(int n) {\n    if (n <= 1) return n;\n    int a = 0, b = 1;\n    for (int i = 2; i <= n; i++) {\n        int temp = a + b;\n        a = b;\n        b = temp;\n    }\n    return b;\n}`,
        },
    },
    {
        title:       'Es Palíndromo',
        difficulty:  'Fácil',
        category:    'Strings',
        points:      15,
        description: 'Determina si una cadena de texto es un palíndromo, es decir, si se lee igual de izquierda a derecha que al revés.',
        testCases: [
            { label: 'Es palíndromo',    input: '"racecar"', expected: 'true'  },
            { label: 'No es palíndromo', input: '"hello"',   expected: 'false' },
            { label: 'Una sola letra',   input: '"a"',       expected: 'true'  },
        ],
        snippets: {
            javascript: `function esPalindromo(str) {\n  const rev = str.split('').reverse().join('');\n  return str === rev;\n}`,
            python:     `def es_palindromo(s):\n    return s == s[::-1]`,
            java:       `public boolean esPalindromo(String s) {\n    String rev = new StringBuilder(s)\n        .reverse().toString();\n    return s.equals(rev);\n}`,
        },
    },
    {
        title:       'FizzBuzz',
        difficulty:  'Fácil',
        category:    'Fundamentos',
        points:      15,
        description: 'Retorna "Fizz" si n es divisible por 3, "Buzz" si lo es por 5, "FizzBuzz" si lo es por ambos, o el número como string en caso contrario.',
        testCases: [
            { label: 'Múltiplo de 3',  input: '3',  expected: '"Fizz"'     },
            { label: 'Múltiplo de 5',  input: '5',  expected: '"Buzz"'     },
            { label: 'Múltiplo de 15', input: '15', expected: '"FizzBuzz"' },
            { label: 'Número normal',  input: '7',  expected: '"7"'        },
        ],
        snippets: {
            javascript: `function fizzBuzz(n) {\n  if (n % 15 === 0) return 'FizzBuzz';\n  if (n % 3 === 0)  return 'Fizz';\n  if (n % 5 === 0)  return 'Buzz';\n  return String(n);\n}`,
            python:     `def fizz_buzz(n):\n    if n % 15 == 0:\n        return 'FizzBuzz'\n    if n % 3 == 0:\n        return 'Fizz'\n    if n % 5 == 0:\n        return 'Buzz'\n    return str(n)`,
            java:       `public String fizzBuzz(int n) {\n    if (n % 15 == 0) return "FizzBuzz";\n    if (n % 3 == 0)  return "Fizz";\n    if (n % 5 == 0)  return "Buzz";\n    return String.valueOf(n);\n}`,
        },
    },
    {
        title:       'Número Primo',
        difficulty:  'Medio',
        category:    'Algoritmos',
        points:      25,
        description: 'Determina si un número entero positivo es primo. Un número primo es mayor que 1 y solo divisible por 1 y por sí mismo.',
        testCases: [
            { label: 'Es primo',         input: '7', expected: 'true'  },
            { label: 'No es primo',      input: '4', expected: 'false' },
            { label: 'El 2 es primo',    input: '2', expected: 'true'  },
            { label: 'El 1 no es primo', input: '1', expected: 'false' },
        ],
        snippets: {
            javascript: `function esPrimo(n) {\n  if (n < 2) return false;\n\n  for (let i = 2; i <= Math.sqrt(n); i++) {\n    if (n % i === 0) return false;\n  }\n  return true;\n}`,
            python:     `import math\n\ndef es_primo(n):\n    if n < 2:\n        return False\n    for i in range(2, int(math.sqrt(n)) + 1):\n        if n % i == 0:\n            return False\n    return True`,
            java:       `public boolean esPrimo(int n) {\n    if (n < 2) return false;\n    for (int i = 2; i <= Math.sqrt(n); i++) {\n        if (n % i == 0) return false;\n    }\n    return true;\n}`,
        },
    },
]

const challengeIndex = ref(0)
const selectedLang   = ref('javascript')
const visibleHtml    = ref('')
const lineCount      = ref(1)

const current = computed(() => CHALLENGES[challengeIndex.value])

const difficultyClass = computed(() => ({
    'Fácil':   'bg-green-500/20  text-green-400  border border-green-500/40',
    'Medio':   'bg-yellow-500/20 text-yellow-400 border border-yellow-500/40',
    'Difícil': 'bg-red-500/20    text-red-400    border border-red-500/40',
}[current.value.difficulty] ?? ''))

function buildTokens(html) {
    const template = document.createElement('template')
    template.innerHTML = html
    const tokens = []
    const walk = (node) => {
        if (node.nodeType === Node.TEXT_NODE) {
            tokens.push({ type: 'text', value: node.textContent })
        } else if (node.nodeType === Node.ELEMENT_NODE) {
            const open  = node.outerHTML.slice(0, node.outerHTML.indexOf(node.innerHTML))
            const close = `</${node.tagName.toLowerCase()}>`
            tokens.push({ type: 'open',  value: open  })
            node.childNodes.forEach(walk)
            tokens.push({ type: 'close', value: close })
        }
    }
    template.content.childNodes.forEach(walk)
    return tokens
}

function buildHtml(tokens, count) {
    let html      = ''
    let remaining = count
    let lines     = 1

    for (const token of tokens) {
        if (token.type === 'open' || token.type === 'close') {
            html += token.value
        } else {
            if (remaining <= 0) break
            const slice = token.value.slice(0, remaining)
            lines      += (slice.match(/\n/g) ?? []).length
            html       += slice
            remaining  -= slice.length
        }
    }
    lineCount.value = lines
    return html
}

let charIndex  = 0
let tokens     = []
let totalChars = 0
let raf        = null
let lastTime   = 0
const CHAR_INTERVAL = 25

function loadSnippet(cIdx, langId) {
    const code        = CHALLENGES[cIdx].snippets[langId]
    const grammar     = Prism.languages[langId] ?? Prism.languages.javascript
    const highlighted = Prism.highlight(code, grammar, langId)
    tokens          = buildTokens(highlighted)
    totalChars      = code.length
    charIndex       = 0
    lineCount.value = 1
}

function animate(time) {
    if (time - lastTime >= CHAR_INTERVAL) {
        lastTime = time
        charIndex++
        visibleHtml.value = buildHtml(tokens, charIndex)

        if (charIndex >= totalChars) {
            setTimeout(() => {
                challengeIndex.value = (challengeIndex.value + 1) % CHALLENGES.length
                visibleHtml.value    = ''
                loadSnippet(challengeIndex.value, selectedLang.value)
                raf = requestAnimationFrame(animate)
            }, 2000)
            return
        }
    }
    raf = requestAnimationFrame(animate)
}

function switchLanguage(langId) {
    selectedLang.value = langId
    cancelAnimationFrame(raf)
    visibleHtml.value  = ''
    loadSnippet(challengeIndex.value, langId)
    lastTime = 0
    raf = requestAnimationFrame(animate)
}

onMounted(() => {
    loadSnippet(0, 'javascript')
    raf = requestAnimationFrame(animate)
})

onBeforeUnmount(() => cancelAnimationFrame(raf))
</script>

<style scoped>
.cursor {
    display: inline;
    color: #56b6c2;
    animation: blink 0.7s infinite;
    font-weight: bold;
}

@keyframes blink {
    0%, 100% { opacity: 1; }
    50%       { opacity: 0; }
}

.challenge-fade-enter-active,
.challenge-fade-leave-active {
    transition: opacity 0.35s ease;
}
.challenge-fade-enter-from,
.challenge-fade-leave-to {
    opacity: 0;
}

:deep(code[class*="language-"]),
:deep(pre[class*="language-"]) {
    background:  transparent !important;
    padding:     0 !important;
    margin:      0 !important;
    font-size:   inherit !important;
    line-height: inherit !important;
    text-shadow: none !important;
}
</style>

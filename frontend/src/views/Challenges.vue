<template>
    <div class="min-h-[calc(100vh-80px)] bg-base px-8 py-10">
        <div class="max-w-7xl mx-auto">

            <!-- Header -->
            <div class="mb-8">
                <h1 class="text-3xl font-bold text-white mb-2">{{ $t('challenges.title') }}</h1>
                <p class="text-white/50 text-sm">{{ $t('challenges.subtitle') }}</p>
            </div>

            <!-- Filtros -->
            <div class="flex flex-col gap-3 mb-8">

                <!-- Fila 1: search + solved + total -->
                <div class="flex items-center gap-3 flex-wrap">
                    <!-- Search -->
                    <div class="relative flex-1 min-w-48 max-w-72">
                        <svg class="absolute left-3 top-1/2 -translate-y-1/2 w-3.5 h-3.5 text-white/25 pointer-events-none"
                            viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                            <circle cx="11" cy="11" r="8"/><line x1="21" y1="21" x2="16.65" y2="16.65"/>
                        </svg>
                        <input v-model="search" type="text" :placeholder="$t('challenges.search_placeholder')"
                            class="w-full bg-white/4 border border-white/10 rounded-lg pl-8 pr-3 py-1.5 text-sm text-white placeholder-white/25 outline-none focus:border-white/20 transition-colors" />
                    </div>

                    <!-- Solved segmented control (solo auth) -->
                    <div v-if="auth.isAuthenticated" class="flex rounded-lg border border-white/10 overflow-hidden shrink-0">
                        <button v-for="opt in SOLVED_OPTIONS" :key="opt.value"
                            @click="solved = opt.value"
                            :class="[
                                'px-3 py-1.5 text-xs font-medium transition-all border-r border-white/10 last:border-r-0',
                                solved === opt.value
                                    ? 'bg-blue-500/15 text-blue-300'
                                    : 'text-white/35 hover:text-white/60 hover:bg-white/4'
                            ]">
                            {{ opt.label }}
                        </button>
                    </div>

                    <!-- Total -->
                    <span v-if="!loading" class="text-white/25 text-xs ml-auto shrink-0">
                        {{ $t('challenges.total', { n: total }, total) }}
                    </span>
                </div>

                <!-- Fila 2: dificultad + categoría dropdown -->
                <div class="flex items-center gap-3 flex-wrap">
                    <div class="flex gap-1.5 flex-wrap">
                        <button v-for="d in DIFFICULTIES" :key="d.value"
                            @click="difficulty = d.value"
                            :class="[
                                'px-3 py-1 text-xs font-semibold rounded-lg border transition-all capitalize',
                                difficulty === d.value ? d.activeClass : 'border-white/10 text-white/35 hover:border-white/20 hover:text-white/55'
                            ]">
                            {{ d.label }}
                        </button>
                    </div>

                    <div class="w-px h-4 bg-white/10 shrink-0" />

                    <!-- Categoría: dropdown -->
                    <div class="relative" ref="catDropdownRef">
                        <button @click="catOpen = !catOpen"
                            :class="[
                                'flex items-center gap-2 px-3 py-1 text-xs font-semibold rounded-lg border transition-all',
                                category
                                    ? 'border-blue-500/50 text-blue-300 bg-blue-500/10'
                                    : 'border-white/10 text-white/40 hover:border-white/20 hover:text-white/60'
                            ]">
                            <span class="capitalize">{{ category || $t('challenges.category_label') }}</span>
                            <svg :class="['w-3 h-3 transition-transform', catOpen ? 'rotate-180' : '']"
                                viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
                                <polyline points="6 9 12 15 18 9"/>
                            </svg>
                        </button>

                        <Transition
                            enter-active-class="transition-all duration-150 ease-out"
                            enter-from-class="opacity-0 translate-y-1 scale-95"
                            enter-to-class="opacity-100 translate-y-0 scale-100"
                            leave-active-class="transition-all duration-100 ease-in"
                            leave-from-class="opacity-100 translate-y-0 scale-100"
                            leave-to-class="opacity-0 translate-y-1 scale-95">
                            <div v-if="catOpen"
                                class="absolute top-full left-0 mt-1.5 z-20 min-w-35 rounded-lg border border-white/12 bg-[#1a1a2e] shadow-xl overflow-hidden">
                                <button v-for="cat in CATEGORIES_WITH_ALL" :key="cat.value"
                                    @click="category = cat.value; catOpen = false"
                                    :class="[
                                        'w-full text-left px-3 py-2 text-xs capitalize transition-colors',
                                        category === cat.value
                                            ? 'text-blue-300 bg-blue-500/12'
                                            : 'text-white/45 hover:text-white/70 hover:bg-white/5'
                                    ]">
                                    {{ cat.label }}
                                </button>
                            </div>
                        </Transition>
                    </div>
                </div>
            </div>

            <!-- Loading -->
            <div v-if="loading" class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-5">
                <div v-for="i in 6" :key="i"
                    class="h-52 rounded-xl bg-white/3 border border-white/5 animate-pulse" />
            </div>

            <!-- Empty -->
            <div v-else-if="!challenges.length" class="flex flex-col items-center justify-center gap-3 py-24">
                <svg class="w-12 h-12 text-white/8" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
                    <circle cx="11" cy="11" r="8"/><line x1="21" y1="21" x2="16.65" y2="16.65"/>
                </svg>
                <p class="text-white/25 text-sm">{{ $t('challenges.no_results') }}</p>
                <button @click="resetFilters" class="text-xs text-blue-400/60 hover:text-blue-400 transition-colors">
                    {{ $t('challenges.clear_filters') }}
                </button>
            </div>

            <!-- Grid -->
            <div v-else class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-5">
                <ChallengeCard
                    v-for="challenge in challenges" :key="challenge.id"
                    :challenge="challenge"
                    @require-auth="showAuthModal = true"
                />
            </div>

            <!-- Paginación -->
            <div v-if="lastPage > 1" class="flex items-center justify-center gap-1.5 mt-10">
                <button @click="goToPage(page - 1)" :disabled="page === 1"
                    class="w-8 h-8 flex items-center justify-center rounded-lg border border-white/10 text-white/40 hover:text-white/70 hover:border-white/20 disabled:opacity-25 disabled:cursor-not-allowed transition-all">
                    <svg class="w-3.5 h-3.5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
                        <polyline points="15 18 9 12 15 6"/>
                    </svg>
                </button>

                <button v-for="p in pageRange" :key="p"
                    @click="goToPage(p)"
                    :class="[
                        'w-8 h-8 flex items-center justify-center rounded-lg text-xs font-semibold border transition-all',
                        p === page
                            ? 'bg-blue-500/15 border-blue-500/40 text-blue-300'
                            : 'border-white/10 text-white/35 hover:border-white/20 hover:text-white/60'
                    ]">
                    {{ p }}
                </button>

                <button @click="goToPage(page + 1)" :disabled="page === lastPage"
                    class="w-8 h-8 flex items-center justify-center rounded-lg border border-white/10 text-white/40 hover:text-white/70 hover:border-white/20 disabled:opacity-25 disabled:cursor-not-allowed transition-all">
                    <svg class="w-3.5 h-3.5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
                        <polyline points="9 18 15 12 9 6"/>
                    </svg>
                </button>
            </div>

            <AuthModal v-if="showAuthModal" @close="showAuthModal = false" />

        </div>
    </div>
</template>

<script setup>
import { ref, computed, watch, onMounted, onUnmounted } from 'vue'
import { useI18n } from 'vue-i18n'
import { api } from '@/services/api'
import { useAuthStore } from '@/stores/auth'
import { CATEGORIES } from '@/composables/useCreateExercise'
import ChallengeCard from '@/components/ChallengeCard.vue'
import AuthModal from '@/components/AuthModal.vue'

const { t, locale } = useI18n()
const auth          = useAuthStore()
const showAuthModal = ref(false)

// ── Dropdown categoría ──
const catOpen        = ref(false)
const catDropdownRef = ref(null)

function onClickOutside(e) {
    if (catDropdownRef.value && !catDropdownRef.value.contains(e.target)) catOpen.value = false
}
onMounted(() => document.addEventListener('mousedown', onClickOutside))
onUnmounted(() => document.removeEventListener('mousedown', onClickOutside))

// ── Filtros ──
const search     = ref('')
const difficulty = ref('all')
const category   = ref('')
const solved     = ref('')
const page       = ref(1)
const loading      = ref(false)
const initialLoad  = ref(true)

// ── Datos ──
const challenges = ref([])
const total      = ref(0)
const lastPage   = ref(1)

// ── Constantes UI ──
const DIFFICULTIES = computed(() => [
    { value: 'all',    label: t('challenges.difficulty_all'),    activeClass: 'border-blue-500/50   text-blue-300   bg-blue-500/10'   },
    { value: 'easy',   label: t('challenges.difficulty_easy'),   activeClass: 'border-green-500/50  text-green-300  bg-green-500/10'  },
    { value: 'medium', label: t('challenges.difficulty_medium'), activeClass: 'border-yellow-500/50 text-yellow-300 bg-yellow-500/10' },
    { value: 'hard',   label: t('challenges.difficulty_hard'),   activeClass: 'border-red-500/50    text-red-300    bg-red-500/10'    },
    { value: 'insane', label: t('challenges.difficulty_insane'), activeClass: 'border-purple-500/50 text-purple-300 bg-purple-500/10' },
])

const CATEGORIES_WITH_ALL = computed(() => [
    { value: '', label: t('challenges.all_categories') },
    ...CATEGORIES.map(c => ({ value: c, label: c })),
])

const SOLVED_OPTIONS = computed(() => [
    { value: '',  label: t('challenges.solved_all') },
    { value: '1', label: t('challenges.solved_yes') },
    { value: '0', label: t('challenges.solved_no')  },
])

// ── Paginación ──
const pageRange = computed(() => {
    const delta  = 2
    const range  = []
    const start  = Math.max(1, page.value - delta)
    const end    = Math.min(lastPage.value, page.value + delta)
    for (let i = start; i <= end; i++) range.push(i)
    return range
})

// ── Fetch ──
async function fetchChallenges() {
    if (initialLoad.value) loading.value = true
    try {
        const params = new URLSearchParams()
        params.set('page', page.value)
        if (difficulty.value && difficulty.value !== 'all') params.set('difficulty', difficulty.value)
        if (category.value) params.set('category', category.value)
        if (search.value.trim()) params.set('search', search.value.trim())
        if (auth.isAuthenticated && solved.value !== '') params.set('solved', solved.value)

        const res = await api.get(`/exercises?${params.toString()}`)
        challenges.value = res.data          ?? []
        total.value      = res.meta?.total     ?? 0
        lastPage.value   = res.meta?.last_page ?? 1
    } catch {
        challenges.value = []
    } finally {
        loading.value     = false
        initialLoad.value = false
    }
}

function resetFilters() {
    search.value     = ''
    difficulty.value = 'all'
    category.value   = ''
    solved.value     = ''
    page.value       = 1
}

function goToPage(p) {
    if (p < 1 || p > lastPage.value) return
    page.value = p
    window.scrollTo({ top: 0, behavior: 'smooth' })
}

// Reset página al cambiar filtros
watch([difficulty, category, solved], () => {
    page.value = 1
    fetchChallenges()
})

// Debounce en búsqueda
let searchTimer = null
watch(search, () => {
    clearTimeout(searchTimer)
    searchTimer = setTimeout(() => {
        page.value = 1
        fetchChallenges()
    }, 350)
})

watch(page, fetchChallenges)
watch(locale, () => { page.value = 1; fetchChallenges() })

onMounted(fetchChallenges)
</script>

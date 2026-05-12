<template>
    <div class="min-h-[calc(100vh-80px)] bg-base px-8 py-10">
        <div class="max-w-2xl mx-auto">

            <RouterLink to="/tournaments" class="inline-flex items-center gap-1.5 text-white/35 hover:text-white/60 text-sm mb-6 transition-colors">
                <svg class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <polyline points="15 18 9 12 15 6"/>
                </svg>
                {{ $t('tournaments.title') }}
            </RouterLink>

            <h1 class="text-2xl font-bold text-white mb-8">{{ $t('create_tournament.title') }}</h1>

            <form @submit.prevent="submit" class="flex flex-col gap-5">

                <!-- Nombre -->
                <div>
                    <label class="block text-xs font-semibold text-white/40 uppercase tracking-widest mb-2">{{ $t('create_tournament.name_label') }}</label>
                    <input v-model="form.name" type="text" required :placeholder="$t('create_tournament.name_placeholder')"
                        class="w-full bg-white/5 border border-white/10 rounded-xl px-4 py-3 text-sm text-white placeholder-white/20 outline-none focus:border-blue-500/40 transition-colors" />
                </div>

                <!-- Descripción -->
                <div>
                    <label class="block text-xs font-semibold text-white/40 uppercase tracking-widest mb-2">{{ $t('create_tournament.description_label') }} <span class="text-white/20 normal-case font-normal">({{ $t('create_tournament.optional') }})</span></label>
                    <textarea v-model="form.description" rows="3" :placeholder="$t('create_tournament.description_placeholder')"
                        class="w-full bg-white/5 border border-white/10 rounded-xl px-4 py-3 text-sm text-white placeholder-white/20 outline-none focus:border-blue-500/40 transition-colors resize-none" />
                </div>

                <!-- Fecha inicio + Duración -->
                <div class="grid grid-cols-2 gap-4">
                    <div>
                        <label class="block text-xs font-semibold text-white/40 uppercase tracking-widest mb-2">{{ $t('create_tournament.start_date_label') }}</label>
                        <input v-model="form.starts_at" type="datetime-local" required
                            class="w-full bg-white/5 border border-white/10 rounded-xl px-4 py-3 text-sm text-white outline-none focus:border-blue-500/40 transition-colors [color-scheme:dark]" />
                    </div>
                    <div>
                        <label class="block text-xs font-semibold text-white/40 uppercase tracking-widest mb-2">{{ $t('create_tournament.duration_label') }}</label>
                        <input v-model.number="form.duration_minutes" type="number" min="5" max="480" required placeholder="90"
                            class="w-full bg-white/5 border border-white/10 rounded-xl px-4 py-3 text-sm text-white placeholder-white/20 outline-none focus:border-blue-500/40 transition-colors" />
                    </div>
                </div>

                <!-- Imagen de portada -->
                <div>
                    <label class="block text-xs font-semibold text-white/40 uppercase tracking-widest mb-2">
                        {{ $t('create_tournament.cover_label') }} <span class="text-white/20 normal-case font-normal">({{ $t('create_tournament.cover_hint') }})</span>
                    </label>
                    <input v-model="form.cover_url" type="url" placeholder="https://ejemplo.com/imagen.jpg"
                        class="w-full bg-white/5 border border-white/10 rounded-xl px-4 py-3 text-sm text-white placeholder-white/20 outline-none focus:border-blue-500/40 transition-colors" />
                    <div v-if="form.cover_url" class="mt-2 h-32 rounded-xl overflow-hidden border border-white/10">
                        <img :src="form.cover_url" class="w-full h-full object-cover" @error="form.cover_url = ''" />
                    </div>
                </div>

                <!-- Visibilidad -->
                <div>
                    <label class="block text-xs font-semibold text-white/40 uppercase tracking-widest mb-3">{{ $t('create_tournament.visibility_label') }}</label>
                    <div class="grid grid-cols-2 gap-3">
                        <!-- Público -->
                        <button type="button" @click="form.is_public = true"
                            :class="['flex items-start gap-3 px-4 py-3 rounded-xl border text-left transition-all',
                                form.is_public
                                    ? 'border-blue-500/40 bg-blue-500/8'
                                    : 'border-white/8 bg-white/2 hover:border-white/15']">
                            <svg class="w-4 h-4 mt-0.5 shrink-0" :class="form.is_public ? 'text-blue-400' : 'text-white/30'" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                <circle cx="12" cy="12" r="10"/><line x1="2" y1="12" x2="22" y2="12"/>
                                <path d="M12 2a15.3 15.3 0 0 1 4 10 15.3 15.3 0 0 1-4 10 15.3 15.3 0 0 1-4-10 15.3 15.3 0 0 1 4-10z"/>
                            </svg>
                            <div>
                                <p :class="['text-sm font-semibold', form.is_public ? 'text-blue-300' : 'text-white/50']">{{ $t('create_tournament.public_label') }}</p>
                                <p class="text-xs text-white/30 mt-0.5">{{ $t('create_tournament.public_hint') }}</p>
                            </div>
                        </button>

                        <!-- Privado -->
                        <button type="button" @click="form.is_public = false"
                            :class="['flex items-start gap-3 px-4 py-3 rounded-xl border text-left transition-all',
                                !form.is_public
                                    ? 'border-purple-500/40 bg-purple-500/8'
                                    : 'border-white/8 bg-white/2 hover:border-white/15']">
                            <svg class="w-4 h-4 mt-0.5 shrink-0" :class="!form.is_public ? 'text-purple-400' : 'text-white/30'" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                <path d="M2 9a3 3 0 0 1 0 6v2a2 2 0 0 0 2 2h16a2 2 0 0 0 2-2v-2a3 3 0 0 1 0-6V7a2 2 0 0 0-2-2H4a2 2 0 0 0-2 2Z"/>
                                <line x1="9" y1="12" x2="15" y2="12"/>
                            </svg>
                            <div>
                                <p :class="['text-sm font-semibold', !form.is_public ? 'text-purple-300' : 'text-white/50']">{{ $t('create_tournament.private_label') }}</p>
                                <p class="text-xs text-white/30 mt-0.5">{{ $t('create_tournament.private_hint') }}</p>
                            </div>
                        </button>
                    </div>
                </div>

                <!-- Selección de ejercicios -->
                <div>
                    <div class="flex items-center justify-between mb-2">
                        <label class="text-xs font-semibold text-white/40 uppercase tracking-widest">
                            {{ $t('create_tournament.exercises_label') }}
                            <span class="text-blue-400/60 ml-1">{{ form.exercise_ids.length }} {{ $t('create_tournament.selected') }}</span>
                        </label>
                        <RouterLink :to="{ name: 'exercise-create', query: { from: 'tournament' } }"
                            class="inline-flex items-center gap-1.5 text-xs text-blue-400/70 hover:text-blue-300 transition-colors">
                            <svg class="w-3.5 h-3.5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
                                <line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/>
                            </svg>
                            {{ $t('create_tournament.create_hidden_exercise') }}
                        </RouterLink>
                    </div>
                    <p class="flex items-center gap-1.5 text-xs text-white/25 mb-3">
                        <svg class="w-3 h-3 shrink-0" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                            <circle cx="12" cy="12" r="10"/><line x1="12" y1="8" x2="12" y2="12"/><line x1="12" y1="16" x2="12.01" y2="16"/>
                        </svg>
                        {{ $t('create_tournament.exercises_hint') }}
                    </p>

                    <!-- Buscador -->
                    <div v-if="exercises.length" class="relative mb-2">
                        <svg class="absolute left-3 top-1/2 -translate-y-1/2 w-3.5 h-3.5 text-white/25 pointer-events-none"
                            viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                            <circle cx="11" cy="11" r="8"/><line x1="21" y1="21" x2="16.65" y2="16.65"/>
                        </svg>
                        <input v-model="exerciseSearch" type="text" :placeholder="$t('create_tournament.search_placeholder')"
                            class="w-full bg-white/4 border border-white/10 rounded-lg pl-8 pr-3 py-2 text-sm text-white placeholder-white/25 outline-none focus:border-white/20 transition-colors" />
                    </div>

                    <div v-if="loadingExercises" class="space-y-2">
                        <div v-for="i in 4" :key="i" class="h-12 rounded-lg bg-white/3 animate-pulse" />
                    </div>

                    <div v-else-if="!exercises.length" class="text-white/30 text-sm py-4 text-center">
                        {{ $t('create_tournament.no_exercises') }}
                    </div>

                    <div v-else-if="!filteredExercises.length" class="text-white/25 text-sm py-4 text-center">
                        {{ $t('create_tournament.no_results', { search: exerciseSearch }) }}
                    </div>

                    <div v-else class="flex flex-col gap-2 max-h-72 overflow-y-auto pr-1">
                        <label v-for="ex in filteredExercises" :key="ex.id"
                            :class="[
                                'flex items-center gap-3 px-4 py-3 rounded-xl border cursor-pointer transition-all',
                                form.exercise_ids.includes(ex.id)
                                    ? 'border-blue-500/30 bg-blue-500/8'
                                    : 'border-white/8 bg-white/2 hover:border-white/15'
                            ]">
                            <input type="checkbox" :value="ex.id" v-model="form.exercise_ids" class="hidden" />
                            <div :class="['w-4 h-4 rounded border-2 shrink-0 flex items-center justify-center transition-all',
                                form.exercise_ids.includes(ex.id) ? 'border-blue-400 bg-blue-500' : 'border-white/20']">
                                <svg v-if="form.exercise_ids.includes(ex.id)" class="w-2.5 h-2.5 text-white" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3.5" stroke-linecap="round" stroke-linejoin="round">
                                    <polyline points="20 6 9 17 4 12"/>
                                </svg>
                            </div>
                            <span class="text-sm text-white/80 flex-1">{{ ex.title }}</span>
                            <span :class="['px-2 py-0.5 rounded text-[10px] font-semibold capitalize', diffClass[ex.difficulty]]">
                                {{ ex.difficulty }}
                            </span>
                        </label>
                    </div>
                </div>

                <!-- Error -->
                <p v-if="error" class="text-red-400/80 text-sm">{{ error }}</p>

                <!-- Submit -->
                <button type="submit" :disabled="submitting || !form.exercise_ids.length"
                    class="w-full py-3 rounded-xl bg-blue-500/20 border border-blue-500/30 text-blue-300 font-semibold hover:bg-blue-500/30 transition-all disabled:opacity-40 disabled:cursor-not-allowed">
                    {{ submitting ? $t('create_tournament.submitting') : $t('create_tournament.submit') }}
                </button>

            </form>
        </div>
    </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, watch } from 'vue'
import { useI18n } from 'vue-i18n'
import { useRouter } from 'vue-router'
import { api } from '@/services/api'
import { DIFFICULTY_BADGE_CLASS } from '@/constants/difficulties'

const DRAFT_KEY = 'tournament_draft'

const router = useRouter()
const diffClass = DIFFICULTY_BADGE_CLASS

const saved = JSON.parse(sessionStorage.getItem(DRAFT_KEY) || 'null')

const form = reactive({
    name:             saved?.name             ?? '',
    description:      saved?.description      ?? '',
    starts_at:        saved?.starts_at        ?? '',
    duration_minutes: saved?.duration_minutes ?? 90,
    is_public:        saved?.is_public        ?? true,
    cover_url:        saved?.cover_url        ?? '',
    exercise_ids:     saved?.exercise_ids     ?? [],
})

watch(form, val => sessionStorage.setItem(DRAFT_KEY, JSON.stringify({ ...val })), { deep: true })

const exercises        = ref([])
const exerciseSearch   = ref('')
const loadingExercises = ref(false)
const submitting       = ref(false)
const error            = ref('')

const filteredExercises = computed(() => {
    const q = exerciseSearch.value.trim().toLowerCase()
    if (!q) return exercises.value
    return exercises.value.filter(e => e.title.toLowerCase().includes(q))
})

async function fetchExercises() {
    loadingExercises.value = true
    try {
        const res = await api.get('/users/me/exercises')
        exercises.value = (res.data ?? []).filter(e => !e.is_published)
    } catch {
        exercises.value = []
    } finally {
        loadingExercises.value = false
    }
}

async function submit() {
    error.value    = ''
    submitting.value = true
    try {
        const payload = {
            ...form,
            starts_at: new Date(form.starts_at).toISOString(),
        }
        const res = await api.post('/tournaments', payload)
        sessionStorage.removeItem(DRAFT_KEY)
        router.push({ name: 'tournament', params: { id: res.data.id } })
    } catch (e) {
        error.value = e?.message ?? Object.values(e?.errors ?? {})?.[0]?.[0] ?? t('create_tournament.create_error')
    } finally {
        submitting.value = false
    }
}

const { locale, t } = useI18n()

onMounted(fetchExercises)
watch(locale, fetchExercises)
</script>

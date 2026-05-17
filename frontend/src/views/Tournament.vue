<template>
    <div class="min-h-[calc(100vh-80px)] bg-base">

        <!-- Hero -->
        <div :style="heroBg" class="px-8 pt-10 pb-12 border-b border-white/6">
            <div class="max-w-5xl mx-auto">

                <RouterLink to="/tournaments" class="inline-flex items-center gap-1.5 text-white/35 hover:text-white/60 text-sm mb-8 transition-colors">
                    <svg class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <polyline points="15 18 9 12 15 6"/>
                    </svg>
                    {{ $t('tournament.back') }}
                </RouterLink>

                <div v-if="loading" class="space-y-4">
                    <div class="h-10 w-72 rounded-xl bg-white/5 animate-pulse" />
                    <div class="h-5 w-48 rounded-lg bg-white/4 animate-pulse" />
                </div>

                <template v-else-if="tournament">
                    <div class="flex items-center justify-between gap-8">

                        <!-- Izquierda: info + acción -->
                        <div class="flex-1 min-w-0">
                            <!-- Status + creator -->
                            <div class="flex items-center gap-3 mb-3">
                                <span :class="['flex items-center gap-1.5 px-2.5 py-1 rounded-lg text-xs font-bold', statusClass]">
                                    <span v-if="tournament.status === 'active'" class="w-1.5 h-1.5 rounded-full bg-current animate-pulse" />
                                    {{ statusLabel }}
                                </span>
                                <span class="text-white/20 text-xs">·</span>
                                <div class="flex items-center gap-1.5">
                                    <UserAvatar :src="tournament.creator?.avatar_url ?? null" :name="tournament.creator?.name ?? ''" size="sm" />
                                    <span class="text-white/55 text-xs font-medium">{{ tournament.creator?.name }}</span>
                                </div>
                            </div>

                            <h1 class="text-4xl font-bold text-white mb-2 leading-tight">{{ tOrRaw(tournament.name) }}</h1>
                            <p v-if="tournament.description" class="text-white/45 text-sm mb-8 max-w-xl">{{ tOrRaw(tournament.description) }}</p>

                            <!-- Stats row -->
                            <div class="flex flex-wrap gap-6 mb-8 text-sm">
                                <div class="flex items-center gap-2 text-white/40">
                                    <svg class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">
                                        <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/>
                                        <path d="M23 21v-2a4 4 0 0 0-3-3.87"/><path d="M16 3.13a4 4 0 0 1 0 7.75"/>
                                    </svg>
                                    <span class="text-white/70 font-semibold">{{ tournament.participants_count }}</span> {{ $t('tournament.participants') }}
                                </div>
                                <div class="flex items-center gap-2 text-white/40">
                                    <svg class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">
                                        <circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/>
                                    </svg>
                                    <span class="text-white/70 font-semibold">{{ tournament.duration_minutes }} min</span>
                                </div>
                                <div class="flex items-center gap-2 text-white/40">
                                    <svg class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">
                                        <rect x="3" y="4" width="18" height="18" rx="2" ry="2"/><line x1="16" y1="2" x2="16" y2="6"/><line x1="8" y1="2" x2="8" y2="6"/><line x1="3" y1="10" x2="21" y2="10"/>
                                    </svg>
                                    {{ formatDate(tournament.starts_at) }}
                                </div>
                            </div>

                            <!-- Inscripción alumno -->
                            <div v-if="auth.isAuthenticated && !auth.isTeacher">
                                <div v-if="!tournament.is_participant && tournament.status !== 'finished'" class="flex flex-col gap-2">
                                    <!-- Input código si torneo privado -->
                                    <input v-if="!tournament.is_public"
                                        v-model="joinCode" type="text" :placeholder="$t('tournament.access_code_placeholder')"
                                        class="w-48 bg-white/5 border border-white/10 rounded-xl px-4 py-2 text-sm text-white placeholder-white/25 outline-none focus:border-blue-500/40 transition-colors uppercase tracking-widest" />
                                    <button @click="join" :disabled="joining || (!tournament.is_public && !joinCode)"
                                        class="px-8 py-3 rounded-xl font-semibold text-sm transition-all disabled:opacity-40 disabled:cursor-not-allowed w-fit"
                                        :class="tournament.status === 'active'
                                            ? 'bg-green-500/20 border border-green-500/30 text-green-300 hover:bg-green-500/30'
                                            : 'bg-blue-500/20 border border-blue-500/30 text-blue-300 hover:bg-blue-500/30'">
                                        {{ joining ? $t('tournament.joining') : $t('tournament.join_btn') }}
                                    </button>
                                </div>
                                <p v-if="joinError" class="text-red-400/80 text-xs mt-2">{{ joinError }}</p>

                                <div v-if="tournament.is_participant"
                                    class="inline-flex items-center gap-2 px-4 py-2 rounded-xl bg-green-500/10 border border-green-500/20 text-green-400 text-sm font-medium">
                                    <svg class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
                                        <polyline points="20 6 9 17 4 12"/>
                                    </svg>
                                    {{ $t('tournament.enrolled') }}
                                </div>
                            </div>

                            <!-- Código de acceso (solo teacher creador) -->
                            <div v-if="auth.isTeacher && tournament.join_code" class="mt-4 flex items-center gap-3">
                                <div class="flex items-center gap-2 px-4 py-2 rounded-xl bg-white/5 border border-white/10">
                                    <span class="text-xs text-white/40 uppercase tracking-widest">{{ $t('tournament.code_label') }}</span>
                                    <span class="font-mono font-bold text-white tracking-widest">{{ tournament.join_code }}</span>
                                </div>
                                <button @click="copyCode"
                                    class="px-3 py-2 rounded-xl text-xs font-medium transition-all border"
                                    :class="codeCopied ? 'bg-green-500/15 border-green-500/30 text-green-400' : 'bg-white/5 border-white/10 text-white/50 hover:text-white/80'">
                                    {{ codeCopied ? $t('tournament.copied') : $t('tournament.copy') }}
                                </button>
                            </div>
                        </div>

                        <!-- Derecha: countdown (activo) o fecha (próximo) -->
                        <div v-if="tournament.status === 'active'" class="shrink-0 self-center flex flex-col items-center gap-1 pl-8 mr-14 border-l border-white/10">
                            <p class="text-sm font-semibold uppercase tracking-widest text-green-400/60">{{ $t('tournament.time_remaining') }}</p>
                            <p v-if="countdownSecs > 0" :class="[
                                'text-4xl font-bold font-mono tabular-nums tracking-wider transition-colors duration-300',
                                countdownSecs <= 10 ? 'text-red-400 animate-pulse' : 'text-white'
                            ]">{{ heroCountdown }}</p>
                            <p v-else-if="showTimeUp" class="text-4xl font-bold text-red-400 tracking-widest animate-time-up">¡TIEMPO!</p>
                        </div>
                        <div v-else-if="tournament.status === 'upcoming'" class="shrink-0 self-center flex flex-col items-center gap-1 pl-8 mr-25 border-l border-white/10">
                            <p class="text-sm font-semibold uppercase tracking-widest text-blue-400/60">{{ $t('tournament.starts_at') }}</p>
                            <p class="text-2xl font-bold text-white">{{ formatStartDate(tournament.starts_at) }}</p>
                            <p class="text-sm font-mono text-blue-300/70">{{ formatStartTime(tournament.starts_at) }}</p>
                        </div>
                        <div v-else class="shrink-0 self-center flex flex-col items-center gap-2 pl-8 mr-14 border-l border-white/10">
                            <svg class="w-8 h-8 text-white/20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
                                <path d="M6 9H4.5a2.5 2.5 0 0 1 0-5H6"/><path d="M18 9h1.5a2.5 2.5 0 0 0 0-5H18"/>
                                <path d="M18 2H6v7a6 6 0 0 0 12 0V2Z"/>
                                <path d="M12 17v4"/><path d="M8 21h8"/>
                            </svg>
                            <p class="text-sm font-semibold uppercase tracking-widest text-white/30">{{ $t('tournament.status_finished') }}</p>
                            <p class="text-xs text-white/20">{{ formatDate(tournament.ends_at) }}</p>
                        </div>

                    </div>
                </template>
            </div>
        </div>

        <!-- Contenido -->
        <div class="max-w-5xl mx-auto px-8 py-8" v-if="tournament && !loading">
            <div class="grid grid-cols-1 lg:grid-cols-[1fr_340px] gap-6 items-start">

                <!-- Ejercicios -->
                <div v-if="tournament.exercises?.length">
                    <div class="flex items-center justify-between mb-4">
                        <h2 class="text-sm font-semibold text-white/50 uppercase tracking-widest">{{ $t('tournament.exercises_header') }}</h2>
                        <span class="text-xs text-white/30">
                            {{ solvedCount }}/{{ tournament.exercises.length }} {{ $t('tournament.completed') }}
                        </span>
                    </div>

                    <!-- Barra progreso -->
                    <div class="h-1.5 bg-white/5 rounded-full overflow-hidden mb-5">
                        <div class="h-full bg-green-500/60 rounded-full transition-all duration-500"
                            :style="{ width: progressPct + '%' }" />
                    </div>

                    <div class="flex flex-col gap-2">
                        <RouterLink v-for="ex in tournament.exercises" :key="ex.id"
                            :to="{ name: 'challenge', params: { id: ex.id } }"
                            :class="[
                                'flex items-center gap-4 px-5 py-4 rounded-xl border transition-all group',
                                ex.is_solved
                                    ? 'bg-green-500/5 border-green-500/15 hover:border-green-500/30'
                                    : 'bg-white/2 border-white/6 hover:border-white/15 hover:bg-white/4'
                            ]">

                            <div :class="[
                                'w-6 h-6 rounded-full shrink-0 flex items-center justify-center transition-all',
                                ex.is_solved ? 'bg-green-500/25 ring-1 ring-green-500/30' : 'bg-white/6 ring-1 ring-white/10'
                            ]">
                                <svg v-if="ex.is_solved" class="w-3.5 h-3.5 text-green-400" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3" stroke-linecap="round" stroke-linejoin="round">
                                    <polyline points="20 6 9 17 4 12"/>
                                </svg>
                                <div v-else class="w-2 h-2 rounded-full bg-white/20" />
                            </div>

                            <div class="flex-1 min-w-0">
                                <p :class="['text-sm font-semibold', ex.is_solved ? 'text-white/50 line-through decoration-white/20' : 'text-white/85']">
                                    {{ ex.title }}
                                </p>
                                <p v-if="ex.short_description" class="text-xs text-white/30 truncate mt-0.5">{{ ex.short_description }}</p>
                            </div>

                            <div class="flex items-center gap-3 shrink-0">
                                <span :class="['px-2 py-0.5 rounded text-[10px] font-semibold capitalize', DIFFICULTY_BADGE_CLASS[ex.difficulty]]">
                                    {{ ex.difficulty }}
                                </span>
                                <span class="text-xs text-white/25 font-mono">{{ DIFFICULTY_POINTS[ex.difficulty] }}pt</span>
                                <svg class="w-3.5 h-3.5 text-white/15 group-hover:text-white/35 transition-colors" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                    <polyline points="9 18 15 12 9 6"/>
                                </svg>
                            </div>
                        </RouterLink>
                    </div>
                </div>

                <div v-else-if="tournament.is_participant && tournament.status === 'upcoming'"
                    class="flex flex-col items-center justify-center py-16 text-white/25 text-sm gap-2">
                    <svg class="w-10 h-10 text-white/8" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
                        <circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/>
                    </svg>
                    {{ $t('tournament.available_when_started') }}
                </div>
                <div v-else class="flex flex-col items-center justify-center py-16 text-white/25 text-sm gap-2">
                    <svg class="w-10 h-10 text-white/8" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
                        <rect x="3" y="11" width="18" height="11" rx="2" ry="2"/><path d="M7 11V7a5 5 0 0 1 10 0v4"/>
                    </svg>
                    {{ $t('tournament.join_to_see') }}
                </div>

                <!-- Leaderboard sticky -->
                <div v-if="leaderboard !== null" class="lg:sticky lg:top-6">
                    <div class="bg-surface/40 border border-white/8 rounded-2xl p-5">
                        <div class="flex items-center justify-between mb-4">
                            <h2 class="text-sm font-semibold text-white/50 uppercase tracking-widest">Leaderboard</h2>
                            <span v-if="tournament.status === 'active'" class="flex items-center gap-1.5 text-[10px] text-white/25">
                                <span class="w-1.5 h-1.5 rounded-full bg-green-400 animate-pulse" />
                                {{ $t('tournament.live') }}
                            </span>
                        </div>
                        <TournamentLeaderboard :rows="leaderboard" />
                    </div>
                </div>
            </div>
        </div>

    </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, watch } from 'vue'
import { useI18n } from 'vue-i18n'
import { useRoute } from 'vue-router'
import { api } from '@/services/api'
import { useAuthStore } from '@/stores/auth'
import TournamentLeaderboard from '@/components/TournamentLeaderboard.vue'
import UserAvatar from '@/components/UserAvatar.vue'
import { DIFFICULTY_BADGE_CLASS, DIFFICULTY_POINTS } from '@/constants/difficulties'

const { locale, t, te } = useI18n()

// Helper ayuxiliar para mostrar texto sin traducir si no existe la clave (ej. para torneos de demo con nombre/desc ya localizados en backend)
const tOrRaw = (keyOrText) => keyOrText && te(keyOrText) ? t(keyOrText) : (keyOrText ?? '')
const route        = useRoute()
const auth         = useAuthStore()
const tournamentId = route.params.id

const tournament  = ref(null)
const leaderboard = ref(null) 
const loading     = ref(true)
const joining     = ref(false)
const joinError   = ref('')
const joinCode    = ref('')
const codeCopied  = ref(false)

const countdownSecs   = ref(0)
const showTimeUp      = ref(false)
let countdownTimer    = null
let pollTimer         = null
let transitionPending = false
let audioStarted      = false

const countdownAudio = new Audio('/sounds/countdown-tick.mp3')

// Actualiza el countdown cada segundo. Cuando llega a 0 refetchea el torneo para
// recoger el cambio de estado (upcoming→active o active→finished) sin recargar la página.
// showTimeUp solo se activa si el torneo estaba activo al llegar a 0 (no durante upcoming→active).
function updateCountdown() {
    if (!tournament.value) return
    const isActive   = tournament.value.status === 'active'
    const targetDate = isActive ? tournament.value.ends_at : tournament.value.starts_at
    const secs       = Math.max(0, Math.floor((new Date(targetDate) - Date.now()) / 1000))

    // Arranca el audio de cuenta atrás al entrar en los últimos 10 segundos del torneo activo.
    // Si la página se abre con menos de 10s restantes, ajustamos el offset para sincronizar.
    if (isActive && secs <= 10 && secs > 0 && !audioStarted) {
        audioStarted = true
        countdownAudio.currentTime = Math.max(0, 10 - secs)
        countdownAudio.play().catch(() => {})
    }

    if (secs === 0 && countdownSecs.value > 0 && !transitionPending) {
        transitionPending = true
        if (isActive) showTimeUp.value = true
        setTimeout(async () => {
            await fetchTournament()
            showTimeUp.value = false
            audioStarted = false
            updateCountdown()
            await fetchLeaderboard()
            if (tournament.value?.status === 'active' && !pollTimer) {
                pollTimer = setInterval(fetchLeaderboard, 30000)
            }
            transitionPending = false
        }, 1500)
    }

    countdownSecs.value = secs
}

// Formatea el countdown en formato HH:MM:SS
const heroCountdown = computed(() => {
    const totalSecs = countdownSecs.value
    const hours     = Math.floor(totalSecs / 3600)
    const mins      = Math.floor((totalSecs % 3600) / 60)
    const secs      = totalSecs % 60
    return [hours, mins, secs].map(n => String(n).padStart(2, '0')).join(':')
})

// computed (not const) so t() re-evaluates when locale changes
const STATUS_MAP = computed(() => ({
    active:   { label: t('tournament.status_active'),   class: 'bg-green-500/15 text-green-400', bg: 'radial-gradient(ellipse at top left, rgb(34 197 94 / 0.06) 0%, transparent 60%)' },
    upcoming: { label: t('tournament.status_upcoming'), class: 'bg-blue-500/15 text-blue-400',   bg: 'radial-gradient(ellipse at top left, rgb(59 130 246 / 0.07) 0%, transparent 60%)' },
    finished: { label: t('tournament.status_finished'), class: 'bg-white/8 text-white/35',       bg: 'none' },
}))

// Computed para mostrar texto y estilos según el estado del torneo
const statusLabel = computed(() => STATUS_MAP.value[tournament.value?.status]?.label ?? '')
const statusClass = computed(() => STATUS_MAP.value[tournament.value?.status]?.class ?? '')
const heroBg      = computed(() => ({ background: STATUS_MAP.value[tournament.value?.status]?.bg ?? 'none' }))

const solvedCount = computed(() => tournament.value?.exercises?.filter(e => e.is_solved).length ?? 0)

// Calcula el % de progreso para la barra según ejercicios resueltos vs total
const progressPct = computed(() => {
    const total = tournament.value?.exercises?.length ?? 0
    return total ? Math.round((solvedCount.value / total) * 100) : 0
})


// Formatear fechas con formato local según necesidades (fecha completa para inicio torneo, fecha+hora para ejercicios)

function formatDate(isoDate) {
    return new Date(isoDate).toLocaleString(locale.value, { day: 'numeric', month: 'short', hour: '2-digit', minute: '2-digit' })
}

function formatStartDate(isoDate) {
    return new Date(isoDate).toLocaleDateString(locale.value, { weekday: 'short', day: 'numeric', month: 'long' })
}

function formatStartTime(isoDate) {
    return new Date(isoDate).toLocaleTimeString(locale.value, { hour: '2-digit', minute: '2-digit' })
}

// Fetch del torneo (info + ejercicios) y leaderboard (si participante o teacher)

async function fetchTournament() {
    try {
        const res = await api.get(`/tournaments/${tournamentId}`)
        tournament.value = res.data
    } catch {
        tournament.value = null
    } finally {
        loading.value = false
    }
}

async function fetchLeaderboard() {
    if (!auth.isAuthenticated) return
    if (!tournament.value?.is_participant && !auth.isTeacher) return
    try {
        const res = await api.get(`/tournaments/${tournamentId}/leaderboard`)
        leaderboard.value = res.data ?? []
    } catch {
        leaderboard.value = []
    }
}

// Acción de unirse a torneo (con join code si es privado), luego refetch torneo y leaderboard para actualizar estado
async function join() {
    joining.value   = true
    joinError.value = ''
    try {
        const payload = tournament.value.is_public ? {} : { join_code: joinCode.value }
        await api.post(`/tournaments/${tournamentId}/join`, payload)
        await fetchTournament()
        await fetchLeaderboard()
    } catch (err) {
        joinError.value = err?.message ?? t('tournament.join_error')
    } finally {
        joining.value = false
    }
}


async function copyCode() {
    await navigator.clipboard.writeText(tournament.value.join_code)
    codeCopied.value = true
    setTimeout(() => codeCopied.value = false, 2000)
}

// Refetch torneo al cambiar idioma para actualizar textos que vienen del backend (nombre/desc traducidos, estado traducido)
watch(locale, fetchTournament)

// Fetch inicial al montar componente, y setup de timers para countdown y polling leaderboard (solo si torneo activo)
onMounted(async () => {
    await fetchTournament()
    await fetchLeaderboard()
    updateCountdown()
    countdownTimer = setInterval(updateCountdown, 1000)
    // only poll leaderboard while tournament is live
    if (tournament.value?.status === 'active') {
        pollTimer = setInterval(fetchLeaderboard, 30000)
    }
})

onUnmounted(() => {
    clearInterval(pollTimer)
    clearInterval(countdownTimer)
    countdownAudio.pause()
    countdownAudio.src = ''
})
</script>

<style scoped>
@keyframes time-up {
    0%   { transform: scale(1.4); opacity: 0; }
    60%  { transform: scale(0.95); opacity: 1; }
    100% { transform: scale(1); opacity: 1; }
}
.animate-time-up {
    animation: time-up 0.5s cubic-bezier(0.22, 1, 0.36, 1) forwards;
}
</style>

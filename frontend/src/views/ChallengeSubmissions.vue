<template>
    <div class="min-h-[calc(100vh-64px)] bg-base">

        <!-- ───── Page header ───── -->
        <div class="border-b border-white/5 px-4 md:px-8 py-4 flex items-center gap-3 md:gap-4">
            <RouterLink :to="{ name: 'challenge', params: { id } }"
                class="flex items-center gap-1.5 text-white/30 hover:text-white/60 text-xs font-medium transition-colors shrink-0">
                <svg class="w-3.5 h-3.5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
                    <polyline points="15 18 9 12 15 6"/>
                </svg>
                <span class="hidden sm:inline">{{ $t('challenge_subs.back_to_challenge') }}</span>
            </RouterLink>

            <div class="w-px h-4 bg-white/10 shrink-0" />

            <span :class="['px-2.5 py-0.5 text-xs font-semibold rounded-full capitalize shrink-0', difficultyClass]">
                {{ challenge.difficulty }}
            </span>
            <h1 class="text-white/80 font-semibold text-sm truncate">{{ challenge.title }}</h1>
        </div>

        <!-- ───── Tabs ───── -->
        <div class="border-b border-white/5 px-4 md:px-8 flex gap-0">
            <button v-for="tab in tabs" :key="tab.id"
                @click="switchTab(tab.id)"
                :class="[
                    'px-4 py-3 text-xs font-semibold transition-colors relative',
                    activeTab === tab.id ? 'text-white' : 'text-white/35 hover:text-white/60'
                ]">
                {{ tab.label }}
                <div :class="[
                    'absolute bottom-0 left-0 right-0 h-px transition-all',
                    activeTab === tab.id ? 'bg-blue-500' : 'bg-transparent'
                ]" />
            </button>
        </div>

        <!-- ───── Content ───── -->
        <div class="max-w-4xl mx-auto px-4 md:px-8 py-6 md:py-8">

            <div v-if="isLoading" class="flex items-center gap-2.5 text-white/30 text-sm">
                <svg class="w-4 h-4 animate-spin" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">
                    <path d="M21 12a9 9 0 1 1-6.219-8.56"/>
                </svg>
                {{ $t('challenge_subs.loading') }}
            </div>

            <!-- ── Mis envíos ── -->
            <template v-else-if="activeTab === 'mine'">
                <div v-if="!submissions.length" class="flex flex-col items-center justify-center gap-3 py-20">
                    <svg class="w-10 h-10 text-white/8" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
                        <line x1="22" y1="2" x2="11" y2="13"/><polygon points="22 2 15 22 11 13 2 9 22 2"/>
                    </svg>
                    <span v-if="auth.isTeacher" class="text-white/25 text-sm">{{ $t('challenge_subs.no_submissions_teacher') }}</span>
                    <template v-else>
                        <span class="text-white/25 text-sm">{{ $t('challenge_subs.no_submissions_student') }}</span>
                        <RouterLink :to="{ name: 'challenge', params: { id } }"
                            class="mt-1 px-4 py-1.5 rounded-lg bg-white/5 hover:bg-white/8 text-white/45 hover:text-white/65 text-xs font-medium transition-all border border-white/8">
                            {{ $t('challenge_subs.go_to_solve') }}
                        </RouterLink>
                    </template>
                </div>

                <div v-else class="flex flex-col gap-3">
                    <SubmissionRow
                        v-for="sub in submissions" :key="sub.id"
                        :sub="sub"
                        :expanded="expanded"
                        :is-own="true"
                        :is-teacher="auth.isTeacher"
                        @toggle="toggleExpand"
                    />
                </div>
            </template>

            <!-- ── Envíos globales ── -->
            <template v-else-if="activeTab === 'global'">
                <div v-if="isLoadingGlobal" class="flex items-center gap-2.5 text-white/30 text-sm">
                    <svg class="w-4 h-4 animate-spin" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">
                        <path d="M21 12a9 9 0 1 1-6.219-8.56"/>
                    </svg>
                    {{ $t('challenge_subs.loading_short') }}
                </div>
                <div v-else-if="!globalSubmissions.length" class="flex flex-col items-center justify-center gap-3 py-20">
                    <svg class="w-10 h-10 text-white/8" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
                        <line x1="22" y1="2" x2="11" y2="13"/><polygon points="22 2 15 22 11 13 2 9 22 2"/>
                    </svg>
                    <span class="text-white/25 text-sm">{{ $t('challenge_subs.no_global_submissions') }}</span>
                </div>
                <div v-else class="flex flex-col gap-3">
                    <SubmissionRow
                        v-for="sub in globalSubmissions" :key="sub.id"
                        :sub="sub"
                        :expanded="expanded"
                        :is-own="sub.user_id === auth.user?.id"
                        :is-teacher="auth.isTeacher"
                        @toggle="toggleExpand"
                    />
                </div>
            </template>

            <!-- ── Alumnos (solo teacher) ── -->
            <template v-else-if="activeTab === 'students'">
                <div v-if="isLoadingGlobal" class="flex items-center gap-2.5 text-white/30 text-sm">
                    <svg class="w-4 h-4 animate-spin" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">
                        <path d="M21 12a9 9 0 1 1-6.219-8.56"/>
                    </svg>
                    {{ $t('challenge_subs.loading_short') }}
                </div>
                <div v-else-if="!studentSummary.length" class="flex flex-col items-center justify-center gap-3 py-20">
                    <svg class="w-10 h-10 text-white/8" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
                        <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/>
                        <path d="M23 21v-2a4 4 0 0 0-3-3.87"/><path d="M16 3.13a4 4 0 0 1 0 7.75"/>
                    </svg>
                    <span class="text-white/25 text-sm">{{ $t('challenge_subs.no_students') }}</span>
                </div>
                <div v-else class="flex flex-col gap-2">
                    <!-- Cabecera (desktop only) -->
                    <div class="hidden md:grid grid-cols-[1fr_auto_auto_auto] gap-4 px-4 py-2 text-[10px] font-bold uppercase tracking-wider text-white/25">
                        <span>{{ $t('challenge_subs.col_student') }}</span>
                        <span class="text-right w-20">{{ $t('challenge_subs.col_attempts') }}</span>
                        <span class="text-right w-20">{{ $t('challenge_subs.col_status') }}</span>
                        <span class="text-right w-24">{{ $t('challenge_subs.col_last') }}</span>
                    </div>

                    <div v-for="student in studentSummary" :key="student.id"
                        class="rounded-xl border border-white/8 bg-white/2 px-4 py-3">

                        <!-- Desktop: grid row -->
                        <div class="hidden md:grid grid-cols-[1fr_auto_auto_auto] gap-4 items-center">
                            <div class="flex items-center gap-2.5 min-w-0">
                                <div class="w-7 h-7 rounded-full bg-blue-500/20 flex items-center justify-center text-xs font-bold text-blue-300 shrink-0">
                                    {{ initials(student.name) }}
                                </div>
                                <span class="text-sm text-white/75 font-medium truncate">{{ student.name }}</span>
                            </div>
                            <span class="text-sm text-white/50 font-mono text-right w-20">{{ student.attempts }}</span>
                            <div class="flex justify-end w-20">
                                <span v-if="student.accepted"
                                    class="flex items-center gap-1 px-2 py-0.5 rounded-md bg-green-500/15 text-green-400 text-[10px] font-bold ring-1 ring-green-500/25">
                                    <svg class="w-2.5 h-2.5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
                                        <polyline points="20 6 9 17 4 12"/>
                                    </svg>
                                    {{ $t('challenge_subs.status_solved') }}
                                </span>
                                <span v-else class="px-2 py-0.5 rounded-md bg-white/5 text-white/30 text-[10px] font-bold ring-1 ring-white/10">
                                    {{ $t('challenge_subs.status_in_progress') }}
                                </span>
                            </div>
                            <span class="text-[11px] text-white/25 text-right w-24">{{ relativeDate(student.lastAt) }}</span>
                        </div>

                        <!-- Mobile: card layout -->
                        <div class="flex md:hidden items-center gap-3">
                            <div class="w-8 h-8 rounded-full bg-blue-500/20 flex items-center justify-center text-xs font-bold text-blue-300 shrink-0">
                                {{ initials(student.name) }}
                            </div>
                            <div class="flex-1 min-w-0">
                                <p class="text-sm text-white/75 font-medium truncate">{{ student.name }}</p>
                                <p class="text-[10px] text-white/30 mt-0.5">
                                    {{ student.attempts }} {{ $t('challenge_subs.col_attempts').toLowerCase() }}
                                    · {{ relativeDate(student.lastAt) }}
                                </p>
                            </div>
                            <span v-if="student.accepted"
                                class="flex items-center gap-1 px-2 py-0.5 rounded-md bg-green-500/15 text-green-400 text-[10px] font-bold ring-1 ring-green-500/25 shrink-0">
                                <svg class="w-2.5 h-2.5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
                                    <polyline points="20 6 9 17 4 12"/>
                                </svg>
                                {{ $t('challenge_subs.status_solved') }}
                            </span>
                            <span v-else class="px-2 py-0.5 rounded-md bg-white/5 text-white/30 text-[10px] font-bold ring-1 ring-white/10 shrink-0">
                                {{ $t('challenge_subs.status_in_progress') }}
                            </span>
                        </div>

                    </div>
                </div>
            </template>

        </div>
    </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { useChallenge } from '@/composables/useChallenge'
import { useSubmission } from '@/composables/useSubmission'
import { useAuthStore } from '@/stores/auth'
import SubmissionRow from '@/components/SubmissionRow.vue'

const { t } = useI18n()
const route = useRoute()
const id    = route.params.id
const auth  = useAuthStore()

const { challenge, fetchChallenge, difficultyClass } = useChallenge(id)
const { submissions, globalSubmissions, fetchSubmissions, fetchGlobalSubmissions } = useSubmission(challenge)

const isLoading       = ref(true)
const isLoadingGlobal = ref(false)
const activeTab       = ref('mine')
const expanded        = ref(new Set())

const tabs = computed(() => {
    const base = [
        { id: 'mine',   label: t('challenge_subs.tab_mine') },
        { id: 'global', label: t('challenge_subs.tab_global') },
    ]
    if (auth.isTeacher) base.push({ id: 'students', label: t('challenge_subs.tab_students') })
    return base
})

// Agrupa globalSubmissions por alumno — solo usado en tab 'students'.
const studentSummary = computed(() => {
    const map = new Map()

    for (const sub of globalSubmissions.value) {
        if (!sub.user) continue
        const uid = sub.user_id

        if (!map.has(uid)) {
            map.set(uid, {
                id:       uid,
                name:     sub.user.name,
                attempts: 0,
                accepted: false,
                lastAt:   sub.created_at,
            })
        }

        const entry = map.get(uid)
        entry.attempts++
        if (sub.status === 'accepted') entry.accepted = true
        if (sub.created_at > entry.lastAt) entry.lastAt = sub.created_at
    }

    // Resueltos primero, luego por intentos desc.
    return [...map.values()].sort((a, b) =>
        Number(b.accepted) - Number(a.accepted) || b.attempts - a.attempts
    )
})

async function switchTab(tab) {
    activeTab.value = tab
    if ((tab === 'global' || tab === 'students') && !globalSubmissions.value.length) {
        isLoadingGlobal.value = true
        await fetchGlobalSubmissions()
        isLoadingGlobal.value = false
    }
}

function toggleExpand(subId) {
    const s = new Set(expanded.value)
    s.has(subId) ? s.delete(subId) : s.add(subId)
    expanded.value = s
}

function relativeDate(iso) {
    const diff = Date.now() - new Date(iso).getTime()
    const m = Math.floor(diff / 60000)
    if (m < 1)  return t('challenge_subs.relative_now')
    if (m < 60) return t('challenge_subs.relative_minutes', { n: m })
    const h = Math.floor(m / 60)
    if (h < 24) return t('challenge_subs.relative_hours', { n: h })
    const d = Math.floor(h / 24)
    return t('challenge_subs.relative_days', { n: d })
}

function initials(name) {
    if (!name) return '?'
    return name.split(' ').map(w => w[0]).slice(0, 2).join('').toUpperCase()
}



onMounted(async () => {
    await fetchChallenge()
    await fetchSubmissions()
    isLoading.value = false
})
</script>

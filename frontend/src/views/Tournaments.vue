<template>
    <div class="min-h-[calc(100vh-80px)] bg-base px-4 py-6 md:px-8 md:py-10">
        <div class="max-w-7xl mx-auto">

            <!-- Header -->
            <div class="flex flex-col gap-4 mb-8 sm:flex-row sm:items-center sm:justify-between">
                <div>
                    <h1 class="text-2xl font-bold text-white mb-1 md:text-3xl">{{ $t('tournaments.title') }}</h1>
                    <p class="text-white/50 text-sm">{{ $t('tournaments.subtitle') }}</p>
                </div>
                <div class="flex items-center gap-2 flex-wrap">
                    <!-- Botón desplegable código privado -->
                    <div v-if="auth.isAuthenticated" class="relative">
                        <button @click="showCodeInput = !showCodeInput; findCodeError = ''"
                            :class="['px-4 py-2 rounded-lg border text-sm font-semibold transition-all',
                                showCodeInput
                                    ? 'bg-white/8 border-white/20 text-white/80'
                                    : 'bg-white/4 border-white/10 text-white/40 hover:text-white/70 hover:border-white/20']">
                            {{ $t('tournaments.join_private_btn') }}
                        </button>

                        <!-- Dropdown -->
                        <div v-if="showCodeInput"
                            class="absolute right-0 top-full mt-2 z-20 flex flex-col gap-2 p-3 rounded-xl border border-white/10 bg-[#111318] shadow-xl w-64">
                            <p class="text-xs text-white/35 mb-1">{{ $t('tournaments.private_code_hint') }}</p>
                            <input v-model="privateCode" type="text" placeholder="XXXXXXXX"
                                @keyup.enter="joinByCode" maxlength="8" autofocus
                                class="w-full bg-white/5 border border-white/10 rounded-lg px-3 py-2 text-sm font-mono font-bold text-white placeholder-white/20 outline-none focus:border-blue-500/40 transition-colors uppercase tracking-[0.2em]" />
                            <button @click="joinByCode" :disabled="findingCode || !privateCode.trim()"
                                class="flex items-center justify-center gap-2 w-full py-2 rounded-lg text-sm font-semibold bg-blue-500/15 border border-blue-500/25 text-blue-300 hover:bg-blue-500/25 transition-all disabled:opacity-40 disabled:cursor-not-allowed">
                                <svg v-if="!findingCode" class="w-3.5 h-3.5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
                                    <line x1="5" y1="12" x2="19" y2="12"/><polyline points="12 5 19 12 12 19"/>
                                </svg>
                                {{ findingCode ? $t('tournaments.finding') : $t('tournaments.access') }}
                            </button>
                            <p v-if="findCodeError" class="text-red-400/70 text-xs text-center">{{ findCodeError }}</p>
                        </div>
                    </div>

                    <RouterLink v-if="auth.isTeacher" to="/tournaments/create"
                        class="flex items-center gap-2 px-4 py-2 rounded-lg bg-blue-500/15 border border-blue-500/25 text-blue-300 text-sm font-semibold hover:bg-blue-500/25 transition-all">
                        <svg class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
                            <line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/>
                        </svg>
                        {{ $t('tournaments.create_btn') }}
                    </RouterLink>
                </div>
            </div>


            <!-- Tabs -->
            <div class="flex gap-1 mb-8 border-b border-white/8 pb-0 overflow-x-auto scrollbar-none">
                <button v-for="tab in TABS" :key="tab.value"
                    @click="activeTab = tab.value"
                    :class="[
                        'px-4 py-2.5 text-sm font-semibold transition-all border-b-2 -mb-px',
                        activeTab === tab.value
                            ? 'border-blue-400 text-blue-300'
                            : 'border-transparent text-white/35 hover:text-white/60'
                    ]">
                    {{ tab.label }}
                    <span v-if="counts[tab.value]" class="ml-1.5 px-1.5 py-0.5 rounded text-[10px] bg-white/8 text-white/40">
                        {{ counts[tab.value] }}
                    </span>
                </button>
            </div>

            <!-- Loading -->
            <div v-if="loading" class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-5">
                <div v-for="i in 6" :key="i" class="h-44 rounded-xl bg-white/3 border border-white/5 animate-pulse" />
            </div>

            <!-- Empty -->
            <div v-else-if="!filtered.length" class="flex flex-col items-center justify-center gap-3 py-24">
                <svg class="w-12 h-12 text-white/8" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
                    <path d="M6 9H4.5a2.5 2.5 0 0 1 0-5H6"/><path d="M18 9h1.5a2.5 2.5 0 0 0 0-5H18"/>
                    <path d="M18 2H6v7a6 6 0 0 0 12 0V2Z"/>
                </svg>
                <p class="text-white/25 text-sm">{{ $t('tournaments.empty', { tab: TABS.find(tab => tab.value === activeTab)?.label.toLowerCase() }) }}</p>
            </div>

            <!-- Grid -->
            <div v-else class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-5">
                <TournamentCard v-for="t in filtered" :key="t.id" :tournament="t" @require-auth="showAuthModal = true" />
            </div>

            <AuthModal v-if="showAuthModal" @close="showAuthModal = false" />

        </div>
    </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { api } from '@/services/api'
import { useAuthStore } from '@/stores/auth'
import TournamentCard from '@/components/TournamentCard.vue'
import AuthModal from '@/components/AuthModal.vue'

const { t } = useI18n()
const auth    = useAuthStore()
const router  = useRouter()
const loading = ref(false)
const tournaments = ref([])
const activeTab   = ref('active')

const showAuthModal  = ref(false)
const privateCode    = ref('')
const findingCode    = ref(false)
const findCodeError  = ref('')
const showCodeInput  = ref(false)

async function joinByCode() {
    if (!privateCode.value.trim()) return
    findingCode.value   = true
    findCodeError.value = ''
    try {
        const res = await api.get(`/tournaments/code/${privateCode.value.trim().toUpperCase()}`)
        router.push({ name: 'tournament', params: { id: res.data.id } })
    } catch {
        findCodeError.value = t('tournaments.invalid_code')
    } finally {
        findingCode.value = false
    }
}

const TABS = computed(() => [
    { value: 'active',   label: t('tournaments.tab_active') },
    { value: 'upcoming', label: t('tournaments.tab_upcoming') },
    { value: 'finished', label: t('tournaments.tab_finished') },
])

const filtered = computed(() => tournaments.value.filter(tournament => tournament.status === activeTab.value))
const counts   = computed(() => Object.fromEntries(TABS.value.map(tab => [tab.value, tournaments.value.filter(x => x.status === tab.value).length])))

async function fetchTournaments() {
    loading.value = true
    try {
        const res = await api.get('/tournaments')
        tournaments.value = res.data ?? []
    } catch {
        tournaments.value = []
    } finally {
        loading.value = false
    }
}

onMounted(fetchTournaments)
</script>

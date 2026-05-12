<template>
    <div @click="auth.isAuthenticated ? router.push({ name: 'tournament', params: { id: tournament.id } }) : emit('require-auth')"
        class="group bg-surface/40 backdrop-blur-sm rounded-xl overflow-hidden flex flex-col cursor-pointer border border-white/5 transition-all duration-200 hover:-translate-y-1 hover:border-blue-500/20 hover:shadow-lg hover:shadow-blue-500/5">

        <!-- Cover image -->
        <div v-if="tournament.cover_url" class="h-32 overflow-hidden shrink-0">
            <img :src="tournament.cover_url" class="w-full h-full object-cover transition-transform duration-300 group-hover:scale-105" />
        </div>

        <div class="p-5 flex flex-col gap-3 flex-1">
        <!-- Status + nombre -->
        <div class="flex items-start justify-between gap-2">
            <h2 class="font-bold text-white text-[15px] leading-tight">{{ tOrRaw(tournament.name) }}</h2>
            <span :class="['shrink-0 flex items-center gap-1.5 px-2 py-0.5 rounded-md text-[10px] font-bold', statusClass]">
                <span v-if="tournament.status === 'active'" class="w-1.5 h-1.5 rounded-full bg-current animate-pulse" />
                {{ statusLabel }}
            </span>
        </div>

        <!-- Descripción -->
        <p v-if="tournament.description" class="text-white/45 text-sm leading-relaxed line-clamp-2 flex-1">
            {{ tOrRaw(tournament.description) }}
        </p>

        <!-- Creator -->
        <div class="flex items-center gap-2">
            <UserAvatar :src="null" :name="tournament.creator?.name ?? ''" size="sm" />
            <span class="text-white/35 text-xs">{{ tournament.creator?.name }}</span>
        </div>

        <!-- Footer -->
        <div class="flex items-center justify-between pt-2 border-t border-white/5">
            <div class="flex items-center gap-1 text-white/30 text-xs">
                <svg class="w-3.5 h-3.5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/>
                    <path d="M23 21v-2a4 4 0 0 0-3-3.87"/><path d="M16 3.13a4 4 0 0 1 0 7.75"/>
                </svg>
                {{ tournament.participants_count }} {{ $t('tournament.participants') }}
            </div>

            <TournamentCountdown
                v-if="tournament.status === 'active'"
                :target="tournament.ends_at"
                :label="$t('tournament.ends_in')"
                class="text-right"
            />
            <TournamentCountdown
                v-else-if="tournament.status === 'upcoming'"
                :target="tournament.starts_at"
                :label="$t('tournament.starts_in')"
                class="text-right"
            />
            <span v-else class="text-xs text-white/25">
                {{ $t('tournament.finished_on') }} {{ formatDate(tournament.ends_at) }}
            </span>
        </div>
    </div>
</div>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { useAuthStore } from '@/stores/auth'
import UserAvatar from '@/components/UserAvatar.vue'
import TournamentCountdown from '@/components/TournamentCountdown.vue'

const { t, te, locale } = useI18n()
const router = useRouter()
const auth   = useAuthStore()
const emit   = defineEmits(['require-auth'])

const props = defineProps({
    tournament: { type: Object, required: true },
})

// Función auxiliar para traducir un valor si existe la clave de traducción, o mostrar el valor raw si no hay traducción (ej: para nombres/desc que vienen del backend y ya están traducidos según locale)
const tOrRaw = (val) => val && te(val) ? t(val) : (val ?? '')

// Mapeo de status a clases de estilo para badge
const STATUS_CLASS = {
    active:   'bg-green-500/15 text-green-400',
    upcoming: 'bg-blue-500/15 text-blue-400',
    finished: 'bg-white/8 text-white/35',
}

const statusLabel = computed(() => t(`tournament.status_${props.tournament.status}`) ?? props.tournament.status)
const statusClass = computed(() => STATUS_CLASS[props.tournament.status] ?? '')

function formatDate(dt) {
    return new Date(dt).toLocaleDateString(locale.value, { day: 'numeric', month: 'short' })
}
</script>

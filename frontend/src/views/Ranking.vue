<template>
    <div class="min-h-[calc(100vh-80px)] px-8 py-10" style="background: radial-gradient(ellipse at center top, rgb(30 58 138 / 0.1) 0%, #0b0f19 60%)">
        <div class="max-w-3xl mx-auto">

            <!-- Header -->
            <div class="mb-10 text-center">
                <div class="inline-flex items-center justify-center w-14 h-14 rounded-2xl bg-yellow-500/10 border border-yellow-500/20 mb-4">
                    <svg class="w-7 h-7 text-yellow-400" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">
                        <path d="M6 9H4.5a2.5 2.5 0 0 1 0-5H6"/><path d="M18 9h1.5a2.5 2.5 0 0 0 0-5H18"/>
                        <path d="M4 22h16"/><path d="M10 14.66V17c0 .55-.47.98-.97 1.21C7.85 18.75 7 20.24 7 22"/>
                        <path d="M14 14.66V17c0 .55.47.98.97 1.21C16.15 18.75 17 20.24 17 22"/>
                        <path d="M18 2H6v7a6 6 0 0 0 12 0V2Z"/>
                    </svg>
                </div>
                <h1 class="text-3xl font-bold text-white mb-1">{{ $t('ranking.title') }}</h1>
                <p class="text-white/40 text-sm">{{ $t('ranking.subtitle') }}</p>
            </div>

            <!-- Loading -->
            <div v-if="loading" class="space-y-3">
                <div class="flex gap-3 mb-6">
                    <div v-for="i in 3" :key="i" class="flex-1 h-36 rounded-2xl bg-white/3 border border-white/5 animate-pulse" />
                </div>
                <div v-for="i in 7" :key="i" class="h-14 rounded-xl bg-white/3 border border-white/5 animate-pulse" />
            </div>

            <template v-else-if="rows.length">

                <!-- Podium top 3 -->
                <div class="flex items-end gap-3 mb-8">

                    <!-- #2 silver -->
                    <div v-if="rows[1]" class="spin-wrap spin-silver flex-1">
                        <div class="spin-inner flex flex-col items-center gap-2 pb-4 pt-5">
                            <span class="text-2xl">🥈</span>
                            <UserAvatar :src="rows[1].avatar_url" :name="rows[1].name" size="xl" />
                            <p class="text-sm font-semibold text-white/80 text-center px-2 truncate w-full">{{ rows[1].name }}</p>
                            <p class="text-xl font-bold text-white/60">{{ rows[1].total_points }}</p>
                            <p class="text-xs text-white/25">{{ rows[1].solved }} {{ $t('ranking.solved') }}</p>
                        </div>
                    </div>
                    <div v-else class="flex-1" />

                    <!-- #1 gold -->
                    <div class="spin-wrap spin-gold flex-1">
                        <div class="spin-inner flex flex-col items-center gap-2 pb-4 pt-6">
                            <span class="text-2xl">🥇</span>
                            <UserAvatar :src="rows[0].avatar_url" :name="rows[0].name" size="xl" />
                            <p class="text-sm font-bold text-white text-center px-2 truncate w-full">{{ rows[0].name }}</p>
                            <p class="text-2xl font-bold text-yellow-300">{{ rows[0].total_points }}</p>
                            <p class="text-xs text-white/30">{{ rows[0].solved }} {{ $t('ranking.solved') }}</p>
                        </div>
                    </div>

                    <!-- #3 bronze -->
                    <div v-if="rows[2]" class="spin-wrap spin-bronze flex-1">
                        <div class="spin-inner flex flex-col items-center gap-2 pb-4 pt-5">
                            <span class="text-2xl">🥉</span>
                            <UserAvatar :src="rows[2].avatar_url" :name="rows[2].name" size="xl" />
                            <p class="text-sm font-semibold text-white/70 text-center px-2 truncate w-full">{{ rows[2].name }}</p>
                            <p class="text-xl font-bold text-orange-300/80">{{ rows[2].total_points }}</p>
                            <p class="text-xs text-white/25">{{ rows[2].solved }} {{ $t('ranking.solved') }}</p>
                        </div>
                    </div>
                    <div v-else class="flex-1" />
                </div>

                <!-- Resto del ranking -->
                <div class="space-y-1.5">
                    <div v-for="row in rows.slice(3)" :key="row.user_id"
                        class="flex items-center gap-4 px-4 py-3 rounded-xl border border-white/6 bg-white/2 hover:bg-white/4 transition-colors">

                        <span class="w-7 text-center text-sm font-semibold text-white/25 shrink-0">{{ row.position }}</span>

                        <UserAvatar :src="row.avatar_url" :name="row.name" size="lg" />

                        <div class="flex-1 min-w-0">
                            <p class="text-sm font-semibold text-white/80 truncate">{{ row.name }}</p>
                            <div class="mt-1 h-1 w-full bg-white/5 rounded-full overflow-hidden">
                                <div class="h-full bg-blue-500/40 rounded-full transition-all"
                                    :style="{ width: progressPct(row.total_points) + '%' }" />
                            </div>
                        </div>

                        <div class="text-right shrink-0">
                            <p class="text-xs text-white/25">{{ row.solved }} {{ $t('ranking.challenges') }}</p>
                            <p class="text-sm font-bold text-blue-300/80">{{ row.total_points }} pt</p>
                        </div>
                    </div>
                </div>

            </template>

            <!-- Empty -->
            <div v-else class="flex flex-col items-center justify-center gap-3 py-24">
                <svg class="w-12 h-12 text-white/8" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
                    <path d="M6 9H4.5a2.5 2.5 0 0 1 0-5H6"/><path d="M18 9h1.5a2.5 2.5 0 0 0 0-5H18"/>
                    <path d="M18 2H6v7a6 6 0 0 0 12 0V2Z"/>
                </svg>
                <p class="text-white/25 text-sm">{{ $t('ranking.empty') }}</p>
            </div>

        </div>
    </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { api } from '@/services/api'
import UserAvatar from '@/components/UserAvatar.vue'

const rows    = ref([])
const loading = ref(false)

const maxPoints   = computed(() => rows.value[0]?.total_points ?? 1)
const progressPct = (pts) => Math.round((pts / maxPoints.value) * 100)

async function fetchLeaderboard() {
    loading.value = true
    try {
        const res = await api.get('/leaderboard')
        rows.value = res.data ?? []
    } catch {
        rows.value = []
    } finally {
        loading.value = false
    }
}

onMounted(fetchLeaderboard)
</script>

<style scoped>
@keyframes border-rotate {
    to { transform: rotate(1turn); }
}

.spin-wrap {
    position: relative;
    padding: 1.5px;
    border-radius: 1rem;
    overflow: hidden;
}

.spin-wrap::before {
    content: '';
    position: absolute;
    inset: -100%;
    animation: border-rotate linear infinite;
}

.spin-gold  { box-shadow: 0 0 28px -6px rgba(255, 215, 0, 0.25); }
.spin-silver { box-shadow: 0 0 22px -6px rgba(192, 192, 192, 0.18); }
.spin-bronze { box-shadow: 0 0 22px -6px rgba(205, 127, 50, 0.18); }

/* Oro → platino → oro */
.spin-gold::before {
    background: conic-gradient(
        #7a5900 0deg,
        #c8960c 60deg,
        #ffd700 110deg,
        #fff0a0 165deg,
        #e8e8e8 180deg,
        #fff0a0 195deg,
        #ffd700 250deg,
        #c8960c 300deg,
        #7a5900 360deg
    );
    animation-duration: 5s;
}

/* Plata → blanco → plata */
.spin-silver::before {
    background: conic-gradient(
        #484848 0deg,
        #909090 60deg,
        #c0c0c0 110deg,
        #e8e8e8 165deg,
        #f5f5f5 180deg,
        #e8e8e8 195deg,
        #c0c0c0 250deg,
        #909090 300deg,
        #484848 360deg
    );
    animation-duration: 7s;
    animation-direction: reverse;
}

/* Bronce → cobre → bronce */
.spin-bronze::before {
    background: conic-gradient(
        #4a2800 0deg,
        #8b4513 60deg,
        #cd7f32 110deg,
        #deb887 165deg,
        #e8c89a 180deg,
        #deb887 195deg,
        #cd7f32 250deg,
        #8b4513 300deg,
        #4a2800 360deg
    );
    animation-duration: 9s;
}

.spin-inner {
    position: relative;
    border-radius: calc(1rem - 1.5px);
    height: 100%;
    background: #0b0f19;
}
</style>

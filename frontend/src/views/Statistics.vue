<template>
    <div class="min-h-[calc(100vh-80px)] bg-base px-8 py-10">
        <div class="max-w-7xl mx-auto">

            <div class="mb-10">
                <h1 class="text-3xl font-bold text-white mb-2">{{ $t('statistics.title') }}</h1>
                <p class="text-white/50 text-sm">{{ $t('statistics.subtitle') }}</p>
            </div>

            <div v-if="loading" class="text-white/40 text-sm">{{ $t('common.loading') }}</div>

            <template v-else>
                <!-- Stats globales -->
                <div class="grid grid-cols-2 md:grid-cols-4 gap-4 mb-10">
                    <div v-for="stat in globalStats" :key="stat.label"
                        class="bg-surface border border-white/5 rounded-lg p-5">
                        <p class="text-2xl font-bold text-white mb-1">{{ stat.value }}</p>
                        <p class="text-xs text-white/40">{{ stat.label }}</p>
                    </div>
                </div>

                <!-- Gráfica retos creados -->
                <div class="bg-surface border border-white/5 rounded-lg p-6 mb-10">
                    <h2 class="text-sm font-semibold text-white/60 uppercase tracking-wider mb-4">{{ $t('statistics.created_by_month') }}</h2>
                    <apexchart
                        type="line"
                        height="200"
                        :options="createdChartOptions"
                        :series="createdSeries"
                    />
                </div>

                <!-- Por ejercicio -->
                <div>
                    <h2 class="text-lg font-semibold text-white mb-4">{{ $t('statistics.performance_header') }}</h2>
                    <div v-if="exercises.length === 0" class="text-white/40 text-sm">
                        {{ $t('statistics.no_data') }}
                    </div>
                    <div v-else class="flex flex-col gap-2">
                        <div v-for="ex in exercises" :key="ex.id">
                            <!-- Fila clickable -->
                            <div
                                class="flex items-center justify-between bg-surface border border-white/5 rounded-lg px-5 py-3 cursor-pointer hover:border-white/15 transition-colors"
                                :class="{ 'border-accent/40': selectedId === ex.id }"
                                @click="toggleDetail(ex)"
                            >
                                <span class="text-sm text-white/80">{{ ex.title }}</span>
                                <div class="flex items-center gap-6 text-xs text-white/50">
                                    <span>{{ ex.submissions_count ?? 0 }} {{ $t('statistics.attempts') }}</span>
                                    <span class="text-green-400">{{ ex.accepted_count ?? 0 }} {{ $t('statistics.accepted') }}</span>
                                    <span>{{ successRate(ex) }}{{ $t('statistics.success_pct') }}</span>
                                    <span class="text-white/20">{{ selectedId === ex.id ? '▲' : '▼' }}</span>
                                </div>
                            </div>

                            <!-- Panel detalle -->
                            <div v-if="selectedId === ex.id"
                                class="bg-surface/60 border border-white/5 border-t-0 rounded-b-lg px-6 py-5">
                                <div v-if="detailLoading" class="text-white/40 text-xs py-4">{{ $t('statistics.loading_detail') }}</div>
                                <template v-else-if="detail">
                                    <div class="flex items-center gap-6 mb-5">
                                        <div class="bg-surface border border-white/5 rounded-lg px-5 py-3 text-center">
                                            <p class="text-xl font-bold text-accent">{{ detail.unique_students }}</p>
                                            <p class="text-xs text-white/40 mt-1">{{ $t('statistics.unique_students') }}</p>
                                        </div>
                                        <div class="bg-surface border border-white/5 rounded-lg px-5 py-3 text-center">
                                            <p class="text-xl font-bold text-white">{{ ex.submissions_count ?? 0 }}</p>
                                            <p class="text-xs text-white/40 mt-1">{{ $t('statistics.total_submissions_label') }}</p>
                                        </div>
                                        <div class="bg-surface border border-white/5 rounded-lg px-5 py-3 text-center">
                                            <p class="text-xl font-bold text-green-400">{{ ex.accepted_count ?? 0 }}</p>
                                            <p class="text-xs text-white/40 mt-1">{{ $t('statistics.accepted') }}</p>
                                        </div>
                                    </div>
                                    <div v-if="detail.submissions_by_date.length > 0">
                                        <p class="text-xs text-white/40 uppercase tracking-wider mb-3">{{ $t('statistics.submissions_by_day') }}</p>
                                        <apexchart
                                            type="line"
                                            height="180"
                                            :options="detailChartOptions"
                                            :series="detailSeries"
                                        />
                                    </div>
                                    <div v-else class="text-white/30 text-xs py-2">{{ $t('statistics.no_submissions') }}</div>
                                </template>
                            </div>
                        </div>
                    </div>
                </div>
            </template>

        </div>
    </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useI18n } from 'vue-i18n'
import { api } from '@/services/api'

const { locale, t } = useI18n()
const exercises    = ref([])
const loading      = ref(true)
const selectedId   = ref(null)
const detail       = ref(null)
const detailLoading = ref(false)

async function loadStats() {
    loading.value = true
    try {
        const response = await api.get('/users/me/exercises/stats')
        if (response) exercises.value = response
    } catch (error) {
        console.error(error)
    } finally {
        loading.value = false
    }
}

onMounted(loadStats)
watch(locale, loadStats)

async function toggleDetail(ex) {
    if (selectedId.value === ex.id) {
        selectedId.value = null
        detail.value = null
        return
    }
    selectedId.value = ex.id
    detail.value = null
    detailLoading.value = true
    try {
        detail.value = await api.get(`/exercises/${ex.id}/detail-stats`)
    } catch (e) {
        console.error(e)
    } finally {
        detailLoading.value = false
    }
}

// --- Global stats ---
const globalStats = computed(() => {
    const totalSubs     = exercises.value.reduce((acc, e) => acc + (e.submissions_count ?? 0), 0)
    const totalAccepted = exercises.value.reduce((acc, e) => acc + (e.accepted_count ?? 0), 0)
    return [
        { label: t('statistics.stat_created'),       value: exercises.value.length },
        { label: t('statistics.stat_total_subs'),    value: totalSubs },
        { label: t('statistics.stat_total_accepted'), value: totalAccepted },
        { label: t('statistics.stat_success_rate'),  value: totalSubs ? `${Math.round((totalAccepted / totalSubs) * 100)}%` : '—' },
    ]
})

function successRate(ex) {
    if (!ex.submissions_count) return 0
    return Math.round(((ex.accepted_count ?? 0) / ex.submissions_count) * 100)
}

// --- Gráfica retos creados por mes ---
const createdSeries = computed(() => {
    const counts = {}
    exercises.value.forEach(e => {
        const month = e.created_at?.slice(0, 7) // "2025-04"
        if (month) counts[month] = (counts[month] ?? 0) + 1
    })
    const sorted = Object.keys(counts).sort()
    return [{ name: t('statistics.series_created'), data: sorted.map(m => ({ x: m, y: counts[m] })) }]
})

const createdChartOptions = {
    chart:  { background: 'transparent', toolbar: { show: false }, sparkline: { enabled: false } },
    stroke: { curve: 'smooth', width: 2 },
    colors: ['#60a5fa'],
    xaxis:  { type: 'category', labels: { style: { colors: '#6b7280', fontSize: '11px' } } },
    yaxis:  { labels: { style: { colors: '#6b7280', fontSize: '11px' } }, min: 0, tickAmount: 3, forceNiceScale: true },
    grid:   { borderColor: '#1f2937', strokeDashArray: 3 },
    tooltip: { theme: 'dark' },
    markers: { size: 4, colors: ['#60a5fa'] },
}

// --- Gráfica detalle entregas por día ---
const detailSeries = computed(() => {
    if (!detail.value) return []
    return [{
        name: t('statistics.series_deliveries'),
        data: detail.value.submissions_by_date.map(r => ({ x: r.date, y: r.total })),
    }]
})

const detailChartOptions = {
    chart:  { background: 'transparent', toolbar: { show: false } },
    stroke: { curve: 'smooth', width: 2 },
    colors: ['#34d399'],
    xaxis:  { type: 'category', labels: { style: { colors: '#6b7280', fontSize: '11px' } } },
    yaxis:  { labels: { style: { colors: '#6b7280', fontSize: '11px' } }, min: 0, tickAmount: 3, forceNiceScale: true },
    grid:   { borderColor: '#1f2937', strokeDashArray: 3 },
    tooltip: { theme: 'dark' },
    markers: { size: 3, colors: ['#34d399'] },
}
</script>

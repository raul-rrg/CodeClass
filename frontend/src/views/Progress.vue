<template>
    <div class="min-h-[calc(100vh-80px)] bg-base px-8 py-10">
        <div class="max-w-7xl mx-auto">

            <div class="mb-10">
                <h1 class="text-3xl font-bold text-white mb-2">{{ $t('progress.title') }}</h1>
                <p class="text-white/50 text-sm">{{ $t('progress.subtitle') }}</p>
            </div>

            <div v-if="loading" class="text-white/40 text-sm">{{ $t('common.loading') }}</div>

            <template v-else>
                <!-- Stats rápidas -->
                <div class="grid grid-cols-2 md:grid-cols-4 gap-4 mb-8">
                    <div v-for="stat in stats" :key="stat.label"
                        class="bg-surface border border-white/5 rounded-xl p-5">
                        <p class="text-2xl font-bold text-white mb-1">{{ stat.value }}</p>
                        <p class="text-xs text-white/40">{{ stat.label }}</p>
                    </div>
                </div>

                <!-- Actividad 30 días -->
                <div class="bg-surface border border-white/5 rounded-xl p-6 mb-4" v-if="submissions.length">
                    <h2 class="text-sm font-semibold text-white/60 uppercase tracking-widest mb-4">{{ $t('progress.activity_header') }}</h2>
                    <apexchart type="bar" height="160" :options="activityOptions" :series="activitySeries" />
                </div>

                <!-- Dificultad + Lenguajes -->
                <div class="grid grid-cols-1 md:grid-cols-2 gap-4 mb-8" v-if="submissions.length">

                    <div class="bg-surface border border-white/5 rounded-xl p-6">
                        <h2 class="text-sm font-semibold text-white/60 uppercase tracking-widest mb-4">{{ $t('progress.difficulty_header') }}</h2>
                        <apexchart type="donut" height="220" :options="difficultyOptions" :series="difficultySeries" />
                    </div>

                    <div class="bg-surface border border-white/5 rounded-xl p-6">
                        <h2 class="text-sm font-semibold text-white/60 uppercase tracking-widest mb-4">{{ $t('progress.languages_header') }}</h2>
                        <apexchart type="donut" height="220" :options="languageOptions" :series="languageSeries" />
                    </div>
                </div>

                <!-- Submissions recientes -->
                <div>
                    <h2 class="text-lg font-semibold text-white mb-4">{{ $t('progress.recent_header') }}</h2>
                    <div v-if="submissions.length === 0" class="text-white/40 text-sm">
                        {{ $t('progress.no_submissions') }}
                    </div>
                    <div v-else class="flex flex-col gap-2">
                        <div v-for="sub in submissions" :key="sub.id"
                            class="flex items-center justify-between bg-surface border border-white/5 rounded-lg px-5 py-3">
                            <span class="text-sm text-white/80">{{ sub.exercise?.title }}</span>
                            <div class="flex items-center gap-5 shrink-0">
                                <div v-if="sub.max_execution_time != null" class="flex items-center gap-1.5 text-sky-400/80">
                                    <svg class="w-3 h-3" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                        <circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/>
                                    </svg>
                                    <span class="text-[10px] font-mono">{{ parseFloat(sub.max_execution_time).toFixed(3) }}s</span>
                                </div>
                                <div v-if="sub.max_memory != null" class="flex items-center gap-1.5 text-violet-400/80">
                                    <svg class="w-3 h-3" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                        <rect x="4" y="4" width="16" height="16" rx="2"/><rect x="9" y="9" width="6" height="6"/>
                                        <line x1="9" y1="1" x2="9" y2="4"/><line x1="15" y1="1" x2="15" y2="4"/>
                                        <line x1="9" y1="20" x2="9" y2="23"/><line x1="15" y1="20" x2="15" y2="23"/>
                                        <line x1="20" y1="9" x2="23" y2="9"/><line x1="20" y1="14" x2="23" y2="14"/>
                                        <line x1="1" y1="9" x2="4" y2="9"/><line x1="1" y1="14" x2="4" y2="14"/>
                                    </svg>
                                    <span class="text-[10px] font-mono">{{ sub.max_memory >= 1024 ? (sub.max_memory / 1024).toFixed(1) + ' MB' : sub.max_memory + ' KB' }}</span>
                                </div>
                                <div class="flex items-center gap-1 px-2 py-0.5 rounded font-medium text-xs"
                                    :class="sub.status === 'accepted' ? 'bg-green-500/15 text-green-400' : 'bg-red-500/15 text-red-400'">
                                    <svg class="w-3 h-3" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
                                        <template v-if="sub.status === 'accepted'">
                                            <polyline points="20 6 9 17 4 12"/>
                                        </template>
                                        <template v-else>
                                            <line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/>
                                        </template>
                                    </svg>
                                    {{ sub.status }}
                                </div>
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
import VueApexCharts from 'vue3-apexcharts'

const apexchart = VueApexCharts

const { locale, t } = useI18n()
const submissions = ref([])
const loading     = ref(true)

// ── Carga de datos ────────────────────────────────────────────────

// Obtiene los envíos del usuario autenticado desde la API
async function loadSubmissions() {
    loading.value = true
    try {
        const data = await api.get('/users/me/submissions')
        if (data) submissions.value = data
    } catch (e) {
        console.error(e)
    } finally {
        loading.value = false
    }
}

onMounted(loadSubmissions)
watch(locale, loadSubmissions) // recarga al cambiar idioma para re-traducir etiquetas de series

// ── Estadísticas rápidas ──────────────────────────────────────────

const stats = computed(() => {
    const total         = submissions.value.length
    const totalAccepted = submissions.value.filter(s => s.status === 'accepted').length
    const uniqueSolved  = new Set(
        submissions.value.filter(s => s.status === 'accepted').map(s => s.exercise_id)
    ).size

    return [
        { label: t('progress.stat_solved'),       value: uniqueSolved },
        { label: t('progress.stat_submissions'),  value: total },
        { label: t('progress.stat_accepted'),     value: totalAccepted },
        { label: t('progress.stat_success_rate'), value: total ? `${Math.round((totalAccepted / total) * 100)}%` : '—' },
    ]
})

// ── Actividad 30 días ─────────────────────────────────────────────

// Genera array de fechas YYYY-MM-DD de los últimos 30 días (orden cronológico)
const last30Days = computed(() => {
    const today = new Date()
    return Array.from({ length: 30 }, (_, i) => {
        const date = new Date(today)
        date.setDate(today.getDate() - (29 - i))
        return date.toISOString().split('T')[0]
    })
})

// Cuenta envíos aceptados y rechazados agrupados por día
const activitySeries = computed(() => {
    const accepted = Object.fromEntries(last30Days.value.map(d => [d, 0]))
    const rejected = Object.fromEntries(last30Days.value.map(d => [d, 0]))

    submissions.value.forEach(s => {
        const day = s.created_at?.split('T')[0]
        if (day && day in accepted) {
            if (s.status === 'accepted') accepted[day]++
            else rejected[day]++
        }
    })

    return [
        { name: t('progress.series_accepted'), data: last30Days.value.map(d => accepted[d]) },
        { name: t('progress.series_rejected'), data: last30Days.value.map(d => rejected[d]) },
    ]
})

const activityOptions = computed(() => ({
    chart:  { type: 'bar', stacked: true, background: 'transparent', toolbar: { show: false }, fontFamily: 'inherit' },
    colors: ['#4ade80', '#f87171'],
    plotOptions: { bar: { borderRadius: 3, columnWidth: '60%' } },
    dataLabels: { enabled: false },
    xaxis: {
        categories: last30Days.value.map(d => {
            const [, month, day] = d.split('-')
            return `${day}/${month}`
        }),
        tickAmount: 6,
        labels: { style: { colors: 'rgba(255,255,255,0.3)', fontSize: '10px' } },
        axisBorder: { show: false },
        axisTicks:  { show: false },
    },
    yaxis:  { labels: { style: { colors: 'rgba(255,255,255,0.3)', fontSize: '10px' } } },
    grid:   { borderColor: 'rgba(255,255,255,0.05)', strokeDashArray: 4 },
    legend: { labels: { colors: 'rgba(255,255,255,0.4)' }, fontSize: '12px' },
    tooltip: { theme: 'dark' },
}))

// ── Gráfico de dificultad ─────────────────────────────────────────

// Etiquetas y colores por nivel de dificultad (reactivo al idioma)
const DIFF_CONFIG = computed(() => ({
    easy:   { label: t('progress.diff_easy'),   color: '#4ade80' },
    medium: { label: t('progress.diff_medium'), color: '#facc15' },
    hard:   { label: t('progress.diff_hard'),   color: '#f87171' },
    insane: { label: t('progress.diff_insane'), color: '#c084fc' },
}))

// Cuenta ejercicios únicos resueltos (un envío aceptado por ejercicio) agrupados por dificultad
const difficultyData = computed(() => {
    const solved = new Map()
    submissions.value
        .filter(s => s.status === 'accepted')
        .forEach(s => { if (!solved.has(s.exercise_id)) solved.set(s.exercise_id, s.exercise?.difficulty) })

    const counts = { easy: 0, medium: 0, hard: 0, insane: 0 }
    solved.forEach(diff => { if (diff && diff in counts) counts[diff]++ })

    return Object.entries(counts).filter(([, v]) => v > 0)
})

const difficultySeries = computed(() => difficultyData.value.map(([, v]) => v))

const difficultyOptions = computed(() => ({
    chart:      { background: 'transparent', toolbar: { show: false }, fontFamily: 'inherit' },
    colors:     difficultyData.value.map(([k]) => DIFF_CONFIG.value[k].color),
    labels:     difficultyData.value.map(([k]) => DIFF_CONFIG.value[k].label),
    legend:     { position: 'bottom', labels: { colors: 'rgba(255,255,255,0.4)' }, fontSize: '12px' },
    dataLabels: { enabled: false },
    plotOptions: { pie: { donut: { size: '65%' } } },
    stroke:     { colors: ['#0f1320'], width: 2 },
    tooltip:    { theme: 'dark' },
}))

// ── Gráfico de lenguajes ──────────────────────────────────────────

const LANG_LABELS = { javascript: 'JavaScript', python: 'Python', java: 'Java' }
const LANG_COLORS = ['#60a5fa', '#f59e0b', '#a78bfa']

// Agrupa todos los envíos (no solo aceptados) por lenguaje, ordenado por frecuencia
const languageData = computed(() => {
    const counts = {}
    submissions.value.forEach(s => {
        if (s.language) counts[s.language] = (counts[s.language] || 0) + 1
    })
    return Object.entries(counts).sort((a, b) => b[1] - a[1])
})

const languageSeries = computed(() => languageData.value.map(([, v]) => v))

const languageOptions = computed(() => ({
    chart:      { background: 'transparent', toolbar: { show: false }, fontFamily: 'inherit' },
    colors:     LANG_COLORS,
    labels:     languageData.value.map(([k]) => LANG_LABELS[k] ?? k),
    legend:     { position: 'bottom', labels: { colors: 'rgba(255,255,255,0.4)' }, fontSize: '12px' },
    dataLabels: { enabled: false },
    plotOptions: { pie: { donut: { size: '65%' } } },
    stroke:     { colors: ['#0f1320'], width: 2 },
    tooltip:    { theme: 'dark' },
}))
</script>

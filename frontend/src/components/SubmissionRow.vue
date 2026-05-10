<template>
    <div class="rounded-xl border border-white/8 bg-white/2 overflow-hidden">

        <button
            @click="canExpand && emit('toggle', sub.id)"
            :class="['w-full px-4 py-3 transition-colors text-left', canExpand ? 'hover:bg-white/2 cursor-pointer' : 'cursor-default']">

            <!-- Desktop: single row -->
            <div class="hidden md:flex items-center gap-3">
                <!-- Status icon -->
                <StatusIcon :status="sub.status" class="w-3.5 h-3.5 shrink-0" :class="statusColor" />

                <!-- Status label -->
                <span :class="['text-xs font-semibold w-20 shrink-0', statusColor]">
                    {{ statusText }}
                </span>

                <!-- Test score (solo si hay resultados) -->
                <span v-if="total > 0" class="text-white/35 text-xs font-mono shrink-0">
                    {{ passed }}/{{ total }} {{ $t('challenge_subs.test_cases') }}
                </span>

                <!-- User (global tab) -->
                <div v-if="sub.user" class="flex items-center gap-1.5 shrink-0">
                    <UserAvatar :src="sub.user.avatar_url ?? null" :name="sub.user.name" size="sm" />
                    <span class="text-white/45 text-xs font-medium">{{ sub.user.name }}</span>
                </div>

                <div class="flex-1" />

                <!-- Time -->
                <div v-if="timeStr" class="flex items-center gap-1.5 text-sky-400/80 shrink-0">
                    <svg class="w-3 h-3" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/>
                    </svg>
                    <span class="text-[10px] font-mono">{{ timeStr }}</span>
                </div>

                <!-- Memory -->
                <div v-if="memStr" class="flex items-center gap-1.5 text-violet-400/80 shrink-0">
                    <svg class="w-3 h-3" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <rect x="4" y="4" width="16" height="16" rx="2"/>
                        <rect x="9" y="9" width="6" height="6"/>
                        <line x1="9" y1="1" x2="9" y2="4"/><line x1="15" y1="1" x2="15" y2="4"/>
                        <line x1="9" y1="20" x2="9" y2="23"/><line x1="15" y1="20" x2="15" y2="23"/>
                        <line x1="20" y1="9" x2="23" y2="9"/><line x1="20" y1="14" x2="23" y2="14"/>
                        <line x1="1" y1="9" x2="4" y2="9"/><line x1="1" y1="14" x2="4" y2="14"/>
                    </svg>
                    <span class="text-[10px] font-mono">{{ memStr }}</span>
                </div>

                <!-- Language -->
                <div :class="['flex items-center gap-1 px-2 py-0.5 rounded-md text-[10px] font-semibold shrink-0', lang?.activeClass ?? 'text-white/25']">
                    <component v-if="lang?.icon" :is="lang.icon" class="w-3 h-3" />
                    <span>{{ lang?.label ?? sub.language }}</span>
                </div>

                <!-- Date -->
                <span class="text-white/45 text-[11px] shrink-0 w-20 text-right">{{ dateStr }}</span>

                <!-- Arrow / lock -->
                <ExpandIcon :can-expand="canExpand" :is-expanded="isExpanded" />
            </div>

            <!-- Mobile: two-row layout -->
            <div class="flex flex-col gap-2 md:hidden">
                <!-- Row 1: status + score + arrow -->
                <div class="flex items-center gap-2">
                    <StatusIcon :status="sub.status" class="w-3.5 h-3.5 shrink-0" :class="statusColor" />
                    <span :class="['text-xs font-semibold shrink-0', statusColor]">{{ statusText }}</span>
                    <span v-if="total > 0" class="text-white/30 text-xs font-mono">{{ passed }}/{{ total }}</span>
                    <div class="flex-1" />
                    <div :class="['flex items-center gap-1 px-2 py-0.5 rounded-md text-[10px] font-semibold shrink-0', lang?.activeClass ?? 'text-white/25']">
                        <component v-if="lang?.icon" :is="lang.icon" class="w-3 h-3" />
                        <span>{{ lang?.label ?? sub.language }}</span>
                    </div>
                    <ExpandIcon :can-expand="canExpand" :is-expanded="isExpanded" />
                </div>
                <!-- Row 2: secondary info -->
                <div class="flex items-center gap-3 pl-5">
                    <div v-if="sub.user" class="flex items-center gap-1.5 min-w-0">
                        <UserAvatar :src="sub.user.avatar_url ?? null" :name="sub.user.name" size="sm" />
                        <span class="text-white/40 text-xs font-medium truncate">{{ sub.user.name }}</span>
                    </div>
                    <span v-if="timeStr" class="text-sky-400/70 text-[10px] font-mono shrink-0">{{ timeStr }}</span>
                    <span v-if="memStr" class="text-violet-400/70 text-[10px] font-mono shrink-0">{{ memStr }}</span>
                    <div class="flex-1" />
                    <span class="text-white/35 text-[10px] shrink-0">{{ dateStr }}</span>
                </div>
            </div>
        </button>

        <!-- Expanded: test results -->
        <template v-if="isExpanded && canExpand">
            <div class="h-px bg-white/5" />
            <div class="px-4 py-3 flex flex-col gap-2 font-mono text-xs">
                <div v-for="(r, i) in sub.submission_results" :key="i" class="flex items-start gap-2.5">
                    <span :class="['text-[13px] leading-none shrink-0 mt-0.5', r.passed ? 'text-green-400' : 'text-red-400/70']">›</span>
                    <span :class="['shrink-0 font-semibold', r.passed ? 'text-white/45' : 'text-white/40']">
                        {{ $t('challenge.case_label') }} {{ i + 1 }}
                    </span>
                    <template v-if="r.passed">
                        <span class="text-green-400/55 font-semibold">OK</span>
                        <!-- output null = backend lo ocultó (caso oculto aprobado sin permiso) -->
                        <template v-if="r.output === null && r.test_case?.is_hidden">
                            <span class="text-white/20 text-[10px] italic">{{ $t('challenge_subs.hidden') }}</span>
                        </template>
                        <template v-else>
                            <code v-if="r.output?.trim()" class="text-green-400/40 text-[10px]">{{ r.output.trim() }}</code>
                            <span v-if="r.execution_time" class="text-white/20 text-[10px] relative top-0.5">{{ r.execution_time }}s</span>
                        </template>
                    </template>
                    <span v-else-if="r.status === 'time_limit_exceeded'" class="text-orange-400/70">
                        {{ $t('challenge_subs.time_exceeded') }}
                    </span>
                    <span v-else class="text-white/30">
                        {{ $t('challenge.obtained') }}
                        <code :class="r.output?.trim() ? 'text-red-400/70' : 'text-white/20 italic'">
                            {{ r.output?.trim() || $t('challenge.empty_output') }}
                        </code>
                        <span v-if="r.test_case?.is_hidden" class="text-white/20 italic ml-1">({{ $t('challenge_subs.hidden') }})</span>
                    </span>
                </div>
            </div>
        </template>

    </div>
</template>

<script setup>
import { computed, h } from 'vue'
import { useI18n } from 'vue-i18n'
import { LANGUAGES } from '@/constants/languages'
import UserAvatar from '@/components/UserAvatar.vue'

const props = defineProps({
    sub:       { type: Object,  required: true },
    expanded:  { type: Object,  required: true },
    isOwn:     { type: Boolean, required: true },
    isTeacher: { type: Boolean, default: false },
})
const emit = defineEmits(['toggle'])

const { t } = useI18n()

const canExpand  = computed(() => props.isOwn || props.isTeacher)
const isExpanded = computed(() => props.expanded.has(props.sub.id))

const passed = computed(() => props.sub.submission_results?.filter(r => r.passed).length ?? 0)
const total  = computed(() => props.sub.submission_results?.length ?? 0)

const lang = computed(() => LANGUAGES.find(l => l.id === props.sub.language))

const statusColor = computed(() => {
    if (props.sub.status === 'accepted') return 'text-green-400'
    if (props.sub.status === 'rejected') return 'text-red-400/70'
    return 'text-orange-400/70'
})

const statusText = computed(() => {
    if (props.sub.status === 'accepted') return t('challenge_subs.status_accepted')
    if (props.sub.status === 'rejected') return t('challenge_subs.status_rejected')
    return t('challenge_subs.status_error')
})

function formatTime(v) {
    return v != null ? parseFloat(v).toFixed(3) + 's' : null
}
function formatMemory(kb) {
    if (kb == null) return null
    return kb >= 1024 ? (kb / 1024).toFixed(1) + ' MB' : kb + ' KB'
}
function relativeDate(iso) {
    const diff = Date.now() - new Date(iso).getTime()
    const m = Math.floor(diff / 60000)
    if (m < 1)  return t('challenge_subs.relative_now')
    if (m < 60) return t('challenge_subs.relative_minutes', { n: m })
    const h = Math.floor(m / 60)
    if (h < 24) return t('challenge_subs.relative_hours', { n: h })
    return t('challenge_subs.relative_days', { n: Math.floor(h / 24) })
}

const timeStr = computed(() => formatTime(props.sub.max_execution_time))
const memStr  = computed(() => formatMemory(props.sub.max_memory))
const dateStr = computed(() => relativeDate(props.sub.created_at))

// Sub-components for icons (avoid repeating SVG markup)
const StatusIcon = (p, { attrs }) => {
    const paths = {
        accepted: h('polyline', { points: '20 6 9 17 4 12' }),
        rejected: [h('line', { x1: '18', y1: '6', x2: '6', y2: '18' }), h('line', { x1: '6', y1: '6', x2: '18', y2: '18' })],
    }
    return h('svg', { viewBox: '0 0 24 24', fill: 'none', stroke: 'currentColor', 'stroke-width': '2.5', 'stroke-linecap': 'round', 'stroke-linejoin': 'round', ...attrs },
        paths[p.status] ?? [h('circle', { cx: '12', cy: '12', r: '10' }), h('line', { x1: '12', y1: '8', x2: '12', y2: '12' }), h('line', { x1: '12', y1: '16', x2: '12.01', y2: '16' })])
}

const ExpandIcon = (p) => {
    if (p.canExpand) {
        return h('svg', {
            class: `w-3.5 h-3.5 text-white/20 shrink-0 transition-transform duration-200 ${p.isExpanded ? 'rotate-90' : ''}`,
            viewBox: '0 0 24 24', fill: 'none', stroke: 'currentColor', 'stroke-width': '2.5', 'stroke-linecap': 'round', 'stroke-linejoin': 'round',
        }, [h('polyline', { points: '9 18 15 12 9 6' })])
    }
    return null
}
</script>

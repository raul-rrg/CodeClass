<template>
    <div :class="['flex flex-col gap-1', align === 'end' ? 'items-end' : 'items-center']">
        <p :class="['font-semibold uppercase tracking-widest', size === 'lg' ? 'text-xs text-white/40' : 'text-[10px] text-white/30']">{{ label }}</p>
        <p v-if="remaining > 0" :class="[
            'font-bold font-mono tracking-widest transition-colors duration-300',
            size === 'lg' ? 'text-5xl' : 'text-2xl',
            remaining <= 10 ? 'text-red-400 animate-pulse' : 'text-white'
        ]">{{ formatted }}</p>
        <p v-else :class="['font-semibold text-white/40', size === 'lg' ? 'text-base' : 'text-sm']">{{ $t('tournament.status_finished') }}</p>
    </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'

const props = defineProps({
    target: { type: String, required: true },
    label:  { type: String, default: 'Tiempo restante' },
    size:   { type: String, default: 'sm' },   // 'sm' | 'lg'
    align:  { type: String, default: 'center' }, // 'center' | 'end'
})

const remaining = ref(0)
let interval = null

function update() {
    remaining.value = Math.max(0, Math.floor((new Date(props.target) - Date.now()) / 1000))
}

const formatted = computed(() => {
    const s   = remaining.value
    const h   = Math.floor(s / 3600)
    const m   = Math.floor((s % 3600) / 60)
    const sec = s % 60
    return [h, m, sec].map(v => String(v).padStart(2, '0')).join(':')
})

onMounted(() => { update(); interval = setInterval(update, 1000) })
onUnmounted(() => clearInterval(interval))
</script>

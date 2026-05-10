<template>
    <Teleport to="body">
        <Transition name="modal">
            <div v-if="show"
                class="fixed inset-0 z-50 flex items-center justify-center p-4"
                @click.self="$emit('close')">

                <!-- Backdrop -->
                <div class="absolute inset-0 bg-black/60" />

                <!-- Flash rojo -->
                <Transition name="flash">
                    <div v-if="showFlash" class="absolute inset-0 bg-red-500/30 pointer-events-none z-20" />
                </Transition>

                <!-- Card -->
                <div :class="['relative z-10 w-full max-w-sm rounded-2xl border flex flex-col items-center overflow-hidden shadow-2xl', shaking && 'shake']"
                    :style="accepted
                        ? { background: '#09180f', borderColor: 'rgba(34,197,94,0.3)', boxShadow: '0 0 50px rgba(34,197,94,0.12)' }
                        : { background: '#180909', borderColor: 'rgba(239,68,68,0.25)',  boxShadow: '0 0 50px rgba(239,68,68,0.1)' }">

                    <!-- ── ÉXITO ─────────────────────────────────────── -->
                    <template v-if="accepted">

                        <div class="p-6 flex flex-col items-center gap-4 w-full">
                            <!-- Icono con glow -->
                            <div class="w-20 h-20 rounded-full bg-green-500/10 flex items-center justify-center"
                                style="box-shadow: 0 0 30px rgba(34,197,94,0.25)">
                                <svg class="w-10 h-10 text-green-400" viewBox="0 0 24 24" fill="none"
                                    stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
                                    <path d="M6 9H4.5a2.5 2.5 0 0 1 0-5H6"/>
                                    <path d="M18 9h1.5a2.5 2.5 0 0 0 0-5H18"/>
                                    <path d="M4 22h16"/>
                                    <path d="M10 14.66V17c0 .55-.47.98-.97 1.21C7.85 18.75 7 20.24 7 22"/>
                                    <path d="M14 14.66V17c0 .55.47.98.97 1.21C16.15 18.75 17 20.24 17 22"/>
                                    <path d="M18 2H6v7a6 6 0 0 0 12 0V2Z"/>
                                </svg>
                            </div>

                            <!-- Título -->
                            <div class="text-center">
                                <h2 class="text-2xl font-bold text-green-300 mb-1">{{ $t('submission_modal.correct_title') }}</h2>
                                <p class="text-white/40 text-sm">{{ $t('submission_modal.correct_hint') }}</p>
                            </div>

                            <!-- Stats -->
                            <div class="w-full flex gap-3">
                                <div class="flex-1 rounded-xl border border-green-500/15 bg-green-500/8 py-3 flex flex-col items-center gap-0.5">
                                    <span class="text-xl font-bold text-green-400">{{ details.passed_count }}/{{ details.total_count }}</span>
                                    <span class="text-[11px] text-white/30 font-medium">{{ $t('submission_modal.tests_passed') }}</span>
                                </div>
                                <div class="flex-1 rounded-xl border border-blue-500/15 bg-blue-500/8 py-3 flex flex-col items-center gap-0.5">
                                    <span class="text-xl font-bold text-blue-400">+{{ points }}</span>
                                    <span class="text-[11px] text-white/30 font-medium">{{ $t('submission_modal.points_earned') }}</span>
                                </div>
                            </div>

                            <!-- Botón -->
                            <button @click="$emit('close')"
                                class="w-full py-2.5 rounded-xl bg-green-500/15 hover:bg-green-500/25 border border-green-500/25 hover:border-green-500/40 text-green-300 text-sm font-semibold transition-all">
                                {{ $t('submission_modal.continue_btn') }}
                            </button>
                        </div>
                    </template>

                    <!-- ── FALLO ──────────────────────────────────────── -->
                    <template v-else>
                        <div class="p-6 flex flex-col items-center gap-4 w-full">

                            <!-- Icono -->
                            <div class="w-16 h-16 rounded-full bg-red-500/10 flex items-center justify-center">
                                <svg class="w-8 h-8 text-red-400" viewBox="0 0 24 24" fill="none"
                                    stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
                                    <circle cx="12" cy="12" r="10"/>
                                    <line x1="15" y1="9" x2="9" y2="15"/>
                                    <line x1="9" y1="9" x2="15" y2="15"/>
                                </svg>
                            </div>

                            <!-- Título + mensaje dinámico -->
                            <div class="text-center">
                                <h2 class="text-xl font-bold text-red-300 mb-1">{{ $t('submission_modal.wrong_title') }}</h2>
                                <p class="text-white/40 text-sm">{{ failureMessage }}</p>
                            </div>

                            <!-- Barra de progreso -->
                            <div class="w-full flex flex-col gap-1.5">
                                <div class="flex justify-between text-[11px] text-white/30 font-medium">
                                    <span>{{ $t('submission_modal.tests_passed') }}</span>
                                    <span>{{ details.passed_count }}/{{ details.total_count }}</span>
                                </div>
                                <div class="w-full bg-white/5 rounded-full h-1.5 overflow-hidden">
                                    <div class="h-full rounded-full transition-all duration-500"
                                        :class="progressColor"
                                        :style="{ width: progressPct + '%' }" />
                                </div>
                            </div>

                            <!-- Tests fallidos -->
                            <div v-if="failedTests.length" class="w-full flex flex-wrap gap-1.5">
                                <span v-for="i in failedTests" :key="i"
                                    class="text-[11px] px-2.5 py-1 rounded-lg bg-red-500/10 border border-red-500/20 text-red-400/80 font-mono">
                                    ✗ {{ $t('submission_modal.test_case_item', { n: i + 1 }) }}
                                </span>
                            </div>

                            <!-- Botones -->
                            <div class="w-full flex gap-2.5">
                                <button @click="$emit('close')"
                                    class="flex-1 py-2.5 rounded-xl border border-white/10 text-white/45 hover:text-white/65 hover:bg-white/5 text-sm font-medium transition-all">
                                    {{ $t('submission_modal.close_btn') }}
                                </button>
                                <button @click="$emit('retry')"
                                    class="flex-1 py-2.5 rounded-xl bg-red-500/15 hover:bg-red-500/25 border border-red-500/25 hover:border-red-500/40 text-red-300 text-sm font-semibold transition-all">
                                    {{ $t('submission_modal.retry_btn') }}
                                </button>
                            </div>
                        </div>
                    </template>

                </div>
            </div>
        </Transition>
    </Teleport>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { useI18n } from 'vue-i18n'
import confetti from 'canvas-confetti'

const { t } = useI18n()

const props = defineProps({
    show:     { type: Boolean, default: false },
    accepted: { type: Boolean, default: false },
    details:  { type: Object,  default: () => ({ passed_count: 0, total_count: 0, results: [] }) },
    points:   { type: Number,  default: 0 },
})

defineEmits(['close', 'retry'])

const showFlash = ref(false)
const shaking   = ref(false)

const failedTests   = computed(() => (props.details.results ?? []).map((_, i) => i).filter(i => !props.details.results[i].passed))
const progressPct   = computed(() => props.details.total_count ? (props.details.passed_count / props.details.total_count) * 100 : 0)
const progressColor = computed(() => progressPct.value === 0 ? 'bg-red-500' : progressPct.value < 50 ? 'bg-orange-500' : 'bg-yellow-400')
const failureMessage = computed(() => {
    const pct = progressPct.value
    if (pct === 0)   return t('submission_modal.msg_0')
    if (pct < 50)    return t('submission_modal.msg_50')
    if (pct < 100)   return t('submission_modal.msg_almost')
    return ''
})

const COLORS = ['#60a5fa', '#8b5cf6', '#34d399', '#f59e0b', '#a78bfa']

function fireConfetti() {
    const base = { colors: COLORS, ticks: 300 }
    confetti({ ...base, particleCount: 120, spread: 70, startVelocity: 45, origin: { x: 0.35, y: 0.6 } })
    confetti({ ...base, particleCount: 120, spread: 70, startVelocity: 45, origin: { x: 0.65, y: 0.6 } })
    setTimeout(() => confetti({ ...base, particleCount: 150, spread: 100, startVelocity: 55, origin: { x: 0.5, y: 0.55 } }), 150)
    setTimeout(() => {
        confetti({ ...base, particleCount: 100, spread: 80, startVelocity: 40, origin: { x: 0.4, y: 0.6 } })
        confetti({ ...base, particleCount: 100, spread: 80, startVelocity: 40, origin: { x: 0.6, y: 0.6 } })
    }, 350)
    setTimeout(() => confetti({ ...base, particleCount: 200, spread: 120, startVelocity: 20, gravity: 0.6, origin: { x: 0.5, y: 0 } }), 600)
}

function fireFailure() {
    showFlash.value = true
    setTimeout(() => showFlash.value = false, 350)
    shaking.value = true
    setTimeout(() => shaking.value = false, 600)
}

watch(() => props.show, (val) => {
    if (!val) return
    if (props.accepted) fireConfetti()
    else fireFailure()
})
</script>

<style scoped>
.modal-enter-active,
.modal-leave-active { transition: opacity 0.2s ease; }
.modal-enter-active .relative,
.modal-leave-active .relative { transition: transform 0.2s ease, opacity 0.2s ease; }
.modal-enter-from,
.modal-leave-to { opacity: 0; }
.modal-enter-from .relative { transform: scale(0.92) translateY(16px); opacity: 0; }
.modal-leave-to .relative { transform: scale(0.95); opacity: 0; }

.flash-enter-active { transition: opacity 0.05s ease; }
.flash-leave-active { transition: opacity 0.3s ease; }
.flash-enter-from,
.flash-leave-to { opacity: 0; }

@keyframes shake {
    0%   { transform: translateX(0); }
    15%  { transform: translateX(-10px); }
    30%  { transform: translateX(10px); }
    45%  { transform: translateX(-8px); }
    60%  { transform: translateX(8px); }
    75%  { transform: translateX(-4px); }
    90%  { transform: translateX(4px); }
    100% { transform: translateX(0); }
}
.shake { animation: shake 0.55s ease; }
</style>

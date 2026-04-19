<template>
    <div @click="goToChallenge" class="group bg-surface/40 backdrop-blur-sm border border-white/5 rounded-xl p-5 flex flex-col gap-3 cursor-pointer transition-all duration-200 hover:border-blue-500/30 hover:-translate-y-1 hover:shadow-lg hover:shadow-blue-500/10">

        <!-- Title -->
        <div class="flex items-start justify-between gap-2">
            <h2 class="font-bold text-white text-lg leading-tight">{{ challenge.title }}</h2>
            
            <svg v-if="!auth.isAuthenticated"  class="w-4 h-4 text-white/25 shrink-0 mt-0.5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <rect x="3" y="11" width="18" height="11" rx="2" ry="2"/>
                <path d="M7 11V7a5 5 0 0 1 10 0v4"/>
            </svg>
        </div>

        <!-- Categoria -->
        <div>
            <span class="px-2.5 py-0.5 text-xs font-medium border border-white/15 text-white/55 rounded">
                {{ challenge.category }}
            </span>
        </div>

        <!-- Description -->
        <p class="text-white/50 text-sm leading-relaxed line-clamp-2 flex-1">{{ challenge.description }}</p>

        <!-- Author -->
        <div class="flex items-center gap-2">
            <div class="w-7 h-7 rounded-full bg-blue-500/20 flex items-center justify-center text-xs font-bold text-blue-300 shrink-0">
                {{ initials(challenge.author) }}
            </div>
            <span class="text-white/45 text-xs">Created by <span class="font-semibold text-white/65">{{ challenge.author ?? 'Unknown' }}</span></span>
        </div>

        <!-- Footer: difficulty + points -->
        <div class="flex items-center justify-between mt-1">
            <span :class="['px-2.5 py-0.5 text-xs font-semibold rounded capitalize', difficultyClass]">
                {{ challenge.difficulty }}
            </span>
            <div class="flex items-center gap-1.5 text-blue-400 text-sm font-semibold">
                <svg class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <path d="M6 9H4.5a2.5 2.5 0 0 1 0-5H6"/><path d="M18 9h1.5a2.5 2.5 0 0 0 0-5H18"/>
                    <path d="M4 22h16"/><path d="M10 14.66V17c0 .55-.47.98-.97 1.21C7.85 18.75 7 20.24 7 22"/>
                    <path d="M14 14.66V17c0 .55.47.98.97 1.21C16.15 18.75 17 20.24 17 22"/>
                    <path d="M18 2H6v7a6 6 0 0 0 12 0V2Z"/>
                </svg>
                {{ points }} pts
            </div>
        </div>

    </div>
</template>

<script setup>

import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { DIFFICULTY_BADGE_CLASS, DIFFICULTY_POINTS } from '@/constants/difficulties'

const router = useRouter()
const auth   = useAuthStore()
const emit   = defineEmits(['require-auth'])

function goToChallenge() {
    if (!auth.isAuthenticated) { emit('require-auth'); return }
    router.push({ name: 'challenge', params: { id: props.challenge.id } })
}

const props = defineProps({
    challenge: { type: Object, required: true },
})

const difficultyClass = computed(() => DIFFICULTY_BADGE_CLASS[props.challenge.difficulty] ?? '')
const points          = computed(() => DIFFICULTY_POINTS[props.challenge.difficulty] ?? 0)


// Extrae hasta 2 iniciales del nombre, ej: "John Doe" → "JD"
function initials(name) {
    if (!name) return '?'
    return name.split(' ').map(w => w[0]).slice(0, 2).join('').toUpperCase()
}

</script>

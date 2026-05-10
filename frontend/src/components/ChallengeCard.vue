<template>
    <div @click="goToChallenge" :class="[
            'group bg-surface/40 backdrop-blur-sm rounded-xl p-5 flex flex-col gap-3 cursor-pointer transition-all duration-200 hover:-translate-y-1 hover:shadow-lg',
            challenge.is_solved
                ? 'border border-green-500/30 shadow-[0_0_20px_-4px_rgba(34,197,94,0.2)] hover:border-green-500/50 hover:shadow-[0_0_28px_-4px_rgba(34,197,94,0.3)]'
                : 'border border-white/5 hover:border-blue-500/30 hover:shadow-blue-500/10'
        ]">

        <!-- Title -->
        <div class="flex items-start justify-between gap-2">
            <h2 class="font-bold text-white text-lg leading-tight">{{ challenge.title }}</h2>

            <div class="flex items-center gap-1.5 shrink-0">
                <!-- Badge oculto -->
                <span v-if="!challenge.is_published"
                    class="flex items-center gap-1 px-1.5 py-0.5 rounded-md bg-white/5 text-white/35 text-[10px] font-medium ring-1 ring-white/10">
                    <svg class="w-3 h-3" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24"/>
                        <line x1="1" y1="1" x2="23" y2="23"/>
                    </svg>
                    {{ $t('common.hidden') }}
                </span>

                <!-- Badge resuelto -->
                <span v-if="challenge.is_solved"
                    class="flex items-center gap-1 px-2 py-0.5 rounded-md bg-green-500/15 text-green-400 text-[10px] font-bold ring-1 ring-green-500/30">
                    <svg class="w-3 h-3" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
                        <polyline points="20 6 9 17 4 12"/>
                    </svg>
                    {{ $t('common.solved') }}
                </span>
                <svg v-else-if="!auth.isAuthenticated" class="w-4 h-4 text-white/25 mt-0.5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <rect x="3" y="11" width="18" height="11" rx="2" ry="2"/>
                    <path d="M7 11V7a5 5 0 0 1 10 0v4"/>
                </svg>
            </div>
        </div>

        <!-- Categoria + Dificultad -->
        <div class="flex items-center gap-2">
            <span class="px-2.5 py-0.5 text-xs font-medium border border-white/15 text-white/55 rounded capitalize">
                {{ challenge.category }}
            </span>
            <span :class="['px-2.5 py-0.5 text-xs font-semibold rounded capitalize', difficultyClass]">
                {{ challenge.difficulty }}
            </span>
        </div>

        <!-- Description -->
        <p class="text-white/50 text-sm leading-relaxed line-clamp-2 flex-1">{{ challenge.short_description || descriptionPreview }}</p>

        <!-- Author -->
        <div class="flex items-center gap-2">
            <UserAvatar :src="challenge.author_avatar" :name="challenge.author ?? ''" size="md" />
            <span class="text-white/45 text-xs">{{ $t('challenge_card.created_by') }} <span class="font-semibold text-white/65">{{ challenge.author ?? $t('challenge_card.unknown_author') }}</span></span>
        </div>

        <!-- Footer: submissions btn + points -->
        <div class="flex items-center justify-between mt-1">
            <button v-if="auth.isAuthenticated"
                @click.stop="router.push({ name: 'challenge-submissions', params: { id: challenge.id } })"
                class="flex items-center gap-1 px-2 py-1 rounded-lg text-[10px] font-semibold transition-all text-violet-300/80 bg-violet-500/10 border border-violet-500/20 hover:bg-violet-500/20 hover:text-violet-300 hover:border-violet-500/35">
                <svg class="w-3 h-3" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <line x1="18" y1="20" x2="18" y2="10"/><line x1="12" y1="20" x2="12" y2="4"/><line x1="6" y1="20" x2="6" y2="14"/>
                </svg>
                {{ $t('challenge_card.view_submissions') }}
            </button>
            <div v-else></div>
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

        <!-- Acciones teacher -->
        <div v-if="showActions" class="flex gap-2 pt-3 border-t border-white/8 mt-1">
            <button
                @click.stop="emit('edit')"
                class="flex-1 flex items-center justify-center gap-1.5 py-1.5 rounded-lg text-xs font-semibold transition-all text-blue-300/70 bg-blue-500/8 border border-blue-500/15 hover:bg-blue-500/18 hover:text-blue-300 hover:border-blue-500/30">
                <svg class="w-3.5 h-3.5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"/>
                    <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"/>
                </svg>
                {{ $t('challenge_card.edit') }}
            </button>
            <button
                @click.stop="emit('delete')"
                class="flex-1 flex items-center justify-center gap-1.5 py-1.5 rounded-lg text-xs font-semibold transition-all text-red-300/70 bg-red-500/8 border border-red-500/15 hover:bg-red-500/18 hover:text-red-300 hover:border-red-500/30">
                <svg class="w-3.5 h-3.5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <polyline points="3 6 5 6 21 6"/><path d="M19 6l-1 14a2 2 0 0 1-2 2H8a2 2 0 0 1-2-2L5 6"/>
                    <path d="M10 11v6"/><path d="M14 11v6"/>
                    <path d="M9 6V4a1 1 0 0 1 1-1h4a1 1 0 0 1 1 1v2"/>
                </svg>
                {{ $t('challenge_card.delete') }}
            </button>
        </div>

    </div>
</template>

<script setup>

import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { DIFFICULTY_BADGE_CLASS, DIFFICULTY_POINTS } from '@/constants/difficulties'
import UserAvatar from '@/components/UserAvatar.vue'

const router = useRouter()
const auth   = useAuthStore()
const emit   = defineEmits(['require-auth', 'edit', 'delete'])

function goToChallenge() {
    if (!auth.isAuthenticated) { emit('require-auth'); return }
    router.push({ name: 'challenge', params: { id: props.challenge.id } })
}

const props = defineProps({
    challenge:   { type: Object,  required: true },
    showActions: { type: Boolean, default: false },
})

const difficultyClass   = computed(() => DIFFICULTY_BADGE_CLASS[props.challenge.difficulty] ?? '')
const points            = computed(() => DIFFICULTY_POINTS[props.challenge.difficulty] ?? 0)
const descriptionPreview = computed(() => {
    const text = props.challenge.description ?? ''
    // tomar solo primer bloque antes de heading o código
    const firstParagraph = text.split(/\n#{1,6}\s|\n```/)[0]
    return firstParagraph
        .replace(/`[^`]*`/g, '')
        .replace(/\*\*([^*]+)\*\*/g, '$1')
        .replace(/\*([^*]+)\*/g, '$1')
        .replace(/\[([^\]]+)\]\(.*?\)/g, '$1')
        .replace(/\n+/g, ' ')
        .trim()
})



</script>

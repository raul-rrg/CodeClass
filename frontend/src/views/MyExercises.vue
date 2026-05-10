<template>
    <div class="min-h-[calc(100vh-80px)] bg-base px-8 py-10">
        <div class="max-w-7xl mx-auto">

            <!-- Header -->
            <div class="mb-10">
                <h1 class="text-3xl font-bold text-white mb-2">{{ $t('my_exercises.title') }}</h1>
                <p class="text-white/50 text-sm">{{ $t('my_exercises.subtitle') }}</p>
            </div>

            <!-- Loading -->
            <div v-if="loading" class="text-white/40 text-sm">{{ $t('common.loading') }}</div>

            <!-- Empty -->
            <div v-else-if="exercises.length === 0" class="text-white/40 text-sm">
                {{ $t('my_exercises.empty') }}
            </div>

            <!-- Grid -->
            <div v-else class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-5">
                <ChallengeCard
                    v-for="exercise in exercises"
                    :key="exercise.id"
                    :challenge="exercise"
                    :show-actions="true"
                    @edit="editExercise(exercise.id)"
                    @delete="deleteExercise(exercise.id)"
                />
            </div>

        </div>

        <!-- Modal confirmar eliminación -->
        <Transition name="overlay">
            <div v-if="pendingDeleteId" class="fixed inset-0 z-50 flex items-center justify-center p-4">
                <div class="absolute inset-0 bg-black/30" @click="pendingDeleteId = null" />
                <Transition name="modal" appear>
                    <div v-if="pendingDeleteId" class="relative bg-surface border border-white/10 rounded-2xl p-6 w-full max-w-sm shadow-2xl flex flex-col gap-4">
                    <div class="flex items-center gap-3">
                        <div class="w-10 h-10 rounded-full bg-red-500/15 flex items-center justify-center shrink-0">
                            <svg class="w-5 h-5 text-red-400" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                <polyline points="3 6 5 6 21 6"/><path d="M19 6l-1 14a2 2 0 0 1-2 2H8a2 2 0 0 1-2-2L5 6"/>
                                <path d="M10 11v6"/><path d="M14 11v6"/>
                                <path d="M9 6V4a1 1 0 0 1 1-1h4a1 1 0 0 1 1 1v2"/>
                            </svg>
                        </div>
                        <div>
                            <h3 class="text-white font-semibold text-[15px]">{{ $t('my_exercises.delete_title') }}</h3>
                            <p class="text-white/45 text-sm mt-0.5">{{ $t('my_exercises.delete_hint') }}</p>
                        </div>
                    </div>
                    <div class="flex gap-2 mt-1">
                        <button
                            @click="pendingDeleteId = null"
                            class="flex-1 py-2 rounded-lg text-sm font-semibold text-white/60 bg-white/6 border border-white/10 hover:bg-white/10 hover:text-white/80 transition-all">
                            {{ $t('common.cancel') }}
                        </button>
                        <button
                            @click="confirmDelete"
                            :disabled="deleting"
                            class="flex-1 py-2 rounded-lg text-sm font-semibold text-red-300 bg-red-500/15 border border-red-500/25 hover:bg-red-500/25 hover:text-red-200 hover:border-red-500/40 disabled:opacity-50 transition-all">
                            {{ deleting ? $t('common.deleting') : $t('my_exercises.delete_btn') }}
                        </button>
                    </div>
                </div>
                </Transition>
            </div>
        </Transition>

    </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useI18n } from 'vue-i18n'
import { useRouter } from 'vue-router'
import { api } from '@/services/api'
import ChallengeCard from '@/components/ChallengeCard.vue'

const { locale, t } = useI18n()
const router = useRouter()
const exercises = ref([])
const loading = ref(true)
const pendingDeleteId = ref(null)
const deleting = ref(false)

async function loadExercises() {
    loading.value = true
    try {
        const response = await api.get('/users/me/exercises')
        if (response) exercises.value = response.data
    } catch (error) {
        console.error(error)
    } finally {
        loading.value = false
    }
}

onMounted(loadExercises)
watch(locale, loadExercises)

function editExercise(id) {
    router.push({ name: 'exercise-edit', params: { id } })
}

function deleteExercise(id) {
    pendingDeleteId.value = id
}

async function confirmDelete() {
    deleting.value = true
    try {
        await api.delete(`/exercises/${pendingDeleteId.value}`)
        exercises.value = exercises.value.filter(e => e.id !== pendingDeleteId.value)
        pendingDeleteId.value = null
    } catch (error) {
        console.error(error)
    } finally {
        deleting.value = false
    }
}
</script>

<style scoped>
.overlay-enter-active, .overlay-leave-active { transition: opacity 0.2s ease; }
.overlay-enter-from, .overlay-leave-to { opacity: 0; }

.modal-enter-active  { transition: opacity 0.2s ease, transform 0.2s cubic-bezier(0.34, 1.56, 0.64, 1); }
.modal-leave-active  { transition: opacity 0.15s ease, transform 0.15s ease; }
.modal-enter-from    { opacity: 0; transform: scale(0.92) translateY(8px); }
.modal-leave-to      { opacity: 0; transform: scale(0.95) translateY(4px); }
</style>

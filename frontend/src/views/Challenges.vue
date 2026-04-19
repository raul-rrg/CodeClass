<template>
    <div class="min-h-[calc(100vh-80px)] bg-base px-8 py-10">
        <div class="max-w-7xl mx-auto">

            <!-- Header -->
            <div class="mb-10">
                <h1 class="text-3xl font-bold text-white mb-2">Retos de Programación</h1>
                <p class="text-white/50 text-sm">Pon a prueba tus habilidades con estos desafíos algorítmicos</p>
            </div>

            <!-- Filters -->
            <div class="flex items-center gap-4 mb-8 flex-wrap">
                <div class="flex items-center gap-2 text-white/40 text-sm shrink-0">
                    <svg class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <polygon points="22 3 2 3 10 12.46 10 19 14 21 14 12.46 22 3"/>
                    </svg>
                    <span>Filtros:</span>
                </div>

                <div class="flex gap-2">
                    <button v-for="difficulty in difficulties" :key="difficulty"
                        @click="activeFilter = difficulty"
                        :class="[
                            'px-4 py-1.5 text-sm border rounded transition-all capitalize',
                            activeFilter === difficulty
                                ? DIFFICULTY_ACTIVE_CLASS[difficulty]
                                : 'border-white/15 text-white/60 hover:border-white/30 hover:text-white/90'
                        ]">
                        {{ difficulty }}
                    </button>
                </div>

                <div class="w-px h-5 bg-white/10 self-center"></div>

                <select v-model="activeCategory"
                    class="bg-surface border border-white/15 text-white/60 text-sm rounded px-3 py-1.5 outline-none hover:border-white/30 transition-colors cursor-pointer">
                    <option value="">Todas las categorías</option>
                    <option v-for="cat in categories" :key="cat" :value="cat">{{ cat }}</option>
                </select>
            </div>

            <!-- Grid -->
            <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-5">
               <ChallengeCard v-for="challenge in filtered" :key="challenge.id" :challenge="challenge" @require-auth="showAuthModal = true" />
            </div>

            <AuthModal v-if="showAuthModal" @close="showAuthModal = false" />

        </div>
    </div>
</template>

<script setup>

import { ref, computed, onMounted } from 'vue'
import { api } from '@/services/api'
import { DIFFICULTY_FILTER_CLASS as DIFFICULTY_ACTIVE_CLASS } from '@/constants/difficulties'
import ChallengeCard from '@/components/ChallengeCard.vue'
import AuthModal from '@/components/AuthModal.vue'

const showAuthModal = ref(false)



const difficulties = ['all', 'easy', 'medium', 'hard', 'insane']
const challenges = ref([])

onMounted(fetchChallenges)


// TODO hacer la peticion con paiginacion y con filtros de dificultad y categoria,
// ahora mismo se traen todos los retos de golpe, lo cual no es escalable
async function fetchChallenges() {
    try {
        const response = await api.get('/exercises')
        if (response) challenges.value = response.data
    } catch (error) {
        console.error(error)
    }
}

const activeFilter   = ref('all')
const activeCategory = ref('')

const categories = computed(() => [...new Set(challenges.value.map(c => c.category))])

const filtered = computed(() => challenges.value.filter(c => {
    const byDiff = activeFilter.value === 'all' || c.difficulty === activeFilter.value
    const byCat  = !activeCategory.value || c.category === activeCategory.value
    return byDiff && byCat
}))

</script>

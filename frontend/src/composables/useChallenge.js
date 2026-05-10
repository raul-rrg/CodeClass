import { ref, computed } from 'vue'
import { marked } from 'marked'
import DOMPurify from 'dompurify'
import { api } from '@/services/api'
import { DIFFICULTY_BADGE_CLASS, DIFFICULTY_TEXT_CLASS, DIFFICULTY_POINTS } from '@/constants/difficulties'


// Composable para controlar el estado y la lógica relacionada con un reto 
export function useChallenge(id) {

    // Estado del reto
    const challenge = ref({
        id:            null,
        title:         '',
        description:   '',
        category:      '',
        difficulty:    'easy',
        author:        null,
        is_verified:   false,
        is_solved:     false,
        function_name: '',
        parameters:    [],
        return_type:   '',
        test_cases:    [],
    })

    async function fetchChallenge() {
        try {
            const response = await api.get(`/exercises/${id}`)
            if (response?.data) challenge.value = response.data
        } catch (error) {
            console.error('Error al cargar el reto:', error)
        }
    }

    // La descripción llega de la API en Markdown. marked.parse() lo convierte a HTML,
    // DOMPurify elimina posibles XSS antes de renderizar con v-html en el template.
    const descriptionHtml = computed(() =>
        DOMPurify.sanitize(marked.parse(challenge.value.description || ''))
    )

    // Computed para obtener las clases CSS y puntos asociados a la dificultad del reto
    const difficultyClass     = computed(() => DIFFICULTY_BADGE_CLASS[challenge.value.difficulty] ?? '')
    const difficultyTextClass = computed(() => DIFFICULTY_TEXT_CLASS[challenge.value.difficulty] ?? '')
    const challengePoints     = computed(() => DIFFICULTY_POINTS[challenge.value.difficulty] ?? 0)

    // Función para obtener las iniciales del autor del reto
    function initials(name) {
        if (!name) return '?'
        return name.split(' ').map(w => w[0]).slice(0, 2).join('').toUpperCase()
    }

    return { challenge, fetchChallenge, descriptionHtml, difficultyClass, difficultyTextClass, challengePoints, initials }
}

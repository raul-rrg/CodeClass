<template>

    <div class="min-h-[calc(100vh-80px)] flex items-center justify-center px-4 py-12">
        <div class="w-full max-w-md">

            <!-- Header -->
            <div class="text-center mb-8">
                <h1 class="font-bold text-3xl text-title mb-2">
                    Crear Cuenta
                </h1>
                <p class="text-body">
                    Únete a la comunidad CodeClass
                </p>
            </div>

            <!-- Form -->
            <div class="bg-surface/40 backdrop-blur-sm border border-white/5 p-8">
                <form class="space-y-6" @submit.prevent="submit">

                    <!-- Nombre -->
                    <div>
                        <label class="block text-sm text-subtitle mb-2">
                            Nombre Completo
                        </label>
                        <div class="relative">
                            <svg class="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-muted" viewBox="0 0 24 24"
                                fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round"
                                stroke-linejoin="round">
                                <circle cx="12" cy="8" r="4" />
                                <path d="M4 20c0-4 3.6-7 8-7s8 3 8 7" />
                            </svg>
                            <input v-model="name" type="text" placeholder="Juan Pérez"
                                class="w-full bg-base border border-white/10 text-title pl-11 pr-4 py-3 focus:outline-none focus:border-primary/50 transition-colors" />
                        </div>
                    </div>

                    <!-- Email -->
                    <div>
                        <label class="block text-sm text-subtitle mb-2">
                            Correo Electrónico
                        </label>
                        <div class="relative">
                            <svg class="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-muted" viewBox="0 0 24 24"
                                fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round"
                                stroke-linejoin="round">
                                <rect width="20" height="16" x="2" y="4" rx="2" />
                                <path d="m22 7-8.97 5.7a1.94 1.94 0 0 1-2.06 0L2 7" />
                            </svg>
                            <input v-model="email" type="text" placeholder="tu@email.com"
                                :class="['w-full bg-base border text-title pl-11 pr-4 py-3 focus:outline-none transition-colors',
                                    errorEmail ? 'border-red-500/60 focus:border-red-500/80' : 'border-white/10 focus:border-primary/50']" />
                        </div>
                        <p v-if="errorEmail" class="text-xs text-red-400 mt-1">{{ errorEmail }}</p>
                    </div>

                    <!-- Contraseña -->
                    <div>
                        <label class="block text-sm text-subtitle mb-2">
                            Contraseña
                        </label>
                        <div class="relative">
                            <svg class="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-muted" viewBox="0 0 24 24"
                                fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round"
                                stroke-linejoin="round">
                                <rect width="18" height="11" x="3" y="11" rx="2" ry="2" />
                                <path d="M7 11V7a5 5 0 0 1 10 0v4" />
                            </svg>
                            <input :type="showPassword ? 'text' : 'password'" v-model="password" placeholder="••••••••"
                                class="w-full bg-base border border-white/10 text-title pl-11 pr-11 py-3 focus:outline-none focus:border-primary/50 transition-colors" />
                            <button type="button" @click="showPassword = !showPassword"
                                class="absolute right-3 top-1/2 -translate-y-1/2 text-muted hover:text-subtitle transition-colors">
                                <svg v-if="!showPassword" class="w-5 h-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                    <path d="M2 12s3-7 10-7 10 7 10 7-3 7-10 7-10-7-10-7Z" />
                                    <circle cx="12" cy="12" r="3" />
                                </svg>
                                <svg v-else class="w-5 h-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                    <path d="M9.88 9.88a3 3 0 1 0 4.24 4.24" />
                                    <path d="M10.73 5.08A10.43 10.43 0 0 1 12 5c7 0 10 7 10 7a13.16 13.16 0 0 1-1.67 2.68" />
                                    <path d="M6.61 6.61A13.526 13.526 0 0 0 2 12s3 7 10 7a9.74 9.74 0 0 0 5.39-1.61" />
                                    <line x1="2" x2="22" y1="2" y2="22" />
                                </svg>
                            </button>
                        </div>
                    </div>

                    <!-- Confirmar Contraseña -->
                    <div>
                        <label class="block text-sm text-subtitle mb-2">
                            Confirmar Contraseña
                        </label>
                        <div class="relative">
                            <svg class="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-muted" viewBox="0 0 24 24"
                                fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round"
                                stroke-linejoin="round">
                                <rect width="18" height="11" x="3" y="11" rx="2" ry="2" />
                                <path d="M7 11V7a5 5 0 0 1 10 0v4" />
                            </svg>
                            <input :type="showPasswordConfirmation ? 'text' : 'password'" v-model="password_confirmation" placeholder="••••••••"
                                :class="['w-full bg-base border text-title pl-11 pr-11 py-3 focus:outline-none transition-colors',
                                    errorPassword ? 'border-red-500/60 focus:border-red-500/80' : 'border-white/10 focus:border-primary/50']" />
                            <button type="button" @click="showPasswordConfirmation = !showPasswordConfirmation"
                                class="absolute right-3 top-1/2 -translate-y-1/2 text-muted hover:text-subtitle transition-colors">
                                <svg v-if="!showPasswordConfirmation" class="w-5 h-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                    <path d="M2 12s3-7 10-7 10 7 10 7-3 7-10 7-10-7-10-7Z" />
                                    <circle cx="12" cy="12" r="3" />
                                </svg>
                                <svg v-else class="w-5 h-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                    <path d="M9.88 9.88a3 3 0 1 0 4.24 4.24" />
                                    <path d="M10.73 5.08A10.43 10.43 0 0 1 12 5c7 0 10 7 10 7a13.16 13.16 0 0 1-1.67 2.68" />
                                    <path d="M6.61 6.61A13.526 13.526 0 0 0 2 12s3 7 10 7a9.74 9.74 0 0 0 5.39-1.61" />
                                    <line x1="2" x2="22" y1="2" y2="22" />
                                </svg>
                            </button>
                        </div>
                        <p v-if="errorPassword" class="text-xs text-red-400 mt-1">{{ errorPassword }}</p>
                    </div>

                    <!-- Rol -->
                    <div>
                        <p class="text-sm text-subtitle mb-2">Me registro como</p>
                        <div class="grid grid-cols-2">
                            <button type="button" @click="role = 'student'" :class="role === 'student'
                                ? 'bg-primary text-title'
                                : 'bg-transparent border border-white/10 text-subtitle hover:border-primary/40'"
                                class="py-3 text-sm font-bold transition-colors">
                                Estudiante
                            </button>
                            <button type="button" @click="role = 'teacher'" :class="role === 'teacher'
                                ? 'bg-primary text-title'
                                : 'bg-transparent border border-white/10 text-subtitle hover:border-primary/40'"
                                class="py-3 text-sm font-bold transition-colors">
                                Profesor
                            </button>
                        </div>
                    </div>

                    <!-- Server Error -->
                    <p v-if="serverError" class="text-xs text-red-400 text-center">{{ serverError }}</p>

                    <!-- Submit -->
                    <button type="submit" :disabled="!canSubmit || loading"
                        :class="['w-full py-3 px-6 font-bold text-sm transition-colors', canSubmit && !loading ? 'bg-primary text-title hover:bg-primary/80' : 'bg-primary/30 text-title/40 cursor-not-allowed']">
                        {{ loading ? 'Creando cuenta...' : 'Crear Cuenta' }}
                    </button>

                </form>

                <!-- Login Link -->
                <div class="mt-6 text-center">
                    <p class="text-sm text-body">
                        ¿Ya tienes una cuenta?
                        <a href="/login" class="text-accent hover:text-accent/70 transition-colors">
                            Inicia sesión aquí
                        </a>
                    </p>
                </div>
            </div>

        </div>
    </div>

</template>


<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

// --- Servicios ---
const router = useRouter()
const authStore = useAuthStore()

// --- Formulario ---
const name = ref('')
const email = ref('')
const password = ref('')
const password_confirmation = ref('')
const role = ref('student')

// --- UI ---
const loading = ref(false)
const showPassword = ref(false)
const showPasswordConfirmation = ref(false)

// --- Errores ---
const serverError = ref('')
const errorEmail = ref('')
const errorPassword = ref('')

// --- Validación ---
const EMAIL_REGEX = /^[^\s@]+@[^\s@]+\.[^\s@]+$/

const canSubmit = computed(() =>
    name.value.trim() && email.value.trim() && password.value && password_confirmation.value
)

function validate() {
    errorEmail.value = EMAIL_REGEX.test(email.value) ? '' : 'Introduce un correo válido.'
    if (password.value.length < 8) errorPassword.value = 'La contraseña debe tener al menos 8 caracteres.'
    else if (password.value !== password_confirmation.value) errorPassword.value = 'Las contraseñas no coinciden.'
    else errorPassword.value = ''
    return !errorEmail.value && !errorPassword.value
}

// --- Submit ---
async function submit() {
    if (!validate()) return

    loading.value = true
    serverError.value = ''
    try {
        await authStore.register({ name: name.value, email: email.value, password: password.value, password_confirmation: password_confirmation.value, role: role.value })
        router.push('/')
    } catch (err) {
        serverError.value = err.response?.data?.message || 'Error al registrar'
    } finally {
        loading.value = false
    }
}
</script>

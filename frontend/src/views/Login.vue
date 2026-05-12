<template>

    <div class="min-h-full flex items-center justify-center px-4 py-12">
        <div class="w-full max-w-md">

            <!-- Header -->
            <div class="text-center mb-8">
                <!-- <div class="flex justify-center mb-4">
                    <div class="p-3 bg-primary/10 border border-primary/20 rounded-lg">
                        <svg class="w-8 h-8 text-primary" viewBox="0 0 24 24" fill="none" stroke="currentColor"
                            stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
                            <path d="m18 16 4-4-4-4" />
                            <path d="m6 8-4 4 4 4" />
                            <path d="m14.5 4-5 16" />
                        </svg>
                    </div>
                </div> -->
                <h1 class="font-bold text-3xl text-title mb-2">
                    {{ $t('login.title') }}
                </h1>
                <p class="text-body">
                    {{ $t('login.subtitle') }}
                </p>
            </div>

            <!-- Form -->
            <div class="bg-surface/40 backdrop-blur-sm border border-white/5 p-8">
                <form @submit.prevent="submit" class="space-y-6">

                    <!-- Email -->
                    <div>
                        <label class="block text-sm text-subtitle mb-2">
                            {{ $t('login.email_label') }}
                        </label>
                        <div class="relative">
                            <svg class="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-muted"
                                viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"
                                stroke-linecap="round" stroke-linejoin="round">
                                <rect width="20" height="16" x="2" y="4" rx="2" />
                                <path d="m22 7-8.97 5.7a1.94 1.94 0 0 1-2.06 0L2 7" />
                            </svg>
                            <input type="text" v-model="email" :placeholder="$t('login.email_placeholder')"
                                :class="['w-full bg-base border text-title pl-11 pr-4 py-3 focus:outline-none transition-colors', errors.email ? 'border-red-500/70 focus:border-red-500' : 'border-white/10 focus:border-primary/50']" />
                        </div>
                        <p v-if="errors.email" class="text-xs text-red-400 mt-1">{{ errors.email }}</p>
                    </div>

                    <!-- Password -->
                    <div>
                        <label class="block text-sm text-subtitle mb-2">
                            {{ $t('login.password_label') }}
                        </label>
                        <div class="relative">
                            <svg class="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-muted"
                                viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"
                                stroke-linecap="round" stroke-linejoin="round">
                                <rect width="18" height="11" x="3" y="11" rx="2" ry="2" />
                                <path d="M7 11V7a5 5 0 0 1 10 0v4" />
                            </svg>
                            <input :type="showPassword ? 'text' : 'password'" v-model="password" placeholder="••••••••"
                                class="w-full bg-base border border-white/10 text-title pl-11 pr-11 py-3 focus:outline-none focus:border-primary/50 transition-colors" />
                            <!-- Toggle visibilidad -->
                            <button type="button" @click="showPassword = !showPassword"
                                class="absolute right-3 top-1/2 -translate-y-1/2 text-muted hover:text-subtitle transition-colors">
                                <!-- Ojo abierto -->
                                <svg v-if="!showPassword" class="w-5 h-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                    <path d="M2 12s3-7 10-7 10 7 10 7-3 7-10 7-10-7-10-7Z" />
                                    <circle cx="12" cy="12" r="3" />
                                </svg>
                                <!-- Ojo tachado -->
                                <svg v-else class="w-5 h-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                    <path d="M9.88 9.88a3 3 0 1 0 4.24 4.24" />
                                    <path d="M10.73 5.08A10.43 10.43 0 0 1 12 5c7 0 10 7 10 7a13.16 13.16 0 0 1-1.67 2.68" />
                                    <path d="M6.61 6.61A13.526 13.526 0 0 0 2 12s3 7 10 7a9.74 9.74 0 0 0 5.39-1.61" />
                                    <line x1="2" x2="22" y1="2" y2="22" />
                                </svg>
                            </button>
                        </div>
                    </div>

                    <!-- Error del servidor -->
                    <p v-if="serverError" class="text-sm text-red-400 bg-red-500/10 border border-red-500/20 px-4 py-2">
                        {{ serverError }}
                    </p>

                    <!-- Submit -->
                    <button type="submit" :disabled="!canSubmit"
                        :class="['w-full py-3 px-6 font-bold text-sm transition-colors', canSubmit ? 'bg-primary text-title hover:bg-primary/80' : 'bg-primary/30 text-title/40 cursor-not-allowed']">
                        {{ $t('login.submit') }}
                    </button>

                </form>

                <!-- Register Link -->
                <div class="mt-6 text-center">
                    <p class="text-sm text-body">
                        {{ $t('login.no_account') }}
                        <RouterLink to="/register" class="text-accent hover:text-accent/70 transition-colors">
                            {{ $t('login.register_link') }}
                        </RouterLink>
                    </p>
                </div>
            </div>

        </div>
    </div>

</template>


<script setup>

import { ref, reactive, watch, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { useAuthStore } from '@/stores/auth'

const { t } = useI18n()

// --- Estado ---
const email = ref('')
const password = ref('')
const showPassword = ref(false)
const serverError = ref('')
const errors = reactive({ email: '' })

// --- Store / Router ---
const router = useRouter()
const authStore = useAuthStore()

// --- Computed ---
const canSubmit = computed(() => email.value.trim() && password.value)

// --- Watchers ---
watch(email, () => { errors.email = '' }) // limpia error al escribir

// --- Validación ---
const EMAIL_REGEX = /^[^\s@]+@[^\s@]+\.[^\s@]+$/

function validate() {
    errors.email = ''
    if (!EMAIL_REGEX.test(email.value)) errors.email = t('login.email_invalid')
    return !errors.email
}

// --- Submit ---
async function submit() {
    serverError.value = ''
    if (!validate()) return

    try {
        await authStore.login({ email: email.value, password: password.value })
        router.push('/challenges')
    } catch {
        serverError.value = t('login.credentials_invalid')
    }
}

</script>

<template>
    <Teleport to="body">
        <div class="fixed inset-0 z-50 flex items-center justify-center">

            <!-- Backdrop -->
            <div class="absolute inset-0 bg-black/60 backdrop-blur-sm" @click="emit('close')" />

            <!-- Modal -->
            <div class="relative bg-surface border border-white/10 rounded-2xl p-8 w-full max-w-sm mx-4 flex flex-col gap-6">

                <!-- Icon -->
                <div class="flex justify-center">
                    <div class="w-14 h-14 rounded-full bg-blue-500/10 border border-blue-500/20 flex items-center justify-center">
                        <svg class="w-7 h-7 text-blue-400" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                            <rect x="3" y="11" width="18" height="11" rx="2" ry="2"/>
                            <path d="M7 11V7a5 5 0 0 1 10 0v4"/>
                        </svg>
                    </div>
                </div>

                <!-- Text -->
                <div class="text-center">
                    <h2 class="text-white font-bold text-lg mb-1">Acceso restringido</h2>
                    <p class="text-white/50 text-sm">Inicia sesión para ver el detalle del reto y enviar tu solución.</p>
                </div>

                <!-- Actions -->
                <div class="flex flex-col gap-3">
                    <button @click="goTo('login')"
                        class="w-full py-2.5 rounded-lg bg-blue-600 hover:bg-blue-500 text-white text-sm font-semibold transition-colors">
                        Iniciar sesión
                    </button>
                    <button @click="goTo('register')"
                        class="w-full py-2.5 rounded-lg border border-white/15 hover:border-white/30 text-white/70 hover:text-white text-sm font-semibold transition-colors">
                        Crear cuenta
                    </button>
                </div>

            </div>
        </div>
    </Teleport>
</template>

<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()
const emit   = defineEmits(['close'])

function goTo(name) {
    emit('close')
    router.push({ name })
}
</script>

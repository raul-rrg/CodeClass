<template>
    <nav class="sticky top-0 z-50 border-b border-white/5 bg-[#0b1326]/95 backdrop-blur-sm">
        <div class="mx-auto flex w-full max-w-screen-2xl items-center justify-between px-8 py-4">

            <RouterLink to="/" class="group flex items-center gap-2">
                <svg class="h-6 w-6 text-primary" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"
                    stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
                    <path d="m18 16 4-4-4-4" />
                    <path d="m6 8-4 4 4 4" />
                    <path d="m14.5 4-5 16" />
                </svg>
                <span class="text-xl font-bold text-title transition-colors group-hover:text-accent">
                    CodeClass
                </span>
            </RouterLink>

            <div class="flex items-center gap-8">
                <RouterLink to="/" class="text-sm transition-colors"
                    :class="isActive('/') ? 'text-accent' : 'text-body hover:text-title'">
                    Home
                </RouterLink>
                <RouterLink to="/challenges" class="text-sm transition-colors"
                    :class="isActive('/challenges') ? 'text-accent' : 'text-body hover:text-title'">
                    Challenges
                </RouterLink>
                <RouterLink to="/ranking" class="flex items-center gap-1.5 text-sm transition-colors"
                    :class="isActive('/ranking') ? 'text-accent' : 'text-body hover:text-title'">
                    Ranking
                </RouterLink>
                <RouterLink to="/about" class="flex items-center gap-1.5 text-sm transition-colors"
                    :class="isActive('/about') ? 'text-accent' : 'text-body hover:text-title'">
                    About
                </RouterLink>
            </div>




            <div v-if="!authStore.isAuthenticated" class="flex items-center gap-4">
                <RouterLink to="/login"
                    class="border border-white/10 px-4 py-2 text-sm text-title transition-colors hover:border-white/20">
                    Login
                </RouterLink>
                <RouterLink to="/register"
                    class="bg-title px-6 py-2 text-sm font-bold text-base transition-colors hover:bg-blue-50">
                    Sign up
                </RouterLink>
            </div>

            <div v-else class="relative" ref="dropdownRef">
                <button @click="showDropdown = !showDropdown"
                    class="flex items-center gap-3 rounded px-2 py-1 transition-colors hover:bg-white/5">
                    <!-- Avatar con inicial -->
                    <div
                        class="flex h-8 w-8 items-center justify-center rounded-full bg-primary text-sm font-bold text-white">
                        {{ authStore.user?.name?.charAt(0).toUpperCase() }}
                    </div>
                    <!-- Nombre y rol -->
                    <div class="text-left leading-tight">
                        <p class="text-sm font-semibold text-title">{{ authStore.user?.name }}</p>
                        <p class="text-xs font-medium uppercase tracking-wide text-accent">{{ authStore.user?.role }}
                        </p>
                    </div>
                </button>

                <div v-if="showDropdown"
                    class="absolute right-0 mt-2 w-48 rounded border border-white/10 bg-surface/95 py-1 shadow-lg backdrop-blur-sm">
                    <RouterLink to="/profile" @click="showDropdown = false"
                        class="flex items-center gap-2 px-4 py-2 text-sm text-title transition-colors hover:bg-white/5">
                        <svg class="h-4 w-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"
                            aria-hidden="true">
                            <circle cx="12" cy="8" r="4" />
                            <path d="M4 20c0-4 3.6-7 8-7s8 3 8 7" />
                        </svg>
                        Mi Perfil
                    </RouterLink>
                    <button @click="handleLogout"
                        class="flex w-full items-center gap-2 px-4 py-2 text-sm text-red-400 transition-colors hover:bg-red-500/10">
                        <svg class="h-4 w-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"
                            aria-hidden="true">
                            <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4" />
                            <polyline points="16 17 21 12 16 7" />
                            <line x1="21" y1="12" x2="9" y2="12" />
                        </svg>
                        Cerrar Sesión
                    </button>
                </div>
            </div>

        </div>
    </nav>
</template>

<script setup>

import { ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth';
import { useClickOutside } from '@/composables/useClickOutside'

const authStore = useAuthStore()

// Rutas 
const route = useRoute()   
const router = useRouter() 

// Resaltar el enlace activo en la navbar comparando la ruta actual con el enlace
const isActive = (path) => route.path === path 

// Dropdown

const showDropdown = ref(false) // Ref para controlar la visibilidad del dropdown
const dropdownRef = ref(null)   // Ref para el elemento del dropdown, necesario para detectar clics fuera de él

// Cierra el dropdown cuando el usuario hace clic fuera del elemento referenciado
useClickOutside(dropdownRef, () => showDropdown.value = false)

// Función para manejar el logout, cierra sesión y redirige al inicio
async function handleLogout() {
    try {
        await authStore.logout()
    } catch (error) {
        console.error('Error during logout:', error)
    } finally {
        showDropdown.value = false
        router.push('/')
    }
}


</script>

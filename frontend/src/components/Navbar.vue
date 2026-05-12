<template>
    <nav class="sticky top-0 z-50 border-b border-white/5 bg-[#0b1326]/95 backdrop-blur-sm">
        <div class="mx-auto flex w-full max-w-screen-2xl items-center justify-between pl-4 pr-4 md:pr-8 py-3">

            <!-- Logo -->
            <RouterLink :to="authStore.isAuthenticated ? '/challenges' : '/'" class="group flex items-center gap-2 shrink-0">
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

            <!-- Desktop nav links -->
            <div class="hidden md:flex items-center gap-6">
                <RouterLink v-if="!authStore.isAuthenticated" to="/" class="text-sm transition-colors"
                    :class="isActive('/') ? 'text-accent' : 'text-body hover:text-title'">
                    {{ $t('nav.home') }}
                </RouterLink>
                <RouterLink to="/challenges" class="text-sm transition-colors"
                    :class="isActive('/challenges') ? 'text-accent' : 'text-body hover:text-title'">
                    {{ $t('nav.challenges') }}
                </RouterLink>
                <RouterLink to="/ranking" class="text-sm transition-colors"
                    :class="isActive('/ranking') ? 'text-accent' : 'text-body hover:text-title'">
                    {{ $t('nav.ranking') }}
                </RouterLink>
                <RouterLink to="/tournaments" class="text-sm transition-colors"
                    :class="isActive('/tournaments') ? 'text-accent' : 'text-body hover:text-title'">
                    {{ $t('nav.tournaments') }}
                </RouterLink>
                <RouterLink v-if="!authStore.isAuthenticated" to="/about" class="text-sm transition-colors"
                    :class="isActive('/about') ? 'text-accent' : 'text-body hover:text-title'">
                    {{ $t('nav.about') }}
                </RouterLink>

                <!-- Estudiante -->
                <template v-if="authStore.isAuthenticated && !authStore.isTeacher">
                    <div class="w-px h-4 bg-white/15"></div>
                    <RouterLink to="/progreso"
                        class="px-3 py-1.5 rounded-lg text-sm font-semibold transition-all"
                        :class="isActive('/progreso')
                            ? 'bg-blue-500/20 text-blue-300 ring-1 ring-blue-500/30'
                            : 'text-blue-400/70 hover:text-blue-300 hover:bg-blue-500/10'">
                        {{ $t('nav.my_progress') }}
                    </RouterLink>
                </template>

                <!-- Profesor -->
                <template v-if="authStore.isTeacher">
                    <div class="w-px h-4 bg-white/15"></div>
                    <RouterLink to="/exercises/create"
                        class="px-3 py-1.5 rounded-lg text-sm font-semibold transition-all"
                        :class="isActive('/exercises/create')
                            ? 'bg-blue-500/20 text-blue-300 ring-1 ring-blue-500/30'
                            : 'text-blue-400/70 hover:text-blue-300 hover:bg-blue-500/10'">
                        {{ $t('nav.create_challenge') }}
                    </RouterLink>
                    <RouterLink to="/mis-retos"
                        class="px-3 py-1.5 rounded-lg text-sm font-semibold transition-all"
                        :class="isActive('/mis-retos')
                            ? 'bg-blue-500/20 text-blue-300 ring-1 ring-blue-500/30'
                            : 'text-blue-400/70 hover:text-blue-300 hover:bg-blue-500/10'">
                        {{ $t('nav.my_challenges') }}
                    </RouterLink>
                    <RouterLink to="/estadisticas"
                        class="px-3 py-1.5 rounded-lg text-sm font-semibold transition-all"
                        :class="isActive('/estadisticas')
                            ? 'bg-blue-500/20 text-blue-300 ring-1 ring-blue-500/30'
                            : 'text-blue-400/70 hover:text-blue-300 hover:bg-blue-500/10'">
                        {{ $t('nav.statistics') }}
                    </RouterLink>
                </template>
            </div>

            <!-- Desktop right: language + auth -->
            <div class="hidden md:flex items-center gap-3">
                <!-- Selector de idioma -->
                <div class="relative" ref="langDropdownRef">
                    <button @click="showLangDropdown = !showLangDropdown"
                        class="p-1.5 rounded-lg text-white/40 hover:text-white/70 hover:bg-white/5 transition-colors">
                        <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" class="h-5 w-5" fill="currentColor" aria-hidden="true">
                            <path d="M0 0h24v24H0z" fill="none"/>
                            <path d="M12.87 15.07l-2.54-2.51.03-.03c1.74-1.94 2.98-4.17 3.71-6.53H17V4h-7V2H8v2H1v1.99h11.17C11.5 7.92 10.44 9.75 9 11.35 8.07 10.32 7.3 9.19 6.69 8h-2c.73 1.63 1.73 3.17 2.98 4.56l-5.09 5.02L4 19l5-5 3.11 3.11.76-2.04zM18.5 10h-2L12 22h2l1.12-3h4.75L21 22h2l-4.5-12zm-2.62 7l1.62-4.33L19.12 17h-3.24z"/>
                        </svg>
                    </button>
                    <div v-if="showLangDropdown"
                        class="absolute right-0 mt-2 w-40 rounded-lg border border-white/10 bg-[#0b1326]/95 py-1 shadow-xl backdrop-blur-sm z-50">
                        <p class="px-3 py-1.5 text-[10px] font-semibold text-white/30 uppercase tracking-widest">{{ $t('nav.language') }}</p>
                        <button v-for="lang in LANGUAGES" :key="lang.code" @click="setLocale(lang.code)"
                            :class="[
                                'flex items-center gap-2.5 w-full px-3 py-2 text-sm transition-colors',
                                currentLocale === lang.code
                                    ? 'text-blue-300 bg-blue-500/10'
                                    : 'text-white/50 hover:text-white/80 hover:bg-white/5'
                            ]">
                            <span>{{ lang.flag }}</span>
                            <span>{{ lang.label }}</span>
                            <svg v-if="currentLocale === lang.code" class="ml-auto h-3 w-3 text-blue-400" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3">
                                <polyline points="20 6 9 17 4 12"/>
                            </svg>
                        </button>
                    </div>
                </div>

                <!-- No autenticado -->
                <div v-if="!authStore.isAuthenticated" class="flex items-center gap-4">
                    <RouterLink to="/login"
                        class="border border-white/10 px-4 py-2 text-sm text-title transition-colors hover:border-white/20">
                        {{ $t('nav.login') }}
                    </RouterLink>
                    <RouterLink to="/register"
                        class="bg-title px-6 py-2 text-sm font-bold text-base transition-colors hover:bg-blue-50">
                        {{ $t('nav.signup') }}
                    </RouterLink>
                </div>

                <!-- Autenticado -->
                <div v-else class="relative" ref="dropdownRef">
                    <button @click="showDropdown = !showDropdown"
                        class="flex items-center gap-3 rounded px-2 py-1 transition-colors hover:bg-white/5">
                        <UserAvatar :src="authStore.user?.avatar_url" :name="authStore.user?.name ?? ''" size="lg" />
                        <div class="text-left leading-tight">
                            <p class="text-sm font-semibold text-title">{{ authStore.user?.name }}</p>
                            <p class="text-xs font-medium uppercase tracking-wide text-accent">{{ authStore.user?.role }}</p>
                        </div>
                    </button>
                    <div v-if="showDropdown"
                        class="absolute right-0 mt-2 w-48 rounded border border-white/10 bg-surface/95 py-1 shadow-lg backdrop-blur-sm">
                        <RouterLink to="/profile" @click="showDropdown = false"
                            class="flex items-center gap-2 px-4 py-2 text-sm text-title transition-colors hover:bg-white/5">
                            <svg class="h-4 w-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" aria-hidden="true">
                                <circle cx="12" cy="8" r="4" />
                                <path d="M4 20c0-4 3.6-7 8-7s8 3 8 7" />
                            </svg>
                            {{ $t('nav.my_profile') }}
                        </RouterLink>
                        <button @click="handleLogout"
                            class="flex w-full items-center gap-2 px-4 py-2 text-sm text-red-400 transition-colors hover:bg-red-500/10">
                            <svg class="h-4 w-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" aria-hidden="true">
                                <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4" />
                                <polyline points="16 17 21 12 16 7" />
                                <line x1="21" y1="12" x2="9" y2="12" />
                            </svg>
                            {{ $t('nav.logout') }}
                        </button>
                    </div>
                </div>
            </div>

            <!-- Mobile right: language + hamburger -->
            <div class="flex md:hidden items-center gap-2">
                <!-- Selector de idioma mobile -->
                <div class="relative" ref="langDropdownMobileRef">
                    <button @click="showLangDropdownMobile = !showLangDropdownMobile"
                        class="p-2 rounded-lg text-white/40 hover:text-white/70 hover:bg-white/5 transition-colors">
                        <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" class="h-5 w-5" fill="currentColor" aria-hidden="true">
                            <path d="M0 0h24v24H0z" fill="none"/>
                            <path d="M12.87 15.07l-2.54-2.51.03-.03c1.74-1.94 2.98-4.17 3.71-6.53H17V4h-7V2H8v2H1v1.99h11.17C11.5 7.92 10.44 9.75 9 11.35 8.07 10.32 7.3 9.19 6.69 8h-2c.73 1.63 1.73 3.17 2.98 4.56l-5.09 5.02L4 19l5-5 3.11 3.11.76-2.04zM18.5 10h-2L12 22h2l1.12-3h4.75L21 22h2l-4.5-12zm-2.62 7l1.62-4.33L19.12 17h-3.24z"/>
                        </svg>
                    </button>
                    <div v-if="showLangDropdownMobile"
                        class="absolute right-0 mt-2 w-40 rounded-lg border border-white/10 bg-[#0b1326]/95 py-1 shadow-xl backdrop-blur-sm z-50">
                        <p class="px-3 py-1.5 text-[10px] font-semibold text-white/30 uppercase tracking-widest">{{ $t('nav.language') }}</p>
                        <button v-for="lang in LANGUAGES" :key="lang.code" @click="setLocale(lang.code); showLangDropdownMobile = false"
                            :class="[
                                'flex items-center gap-2.5 w-full px-3 py-2 text-sm transition-colors',
                                currentLocale === lang.code
                                    ? 'text-blue-300 bg-blue-500/10'
                                    : 'text-white/50 hover:text-white/80 hover:bg-white/5'
                            ]">
                            <span>{{ lang.flag }}</span>
                            <span>{{ lang.label }}</span>
                            <svg v-if="currentLocale === lang.code" class="ml-auto h-3 w-3 text-blue-400" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3">
                                <polyline points="20 6 9 17 4 12"/>
                            </svg>
                        </button>
                    </div>
                </div>

                <!-- Hamburger -->
                <button @click="mobileMenuOpen = !mobileMenuOpen"
                    class="p-2 rounded-lg text-white/50 hover:text-white/80 hover:bg-white/5 transition-colors">
                    <svg v-if="!mobileMenuOpen" class="h-5 w-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round">
                        <line x1="3" y1="6" x2="21" y2="6"/><line x1="3" y1="12" x2="21" y2="12"/><line x1="3" y1="18" x2="21" y2="18"/>
                    </svg>
                    <svg v-else class="h-5 w-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round">
                        <line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/>
                    </svg>
                </button>
            </div>

        </div>

        <!-- Mobile menu -->
        <Transition
            enter-active-class="transition-all duration-200 ease-out"
            enter-from-class="opacity-0 -translate-y-2"
            enter-to-class="opacity-100 translate-y-0"
            leave-active-class="transition-all duration-150 ease-in"
            leave-from-class="opacity-100 translate-y-0"
            leave-to-class="opacity-0 -translate-y-2">
            <div v-if="mobileMenuOpen" class="md:hidden border-t border-white/5 bg-[#0b1326]/98 px-4 pb-4 pt-3">

                <!-- Nav links -->
                <div class="flex flex-col gap-1 mb-4">
                    <RouterLink v-if="!authStore.isAuthenticated" to="/" @click="mobileMenuOpen = false"
                        class="px-3 py-2.5 rounded-lg text-sm transition-colors"
                        :class="isActive('/') ? 'text-accent bg-white/5' : 'text-body hover:text-title hover:bg-white/4'">
                        {{ $t('nav.home') }}
                    </RouterLink>
                    <RouterLink to="/challenges" @click="mobileMenuOpen = false"
                        class="px-3 py-2.5 rounded-lg text-sm transition-colors"
                        :class="isActive('/challenges') ? 'text-accent bg-white/5' : 'text-body hover:text-title hover:bg-white/4'">
                        {{ $t('nav.challenges') }}
                    </RouterLink>
                    <RouterLink to="/ranking" @click="mobileMenuOpen = false"
                        class="px-3 py-2.5 rounded-lg text-sm transition-colors"
                        :class="isActive('/ranking') ? 'text-accent bg-white/5' : 'text-body hover:text-title hover:bg-white/4'">
                        {{ $t('nav.ranking') }}
                    </RouterLink>
                    <RouterLink to="/tournaments" @click="mobileMenuOpen = false"
                        class="px-3 py-2.5 rounded-lg text-sm transition-colors"
                        :class="isActive('/tournaments') ? 'text-accent bg-white/5' : 'text-body hover:text-title hover:bg-white/4'">
                        {{ $t('nav.tournaments') }}
                    </RouterLink>
                    <RouterLink v-if="!authStore.isAuthenticated" to="/about" @click="mobileMenuOpen = false"
                        class="px-3 py-2.5 rounded-lg text-sm transition-colors"
                        :class="isActive('/about') ? 'text-accent bg-white/5' : 'text-body hover:text-title hover:bg-white/4'">
                        {{ $t('nav.about') }}
                    </RouterLink>

                    <!-- Estudiante -->
                    <template v-if="authStore.isAuthenticated && !authStore.isTeacher">
                        <div class="h-px bg-white/8 my-1"></div>
                        <RouterLink to="/progreso" @click="mobileMenuOpen = false"
                            class="px-3 py-2.5 rounded-lg text-sm font-semibold transition-all"
                            :class="isActive('/progreso') ? 'bg-blue-500/20 text-blue-300' : 'text-blue-400/70 hover:text-blue-300 hover:bg-blue-500/10'">
                            {{ $t('nav.my_progress') }}
                        </RouterLink>
                    </template>

                    <!-- Profesor -->
                    <template v-if="authStore.isTeacher">
                        <div class="h-px bg-white/8 my-1"></div>
                        <RouterLink to="/exercises/create" @click="mobileMenuOpen = false"
                            class="px-3 py-2.5 rounded-lg text-sm font-semibold transition-all"
                            :class="isActive('/exercises/create') ? 'bg-blue-500/20 text-blue-300' : 'text-blue-400/70 hover:text-blue-300 hover:bg-blue-500/10'">
                            {{ $t('nav.create_challenge') }}
                        </RouterLink>
                        <RouterLink to="/mis-retos" @click="mobileMenuOpen = false"
                            class="px-3 py-2.5 rounded-lg text-sm font-semibold transition-all"
                            :class="isActive('/mis-retos') ? 'bg-blue-500/20 text-blue-300' : 'text-blue-400/70 hover:text-blue-300 hover:bg-blue-500/10'">
                            {{ $t('nav.my_challenges') }}
                        </RouterLink>
                        <RouterLink to="/estadisticas" @click="mobileMenuOpen = false"
                            class="px-3 py-2.5 rounded-lg text-sm font-semibold transition-all"
                            :class="isActive('/estadisticas') ? 'bg-blue-500/20 text-blue-300' : 'text-blue-400/70 hover:text-blue-300 hover:bg-blue-500/10'">
                            {{ $t('nav.statistics') }}
                        </RouterLink>
                    </template>
                </div>

                <!-- Auth section -->
                <div class="border-t border-white/8 pt-3">
                    <!-- No autenticado -->
                    <div v-if="!authStore.isAuthenticated" class="flex flex-col gap-2">
                        <RouterLink to="/login" @click="mobileMenuOpen = false"
                            class="w-full text-center border border-white/10 px-4 py-2.5 text-sm text-title transition-colors hover:border-white/20 rounded-lg">
                            {{ $t('nav.login') }}
                        </RouterLink>
                        <RouterLink to="/register" @click="mobileMenuOpen = false"
                            class="w-full text-center bg-title px-4 py-2.5 text-sm font-bold text-base transition-colors hover:bg-blue-50 rounded-lg">
                            {{ $t('nav.signup') }}
                        </RouterLink>
                    </div>

                    <!-- Autenticado -->
                    <div v-else class="flex flex-col gap-1">
                        <div class="flex items-center gap-3 px-3 py-2 mb-1">
                            <UserAvatar :src="authStore.user?.avatar_url" :name="authStore.user?.name ?? ''" size="lg" />
                            <div class="text-left leading-tight">
                                <p class="text-sm font-semibold text-title">{{ authStore.user?.name }}</p>
                                <p class="text-xs font-medium uppercase tracking-wide text-accent">{{ authStore.user?.role }}</p>
                            </div>
                        </div>
                        <RouterLink to="/profile" @click="mobileMenuOpen = false"
                            class="flex items-center gap-2 px-3 py-2.5 rounded-lg text-sm text-title transition-colors hover:bg-white/5">
                            <svg class="h-4 w-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" aria-hidden="true">
                                <circle cx="12" cy="8" r="4" />
                                <path d="M4 20c0-4 3.6-7 8-7s8 3 8 7" />
                            </svg>
                            {{ $t('nav.my_profile') }}
                        </RouterLink>
                        <button @click="handleLogout"
                            class="flex items-center gap-2 px-3 py-2.5 rounded-lg text-sm text-red-400 transition-colors hover:bg-red-500/10 w-full">
                            <svg class="h-4 w-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" aria-hidden="true">
                                <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4" />
                                <polyline points="16 17 21 12 16 7" />
                                <line x1="21" y1="12" x2="9" y2="12" />
                            </svg>
                            {{ $t('nav.logout') }}
                        </button>
                    </div>
                </div>

            </div>
        </Transition>
    </nav>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { useI18n } from 'vue-i18n'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { useClickOutside } from '@/composables/useClickOutside'
import UserAvatar from '@/components/UserAvatar.vue'

const { locale } = useI18n()
const authStore = useAuthStore()

const LANGUAGES = [
    { code: 'es', label: 'Español', flag: '🇪🇸' },
    { code: 'en', label: 'English', flag: '🇬🇧' },
]

const currentLocale    = computed(() => locale.value)
const showLangDropdown = ref(false)
const langDropdownRef  = ref(null)
useClickOutside(langDropdownRef, () => showLangDropdown.value = false)

const showLangDropdownMobile = ref(false)
const langDropdownMobileRef  = ref(null)
useClickOutside(langDropdownMobileRef, () => showLangDropdownMobile.value = false)

const mobileMenuOpen = ref(false)

function setLocale(code) {
    locale.value = code
    localStorage.setItem('locale', code)
    showLangDropdown.value = false
}

const route  = useRoute()
const router = useRouter()

const isActive = (path) => path === '/' ? route.path === path : route.path.startsWith(path)

// Cierra mobile menu al navegar
watch(() => route.path, () => { mobileMenuOpen.value = false })

const showDropdown = ref(false)
const dropdownRef  = ref(null)
useClickOutside(dropdownRef, () => showDropdown.value = false)

async function handleLogout() {
    try {
        await authStore.logout()
    } catch (error) {
        console.error('Error during logout:', error)
    } finally {
        showDropdown.value   = false
        mobileMenuOpen.value = false
        router.push('/')
    }
}
</script>

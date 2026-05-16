<template>
    <div class="min-h-full bg-hero-radial">

        <!-- El Creador -->
        <section class="px-8 pt-20 pb-16">
            <div class="max-w-2xl mx-auto border border-white/5 bg-surface p-10 flex flex-col sm:flex-row gap-8 items-center sm:items-start">
                <div class="shrink-0 w-16 h-16 rounded-full bg-linear-to-br from-primary to-accent flex items-center justify-center text-xl font-bold text-white shadow-lg shadow-primary/20">
                    RR
                </div>
                <div class="flex-1 text-center sm:text-left">
                    <h2 class="text-title text-xl font-bold mb-1">{{ $t('about.creator_name') }}</h2>
                    <p class="text-accent text-xs font-semibold uppercase tracking-[3px] mb-4">{{ $t('about.creator_role') }}</p>
                    <p class="text-body text-sm leading-relaxed">{{ $t('about.creator_bio') }}</p>
                </div>
            </div>
        </section>

        <!-- Stack -->
        <section class="px-8 pb-24">
            <div class="max-w-7xl mx-auto">
                <h2 class="text-2xl font-semibold text-title text-center mb-2">{{ $t('about.stack_title') }}</h2>
                <p class="text-center text-muted text-xs uppercase tracking-[3px] mb-16">{{ $t('about.stack_subtitle') }}</p>

                <div class="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-6 gap-px bg-white/5">
                    <div v-for="tech in stack" :key="tech.name"
                         class="bg-base p-8 text-center hover:bg-surface transition-colors duration-300">
                        <img :src="tech.icon" :alt="tech.name" class="w-10 h-10 mb-4 mx-auto object-contain" />
                        <p class="text-title text-sm font-semibold">{{ tech.name }}</p>
                        <p class="text-muted text-xs mt-1">{{ $t(tech.roleKey) }}</p>
                    </div>
                </div>
            </div>
        </section>

        <!-- Footer -->
        <section class="px-8 pb-20">
            <div class="max-w-7xl mx-auto text-center">
                <div class="h-px bg-linear-to-r from-transparent via-white/10 to-transparent mb-12" />
                <p class="text-muted/50 text-xs">{{ $t('about.footer_rights') }}</p>
            </div>
        </section>

    </div>
</template>

<script setup>
const stack = [
    { icon: '/icons/vue.svg',     name: 'Vue 3',      roleKey: 'about.role_frontend'  },
    { icon: '/icons/laravel.svg', name: 'Laravel 12', roleKey: 'about.role_backend'   },
    { icon: '/icons/kotlin.svg',  name: 'Kotlin',     roleKey: 'about.role_android'   },
    { icon: '/icons/mysql.svg',   name: 'MySQL',      roleKey: 'about.role_database'  },
    { icon: '/icons/docker.svg',  name: 'Docker',     roleKey: 'about.role_deploy'    },
    { icon: '/icons/judge0_logo.jpeg', name: 'Judge0', roleKey: 'about.role_evaluator' },
]
</script>

<template>
    <div class="min-h-screen bg-hero-radial">

        <!-- Hero Section -->
        <section class="px-8 pt-32 pb-40">
            <div class="max-w-7xl mx-auto text-center">

                <!-- Título -->
                <div class="mb-6">
                    <h2 class="font-medium text-5xl md:text-6xl text-title tracking-tight">
                        CodeClass
                    </h2>
                </div>

                <!-- Subtítulo rotante -->
                <div class="mb-8">
                    <p class="text-2xl text-subtitle">
                        {{ $t('landing.typed_prefix') }}
                        <span class="relative inline-block">
                            <span class="bg-linear-to-r from-accent to-cyan-400 bg-clip-text text-transparent font-bold text-glow-blue border-b-2 border-accent/50">
                                <span ref="typedEl" />
                            </span>
                        </span>
                    </p>
                </div>

                <!-- Descripción -->
                <p class="font-light text-xl text-body max-w-3xl mx-auto mb-6 leading-relaxed">
                    {{ $t('landing.description') }}
                </p>

                <!-- Tagline -->
                <p class="text-lg text-accent/80 mb-12 italic">
                    {{ $t('landing.tagline') }}
                </p>

                <!-- CTA Buttons -->
                <div class="flex flex-wrap gap-6 justify-center">
                    <RouterLink
                        to="/register"
                        class="px-16 py-5 bg-primary text-title font-bold text-xs uppercase tracking-[3px] hover:bg-blue-600 transition-all shadow-lg hover:shadow-primary/50"
                    >
                        {{ $t('landing.cta_register') }}
                    </RouterLink>
                    <RouterLink
                        to="/challenges"
                        class="px-16 py-5 border border-white/10 text-title font-bold text-xs uppercase tracking-[3px] hover:border-white/20 transition-all"
                    >
                        {{ $t('landing.cta_challenges') }}
                    </RouterLink>
                </div>

            </div>
        </section>

    </div>
</template>


<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue'
import { useI18n } from 'vue-i18n'
import Typed from 'typed.js'

const { tm } = useI18n()
const typedEl = ref(null)
let typedInstance

onMounted(() => {
    typedInstance = new Typed(typedEl.value, {
        strings: tm('landing.typed_strings'),
        typeSpeed: 80,
        backSpeed: 50,
        backDelay: 1500,
        startDelay: 400,
        loop: true,
        cursorChar: '|',
    })
})

onBeforeUnmount(() => typedInstance?.destroy())
</script>

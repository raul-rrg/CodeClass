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
                        Pensado para
                        <span class="relative inline-block">
                            <span class="bg-linear-to-r from-accent to-cyan-400 bg-clip-text text-transparent font-bold text-glow-blue border-b-2 border-accent/50">
                                <span ref="typedEl" />
                            </span>
                        </span>
                    </p>
                </div>

                <!-- Descripción -->
                <p class="font-light text-xl text-body max-w-3xl mx-auto mb-6 leading-relaxed">
                    La plataforma donde profesores crean retos de programación
                    <br />
                    y estudiantes los resuelven con corrección automática al instante.
                </p>

                <!-- Tagline -->
                <p class="text-lg text-accent/80 mb-12 italic">
                    Diseñado para enseñar. Construido para aprender.
                </p>

                <!-- CTA Buttons -->
                <div class="flex flex-wrap gap-6 justify-center">
                    <RouterLink
                        to="/register"
                        class="px-16 py-5 bg-primary text-title font-bold text-xs uppercase tracking-[3px] hover:bg-blue-600 transition-all shadow-lg hover:shadow-primary/50"
                    >
                        Empezar gratis
                    </RouterLink>
                    <RouterLink
                        to="/challenges"
                        class="px-16 py-5 border border-white/10 text-title font-bold text-xs uppercase tracking-[3px] hover:border-white/20 transition-all"
                    >
                        Ver ejercicios
                    </RouterLink>
                </div>

            </div>
        </section>

    </div>
</template>


<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue'
import Typed from 'typed.js'

const typedEl = ref(null)
let typedInstance

onMounted(() => {
    typedInstance = new Typed(typedEl.value, {
        strings: ['estudiantes', 'profesores', 'futuros devs', 'coders', 'programadores'],
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

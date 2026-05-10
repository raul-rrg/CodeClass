<template>
    <img v-if="src" :src="src" :class="sizeClass"
        class="rounded-full object-cover shrink-0" :alt="name" />
    <div v-else :class="[sizeClass, textClass]"
        class="rounded-full bg-blue-500/20 flex items-center justify-center font-bold text-blue-300 shrink-0">
        {{ initials }}
    </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
    src:  { type: String,  default: null },
    name: { type: String,  default: '' },
    size: { type: String,  default: 'md' }, // sm | md | lg | xl
})

const SIZES = {
    sm: { box: 'w-5 h-5',   text: 'text-[9px]' },
    md: { box: 'w-7 h-7',   text: 'text-xs'    },
    lg: { box: 'w-8 h-8',   text: 'text-sm'    },
    xl: { box: 'w-20 h-20', text: 'text-3xl'   },
}

const sizeClass = computed(() => SIZES[props.size]?.box ?? SIZES.md.box)
const textClass = computed(() => SIZES[props.size]?.text ?? SIZES.md.text)

const initials = computed(() => {
    if (!props.name) return '?'
    return props.name.split(' ').map(w => w[0]).slice(0, 2).join('').toUpperCase()
})
</script>

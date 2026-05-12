<template>
    <div class="flex flex-col gap-1.5">
        <div v-if="!rows.length" class="text-center py-8 text-white/25 text-sm">
            Nadie ha resuelto retos todavía
        </div>

        <div v-for="row in rows" :key="row.user_id"
            :class="[
                'flex items-center gap-3 px-4 py-2.5 rounded-xl border transition-colors',
                row.position === 1 ? 'bg-yellow-500/8 border-yellow-500/20' :
                row.position === 2 ? 'bg-white/4 border-white/10' :
                row.position === 3 ? 'bg-orange-500/5 border-orange-500/15' :
                                     'bg-white/2 border-white/6'
            ]">

            <div class="w-6 text-center shrink-0">
                <span v-if="row.position === 1">🥇</span>
                <span v-else-if="row.position === 2">🥈</span>
                <span v-else-if="row.position === 3">🥉</span>
                <span v-else class="text-xs font-semibold text-white/25">{{ row.position }}</span>
            </div>

            <UserAvatar :src="row.avatar_url" :name="row.name" size="md" />

            <span class="text-sm font-semibold text-white/80 flex-1 truncate">{{ row.name }}</span>

            <span class="text-xs text-white/30 shrink-0">{{ row.solved }} retos</span>

            <span :class="[
                'text-sm font-bold shrink-0 w-14 text-right',
                row.position === 1 ? 'text-yellow-300' :
                row.position === 2 ? 'text-white/70' :
                row.position === 3 ? 'text-orange-300' : 'text-blue-300/80'
            ]">{{ row.total_points }} pt</span>
        </div>
    </div>
</template>

<script setup>
import UserAvatar from '@/components/UserAvatar.vue'

defineProps({
    rows: { type: Array, default: () => [] },
})
</script>

<template>
    <div class="min-h-[calc(100vh-80px)] bg-base px-8 py-10 overflow-auto">
        <div class="max-w-6xl mx-auto">

            <div v-if="!user.name" class="text-white/40 text-sm pt-20 text-center">{{ $t('common.loading') }}</div>

            <template v-else>

                <!-- ─── Hero header ───────────────────────────────────────── -->
                <div class="flex items-center gap-8 mb-12">
                    <!-- Avatar con glow + upload -->
                    <div class="relative shrink-0 group cursor-pointer" @click="avatarInputRef?.click()">
                        <div class="absolute inset-0 rounded-full bg-blue-500/8 blur-lg scale-110" />
                        <div class="relative w-20 h-20 rounded-full overflow-hidden border border-white/15">
                            <UserAvatar
                                :src="avatarPreview ?? user.avatar_url"
                                :name="user.name ?? ''"
                                size="xl"
                            />
                            <!-- Overlay hover -->
                            <div class="absolute inset-0 bg-black/50 flex flex-col items-center justify-center gap-1 opacity-0 group-hover:opacity-100 transition-opacity">
                                <svg v-if="!avatarUploading" class="w-5 h-5 text-white/80" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                    <path d="M23 19a2 2 0 0 1-2 2H3a2 2 0 0 1-2-2V8a2 2 0 0 1 2-2h4l2-3h6l2 3h4a2 2 0 0 1 2 2z"/>
                                    <circle cx="12" cy="13" r="4"/>
                                </svg>
                                <svg v-else class="w-5 h-5 text-white/80 animate-spin" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                    <path d="M21 12a9 9 0 1 1-6.219-8.56" stroke-linecap="round"/>
                                </svg>
                                <span class="text-[10px] text-white/70 font-medium">{{ avatarUploading ? $t('profile.uploading') : $t('profile.change_avatar') }}</span>
                            </div>
                        </div>
                        <input ref="avatarInputRef" type="file" accept=".jpg,.jpeg,.png,.webp"
                            class="hidden" @change="onAvatarSelected" />
                    </div>

                    <div class="flex-1 min-w-0">
                        <h1 class="text-3xl font-bold text-white mb-1 truncate">{{ user.name }}</h1>
                        <p class="text-white/40 text-sm truncate mb-3">{{ user.email }}</p>
                        <div class="flex items-center gap-3 flex-wrap">
                            <span :class="['px-2.5 py-0.5 rounded-full text-xs font-semibold capitalize', roleClass]">
                                {{ user.role }}
                            </span>
                            <span class="text-white/20 text-xs">·</span>
                            <span class="text-white/35 text-xs">{{ user.points ?? 0 }} {{ $t('profile.points') }}</span>
                            <span class="text-white/20 text-xs">·</span>
                            <span class="text-white/35 text-xs">{{ $t('profile.member_since') }} {{ memberSince }}</span>
                        </div>
                    </div>
                </div>

                <!-- ─── Contenido dos columnas ─────────────────────────── -->
                <div class="grid grid-cols-1 lg:grid-cols-[1fr_380px] gap-5 items-start">

                    <!-- Columna izquierda -->
                    <div class="flex flex-col gap-4">

                        <!-- Cambiar nombre -->
                        <div class="rounded-xl border border-white/7 bg-white/3 backdrop-blur-sm p-6">
                            <h2 class="text-[15px] font-semibold text-white mb-1">{{ $t('profile.change_name_title') }}</h2>
                        <p class="text-xs text-white/35 mb-4">{{ $t('profile.change_name_hint') }}</p>
                            <form @submit.prevent="saveName" class="flex gap-3">
                                <input
                                    v-model="nameForm.name"
                                    type="text"
                                    class="flex-1 bg-white/5 border border-white/8 rounded-lg px-4 py-2.5 text-sm text-white placeholder-white/20 focus:outline-none focus:border-white/20 transition-colors"
                                    :placeholder="user.name"
                                />
                                <button
                                    type="submit"
                                    :disabled="nameForm.loading || !nameForm.name.trim()"
                                    class="px-5 py-2.5 rounded-lg bg-blue-500/15 hover:bg-blue-500/25 border border-blue-500/20 hover:border-blue-500/35 text-blue-300/80 hover:text-blue-300 text-sm font-medium transition-all disabled:opacity-40 disabled:cursor-not-allowed">
                                    {{ nameForm.loading ? $t('common.saving') : $t('common.save') }}
                                </button>
                            </form>
                            <p v-if="nameForm.success" class="text-xs text-green-400/80 mt-2">{{ $t('profile.name_updated') }}</p>
                            <p v-if="nameForm.error"   class="text-xs text-red-400/80 mt-2">{{ nameForm.error }}</p>
                        </div>

                        <!-- Cambiar contraseña -->
                        <div class="rounded-xl border border-white/7 bg-white/3 backdrop-blur-sm p-6">
                            <h2 class="text-[15px] font-semibold text-white mb-1">{{ $t('profile.change_password_title') }}</h2>
                        <p class="text-xs text-white/35 mb-4">{{ $t('profile.change_password_hint') }}</p>
                            <form @submit.prevent="savePassword" class="flex flex-col gap-3">
                                <input
                                    v-model="passwordForm.current_password"
                                    type="password"
                                    :placeholder="$t('profile.current_password')"
                                    class="bg-white/5 border border-white/8 rounded-lg px-4 py-2.5 text-sm text-white placeholder-white/20 focus:outline-none focus:border-white/20 transition-colors"
                                />
                                <input
                                    v-model="passwordForm.password"
                                    type="password"
                                    :placeholder="$t('profile.new_password')"
                                    class="bg-white/5 border border-white/8 rounded-lg px-4 py-2.5 text-sm text-white placeholder-white/20 focus:outline-none focus:border-white/20 transition-colors"
                                />
                                <input
                                    v-model="passwordForm.password_confirmation"
                                    type="password"
                                    :placeholder="$t('profile.confirm_new_password')"
                                    class="bg-white/5 border border-white/8 rounded-lg px-4 py-2.5 text-sm text-white placeholder-white/20 focus:outline-none focus:border-white/20 transition-colors"
                                />
                                <button
                                    type="submit"
                                    :disabled="passwordForm.loading || !canSubmitPassword"
                                    class="self-start px-5 py-2.5 rounded-lg bg-blue-500/15 hover:bg-blue-500/25 border border-blue-500/20 hover:border-blue-500/35 text-blue-300/80 hover:text-blue-300 text-sm font-medium transition-all disabled:opacity-40 disabled:cursor-not-allowed">
                                    {{ passwordForm.loading ? $t('common.saving') : $t('profile.change_password_btn') }}
                                </button>
                            </form>
                            <p v-if="passwordForm.success" class="text-xs text-green-400/80 mt-2">{{ $t('profile.password_updated') }}</p>
                            <p v-if="passwordForm.error"   class="text-xs text-red-400/80 mt-2">{{ passwordForm.error }}</p>
                        </div>

                    </div>

                    <!-- Columna derecha: editor -->
                    <div class="rounded-xl border border-white/7 bg-white/3 backdrop-blur-sm overflow-hidden sticky top-6">

                        <div class="px-5 py-4 border-b border-white/6">
                            <h2 class="text-xs font-semibold text-white/40 uppercase tracking-widest">{{ $t('profile.editor_section') }}</h2>
                        </div>

                        <!-- Preview Monaco -->
                        <div class="h-44 w-full">
                            <VueMonacoEditor
                                v-model:value="previewCode"
                                :theme="previewTheme"
                                language="python"
                                :options="previewOptions"
                            />
                        </div>

                        <div class="px-5 py-5 flex flex-col gap-5 border-t border-white/6">

                            <!-- Tema -->
                            <div>
                                <p class="text-[10px] font-semibold text-white/30 uppercase tracking-widest mb-2.5">{{ $t('profile.theme_label') }}</p>
                                <div class="flex flex-wrap gap-1.5">
                                    <button
                                        v-for="t in THEMES" :key="t.id"
                                        @click="selectTheme(t.id)"
                                        :class="[
                                            'px-2.5 py-1 rounded-md text-xs font-medium border transition-all',
                                            prefs.theme === t.id
                                                ? 'bg-blue-500/15 border-blue-500/30 text-blue-300'
                                                : 'bg-white/3 border-white/7 text-white/35 hover:text-white/60 hover:border-white/15'
                                        ]">
                                        {{ t.label }}
                                    </button>
                                </div>
                            </div>

                            <!-- Fuente -->
                            <div>
                                <p class="text-[10px] font-semibold text-white/30 uppercase tracking-widest mb-2.5">{{ $t('profile.font_label') }}</p>
                                <div class="flex flex-col gap-1.5">
                                    <button
                                        v-for="f in FONTS" :key="f.id"
                                        @click="prefs.setFont(f.id)"
                                        :style="{ fontFamily: f.id }"
                                        :class="[
                                            'w-full text-left px-3 py-2 rounded-lg text-xs border transition-all',
                                            prefs.font === f.id
                                                ? 'bg-blue-500/15 border-blue-500/30 text-blue-300'
                                                : 'bg-white/3 border-white/7 text-white/35 hover:text-white/60 hover:border-white/15'
                                        ]">
                                        {{ f.label }}
                                    </button>
                                </div>
                            </div>

                        </div>
                    </div>

                </div>
            </template>
        </div>
    </div>
</template>

<script setup>
import { ref, computed, reactive } from 'vue'
import { VueMonacoEditor } from '@guolao/vue-monaco-editor'
import { useAuthStore } from '@/stores/auth'
import { useEditorPreferences, THEMES, FONTS } from '@/stores/editorPreferences'
import { registerTheme } from '@/composables/useEditor'
import { api } from '@/services/api'
import UserAvatar from '@/components/UserAvatar.vue'

const auth  = useAuthStore()
const prefs = useEditorPreferences()
const user  = computed(() => auth.user ?? {})

// ── Editor preview ────────────────────────────────────────────────
const previewCode = ref(
`def two_sum(nums, target):
    seen = {}
    for i, n in enumerate(nums):
        if target - n in seen:
            return [seen[target - n], i]
        seen[n] = i`)

const previewTheme = ref('vs-dark')

const previewOptions = computed(() => ({
    readOnly:             true,
    fontSize:             12,
    fontFamily:           prefs.font,
    minimap:              { enabled: false },
    scrollBeyondLastLine: false,
    lineNumbers:          'off',
    padding:              { top: 14 },
    renderLineHighlight:  'none',
    scrollbar:            { vertical: 'hidden', horizontal: 'hidden' },
}))

async function selectTheme(themeId) {
    prefs.setTheme(themeId)
    try {
        previewTheme.value = await registerTheme(themeId)
    } catch {
        previewTheme.value = 'vs-dark'
    }
}

selectTheme(prefs.theme)

// ── Avatar upload ─────────────────────────────────────────────────
const avatarInputRef  = ref(null)
const avatarPreview   = ref(null)
const avatarUploading = ref(false)

async function onAvatarSelected(e) {
    const file = e.target.files?.[0]
    if (!file) return

    avatarPreview.value   = URL.createObjectURL(file)
    avatarUploading.value = true

    try {
        const formData = new FormData()
        formData.append('avatar', file)
        const updated = await api.upload('/users/me/avatar', formData)
        auth.user = updated
        avatarPreview.value = null
    } catch {
        avatarPreview.value = null
    } finally {
        avatarUploading.value = false
        e.target.value = ''
    }
}

// ── Perfil ────────────────────────────────────────────────────────

const roleClass = computed(() =>
    user.value.role === 'teacher'
        ? 'bg-purple-500/15 text-purple-400'
        : 'bg-blue-500/15 text-blue-400'
)

const memberSince = computed(() => {
    if (!user.value.created_at) return '—'
    return new Date(user.value.created_at).toLocaleDateString('es-ES', { year: 'numeric', month: 'short' })
})

// ── Formulario nombre ─────────────────────────────────────────────
const nameForm = reactive({ name: '', loading: false, success: false, error: '' })

async function saveName() {
    nameForm.loading = true
    nameForm.success = false
    nameForm.error   = ''
    try {
        const updated = await api.put('/users/me', { name: nameForm.name.trim() })
        auth.user        = updated
        nameForm.name    = ''
        nameForm.success = true
    } catch (e) {
        nameForm.error = e?.message ?? 'Error al guardar.'
    } finally {
        nameForm.loading = false
    }
}

// ── Formulario contraseña ─────────────────────────────────────────
const passwordForm = reactive({
    current_password: '', password: '', password_confirmation: '',
    loading: false, success: false, error: '',
})

const canSubmitPassword = computed(() =>
    passwordForm.current_password && passwordForm.password && passwordForm.password_confirmation
)

async function savePassword() {
    passwordForm.success = false
    passwordForm.error   = ''
    passwordForm.loading = true
    try {
        await api.put('/users/me', {
            current_password:      passwordForm.current_password,
            password:              passwordForm.password,
            password_confirmation: passwordForm.password_confirmation,
        })
        passwordForm.current_password      = ''
        passwordForm.password              = ''
        passwordForm.password_confirmation = ''
        passwordForm.success = true
    } catch (e) {
        passwordForm.error = e?.errors?.current_password?.[0] ?? e?.message ?? 'Error al guardar.'
    } finally {
        passwordForm.loading = false
    }
}
</script>

import { ref, computed, watch } from 'vue'
import { loader } from '@guolao/vue-monaco-editor'
import { LANGUAGES } from '@/constants/languages'
import { useEditorPreferences } from '@/stores/editorPreferences'

import draculaData           from 'monaco-themes/themes/Dracula.json'
import nightOwlData          from 'monaco-themes/themes/Night Owl.json'
import nordData              from 'monaco-themes/themes/Nord.json'
import monokaiData           from 'monaco-themes/themes/Monokai.json'
import githubDarkData        from 'monaco-themes/themes/GitHub Dark.json'
import cobalt2Data           from 'monaco-themes/themes/Cobalt2.json'
import tomorrowNightData     from 'monaco-themes/themes/Tomorrow-Night-Eighties.json'
import solarizedDarkData     from 'monaco-themes/themes/Solarized-dark.json'


// Mapa themeId → JSON de definición para Monaco
const THEME_DATA = {
    'dracula':                 draculaData,
    'night-owl':               nightOwlData,
    'nord':                    nordData,
    'monokai':                 monokaiData,
    'github-dark':             githubDarkData,
    'cobalt2':                 cobalt2Data,
    'tomorrow-night-eighties': tomorrowNightData,
    'solarized-dark':          solarizedDarkData,
}

// Registra un tema custom en Monaco. Devuelve el themeId para asignarlo al editor.
export async function registerTheme(themeId) {
    const data = THEME_DATA[themeId]
    if (!data) return themeId
    const monaco = await loader.init()
    monaco.editor.defineTheme(themeId, data)
    return themeId
}

export function useEditor(challenge) {
    const prefs = useEditorPreferences()

    // ── Tema ──────────────────────────────────────────────────────────
    
    const editorTheme = ref('vs-dark')

    async function applyTheme(themeId) {
        try {
            editorTheme.value = await registerTheme(themeId)
        } catch {
            editorTheme.value = 'vs-dark' // fallback si el tema falla
        }
    }

    applyTheme(prefs.theme)
    watch(() => prefs.theme, applyTheme) // sincroniza con cambios en preferencias

    // ── Opciones del editor (reactivas a fuente) ───────────────────────

    // computed para que Monaco reaccione si el usuario cambia la fuente en preferencias
    const editorOptions = computed(() => ({
        fontSize:             14,
        fontFamily:           prefs.font,
        minimap:              { enabled: false },
        scrollBeyondLastLine: false,
        lineNumbers:          'on',
        renderLineHighlight:  'all',
        padding:              { top: 16 },
        automaticLayout:      true,
    }))

    // ── Lenguaje y código ─────────────────────────────────────────────

    const activeLang      = ref(LANGUAGES[0].id)
    const code            = ref('')
    const currentTemplate = ref('')
 
    // Obtener el template para un lenguaje dado
    function templateFor(langId) {
        return challenge?.value?.templates?.[langId]
            ?? LANGUAGES.find(l => l.id === langId)?.starter
            ?? ''
    }

    // Cuando se carga el reto o cambian las templates, actualiza el código al template del lenguaje activo
    watch(() => challenge?.value?.templates, () => {
        currentTemplate.value = templateFor(activeLang.value)
        code.value            = currentTemplate.value
    }, { immediate: true })

    // Cambia de lenguaje y resetea el código al template correspondiente
    function selectLang(lang) {
        activeLang.value      = lang.id
        currentTemplate.value = templateFor(lang.id)
        code.value            = currentTemplate.value
    }

    return { languages: LANGUAGES, activeLang, code, currentTemplate, selectLang, editorOptions, editorTheme }
}

import { defineStore } from 'pinia'
import { ref } from 'vue'

export const THEMES = [
    { id: 'dracula',                 file: 'Dracula',                  label: 'Dracula' },
    { id: 'night-owl',               file: 'Night Owl',                label: 'Night Owl' },
    { id: 'nord',                    file: 'Nord',                     label: 'Nord' },
    { id: 'monokai',                 file: 'Monokai',                  label: 'Monokai' },
    { id: 'github-dark',             file: 'GitHub Dark',              label: 'GitHub Dark' },
    { id: 'cobalt2',                 file: 'Cobalt2',                  label: 'Cobalt2' },
    { id: 'tomorrow-night-eighties', file: 'Tomorrow-Night-Eighties',  label: 'Tomorrow Night' },
    { id: 'solarized-dark',          file: 'Solarized-dark',           label: 'Solarized Dark' },
]

export const FONTS = [
    { id: 'JetBrains Mono, monospace', label: 'JetBrains Mono' },
    { id: 'Cascadia Code, monospace',  label: 'Cascadia Code' },
    { id: 'monospace',                 label: 'Monospace (sistema)' },
]

export const useEditorPreferences = defineStore('editorPreferences', () => {
    const theme = ref(localStorage.getItem('editor_theme') ?? 'dracula')
    const font  = ref(localStorage.getItem('editor_font')  ?? 'JetBrains Mono, monospace')

    function setTheme(t) { theme.value = t; localStorage.setItem('editor_theme', t) }
    function setFont(f)  { font.value  = f; localStorage.setItem('editor_font',  f) }

    return { theme, font, setTheme, setFont }
})

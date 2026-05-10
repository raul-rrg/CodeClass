<template>
    <div class="h-full bg-base flex flex-col overflow-hidden">

        <!-- Mobile tab bar -->
        <div class="md:hidden flex shrink-0 border-b border-white/8 bg-[#0b1326]/80">
            <button @click="mobileTab = 'description'"
                :class="['flex-1 py-2.5 text-xs font-semibold transition-colors border-b-2 -mb-px',
                         mobileTab === 'description'
                             ? 'border-blue-400 text-blue-300'
                             : 'border-transparent text-white/35 hover:text-white/60']">
                {{ $t('challenge.description_header') }}
            </button>
            <button @click="mobileTab = 'editor'"
                :class="['flex-1 py-2.5 text-xs font-semibold transition-colors border-b-2 -mb-px',
                         mobileTab === 'editor'
                             ? 'border-blue-400 text-blue-300'
                             : 'border-transparent text-white/35 hover:text-white/60']">
                Editor
            </button>
        </div>

        <!-- Panels -->
        <div class="flex-1 flex overflow-hidden min-h-0">

        <!-- ───── Panel izquierdo ───── -->
        <div :class="[
                'shrink-0 flex-col overflow-y-auto border-r transition-all scrollbar-thin',
                'w-full md:w-120',
                challenge.is_solved
                    ? 'border-white/5 bg-[linear-gradient(to_right,rgba(34,197,94,0.06)_0%,transparent_7%,transparent_93%,rgba(34,197,94,0.06)_100%),linear-gradient(to_bottom,rgba(34,197,94,0.06)_0%,transparent_7%,transparent_93%,rgba(34,197,94,0.06)_100%)]'
                    : 'border-white/5',
                mobileTab === 'editor' ? 'hidden md:flex' : 'flex'
            ]">

            <!-- Header -->
            <div class="p-6 pb-5 flex flex-col gap-4">

                <div class="flex items-center gap-3">
                    <h1 class="text-white font-bold text-2xl leading-tight flex-1">{{ challenge.title }}</h1>
                    <span v-if="challenge.is_solved"
                        class="shrink-0 flex items-center gap-1 px-2 py-0.5 rounded-md bg-green-500/15 text-green-400 text-[10px] font-bold ring-1 ring-green-500/30">
                        <svg class="w-3 h-3" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
                            <polyline points="20 6 9 17 4 12"/>
                        </svg>
                        {{ $t('challenge.solved_badge') }}
                    </span>
                    <RouterLink :to="{ name: 'challenge-submissions', params: { id } }"
                        class="shrink-0 flex items-center gap-1.5 px-3 py-1.5 rounded-lg text-xs font-semibold transition-all text-violet-300/80 bg-violet-500/10 border border-violet-500/20 hover:bg-violet-500/20 hover:text-violet-300 hover:border-violet-500/35">
                        <svg class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                            <line x1="18" y1="20" x2="18" y2="10"/><line x1="12" y1="20" x2="12" y2="4"/><line x1="6" y1="20" x2="6" y2="14"/>
                        </svg>
                        {{ $t('challenge.view_submissions') }}
                    </RouterLink>
                </div>

                <div class="flex items-center justify-between flex-wrap gap-2">
                    <div class="flex items-center gap-3">
                        <span :class="['text-xs font-semibold capitalize', difficultyTextClass]">
                            {{ challenge.difficulty }}
                        </span>
                        <span class="text-white/15">·</span>
                        <div class="flex items-center gap-1.5 text-blue-400/60 text-xs font-semibold">
                            <svg class="w-3 h-3" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                <path d="M6 9H4.5a2.5 2.5 0 0 1 0-5H6"/><path d="M18 9h1.5a2.5 2.5 0 0 0 0-5H18"/>
                                <path d="M4 22h16"/><path d="M10 14.66V17c0 .55-.47.98-.97 1.21C7.85 18.75 7 20.24 7 22"/>
                                <path d="M14 14.66V17c0 .55.47.98.97 1.21C16.15 18.75 17 20.24 17 22"/>
                                <path d="M18 2H6v7a6 6 0 0 0 12 0V2Z"/>
                            </svg>
                            {{ challengePoints }} pts
                        </div>
                        <span class="text-white/15">·</span>
                        <span class="text-xs text-white/35 capitalize">{{ challenge.category }}</span>
                    </div>
                    <span v-if="!challenge.is_published"
                        class="shrink-0 flex items-center gap-1 px-1.5 py-0.5 rounded-md bg-white/5 text-white/35 text-[10px] font-medium ring-1 ring-white/10">
                        <svg class="w-3 h-3" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                            <path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24"/>
                            <line x1="1" y1="1" x2="23" y2="23"/>
                        </svg>
                        {{ $t('challenge.hidden_badge') }}
                    </span>
                </div>

                <div class="flex items-center gap-2.5">
                    <div class="w-7 h-7 rounded-full bg-linear-to-br from-blue-500/25 to-violet-500/25 border border-white/10 flex items-center justify-center text-[11px] font-bold text-blue-300 shrink-0">
                        {{ initials(challenge.author) }}
                    </div>
                    <span class="text-white/50 text-xs font-medium">{{ challenge.author }}</span>
                </div>

            </div>

            <div class="h-px bg-white/5 mx-6" />

            <!-- Body -->
            <div class="p-6 flex flex-col gap-7 flex-1">

                <div>
                    <h2 class="text-white/25 text-[10px] font-bold uppercase tracking-[0.14em] mb-3">{{ $t('challenge.description_header') }}</h2>
                    <div class="prose prose-invert prose-sm max-w-none prose-code:text-blue-300 prose-code:before:content-none prose-code:after:content-none prose-pre:bg-white/5 prose-pre:border prose-pre:border-white/10 prose-p:text-white/65 prose-p:leading-relaxed"
                         v-html="descriptionHtml" />
                </div>

                <div v-if="challenge.test_cases?.some(tc => !tc.is_hidden)">
                    <h2 class="text-white/25 text-[10px] font-bold uppercase tracking-[0.14em] mb-3">{{ $t('challenge.test_cases_header') }}</h2>
                    <div class="flex flex-col gap-3">
                        <div v-for="(tc, i) in challenge.test_cases.filter(tc => !tc.is_hidden)" :key="tc.id"
                            class="rounded-xl border border-white/8 bg-white/2 overflow-hidden">
                            <div class="px-4 py-2 bg-white/2.5 border-b border-white/5 flex items-center gap-2">
                                <span class="w-4.5 h-4.5 rounded-full bg-white/8 flex items-center justify-center text-[10px] font-bold text-white/35 leading-none">{{ i + 1 }}</span>
                                <span class="text-white/30 text-[11px] font-semibold">{{ $t('challenge.test_case_label') }}</span>
                            </div>
                            <div class="px-4 py-3 flex flex-col gap-2.5">
                                <div>
                                    <span class="text-[10px] font-bold uppercase tracking-wider text-white/25 block mb-1.5">{{ $t('challenge.input_label') }}</span>
                                    <div class="flex flex-col gap-1">
                                        <div v-for="(val, j) in tc.input" :key="j"
                                            class="flex items-baseline gap-2 text-xs font-mono">
                                            <span class="text-blue-400/70 shrink-0">{{ challenge.parameters?.[j]?.name ?? `arg${j+1}` }}</span>
                                            <span class="text-white/20">=</span>
                                            <span class="text-white/70">{{ val }}</span>
                                        </div>
                                    </div>
                                </div>
                                <div>
                                    <span class="text-[10px] font-bold uppercase tracking-wider text-white/25 block mb-1.5">{{ $t('challenge.output_label') }}</span>
                                    <span class="text-xs font-mono text-green-400/85 font-semibold whitespace-pre-wrap">{{ tc.expected_output }}</span>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Mobile: botón ir al editor -->
                <button @click="mobileTab = 'editor'"
                    class="md:hidden flex items-center justify-center gap-2 w-full py-3 rounded-xl bg-blue-500/10 border border-blue-500/20 text-blue-300 text-sm font-semibold hover:bg-blue-500/20 transition-all">
                    <svg class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
                        <polyline points="9 18 15 12 9 6"/>
                    </svg>
                    {{ $t('challenge.go_to_editor') }}
                </button>

            </div>
        </div>

        <!-- ───── Panel derecho ───── -->
        <div :class="[
                'flex-1 flex-col min-w-0',
                mobileTab === 'description' ? 'hidden md:flex' : 'flex'
            ]">

            <!-- Toolbar -->
            <div class="h-12 border-b border-white/5 flex items-center px-4 gap-1 shrink-0 bg-white/1.5 overflow-x-auto">
                <button v-for="lang in languages" :key="lang.id"
                    @click="selectLang(lang)"
                    :class="[
                        'flex items-center gap-2 px-3 py-1.5 rounded-lg text-xs font-medium transition-all shrink-0',
                        activeLang === lang.id
                            ? lang.activeClass
                            : 'text-white/35 hover:text-white/60 hover:bg-white/5'
                    ]">
                    <component :is="lang.icon" class="w-4 h-4" />
                    {{ lang.label }}
                </button>

                <div class="flex-1 min-w-2" />

                <button @click="runCode(activeLang, code, currentTemplate)" :disabled="isLoading"
                    class="flex items-center gap-2 px-3 py-1.5 text-white/40 hover:text-white/65 hover:bg-white/5 border border-transparent hover:border-white/8 text-xs font-medium rounded-lg transition-all disabled:opacity-40 disabled:cursor-not-allowed shrink-0">
                    <svg v-if="!isLoading" class="w-3.5 h-3.5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
                        <polygon points="5 3 19 12 5 21 5 3"/>
                    </svg>
                    <svg v-else class="w-3.5 h-3.5 animate-spin" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">
                        <path d="M21 12a9 9 0 1 1-6.219-8.56"/>
                    </svg>
                    {{ $t('challenge.run_btn') }}
                </button>

                <div class="w-px h-4 bg-white/8 mx-1 shrink-0" />

                <button @click="submitSolution(activeLang, code, currentTemplate)" :disabled="isLoading"
                    class="flex items-center gap-2 px-4 py-1.5 bg-green-500/10 hover:bg-green-500/20 text-green-400 border border-green-500/20 hover:border-green-500/35 text-xs font-semibold rounded-lg transition-all disabled:opacity-40 disabled:cursor-not-allowed shrink-0">
                    <svg class="w-3.5 h-3.5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
                        <line x1="22" y1="2" x2="11" y2="13"/><polygon points="22 2 15 22 11 13 2 9 22 2"/>
                    </svg>
                    {{ $t('challenge.submit_btn') }}
                </button>
            </div>

            <!-- Monaco Editor -->
            <div class="flex-1 min-h-0">
                <VueMonacoEditor
                    v-model:value="code"
                    :language="activeLang"
                    :theme="editorTheme"
                    :options="editorOptions"
                    class="h-full w-full"
                />
            </div>

            <!-- Drag handle (desktop only) -->
            <div
                @mousedown="startResize"
                @dblclick="terminalHeight = 192"
                :class="[
                    'hidden md:flex h-2.5 shrink-0 cursor-row-resize items-center justify-center',
                    'border-t border-white/5 hover:border-blue-400/30 transition-colors duration-150',
                    'bg-transparent hover:bg-blue-400/4 group',
                    isResizing && 'border-blue-400/30 bg-blue-400/4'
                ]"
            >
                <div :class="[
                    'w-10 h-0.5 rounded-full transition-colors duration-150',
                    isResizing ? 'bg-blue-400/50' : 'bg-white/12 group-hover:bg-blue-400/40'
                ]" />
            </div>

            <!-- Terminal de resultados -->
            <div
                class="shrink-0 flex flex-col bg-black/20 border-t border-white/5"
                :style="{ height: terminalHeight + 'px' }"
            >

                <!-- Header bar -->
                <div class="px-4 h-9 border-b border-white/5 flex items-center gap-2.5 shrink-0 bg-white/2">
                    <svg class="w-3.5 h-3.5 text-white/25 shrink-0" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <polyline points="4 17 10 11 4 5"/><line x1="12" y1="19" x2="20" y2="19"/>
                    </svg>
                    <span class="text-[10px] font-bold uppercase tracking-[0.14em] text-white/30">{{ $t('challenge.terminal_header') }}</span>
                    <div class="flex-1" />
                    <span :class="[
                        'w-1.5 h-1.5 rounded-full shrink-0 transition-colors',
                        result.status === 'pending'        ? 'bg-white/15' :
                        result.status === 'loading'        ? 'bg-blue-400/70 animate-pulse' :
                        result.status === 'accepted'       ? 'bg-green-400/80' :
                        result.status === 'error'          ? 'bg-orange-400/70' :
                        result.status === 'template_error' ? 'bg-orange-400/70' : 'bg-red-400/70'
                    ]" />
                </div>

                <div class="flex-1 overflow-y-auto px-5 py-3 font-mono text-xs scrollbar-thin">

                    <!-- Idle -->
                    <div v-if="result.status === 'pending'" class="flex items-center gap-2.5 select-none">
                        <span class="text-[15px] leading-none text-blue-400/50">›</span>
                        <span class="text-white/35">{{ $t('challenge.terminal_idle') }}</span>
                    </div>

                    <!-- Cargando -->
                    <div v-else-if="result.status === 'loading'" class="flex items-center gap-2.5">
                        <svg class="w-3.5 h-3.5 animate-spin shrink-0 text-blue-400/70" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">
                            <path d="M21 12a9 9 0 1 1-6.219-8.56"/>
                        </svg>
                        <span class="text-blue-400/70">{{ $t('challenge.terminal_loading') }}</span>
                    </div>

                    <!-- Error de conexión u otros errores de red -->
                    <div v-else-if="result.status === 'error'" class="flex items-start gap-2.5">
                        <span class="text-[15px] leading-none text-red-400/80 shrink-0">›</span>
                        <span class="text-red-400/70">{{ result.details?.error_message }}</span>
                    </div>

                    <!-- El alumno modificó la firma de la función -->
                    <div v-else-if="result.status === 'template_error'" class="flex items-start gap-2.5">
                        <span class="text-[15px] leading-none text-orange-400/80 shrink-0">›</span>
                        <pre class="text-orange-400/70 whitespace-pre-wrap">{{ result.details?.error_message }}</pre>
                    </div>

                    <!-- Resultados -->
                    <template v-else>

                        <!-- Time Limit Exceeded -->
                        <div v-if="result.details?.error_type === 'time_limit_exceeded'" class="mb-3">
                            <div class="flex items-center gap-2.5 mb-3">
                                <span class="text-[15px] leading-none text-orange-400/80">›</span>
                                <span class="text-orange-400/80 font-semibold">{{ $t('challenge.tle_title') }}</span>
                                <span v-if="result.details.time" class="text-white/25 text-[10px]">+{{ result.details.time }}s</span>
                            </div>
                            <div class="ml-5 bg-orange-400/5 border border-orange-400/12 rounded-lg px-3 py-2.5 flex flex-col gap-1.5 mb-3">
                                <span class="text-orange-300/60 text-[11px] font-semibold uppercase tracking-wider mb-0.5">{{ $t('challenge.tle_causes_header') }}</span>
                                <span class="text-white/40 text-xs">{{ $t('challenge.tle_cause_1') }}</span>
                                <span class="text-white/40 text-xs">{{ $t('challenge.tle_cause_2') }}</span>
                                <span class="text-white/40 text-xs">{{ $t('challenge.tle_cause_3') }}</span>
                            </div>
                            <div v-for="(tc, i) in challenge.test_cases?.filter(t => !t.is_hidden)" :key="tc.id" class="mb-2">
                                <div class="flex items-baseline gap-2.5">
                                    <span class="text-[15px] leading-none text-orange-400/50">›</span>
                                    <span class="text-white/35 font-semibold shrink-0">test {{ i + 1 }}</span>
                                    <span class="text-orange-400/50 text-xs">{{ $t('challenge.tle_test_label') }}</span>
                                </div>
                                <div v-if="tc.input?.length" class="ml-5 mt-1 flex flex-wrap gap-x-3 gap-y-0.5 items-baseline">
                                    <span v-for="(val, j) in tc.input" :key="j" class="text-[10px]">
                                        <span class="text-blue-400/45">{{ challenge.parameters?.[j]?.name ?? `arg${j+1}` }}</span>
                                        <span class="text-white/15 mx-0.5">=</span>
                                        <span class="text-white/40">{{ val }}</span>
                                    </span>
                                </div>
                            </div>
                        </div>

                        <!-- Error de compilación o runtime -->
                        <div v-else-if="result.details?.compile_output" class="mb-3">
                            <div class="flex items-center gap-2 mb-2">
                                <span class="text-[15px] leading-none text-yellow-400/80">›</span>
                                <span class="text-yellow-400/80 font-semibold">
                                    {{ result.details.error_type === 'runtime_error' ? $t('challenge.runtime_error') : $t('challenge.compile_error') }}
                                </span>
                            </div>
                            <pre class="text-yellow-300/70 whitespace-pre-wrap bg-yellow-400/6 border border-yellow-400/12 rounded-lg px-3 py-2 ml-5">{{ result.details.compile_output }}</pre>
                        </div>

                        <!-- Líneas de test -->
                        <div v-for="(tc, i) in enrichedResults" :key="i" class="mb-2.5">
                            <div class="flex items-center gap-2.5">
                                <span :class="['text-[15px] leading-none shrink-0', tc.passed ? 'text-green-400' : 'text-red-400/85']">›</span>
                                <span v-if="!tc.isHidden" :class="['shrink-0 font-semibold', tc.passed ? 'text-white/55' : 'text-white/50']">{{ $t('challenge.case_label') }} {{ tc.caseLabel }}</span>
                                <span v-if="tc.isHidden"
                                    :class="[
                                        'inline-flex items-center gap-1.5 px-2 py-0.5 rounded-md border text-[11px] font-medium',
                                        tc.passed
                                            ? 'bg-green-500/6 border-green-500/15 text-green-400/60'
                                            : 'bg-violet-500/6 border-violet-500/15 text-violet-300/50'
                                    ]">
                                    <svg class="w-3 h-3 shrink-0" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                        <path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94"/>
                                        <path d="M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19"/>
                                        <path d="m14.12 14.12a3 3 0 1 1-4.24-4.24"/>
                                        <line x1="1" y1="1" x2="23" y2="23"/>
                                    </svg>
                                    {{ $t('challenge.hidden_case') }}
                                    <span class="w-px h-2.5 rounded-full mx-0.5"
                                        :class="tc.passed ? 'bg-green-500/20' : 'bg-violet-500/20'" />
                                    <span :class="tc.passed ? 'text-green-400/80 font-semibold' : 'text-red-400/60 font-semibold'">
                                        {{ tc.passed ? $t('challenge.case_passed') : $t('challenge.case_failed') }}
                                    </span>
                                </span>
                                <template v-else-if="tc.passed">
                                    <span class="text-green-400/55 font-semibold">{{ $t('challenge.ok') }}</span>
                                    <span v-if="tc.time" class="text-white/25 text-[10px] relative -top-0.5">{{ tc.time }}s</span>
                                </template>
                                <span v-else-if="tc.status === 'time_limit_exceeded'" class="text-orange-400/70">
                                    {{ $t('challenge.tle_inline') }}
                                </span>
                                <span v-else-if="tc.status === 'presentation_error'" class="text-blue-400/70">
                                    {{ $t('challenge.presentation_error') }}
                                </span>
                                <span v-else-if="!tc.error && !tc.expected_output?.includes('\n')" class="text-white/35">
                                    {{ $t('challenge.expected') }} <code class="text-green-400/75 font-semibold">{{ tc.expected_output }}</code>
                                    <span class="text-white/20 mx-1.5">·</span>
                                    {{ $t('challenge.obtained') }}
                                    <code v-if="tc.output?.trim()" class="text-red-400/75 font-semibold">{{ tc.output }}</code>
                                    <code v-else class="text-white/20 font-semibold italic">{{ $t('challenge.empty_output') }}</code>
                                </span>
                                <span v-else-if="!tc.error" class="text-red-400/50 text-xs">{{ $t('challenge.wrong_answer') }}</span>
                                <pre v-else class="text-red-300/70 whitespace-pre-wrap">{{ tc.error }}</pre>
                            </div>
                            <div v-if="tc.testCase?.input?.length && !tc.isHidden && !['time_limit_exceeded', 'presentation_error'].includes(tc.status)"
                                class="ml-5 mt-1 flex flex-wrap gap-x-3 gap-y-0.5 items-baseline">
                                <span v-for="(val, j) in tc.testCase.input" :key="j" class="text-[10px]">
                                    <span class="text-blue-400/45">{{ challenge.parameters?.[j]?.name ?? `arg${j+1}` }}</span>
                                    <span class="text-white/15 mx-0.5">=</span>
                                    <span class="text-white/40">{{ val }}</span>
                                </span>
                                <template v-if="!tc.expected_output?.includes('\n')">
                                    <span class="text-white/10 mx-0.5">→</span>
                                    <span class="text-[10px]">
                                        <span class="text-white/25">{{ $t('challenge.output_inline') }}</span>
                                        <span class="text-white/15 mx-0.5">=</span>
                                        <span :class="tc.passed ? 'text-green-400/60' : 'text-red-400/60'">{{ tc.output?.trim() ? tc.output : '""' }}</span>
                                    </span>
                                </template>
                            </div>
                            <div v-if="!tc.error && !tc.isHidden && tc.expected_output?.includes('\n')"
                                class="ml-5 mt-1.5 flex gap-5 font-mono text-[11px]">
                                <div>
                                    <span class="text-[10px] text-white/25 font-sans block mb-0.5">{{ $t('challenge.expected') }}</span>
                                    <pre class="text-green-400/70 font-semibold whitespace-pre m-0">{{ tc.expected_output }}</pre>
                                </div>
                                <div>
                                    <span class="text-[10px] text-white/25 font-sans block mb-0.5">{{ tc.passed ? $t('challenge.output_inline') : $t('challenge.obtained') }}</span>
                                    <pre :class="tc.passed ? 'text-green-400/60' : 'text-red-400/70'" class="font-semibold whitespace-pre m-0">{{ tc.output || `(${$t('challenge.empty_output')})` }}</pre>
                                </div>
                            </div>
                        </div>

                        <!-- Resumen -->
                        <div class="mt-3 mb-4 pt-2.5 border-t border-white/8 flex items-center gap-2">
                            <span :class="[
                                'inline-flex items-center gap-1.5 px-2.5 py-1 rounded-md text-[11px] font-bold',
                                result.status === 'accepted'
                                    ? 'bg-green-500/12 text-green-400 border border-green-500/20'
                                    : 'bg-red-500/12 text-red-400 border border-red-500/20'
                            ]">
                                {{ result.status === 'accepted' ? '✓' : '✗' }}
                                {{ $t('challenge.test_summary', { passed: result.details?.passed_count ?? 0, total: result.details?.total_count ?? 0 }) }}
                            </span>
                        </div>

                    </template>
                </div>
            </div>

        </div>

        </div><!-- end panels -->
    </div>

    <SubmissionModal
        :show="showModal"
        :accepted="result.status === 'accepted'"
        :details="result.details ?? { passed_count: 0, total_count: 0 }"
        :points="challengePoints"
        @close="showModal = false"
        @retry="showModal = false"
    />
</template>

<script setup>
import { onMounted, onUnmounted, ref, computed, watch } from 'vue'
import { useI18n } from 'vue-i18n'
import { VueMonacoEditor } from '@guolao/vue-monaco-editor'
import { useRoute } from 'vue-router'
import { useChallenge }    from '@/composables/useChallenge'
import { useEditor }       from '@/composables/useEditor'
import { useSubmission }   from '@/composables/useSubmission'
import SubmissionModal     from '@/components/SubmissionModal.vue'

const route = useRoute()
const id    = route.params.id

const { locale } = useI18n()
const { challenge, fetchChallenge, descriptionHtml, difficultyTextClass, challengePoints, initials } = useChallenge(id)
watch(locale, fetchChallenge)
const { languages, activeLang, code, currentTemplate, selectLang, editorOptions, editorTheme } = useEditor(challenge)
const { result, isLoading, showModal, runCode, submitSolution } = useSubmission(challenge)

const mobileTab = ref('description')


const enrichedResults = computed(() => {
    let visibleCount = 0
    return (result.value.details?.results ?? []).map(r => {
        const testCase = challenge.value?.test_cases?.find(t => t.id === r.test_case_id)
        const isHidden = testCase?.is_hidden ?? false
        return { ...r, testCase, isHidden, caseLabel: isHidden ? null : ++visibleCount }
    })
})


const NAVBAR_H    = 64
const MIN_H       = 80
const MAX_H_RATIO = 0.60

const terminalHeight = ref(192)
const isResizing     = ref(false)
let   _startY        = 0
let   _startH        = 0

function startResize(e) {
    isResizing.value = true
    _startY = e.clientY
    _startH = terminalHeight.value
    document.body.style.cursor     = 'row-resize'
    document.body.style.userSelect = 'none'
}

function onMouseMove(e) {
    if (!isResizing.value) return
    const delta     = _startY - e.clientY
    const maxH      = (window.innerHeight - NAVBAR_H) * MAX_H_RATIO
    terminalHeight.value = Math.round(Math.min(maxH, Math.max(MIN_H, _startH + delta)))
}

function onMouseUp() {
    if (!isResizing.value) return
    isResizing.value               = false
    document.body.style.cursor     = ''
    document.body.style.userSelect = ''
}

onMounted(() => {
    fetchChallenge()
    document.body.style.overflow = 'hidden'
    document.addEventListener('mousemove', onMouseMove)
    document.addEventListener('mouseup',   onMouseUp)
})

onUnmounted(() => {
    document.body.style.overflow = ''
    document.removeEventListener('mousemove', onMouseMove)
    document.removeEventListener('mouseup',   onMouseUp)
})
</script>

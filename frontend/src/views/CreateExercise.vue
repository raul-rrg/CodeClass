<template>
    <div class="h-full bg-base flex overflow-hidden"
        :class="isDragging ? 'cursor-col-resize select-none' : ''"
        @mousemove="onDrag" @mouseup="stopDrag" @mouseleave="stopDrag">

        <!-- ───── Left: Wizard ───── -->
        <div :style="{ width: leftWidth + '%' }" class="min-w-100 shrink-0 flex flex-col">

            <!-- Sticky header -->
            <div class="sticky top-0 z-10 px-6 pt-4 pb-0 bg-surface/95 backdrop-blur-sm border-b border-white/8">

                <!-- Stepper + cancel -->
                <div class="flex items-center gap-1">
                    <template v-for="(step, i) in STEPS" :key="i">
                        <button @click="handleStepClick(i + 1)"
                            class="flex flex-col items-center gap-1.5 px-2 pb-3 relative flex-1 group">
                            <div :class="[
                                'w-8 h-8 rounded-full flex items-center justify-center text-xs font-bold transition-all shrink-0',
                                currentStep === i + 1
                                    ? 'bg-blue-500 text-white shadow-lg shadow-blue-500/25'
                                    : stepCompleted(i + 1)
                                        ? 'bg-green-500/15 text-green-400 ring-1 ring-green-500/35'
                                        : stepHasError(i + 1)
                                            ? 'bg-red-500/12 text-red-400 ring-1 ring-red-500/25'
                                            : 'bg-white/6 text-white/30 group-hover:bg-white/10'
                            ]">
                                <svg v-if="stepCompleted(i + 1) && currentStep !== i + 1"
                                    class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">
                                    <path d="M20 6L9 17l-5-5"/>
                                </svg>
                                <svg v-else-if="stepHasError(i + 1)"
                                    class="w-3.5 h-3.5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">
                                    <path d="M18 6L6 18M6 6l12 12"/>
                                </svg>
                                <span v-else>{{ i + 1 }}</span>
                            </div>
                            <span :class="[
                                'text-[10px] font-semibold whitespace-nowrap transition-colors',
                                currentStep === i + 1 ? 'text-white' : 'text-white/30'
                            ]">{{ step.label }}</span>
                            <div :class="[
                                'absolute bottom-0 left-0 right-0 h-px transition-all',
                                currentStep === i + 1 ? 'bg-blue-500' : 'bg-transparent'
                            ]" />
                        </button>
                        <div v-if="i < STEPS.length - 1" class="h-px bg-white/15 flex-1 mb-5 shrink" />
                    </template>
                </div>
            </div>

            <!-- Error banner -->
            <div v-if="error" class="mx-6 mt-4 px-4 py-3 rounded-xl bg-red-500/8 border border-red-500/20 text-red-400/90 text-sm">
                {{ error }}
            </div>

            <!-- Step content -->
            <div class="flex-1 overflow-y-auto scrollbar-thin">

                <!-- ── Step 1: Información ── -->
                <div v-if="currentStep === 1" class="p-6 flex flex-col gap-5">
                    <div>
                        <p class="text-[10px] font-bold uppercase tracking-[0.14em] text-white/25 mb-0.5">{{ $t('create_exercise.step_label', { step: 1, total: STEPS.length }) }}</p>
                        <h2 class="text-white font-bold text-xl">{{ $t('create_exercise.step_info') }}</h2>
                        <p class="text-white/50 text-xs mt-1">{{ $t('create_exercise.step1_hint') }}</p>
                    </div>

                    <div class="flex flex-col gap-1.5">
                        <div class="flex items-center justify-between">
                            <label class="text-[10px] font-bold uppercase tracking-[0.12em] text-white/55">{{ $t('create_exercise.title_label') }}</label>
                            <span class="text-[10px] text-white/35">{{ form.title.length }}/{{ LIMITS.title.max }}</span>
                        </div>
                        <input v-model="form.title" type="text" :maxlength="LIMITS.title.max" :placeholder="$t('create_exercise.title_placeholder')"
                            class="w-full bg-white/6 border border-white/15 rounded-xl px-4 py-2.5 text-white placeholder-white/20 text-sm outline-none focus:border-blue-500/60 focus:bg-white/8 transition-all" />
                        <p v-if="form.title.trim() && validateTitle(form.title)" class="text-[10px] text-red-400">{{ validateTitle(form.title) }}</p>
                        <p v-else class="text-[10px] text-white/40">{{ $t('create_exercise.title_hint', { min: LIMITS.title.min }) }}</p>
                    </div>

                    <div class="flex flex-col gap-1.5">
                        <div class="flex items-center justify-between">
                            <label class="text-[10px] font-bold uppercase tracking-[0.12em] text-white/55">{{ $t('create_exercise.short_desc_label') }}</label>
                            <span class="text-[10px] text-white/35">{{ form.short_description.length }}/{{ LIMITS.shortDesc.max }}</span>
                        </div>
                        <input v-model="form.short_description" type="text" :maxlength="LIMITS.shortDesc.max"
                            :placeholder="$t('create_exercise.short_desc_placeholder')"
                            class="w-full bg-white/6 border border-white/15 rounded-xl px-4 py-2.5 text-white placeholder-white/20 text-sm outline-none focus:border-blue-500/60 focus:bg-white/8 transition-all" />
                        <p class="text-[10px] text-white/40">{{ $t('create_exercise.short_desc_hint', { max: LIMITS.shortDesc.max }) }}</p>
                    </div>

                    <div class="flex gap-6">
                        <div class="flex flex-col gap-1.5">
                            <label class="text-[10px] font-bold uppercase tracking-[0.12em] text-white/55">{{ $t('create_exercise.difficulty_label') }}</label>
                            <div class="flex gap-2">
                                <button v-for="d in DIFFICULTIES" :key="d.value"
                                    @click="form.difficulty = d.value"
                                    :class="[
                                        'flex items-center gap-1.5 px-3 py-1.5 rounded-lg text-xs font-bold capitalize border transition-all',
                                        form.difficulty === d.value ? d.activeClass : 'border-white/10 text-white/30 hover:border-white/20 hover:text-white/50'
                                    ]">
                                    <span :class="['w-1.5 h-1.5 rounded-full', form.difficulty === d.value ? d.dotClass : 'bg-white/20']" />
                                    {{ d.value }}
                                </button>
                            </div>
                        </div>

                        <div class="flex flex-col gap-1.5 flex-1">
                            <label class="text-[10px] font-bold uppercase tracking-[0.12em] text-white/55">{{ $t('create_exercise.category_label') }}</label>
                            <select v-model="form.category"
                                class="w-full bg-white/6 border border-white/15 rounded-xl px-3 py-2 text-white text-sm outline-none focus:border-blue-500/50 transition-all cursor-pointer">
                                <option value="" disabled class="bg-surface">{{ $t('create_exercise.category_option') }}</option>
                                <option v-for="cat in CATEGORIES" :key="cat" :value="cat" class="bg-surface capitalize">{{ cat }}</option>
                            </select>
                        </div>
                    </div>

                    <div class="flex flex-col gap-1.5">
                        <div class="flex items-center justify-between">
                            <label class="text-[10px] font-bold uppercase tracking-[0.12em] text-white/55">{{ $t('create_exercise.description_label') }}</label>
                            <span class="text-[10px] text-white/35">{{ form.description.length }} / {{ LIMITS.desc.max }} · markdown</span>
                        </div>
                        <textarea v-model="form.description" rows="8" :maxlength="LIMITS.desc.max"
                            :placeholder="$t('create_exercise.description_placeholder')"
                            class="w-full bg-white/6 border border-white/15 rounded-xl px-4 py-2.5 text-white placeholder-white/20 text-sm outline-none focus:border-blue-500/60 focus:bg-white/8 transition-all resize-none font-mono" />
                        <p v-if="form.description.trim() && validateDescription(form.description)" class="text-[10px] text-red-400">{{ validateDescription(form.description) }}</p>

                        <div class="rounded-xl border border-white/10 bg-white/2 p-3 flex flex-col gap-2">
                            <label class="flex items-center gap-2 cursor-pointer select-none w-fit">
                                <input v-model="enableImageInsert" type="checkbox"
                                    @change="imageInsertError = ''"
                                    class="w-3.5 h-3.5 rounded border-white/20 bg-white/5 text-blue-500 focus:ring-blue-500/40 focus:ring-1" />
                                <span class="text-[10px] font-bold uppercase tracking-[0.12em] text-white/35">{{ $t('create_exercise.add_image') }}</span>
                            </label>

                            <div v-if="enableImageInsert" class="flex flex-col gap-2">
                                <div class="flex gap-2 flex-wrap">
                                    <input v-model="imageAltText" type="text" :placeholder="$t('create_exercise.image_alt_placeholder')"
                                        class="flex-1 min-w-40 bg-white/6 border border-white/15 rounded-lg px-3 py-1.5 text-white placeholder-white/20 text-xs outline-none focus:border-blue-500/50 transition-all" />
                                    <input v-model="imageUrl" type="url" :placeholder="$t('create_exercise.image_url_placeholder')"
                                        class="flex-2 min-w-56 bg-white/6 border border-white/15 rounded-lg px-3 py-1.5 text-white placeholder-white/20 text-xs outline-none focus:border-blue-500/50 transition-all" />
                                    <input v-model="imageWidth" type="number" min="50" :max="LIMITS.imageWidth.max" step="10" :placeholder="$t('create_exercise.image_width_placeholder')"
                                        class="w-44 bg-white/6 border border-white/15 rounded-lg px-3 py-1.5 text-white placeholder-white/20 text-xs outline-none focus:border-blue-500/50 transition-all" />
                                    <button @click="insertImageInDescription"
                                        class="px-3 py-1.5 rounded-lg text-xs font-semibold bg-blue-500/85 text-white hover:bg-blue-400 transition-colors">
                                        {{ $t('create_exercise.insert_image_btn') }}
                                    </button>
                                </div>
                                <p class="text-[10px] text-white/25">{{ $t('create_exercise.image_hint', { max: LIMITS.imageWidth.max }) }}</p>
                                <p v-if="imageInsertError" class="text-[10px] text-red-400">{{ imageInsertError }}</p>
                            </div>
                        </div>
                    </div>

                </div>

                <!-- ── Step 2: Template ── -->
                <div v-if="currentStep === 2" class="p-6 flex flex-col gap-5">
                    <div>
                        <p class="text-[10px] font-bold uppercase tracking-[0.14em] text-white/25 mb-0.5">{{ $t('create_exercise.step_label', { step: 2, total: STEPS.length }) }}</p>
                        <h2 class="text-white font-bold text-xl">{{ $t('create_exercise.step_template') }}</h2>
                        <p class="text-white/50 text-xs mt-1">{{ $t('create_exercise.step2_hint') }}</p>
                    </div>

                    <div class="flex flex-col gap-1.5">
                        <label class="text-[10px] font-bold uppercase tracking-[0.12em] text-white/55">{{ $t('create_exercise.function_name_label') }}</label>
                        <input v-model="form.function_name" type="text" placeholder="twoSum"
                            class="w-full bg-white/6 border border-white/15 rounded-xl px-4 py-2.5 text-white placeholder-white/20 text-sm font-mono outline-none focus:border-blue-500/60 focus:bg-white/8 transition-all" />
                        <p v-if="form.function_name.trim() && validateFunctionName(form.function_name)" class="text-[10px] text-red-400">{{ validateFunctionName(form.function_name) }}</p>
                        <p v-else class="text-[10px] text-white/40">{{ $t('create_exercise.function_name_hint', { min: LIMITS.fnName.min }) }}</p>
                    </div>

                    <div class="flex flex-col gap-1.5">
                        <label class="text-[10px] font-bold uppercase tracking-[0.12em] text-white/55">{{ $t('create_exercise.parameters_label') }}</label>
                        <div class="flex flex-col gap-2">
                            <div v-for="(param, i) in form.parameters" :key="i" class="flex items-start gap-2">
                                <div class="flex-1">
                                    <input v-model="param.name" type="text" :placeholder="`param${i + 1}`"
                                        class="w-full bg-white/6 border border-white/15 rounded-xl px-3 py-2 text-white placeholder-white/20 text-sm font-mono outline-none focus:border-blue-500/50 transition-all" />
                                    <p v-if="param.name.trim() && validateParameterName(param.name, form.parameters, i)" class="text-[10px] text-red-400 mt-1">{{ validateParameterName(param.name, form.parameters, i) }}</p>
                                </div>
                                <select v-model="param.type"
                                    class="bg-white/6 border border-white/15 rounded-xl px-3 py-2 text-white/70 text-sm outline-none focus:border-blue-500/50 transition-all cursor-pointer">
                                    <option v-for="t in PARAM_TYPES" :key="t" :value="t" class="bg-surface">{{ t }}</option>
                                </select>
                                <button @click="removeParameter(i)" :disabled="form.parameters.length === 1"
                                    class="w-7 h-7 shrink-0 flex items-center justify-center rounded-lg text-white/25 hover:text-red-400 hover:bg-red-500/10 disabled:opacity-25 disabled:cursor-not-allowed transition-all mt-0.5">
                                    <svg class="w-3.5 h-3.5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                        <path d="M18 6L6 18M6 6l12 12"/>
                                    </svg>
                                </button>
                            </div>
                            <button @click="addParameter"
                                class="flex items-center gap-1.5 text-xs text-blue-400/60 hover:text-blue-400 transition-colors w-fit mt-1">
                                <svg class="w-3.5 h-3.5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                    <path d="M12 5v14M5 12h14"/>
                                </svg>
                                {{ $t('create_exercise.add_parameter') }}
                            </button>
                            <p class="text-[10px] text-white/40">{{ $t('create_exercise.parameters_hint') }}</p>
                        </div>
                    </div>

                    <div class="flex flex-col gap-1.5">
                        <label class="text-[10px] font-bold uppercase tracking-[0.12em] text-white/55">{{ $t('create_exercise.return_type_label') }}</label>
                        <select v-model="form.return_type"
                            class="bg-white/6 border border-white/15 rounded-xl px-3 py-2 text-white text-sm outline-none focus:border-blue-500/50 transition-all cursor-pointer w-40">
                            <option v-for="t in PARAM_TYPES" :key="t" :value="t" class="bg-surface">{{ t }}</option>
                        </select>
                    </div>
                </div>

                <!-- ── Step 3: Casos de pruebas ── -->
                <div v-if="currentStep === 3" class="p-6 flex flex-col gap-4">
                    <div>
                        <p class="text-[10px] font-bold uppercase tracking-[0.14em] text-white/25 mb-0.5">{{ $t('create_exercise.step_label', { step: 3, total: STEPS.length }) }}</p>
                        <h2 class="text-white font-bold text-xl">{{ $t('create_exercise.step_tests') }}</h2>
                        <p class="text-white/50 text-xs mt-1">{{ $t('create_exercise.step3_hint') }}</p>
                    </div>

                    <div v-if="activeTestCaseErrorMessage" class="px-3 py-2 rounded-lg border border-red-500/35 bg-red-500/10 text-red-300 text-xs">
                        {{ activeTestCaseErrorMessage }}
                    </div>

                    <div v-for="(tc, i) in form.test_cases" :key="i"
                        :class="[
                            'border rounded-xl overflow-hidden transition-colors',
                            activeTestCaseErrorIndex === i
                                ? 'border-red-500/45 bg-red-500/5'
                                : 'border-white/8'
                        ]">
                        <div class="px-4 py-2.5 bg-white/2 border-b border-white/5 flex items-center justify-between">
                            <span class="text-xs font-semibold text-white/30">{{ $t('create_exercise.test_case_n', { n: i + 1 }) }}</span>
                            <div class="flex items-center gap-4">
                                <label class="flex items-center gap-2 cursor-pointer select-none">
                                    <span class="text-[11px] text-white/25">{{ $t('create_exercise.hidden_toggle') }}</span>
                                    <button @click="toggleTestCaseHidden(i)"
                                        :class="['w-7 h-4 rounded-full transition-all relative shrink-0', tc.is_hidden ? 'bg-blue-500' : 'bg-white/10']">
                                        <span :class="['absolute top-0.5 w-3 h-3 rounded-full bg-white shadow transition-all', tc.is_hidden ? 'left-3.5' : 'left-0.5']" />
                                    </button>
                                </label>
                                <button @click="removeTestCaseWithRules(i)" :disabled="form.test_cases.length <= 2"
                                    class="text-white/20 hover:text-red-400 disabled:opacity-25 disabled:cursor-not-allowed transition-colors">
                                    <svg class="w-3.5 h-3.5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                        <path d="M3 6h18M8 6V4h8v2M19 6l-1 14H6L5 6"/>
                                    </svg>
                                </button>
                            </div>
                        </div>
                        <div class="p-4 flex flex-col gap-3">
                            <div>
                                <span class="text-[10px] font-bold uppercase tracking-wider text-white/20 block mb-2">{{ $t('create_exercise.input_label') }}</span>
                                <div class="flex flex-col gap-2">
                                    <div v-for="(param, j) in form.parameters" :key="j" class="flex items-start gap-2.5">
                                        <span class="text-xs font-mono text-blue-400/60 w-16 shrink-0 truncate mt-1.5">{{ param.name || `arg${j + 1}` }}</span>
                                        <div class="flex-1">
                                            <input v-model="tc.input[j]" type="text"
                                                :placeholder="inputPlaceholder(param.type)"
                                                :class="[
                                                    'w-full bg-white/4 border rounded-lg px-3 py-1.5 text-white placeholder-white/15 text-xs font-mono outline-none transition-all',
                                                    tc.input[j] && validateInputForType(tc.input[j], param.type)
                                                        ? 'border-red-500/40 focus:border-red-500/60'
                                                        : 'border-white/8 focus:border-blue-500/40'
                                                ]" />
                                            <p v-if="tc.input[j] && validateInputForType(tc.input[j], param.type)"
                                                class="text-[10px] text-red-400 mt-0.5">{{ validateInputForType(tc.input[j], param.type) }}</p>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div>
                                <span class="text-[10px] font-bold uppercase tracking-wider text-white/20 block mb-2">{{ $t('create_exercise.expected_output_label') }}</span>
                                <input v-model="tc.expected_output" type="text"
                                    :placeholder="inputPlaceholder(form.return_type)"
                                    :class="[
                                        'w-full bg-white/4 border rounded-lg px-3 py-1.5 text-white placeholder-white/15 text-xs font-mono outline-none transition-all',
                                        tc.expected_output && validateInputForType(tc.expected_output, form.return_type)
                                            ? 'border-red-500/40 focus:border-red-500/60'
                                            : 'border-white/12 focus:border-blue-500/40'
                                    ]" />
                                <p v-if="tc.expected_output && validateInputForType(tc.expected_output, form.return_type)"
                                    class="text-[10px] text-red-400 mt-0.5">{{ validateInputForType(tc.expected_output, form.return_type) }}</p>
                            </div>
                        </div>
                    </div>

                    <button @click="addTestCaseAndResetError"
                        :disabled="form.test_cases.length >= LIMITS.testCases.max"
                        class="flex items-center justify-center gap-2 w-full py-3 border border-dashed border-white/12 rounded-xl text-xs text-white/25 hover:text-white/50 hover:border-white/22 disabled:opacity-30 disabled:cursor-not-allowed transition-all">
                        <svg class="w-3.5 h-3.5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                            <path d="M12 5v14M5 12h14"/>
                        </svg>
                        {{ $t('create_exercise.add_test_case') }}
                    </button>
                </div>

                <!-- ── Step 4: Publicar ── -->
                <div v-if="currentStep === 4" class="p-6 flex flex-col gap-4">
                    <div>
                        <p class="text-[10px] font-bold uppercase tracking-[0.14em] text-white/25 mb-0.5">{{ $t('create_exercise.step_label', { step: 4, total: STEPS.length }) }}</p>
                        <h2 class="text-white font-bold text-xl">{{ isEditMode ? $t('create_exercise.step_save') : $t('create_exercise.step_publish') }}</h2>
                        <p class="text-white/50 text-xs mt-1">{{ isEditMode ? $t('create_exercise.step4_hint_edit') : $t('create_exercise.step4_hint_create') }}</p>
                    </div>

                    <!-- Aviso torneo: forzar oculto -->
                    <div v-if="fromTournament"
                        class="flex items-center gap-3 px-4 py-3 bg-purple-500/8 border border-purple-500/20 rounded-xl">
                        <svg class="w-4 h-4 text-purple-400 shrink-0" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                            <path d="M2 9a3 3 0 0 1 0 6v2a2 2 0 0 0 2 2h16a2 2 0 0 0 2-2v-2a3 3 0 0 1 0-6V7a2 2 0 0 0-2-2H4a2 2 0 0 0-2 2Z"/>
                            <line x1="9" y1="12" x2="15" y2="12"/>
                        </svg>
                        <p class="text-xs text-purple-300/80">{{ $t('create_exercise.tournament_notice') }}</p>
                    </div>

                    <div v-else class="flex items-center justify-between p-4 bg-white/2 border border-white/12 rounded-xl">
                        <div>
                            <p class="text-sm font-semibold text-white">
                                {{ isEditMode
                                    ? (form.is_published ? $t('create_exercise.toggle_unpublish') : $t('create_exercise.toggle_publish'))
                                    : (form.is_published ? $t('create_exercise.toggle_public') : $t('create_exercise.toggle_private')) }}
                            </p>
                            <p class="text-xs text-white/35 mt-0.5">
                                {{ isEditMode
                                    ? (form.is_published ? $t('create_exercise.edit_unpublish_hint') : $t('create_exercise.edit_publish_hint'))
                                    : (form.is_published ? $t('create_exercise.new_public_hint') : $t('create_exercise.new_private_hint')) }}
                            </p>
                        </div>
                        <button @click="form.is_published = !form.is_published"
                            :class="['w-10 h-5.5 rounded-full transition-all relative shrink-0', form.is_published ? 'bg-blue-500' : 'bg-white/10']">
                            <span :class="['absolute top-0.5 w-4.5 h-4.5 rounded-full bg-white shadow transition-all', form.is_published ? 'left-5' : 'left-0.5']" />
                        </button>
                    </div>

                    <div v-if="form.is_published && !canPublish"
                        class="flex items-center gap-2 px-4 py-3 bg-yellow-500/8 border border-yellow-500/20 rounded-xl text-yellow-400/80 text-xs">
                        <svg class="w-4 h-4 shrink-0" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                            <path d="M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z"/>
                            <line x1="12" y1="9" x2="12" y2="13"/><line x1="12" y1="17" x2="12.01" y2="17"/>
                        </svg>
                        {{ $t('create_exercise.publish_warning') }}
                    </div>

                    <div class="rounded-xl border border-white/12 bg-white/2 overflow-hidden">
                        <div class="px-4 py-3 border-b border-white/5 flex items-center justify-between">
                            <div>
                                <p class="text-sm font-semibold text-white">{{ $t('create_exercise.add_solution') }}</p>
                                <p class="text-xs text-white/35 mt-0.5">{{ $t('create_exercise.solution_hint') }}</p>
                            </div>
                            <button @click="includeReferenceSolution = !includeReferenceSolution"
                                :class="['w-10 h-5.5 rounded-full transition-all relative shrink-0', includeReferenceSolution ? 'bg-blue-500' : 'bg-white/10']">
                                <span :class="['absolute top-0.5 w-4.5 h-4.5 rounded-full bg-white shadow transition-all', includeReferenceSolution ? 'left-5' : 'left-0.5']" />
                            </button>
                        </div>

                        <div v-if="includeReferenceSolution" class="p-4 flex flex-col gap-3">
                            <div class="flex gap-2 flex-wrap">
                                <button v-for="lang in LANGUAGES" :key="lang.id"
                                    @click="form.solution_language = lang.id"
                                    :class="[
                                        'flex items-center gap-1.5 px-3 py-1.5 rounded-xl text-xs font-medium border transition-all',
                                        form.solution_language === lang.id ? lang.activeClass : 'border-white/10 text-white/35 hover:border-white/25 hover:text-white/55'
                                    ]">
                                    <component :is="lang.icon" class="w-3.5 h-3.5" />
                                    {{ lang.label }}
                                </button>
                            </div>

                            <div class="rounded-xl overflow-hidden border border-white/10 h-112 md:h-136">
                                <VueMonacoEditor
                                    v-model:value="form.solution_code"
                                    :language="form.solution_language"
                                    :theme="editorTheme"
                                    :options="EDITOR_OPTIONS"
                                    class="h-full w-full"
                                />
                            </div>
                        </div>
                    </div>
                </div>

            </div>

            <!-- Sticky footer nav -->
            <div class="sticky bottom-0 px-6 py-2 bg-surface/95 backdrop-blur-sm border-t border-white/8 flex items-center justify-between">
                <button v-if="currentStep === 1"
                    @click="router.push({ name: isEditMode ? 'my-exercises' : 'challenges' })"
                    class="flex items-center gap-1.5 px-3 py-1.5 rounded-lg text-xs text-red-500/50 hover:text-red-400/80 hover:bg-red-500/8 transition-all">
                    {{ $t('common.cancel') }}
                </button>
                <button v-else @click="currentStep--"
                    class="flex items-center gap-1.5 px-3 py-1.5 rounded-lg text-xs text-white/40 hover:text-white/70 transition-all">
                    <svg class="w-3.5 h-3.5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <path d="M15 18l-6-6 6-6"/>
                    </svg>
                    {{ $t('common.back') }}
                </button>

                <!-- Step dots -->
                <div class="flex items-center gap-1">
                    <div v-for="i in STEPS.length" :key="i"
                        :class="['h-1 rounded-full transition-all duration-300', i === currentStep ? 'bg-blue-500 w-3' : 'bg-white/15 w-1']" />
                </div>

                <button v-if="currentStep < STEPS.length"
                    @click="handleNextStep"
                    :disabled="!canProceedToNextStep"
                    class="flex items-center gap-1.5 px-3 py-1.5 rounded-lg bg-blue-500 text-white text-xs font-semibold hover:bg-blue-400 disabled:opacity-50 disabled:cursor-not-allowed transition-all">
                    {{ $t('common.next') }}
                    <svg class="w-3.5 h-3.5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <path d="M9 18l6-6-6-6"/>
                    </svg>
                </button>
                <button v-else
                    @click="handleSubmit" :disabled="loading"
                    class="flex items-center gap-1.5 px-3 py-1.5 rounded-lg bg-blue-500 text-white text-xs font-semibold hover:bg-blue-400 disabled:opacity-50 disabled:cursor-not-allowed transition-all">
                    <svg v-if="loading" class="w-3.5 h-3.5 animate-spin" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <path d="M21 12a9 9 0 1 1-6.219-8.56" stroke-linecap="round"/>
                    </svg>
                    {{ loading ? $t('create_exercise.saving') : isEditMode ? $t('create_exercise.save_changes') : $t('create_exercise.create') }}
                </button>
            </div>
        </div>

        <!-- ───── Divider ───── -->
        <div class="w-1 shrink-0 bg-white/5 hover:bg-blue-500/40 active:bg-blue-500/60 cursor-col-resize transition-colors"
            @mousedown.prevent="startDrag" />

        <!-- ───── Right: Preview ───── -->
        <div class="flex-1 overflow-y-auto scrollbar-thin bg-white/1.5">
            <div class="p-6 pb-5 flex flex-col gap-4">

                <!-- Preview header -->
                <div class="flex items-center gap-2 text-white/35 text-xs font-semibold uppercase tracking-wider mb-1">
                    <svg class="w-3.5 h-3.5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/><circle cx="12" cy="12" r="3"/>
                    </svg>
                    {{ $t('create_exercise.preview_header') }}
                </div>

                <!-- Title + meta -->
                <div>
                    <h1 class="font-bold text-2xl leading-tight mb-3"
                        :class="form.title ? 'text-white' : 'text-white/20 italic'">
                        {{ form.title || $t('create_exercise.no_title') }}
                    </h1>
                    <div class="flex items-center gap-3 flex-wrap">
                        <span :class="['text-xs font-bold capitalize', DIFFICULTY_TEXT_CLASS[form.difficulty]]">{{ form.difficulty }}</span>
                        <span class="text-white/15">·</span>
                        <span class="text-xs text-white/35 capitalize">{{ form.category || '—' }}</span>
                        <span class="text-white/15">·</span>
                        <div class="flex items-center gap-1 text-blue-400/60 text-xs font-semibold">
                            <svg class="w-3 h-3" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                <path d="M6 9H4.5a2.5 2.5 0 0 1 0-5H6"/><path d="M18 9h1.5a2.5 2.5 0 0 0 0-5H18"/>
                                <path d="M4 22h16"/><path d="M10 14.66V17c0 .55-.47.98-.97 1.21C7.85 18.75 7 20.24 7 22"/>
                                <path d="M14 14.66V17c0 .55.47.98.97 1.21C16.15 18.75 17 20.24 17 22"/>
                                <path d="M18 2H6v7a6 6 0 0 0 12 0V2Z"/>
                            </svg>
                            {{ DIFFICULTY_POINTS[form.difficulty] }} pts
                        </div>
                    </div>
                </div>

                <div class="h-px bg-white/10" />

                <!-- Description -->
                <div>
                    <h2 class="text-white/40 text-[10px] font-bold uppercase tracking-[0.14em] mb-3">{{ $t('create_exercise.description_section') }}</h2>
                    <div v-if="form.description"
                        class="prose prose-invert prose-sm max-w-none prose-code:text-blue-300 prose-code:before:content-none prose-code:after:content-none prose-pre:bg-white/5 prose-pre:border prose-pre:border-white/10 prose-p:text-white/65 prose-p:leading-relaxed"
                        v-html="descriptionHtml" />
                    <p v-else class="text-white/20 text-sm italic">{{ $t('create_exercise.no_description') }}</p>
                </div>

                <!-- Template -->
                <div v-if="form.function_name">
                    <h2 class="text-white/40 text-[10px] font-bold uppercase tracking-[0.14em] mb-3">{{ $t('create_exercise.function_header') }}</h2>
                    <code class="block bg-white/3 border border-white/12 rounded-xl px-4 py-3 text-sm font-mono text-blue-300/80">
                        {{ functionSignature }}
                    </code>
                </div>

                <!-- Public test cases -->
                <div v-if="publicTestCases.length > 0">
                    <div class="flex items-center justify-between mb-3">
                        <h2 class="text-white/40 text-[10px] font-bold uppercase tracking-[0.14em]">{{ $t('create_exercise.examples_header') }}</h2>
                        <span v-if="form.test_cases.some(tc => tc.is_hidden)" class="text-[10px] text-white/20">
                            {{ $t('create_exercise.hidden_count', form.test_cases.filter(tc => tc.is_hidden).length) }}
                        </span>
                    </div>
                    <div class="flex flex-col gap-3">
                        <div v-for="(tc, i) in publicTestCases" :key="i"
                            class="rounded-xl border border-white/12 bg-white/2 overflow-hidden">
                            <div class="px-4 py-2 bg-white/2.5 border-b border-white/5">
                                <span class="text-white/30 text-[11px] font-semibold">{{ $t('create_exercise.test_case_n', { n: i + 1 }) }}</span>
                            </div>
                            <div class="px-4 py-3 grid grid-cols-2 gap-4">
                                <div>
                                    <span class="text-[10px] font-bold uppercase tracking-wider text-white/25 block mb-1.5">{{ $t('create_exercise.preview_input') }}</span>
                                    <div class="flex flex-col gap-1">
                                        <div v-for="(val, j) in tc.input" :key="j" class="flex items-baseline gap-2 text-xs font-mono">
                                            <span class="text-blue-400/70 shrink-0">{{ form.parameters[j]?.name || `arg${j + 1}` }}</span>
                                            <span class="text-white/20">=</span>
                                            <span class="text-white/70">{{ val || '?' }}</span>
                                        </div>
                                    </div>
                                </div>
                                <div>
                                    <span class="text-[10px] font-bold uppercase tracking-wider text-white/25 block mb-1.5">{{ $t('create_exercise.preview_output') }}</span>
                                    <span class="text-xs font-mono text-green-400/80">{{ tc.expected_output || '—' }}</span>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <div v-if="publicTestCases.length === 0 && form.test_cases.length > 0"
                    class="text-white/20 text-xs italic">
                    {{ $t('create_exercise.all_hidden_hint') }}
                </div>

            </div>
        </div>

    </div>
</template>

<script setup>
import { computed, onBeforeUnmount, onMounted, ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { VueMonacoEditor, loader } from '@guolao/vue-monaco-editor'
import activeThemeData from 'monaco-themes/themes/Dracula.json'
import { DIFFICULTY_TEXT_CLASS, DIFFICULTY_POINTS } from '@/constants/difficulties'
import {
    useCreateExercise,
    PARAM_TYPES, CATEGORIES, LIMITS,
    validateTitle, validateDescription, validateFunctionName,
    validateParameterName, validateInputForType,
} from '@/composables/useCreateExercise'

const { t } = useI18n()
const router         = useRouter()
const route          = useRoute()
const exerciseId     = route.params.id ?? null
const fromTournament = route.query.from === 'tournament'

// ── UI constants ──

const STEPS = computed(() => [
    { label: t('create_exercise.step_info') },
    { label: t('create_exercise.step_template') },
    { label: t('create_exercise.step_tests') },
    { label: isEditMode ? t('create_exercise.step_save') : t('create_exercise.step_publish') },
])

const DIFFICULTIES = [
    { value: 'easy',   activeClass: 'border-green-500/40  text-green-400  bg-green-500/10',  dotClass: 'bg-green-400' },
    { value: 'medium', activeClass: 'border-yellow-500/40 text-yellow-400 bg-yellow-500/10', dotClass: 'bg-yellow-400' },
    { value: 'hard',   activeClass: 'border-red-500/40    text-red-400    bg-red-500/10',    dotClass: 'bg-red-400' },
    { value: 'insane', activeClass: 'border-purple-500/40 text-purple-400 bg-purple-500/10', dotClass: 'bg-purple-400' },
]

const EDITOR_OPTIONS = {
    fontSize:             13,
    fontFamily:           'Cascadia Code, monospace',
    minimap:              { enabled: false },
    scrollBeyondLastLine: false,
    lineNumbers:          'on',
    renderLineHighlight:  'all',
    padding:              { top: 12 },
}

// ── Composable ──

const {
    loading, error, form, isEditMode, LANGUAGES,
    addParameter, removeParameter,
    addTestCase, removeTestCase,
    submit: submitExercise, loadExercise,
    isStepValid, stepCompleted, canPublish,
    descriptionHtml, publicTestCases, functionSignature,
} = useCreateExercise(exerciseId)

onMounted(async () => {
    if (isEditMode) await loadExercise(exerciseId)
})

// ── State ──

const currentStep              = ref(1)
const includeReferenceSolution = ref(false)
const enableImageInsert        = ref(false)
const imageUrl                 = ref('')
const imageAltText             = ref('')
const imageWidth               = ref('')
const imageInsertError         = ref('')

// ── Stepper ──

const canProceedToNextStep = computed(() => isStepValid(currentStep.value))

function stepHasError(step) {
    if (step >= currentStep.value) return false
    return !stepCompleted(step)
}

function handleStepClick(targetStep) {
    if (targetStep <= currentStep.value) {
        currentStep.value = targetStep
        return
    }
    for (let step = currentStep.value; step < targetStep; step++) {
        if (!isStepValid(step)) return
    }
    currentStep.value = targetStep
}

function handleNextStep() {
    if (!canProceedToNextStep.value) return
    currentStep.value++
}

async function handleSubmit() {
    if (!includeReferenceSolution.value) form.value.solution_code = ''
    if (fromTournament) form.value.is_published = false
    await submitExercise()
}

// ── Test case rule errors (timed UI feedback) ──

const activeTestCaseErrorMessage = ref('')
const activeTestCaseErrorIndex   = ref(null)
let testCaseErrorTimeoutId = null

function setTestCaseRuleError(message, index = null) {
    if (testCaseErrorTimeoutId) {
        clearTimeout(testCaseErrorTimeoutId)
        testCaseErrorTimeoutId = null
    }
    activeTestCaseErrorMessage.value = message
    activeTestCaseErrorIndex.value   = index
    testCaseErrorTimeoutId = setTimeout(clearTestCaseRuleError, 3500)
}

function clearTestCaseRuleError() {
    activeTestCaseErrorMessage.value = ''
    activeTestCaseErrorIndex.value   = null
    if (testCaseErrorTimeoutId) {
        clearTimeout(testCaseErrorTimeoutId)
        testCaseErrorTimeoutId = null
    }
}

function toggleTestCaseHidden(index) {
    clearTestCaseRuleError()
    const tcs          = form.value.test_cases
    const tc           = tcs[index]
    const hiddenCount  = tcs.filter(t => t.is_hidden).length
    const visibleCount = tcs.length - hiddenCount

    if ( tc.is_hidden && hiddenCount  === 1) { setTestCaseRuleError(t('create_exercise.error_min_hidden'),  index); return }
    if (!tc.is_hidden && visibleCount === 1) { setTestCaseRuleError(t('create_exercise.error_min_visible'), index); return }

    tc.is_hidden = !tc.is_hidden
}

function removeTestCaseWithRules(index) {
    clearTestCaseRuleError()
    const tcs          = form.value.test_cases
    const tc           = tcs[index]
    const hiddenCount  = tcs.filter(t => t.is_hidden).length
    const visibleCount = tcs.length - hiddenCount

    if (tcs.length <= 2)                     { setTestCaseRuleError(t('create_exercise.error_min_cases'),          index); return }
    if ( tc.is_hidden && hiddenCount  === 1) { setTestCaseRuleError(t('create_exercise.error_delete_only_hidden'), index); return }
    if (!tc.is_hidden && visibleCount === 1) { setTestCaseRuleError(t('create_exercise.error_delete_only_visible'),index); return }

    removeTestCase(index)
}

function addTestCaseAndResetError() {
    clearTestCaseRuleError()
    addTestCase()
}

// ── Image insert ──

const ALLOWED_IMAGE_EXTENSIONS = new Set(['png', 'jpg', 'jpeg', 'webp', 'gif', 'avif'])

function validateImageUrl(urlValue) {
    let parsed
    try   { parsed = new URL(urlValue) }
    catch { return t('create_exercise.error_invalid_url') }

    if (!['http:', 'https:'].includes(parsed.protocol)) return t('create_exercise.error_url_protocol')
    const ext = parsed.pathname.toLowerCase().match(/\.([a-z0-9]+)$/)?.[1]
    if (!ext)                               return t('create_exercise.error_url_no_ext')
    const allowed = [...ALLOWED_IMAGE_EXTENSIONS].join(', ')
    if (!ALLOWED_IMAGE_EXTENSIONS.has(ext)) return t('create_exercise.error_url_ext', { ext, allowed })
    return ''
}

function sanitizeImageAlt(value) {
    return value.trim().replace(/[<>"'\[\]\(\)]/g, '') || 'imagen'
}

function insertImageInDescription() {
    imageInsertError.value = ''
    const url = imageUrl.value.trim()
    if (!url) return

    const urlErr = validateImageUrl(url)
    if (urlErr) { imageInsertError.value = urlErr; return }

    const parsedWidth = Number.parseInt(imageWidth.value, 10)
    if (Number.isFinite(parsedWidth) && parsedWidth > LIMITS.imageWidth.max) {
        imageInsertError.value = t('create_exercise.error_image_width', { max: LIMITS.imageWidth.max })
        return
    }

    const alt      = sanitizeImageAlt(imageAltText.value)
    const hasWidth = Number.isFinite(parsedWidth) && parsedWidth > 0
    const markdown = hasWidth
        ? `<img src="${url}" alt="${alt}" width="${parsedWidth}" />`
        : `![${alt}](${url})`

    const sep = form.value.description && !form.value.description.endsWith('\n') ? '\n\n' : ''
    form.value.description = `${form.value.description}${sep}${markdown}`
    imageUrl.value     = ''
    imageAltText.value = ''
    imageWidth.value   = ''
}

// ── Drag resize ──

const leftWidth  = ref(58)
const isDragging = ref(false)

function startDrag() { isDragging.value = true }
function stopDrag()  { isDragging.value = false }
function onDrag(e) {
    if (!isDragging.value) return
    const pct = (e.clientX / e.currentTarget.getBoundingClientRect().width) * 100
    leftWidth.value = Math.min(Math.max(pct, 51), 75)
}

// ── Editor ──

const editorTheme = ref('vs-dark')
loader.init().then(monaco => {
    try {
        monaco.editor.defineTheme('dracula', activeThemeData)
        editorTheme.value = 'dracula'
    } catch { /* fallback vs-dark */ }
})

// ── Utils ──

function inputPlaceholder(type) {
    return { int: '0', float: '3.14', string: 'hola', bool: 'true', array: '[1, 2, 3]' }[type] ?? '...'
}

onBeforeUnmount(() => {
    if (testCaseErrorTimeoutId) clearTimeout(testCaseErrorTimeoutId)
})
</script>

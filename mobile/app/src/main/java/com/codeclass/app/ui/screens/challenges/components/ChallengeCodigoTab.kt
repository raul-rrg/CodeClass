package com.codeclass.app.ui.screens.challenges.components

import android.graphics.Typeface
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.codeclass.app.data.model.ExerciseDetail
import com.codeclass.app.ui.theme.*
import com.codeclass.app.viewmodel.ChallengeDetailViewModel
import io.github.rosemoe.sora.langs.textmate.TextMateColorScheme
import io.github.rosemoe.sora.langs.textmate.TextMateLanguage
import io.github.rosemoe.sora.langs.textmate.registry.FileProviderRegistry
import io.github.rosemoe.sora.langs.textmate.registry.GrammarRegistry
import io.github.rosemoe.sora.langs.textmate.registry.ThemeRegistry
import io.github.rosemoe.sora.langs.textmate.registry.provider.AssetsFileResolver
import io.github.rosemoe.sora.widget.CodeEditor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.eclipse.tm4e.core.registry.IThemeSource

// Singleton que evita reinicializar TextMate en cada recomposición
internal object TextMateState {
    var initialized = false
}

// Pestaña de código: editor, selector de lenguaje, panel de resultados y barra de acciones
@Composable
fun CodigoTab(exercise: ExerciseDetail, vm: ChallengeDetailViewModel) {
    val context          = LocalContext.current
    val runResult        by vm.runResult.collectAsState()
    val submissionDetail by vm.submissionDetail.collectAsState()
    val isRunning        by vm.isRunning.collectAsState()
    val isSubmitting     by vm.isSubmitting.collectAsState()
    val actionError      by vm.actionError.collectAsState()

    val supportedLangs = remember(exercise.templates) {
        val preferred = listOf("javascript", "python", "java")
        (exercise.templates?.keys?.toList()?.takeIf { it.isNotEmpty() } ?: listOf("python"))
            .sortedBy { preferred.indexOf(it).takeIf { it >= 0 } ?: Int.MAX_VALUE }
    }
    var selectedLang by remember { mutableStateOf(supportedLangs.first()) }

    val codeByLang = remember(exercise.id) {
        mutableMapOf<String, String>().also { map ->
            exercise.templates?.forEach { (lang, code) -> map[lang] = code }
            supportedLangs.forEach { lang -> map.getOrPut(lang) { "" } }
        }
    }

    val editorHolder = remember { arrayOfNulls<CodeEditor>(1) }

    var textMateReady by remember { mutableStateOf(TextMateState.initialized) }
    LaunchedEffect(Unit) {
        if (!TextMateState.initialized) {
            withContext(Dispatchers.IO) {
                runCatching {
                    FileProviderRegistry.getInstance().addFileProvider(AssetsFileResolver(context.assets))
                    GrammarRegistry.getInstance().loadGrammars("textmate/languages.json")
                    ThemeRegistry.getInstance().loadTheme(
                        IThemeSource.fromInputStream(
                            context.assets.open("textmate/themes/dracula.json"),
                            "dracula.json",
                            null,
                        )
                    )
                    ThemeRegistry.getInstance().setTheme("Dracula")
                }.onSuccess {
                    TextMateState.initialized = true
                }.onFailure { e ->
                    Log.e("TextMate", "Init failed: ${e.message}", e)
                }
            }
        }
        textMateReady = true
    }

    val scopeByLang = mapOf("python" to "source.python", "javascript" to "source.js", "java" to "source.java")

    Column(modifier = Modifier.fillMaxSize()) {
        LangSelectorRow(
            langs    = supportedLangs,
            selected = selectedLang,
            onSelect = { lang ->
                editorHolder[0]?.text?.toString()?.let { codeByLang[selectedLang] = it }
                selectedLang = lang
            },
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 16.dp, vertical = 4.dp)
                .clip(RoundedCornerShape(12.dp))
                .border(1.dp, Color.White.copy(0.08f), RoundedCornerShape(12.dp)),
        ) {
            if (textMateReady) {
                key(selectedLang) {
                    AndroidView(
                        factory = { ctx ->
                            CodeEditor(ctx).apply {
                                setText(codeByLang[selectedLang] ?: "")
                                typefaceText = Typeface.MONOSPACE
                                setTextSize(13f)
                                val cornerPx = 12f * ctx.resources.displayMetrics.density
                                val bg = android.graphics.drawable.GradientDrawable().also {
                                    it.setColor(android.graphics.Color.parseColor("#282A36"))
                                    it.cornerRadius = cornerPx
                                }
                                background = bg
                                outlineProvider = android.view.ViewOutlineProvider.BACKGROUND
                                clipToOutline   = true
                                if (TextMateState.initialized) {
                                    runCatching {
                                        colorScheme = TextMateColorScheme.create(ThemeRegistry.getInstance())
                                        val scope = scopeByLang[selectedLang] ?: "source.python"
                                        setEditorLanguage(TextMateLanguage.create(scope, true))
                                    }.onFailure { e ->
                                        Log.e("TextMate", "Language setup failed: ${e.message}", e)
                                    }
                                }
                            }.also { editorHolder[0] = it }
                        },
                        modifier = Modifier.fillMaxSize(),
                    )
                }
            } else {
                CircularProgressIndicator(
                    color       = ColorPrimary,
                    strokeWidth = 2.dp,
                    modifier    = Modifier.size(32.dp).align(Alignment.Center),
                )
            }
        }

        val isEvaluating = isSubmitting && submissionDetail == null && actionError == null
        val hasResult    = runResult != null || submissionDetail != null || actionError != null
        if (hasResult || isEvaluating) {
            ResultsPanel(
                runResult        = runResult,
                submissionDetail = submissionDetail,
                isEvaluating     = isEvaluating,
                actionError      = actionError,
                exercise         = exercise,
                onDismiss        = vm::clearResults,
            )
        }

        CodeActionBar(
            isRunning    = isRunning,
            isSubmitting = isSubmitting,
            onRun = {
                val code = editorHolder[0]?.text?.toString() ?: codeByLang[selectedLang] ?: ""
                vm.runCode(selectedLang, code)
            },
            onSubmit = {
                val code = editorHolder[0]?.text?.toString() ?: codeByLang[selectedLang] ?: ""
                vm.submitCode(selectedLang, code)
            },
        )
    }
}

// Fila de chips para seleccionar el lenguaje de programación
@Composable
private fun LangSelectorRow(langs: List<String>, selected: String, onSelect: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        langs.forEach { lang ->
            val isSelected = lang == selected
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isSelected) ColorPrimary.copy(alpha = 0.15f) else Color(0xFF0D1117))
                    .border(
                        1.dp,
                        if (isSelected) ColorPrimary.copy(alpha = 0.5f) else Color.White.copy(alpha = 0.1f),
                        RoundedCornerShape(8.dp),
                    )
                    .clickable { onSelect(lang) }
                    .padding(horizontal = 14.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text       = lang.replaceFirstChar { it.uppercase() },
                    fontSize   = 12.sp,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                    color      = if (isSelected) ColorPrimary else ColorMuted,
                    fontFamily = JetBrainsMonoFamily,
                )
            }
        }
    }
}

// Barra inferior con botones de ejecutar y enviar solución
@Composable
internal fun CodeActionBar(
    isRunning:    Boolean,
    isSubmitting: Boolean,
    onRun:        () -> Unit,
    onSubmit:     () -> Unit,
) {
    val busy = isRunning || isSubmitting
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(ColorBase)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        OutlinedButton(
            onClick        = onRun,
            enabled        = !busy,
            modifier       = Modifier.weight(1f),
            shape          = RoundedCornerShape(10.dp),
            border         = BorderStroke(1.dp, if (busy) ColorMuted.copy(0.3f) else ColorPrimary.copy(0.6f)),
            contentPadding = PaddingValues(vertical = 10.dp),
        ) {
            if (isRunning) {
                CircularProgressIndicator(modifier = Modifier.size(15.dp), strokeWidth = 2.dp, color = ColorPrimary)
            } else {
                Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp), tint = ColorPrimary)
                Spacer(Modifier.width(5.dp))
                Text("Run", color = ColorPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, fontFamily = SpaceGroteskFamily)
            }
        }

        Button(
            onClick        = onSubmit,
            enabled        = !busy,
            modifier       = Modifier.weight(1f),
            shape          = RoundedCornerShape(10.dp),
            colors         = ButtonDefaults.buttonColors(
                containerColor         = ColorPrimary,
                disabledContainerColor = ColorPrimary.copy(alpha = 0.3f),
            ),
            contentPadding = PaddingValues(vertical = 10.dp),
        ) {
            if (isSubmitting) {
                CircularProgressIndicator(modifier = Modifier.size(15.dp), strokeWidth = 2.dp, color = Color.White)
            } else {
                Text("Submit", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, fontFamily = SpaceGroteskFamily)
            }
        }
    }
}

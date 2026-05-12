package com.codeclass.app.ui.screens.auth

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codeclass.app.ui.theme.*

private val CODE_LINES = listOf(
    "// Maximum Subarray — Kadane",
    "function maxSubArray(nums) {",
    "  let best = nums[0], curr = nums[0]",
    "  for (let i = 1; i < nums.length; i++) {",
    "    curr = Math.max(nums[i], curr + nums[i])",
    "    best = Math.max(best, curr)",
    "  }",
    "  return best",
    "}",
    "",
    "// 1. Two Sum — HashMap",
    "function twoSum(nums, target) {",
    "  const seen = new Map()",
    "  for (let i = 0; i < nums.length; i++) {",
    "    const need = target - nums[i]",
    "    if (seen.has(need)) return [seen.get(need), i]",
    "    seen.set(nums[i], i)",
    "  }",
    "}",
    "",
    "// 20. Valid Parentheses — Stack",
    "function isValid(s) {",
    "  const pairs = { ')': '(', ']': '[', '}': '{' }",
    "  const stack = []",
    "  for (const ch of s) {",
    "    if (ch in pairs) {",
    "      if (stack.pop() !== pairs[ch]) return false",
    "    } else stack.push(ch)",
    "  }",
    "  return stack.length === 0",
    "}",
    "",
    "// Binary Search",
    "function binarySearch(arr, target) {",
    "  let lo = 0, hi = arr.length - 1",
    "  while (lo <= hi) {",
    "    const mid = (lo + hi) >> 1",
    "    if (arr[mid] === target) return mid",
    "    if (arr[mid] < target) lo = mid + 1",
    "    else hi = mid - 1",
    "  }",
    "  return -1",
    "}",
    "",
    "// Fibonacci — memoización",
    "function fib(n, memo = {}) {",
    "  if (n <= 1) return n",
    "  if (memo[n]) return memo[n]",
    "  return memo[n] = fib(n - 1, memo) + fib(n - 2, memo)",
    "}",
)

@Composable
private fun ScrollingCodeColumn(
    lines: List<String>,
    scrollFraction: Float,
    alpha: Float,
    modifier: Modifier = Modifier,
) {
    var singleBlockHeight by remember { mutableStateOf(1f) }
    val translationY = -(scrollFraction * singleBlockHeight)

    Box(modifier = modifier.clipToBounds()) {
        Column(
            modifier = Modifier
                .graphicsLayer(translationY = translationY)
                .onGloballyPositioned { coords ->
                    singleBlockHeight = coords.size.height / 2f
                },
        ) {
            // dos copias para loop infinito sin salto visual
            repeat(2) {
                lines.forEach { line ->
                    Text(
                        text = line.ifEmpty { " " },
                        color = Color.White.copy(alpha = alpha),
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        lineHeight = 18.sp,
                        softWrap = false,
                        modifier = Modifier.padding(horizontal = 6.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun ScrollingCodeBackground() {
    val transition = rememberInfiniteTransition(label = "code-bg")

    val scroll1 by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(28000, easing = LinearEasing)),
        label = "col1",
    )
    val scroll2 by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(22000, easing = LinearEasing)),
        label = "col2",
    )

    val linesShifted = remember {
        val half = CODE_LINES.size / 2
        CODE_LINES.drop(half) + CODE_LINES.take(half)
    }

    Row(modifier = Modifier.fillMaxSize()) {
        ScrollingCodeColumn(
            lines = CODE_LINES,
            scrollFraction = scroll1,
            alpha = 0.13f,
            modifier = Modifier.weight(1f).fillMaxHeight(),
        )
        ScrollingCodeColumn(
            lines = linesShifted,
            scrollFraction = scroll2,
            alpha = 0.08f,
            modifier = Modifier.weight(1f).fillMaxHeight(),
        )
    }
}

@Composable
fun WelcomeScreen(
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit,
    onExploreClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ColorBase),
    ) {
        ScrollingCodeBackground()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp)
                .systemBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.weight(1.2f))

            // Icono </>
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = ColorPrimary,
                modifier = Modifier.size(80.dp),
                shadowElevation = 8.dp,
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "</>",
                        color = Color.White,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            Text(
                text = "CodeClass",
                fontSize = 42.sp,
                fontWeight = FontWeight.Bold,
                color = ColorTitle,
                fontFamily = SpaceGroteskFamily,
            )

            Spacer(Modifier.height(12.dp))

            Text(
                text = "La plataforma donde profesores\ncrean retos y estudiantes los resuelven.",
                fontSize = 15.sp,
                color = ColorSubtitle,
                textAlign = TextAlign.Center,
                lineHeight = 23.sp,
                fontFamily = SpaceGroteskFamily,
            )

            Spacer(Modifier.height(10.dp))

            Text(
                text = "Designed to teach. Built to learn.",
                fontSize = 13.sp,
                color = ColorPrimary,
                fontStyle = FontStyle.Italic,
                fontFamily = SpaceGroteskFamily,
            )

            Spacer(Modifier.weight(1f))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Button(
                    onClick = onLoginClick,
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ColorPrimary),
                    shape = RoundedCornerShape(12.dp),
                ) {
                    Text(
                        text = "INICIAR SESIÓN",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp,
                        fontFamily = SpaceGroteskFamily,
                    )
                }

                OutlinedButton(
                    onClick = onRegisterClick,
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.25f)),
                    shape = RoundedCornerShape(12.dp),
                ) {
                    Text(
                        text = "REGISTRARSE",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp,
                        color = ColorTitle,
                        fontFamily = SpaceGroteskFamily,
                    )
                }

                Spacer(Modifier.height(4.dp))

                TextButton(onClick = onExploreClick) {
                    Text(
                        text = "Explorar sin cuenta →",
                        fontSize = 13.sp,
                        color = ColorSubtitle,
                        fontFamily = SpaceGroteskFamily,
                    )
                }
            }
        }
    }
}

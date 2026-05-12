package com.codeclass.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val AppColorScheme = darkColorScheme(
    primary        = ColorPrimary,
    onPrimary      = ColorTitle,
    secondary      = ColorAccent,
    onSecondary    = ColorTitle,
    background     = ColorBase,
    onBackground   = ColorTitle,
    surface        = ColorSurface,
    onSurface      = ColorTitle,
    surfaceVariant = ColorSurface,
    onSurfaceVariant = ColorSubtitle,
)

@Composable
fun CodeClassTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = AppColorScheme,
        typography  = Typography,
        content     = content
    )
}

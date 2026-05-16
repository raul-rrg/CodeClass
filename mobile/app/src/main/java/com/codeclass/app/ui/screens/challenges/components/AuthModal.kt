package com.codeclass.app.ui.screens.challenges.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.codeclass.app.ui.theme.*

// Modal de acceso restringido para usuarios no autenticados
@Composable
fun AuthModal(onLogin: () -> Unit, onRegister: () -> Unit, onDismiss: () -> Unit) {
    Dialog(
        onDismissRequest = onDismiss,
        properties       = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.6f))
                .clickable(onClick = onDismiss),
            contentAlignment = Alignment.Center,
        ) {
            Surface(
                modifier = Modifier
                    .padding(horizontal = 32.dp)
                    .clickable(enabled = false, onClick = {}),
                shape = RoundedCornerShape(20.dp),
                color = ColorSurface,
            ) {
                Column(
                    modifier            = Modifier.padding(28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                ) {
                    // Botón de cerrar en la esquina superior derecha
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.TopEnd) {
                        IconButton(
                            onClick  = onDismiss,
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.White.copy(0.05f))
                                .border(1.dp, Color.White.copy(0.1f), RoundedCornerShape(8.dp)),
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = ColorMuted, modifier = Modifier.size(16.dp))
                        }
                    }

                    // Icono de candado
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(ColorPrimary.copy(0.1f))
                            .border(1.dp, ColorPrimary.copy(0.25f), CircleShape),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(Icons.Default.Lock, contentDescription = null, tint = ColorAccent, modifier = Modifier.size(28.dp))
                    }

                    // Título y descripción
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Text("Acceso restringido", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = ColorTitle)
                        Text(
                            "Inicia sesión para acceder a este reto y hacer seguimiento de tu progreso.",
                            fontSize   = 13.sp,
                            color      = ColorBody,
                            textAlign  = TextAlign.Center,
                            lineHeight = 19.sp,
                        )
                    }

                    // Botones de acción
                    Column(
                        modifier            = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        Button(
                            onClick  = onLogin,
                            modifier = Modifier.fillMaxWidth().height(44.dp),
                            shape    = RoundedCornerShape(10.dp),
                            colors   = ButtonDefaults.buttonColors(containerColor = ColorPrimary),
                        ) {
                            Text("Iniciar sesión", fontWeight = FontWeight.Bold)
                        }
                        OutlinedButton(
                            onClick  = onRegister,
                            modifier = Modifier.fillMaxWidth().height(44.dp),
                            shape    = RoundedCornerShape(10.dp),
                            colors   = ButtonDefaults.outlinedButtonColors(contentColor = ColorSubtitle),
                            border   = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(0.15f)),
                        ) {
                            Text("Crear cuenta", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

package com.codeclass.app.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.codeclass.app.data.api.RetrofitClient
import com.codeclass.app.data.local.TokenDataStore
import com.codeclass.app.data.repository.AuthRepository
import com.codeclass.app.ui.theme.*
import com.codeclass.app.viewmodel.AuthUiState
import com.codeclass.app.viewmodel.AuthViewModel

@Composable
fun LoginScreen(onLoginSuccess: () -> Unit, onGoToRegister: () -> Unit, onBack: () -> Unit) {
    val context = LocalContext.current

    val vm: AuthViewModel = viewModel(factory = object : ViewModelProvider.Factory {
        override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
            val ds = TokenDataStore(context)
            val api = RetrofitClient.create(ds)
            @Suppress("UNCHECKED_CAST")
            return AuthViewModel(AuthRepository(api, ds)) as T
        }
    })

    val state by vm.state.collectAsState()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf("") }

    LaunchedEffect(state) {
        if (state is AuthUiState.Success) {
            vm.resetState()
            onLoginSuccess()
        }
    }

    val isLoading = state is AuthUiState.Loading
    val serverError = (state as? AuthUiState.Error)?.message ?: ""

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ColorBase)
            .systemBarsPadding(),
    ) {
        // Contenido superior
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(top = 16.dp),
        ) {
            // Back button — cuadrado redondeado
            Surface(
                onClick = onBack,
                shape = RoundedCornerShape(12.dp),
                color = ColorSurface,
                modifier = Modifier.size(40.dp),
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver",
                        tint = ColorTitle,
                        modifier = Modifier.size(20.dp),
                    )
                }
            }

            Spacer(Modifier.height(32.dp))

            Text(
                "De vuelta a codear.",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = ColorTitle,
            )
            Spacer(Modifier.height(6.dp))
            Text(
                "Continúa donde lo dejaste.",
                fontSize = 14.sp,
                color = ColorBody,
            )

            Spacer(Modifier.height(32.dp))

            // Email
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    "EMAIL",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = ColorMuted,
                    letterSpacing = 1.sp,
                )
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it; emailError = "" },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("ada@codeclass.dev", color = ColorMuted) },
                    isError = emailError.isNotEmpty(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = authFieldColors(),
                )
                if (emailError.isNotEmpty()) {
                    Text(emailError, fontSize = 11.sp, color = Color(0xFFEF4444))
                }
            }

            Spacer(Modifier.height(20.dp))

            // Contraseña
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    "CONTRASEÑA",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = ColorMuted,
                    letterSpacing = 1.sp,
                )
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("••••••••", color = ColorMuted) },
                    trailingIcon = {
                        IconButton(onClick = { showPassword = !showPassword }) {
                            Icon(
                                if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = null,
                                tint = ColorMuted,
                            )
                        }
                    },
                    visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = authFieldColors(),
                )
            }

            // ¿Olvidaste tu contraseña? — alineado a la derecha
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                TextButton(
                    onClick = { /* TODO: recuperar contraseña */ },
                    contentPadding = PaddingValues(vertical = 4.dp),
                ) {
                    Text("¿Olvidaste tu contraseña?", fontSize = 13.sp, color = ColorAccent)
                }
            }

            if (serverError.isNotEmpty()) {
                Spacer(Modifier.height(8.dp))
                Text(serverError, fontSize = 12.sp, color = Color(0xFFEF4444))
            }
        }

        // Sección fija en el fondo
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Button(
                onClick = {
                    val valid = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
                    if (!valid) { emailError = "Email no válido"; return@Button }
                    vm.login(email.trim(), password)
                },
                enabled = email.isNotBlank() && password.isNotBlank() && !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(containerColor = ColorPrimary),
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = ColorTitle, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                } else {
                    Text("ENTRAR", fontWeight = FontWeight.Bold, fontSize = 15.sp, letterSpacing = 1.sp)
                }
            }

            Spacer(Modifier.height(16.dp))

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("¿Nuevo en CodeClass?", fontSize = 13.sp, color = ColorBody)
                TextButton(
                    onClick = onGoToRegister,
                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp),
                    modifier = Modifier.height(32.dp),
                ) {
                    Text("Regístrate", fontSize = 13.sp, color = ColorAccent, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun authFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = ColorTitle,
    unfocusedTextColor = ColorTitle,
    focusedBorderColor = ColorPrimary.copy(alpha = 0.6f),
    unfocusedBorderColor = Color.White.copy(alpha = 0.1f),
    cursorColor = ColorPrimary,
    focusedContainerColor = ColorSurface,
    unfocusedContainerColor = ColorSurface,
)

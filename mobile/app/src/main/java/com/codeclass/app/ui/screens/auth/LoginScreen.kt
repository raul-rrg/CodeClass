package com.codeclass.app.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.codeclass.app.data.api.RetrofitClient
import com.codeclass.app.data.local.TokenDataStore
import com.codeclass.app.data.repository.AuthRepository
import com.codeclass.app.ui.theme.*
import com.codeclass.app.viewmodel.AuthUiState
import com.codeclass.app.viewmodel.AuthViewModel
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModelProvider

@Composable
fun LoginScreen(onLoginSuccess: () -> Unit, onGoToRegister: () -> Unit, onBack: () -> Unit) {
    val context = LocalContext.current

    // Construcción manual del ViewModel sin Hilt
    val vm: AuthViewModel = viewModel(factory = object : ViewModelProvider.Factory {
        override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
            val ds = TokenDataStore(context)
            val api = RetrofitClient.create(ds)
            @Suppress("UNCHECKED_CAST")
            return AuthViewModel(AuthRepository(api, ds)) as T
        }
    })

    val state by vm.state.collectAsState()

    // Campos del formulario
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf("") }

    // Navega al éxito
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
            .background(ColorBase),
    ) {
        // Back button
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .systemBarsPadding()
                .padding(4.dp)
                .align(Alignment.TopStart),
        ) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Volver",
                tint = ColorSubtitle,
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text("Iniciar sesión", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = ColorTitle)
            Spacer(Modifier.height(4.dp))
            Text("Bienvenido de nuevo", fontSize = 14.sp, color = ColorBody)
            Spacer(Modifier.height(28.dp))

            // --- Card ---
            Surface(
                color = ColorSurface,
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {

                    // Email
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Email", fontSize = 13.sp, color = ColorSubtitle)
                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it; emailError = "" },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("tu@email.com", color = ColorMuted) },
                            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = ColorMuted) },
                            isError = emailError.isNotEmpty(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            singleLine = true,
                            colors = authFieldColors(),
                        )
                        if (emailError.isNotEmpty()) Text(emailError, fontSize = 11.sp, color = Color(0xFFEF4444))
                    }

                    // Contraseña
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Contraseña", fontSize = 13.sp, color = ColorSubtitle)
                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("••••••••", color = ColorMuted) },
                            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = ColorMuted) },
                            trailingIcon = {
                                // Toggle visibilidad de contraseña
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
                            colors = authFieldColors(),
                        )
                    }

                    // Error del servidor
                    if (serverError.isNotEmpty()) {
                        Text(serverError, fontSize = 12.sp, color = Color(0xFFEF4444))
                    }

                    // Botón submit
                    Button(
                        onClick = {
                            val valid = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
                            if (!valid) { emailError = "Email no válido"; return@Button }
                            vm.login(email.trim(), password)
                        },
                        enabled = email.isNotBlank() && password.isNotBlank() && !isLoading,
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ColorPrimary),
                    ) {
                        if (isLoading) CircularProgressIndicator(color = ColorTitle, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                        else Text("Iniciar sesión", fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Link a registro
            Row {
                Text("¿No tienes cuenta? ", fontSize = 13.sp, color = ColorBody)
                TextButton(onClick = onGoToRegister, contentPadding = PaddingValues(0.dp)) {
                    Text("Regístrate", fontSize = 13.sp, color = ColorAccent, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// Colores compartidos para los campos de texto — evita repetir en Register
@Composable
fun authFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = ColorTitle,
    unfocusedTextColor = ColorTitle,
    focusedBorderColor = ColorPrimary.copy(alpha = 0.6f),
    unfocusedBorderColor = Color.White.copy(alpha = 0.1f),
    cursorColor = ColorPrimary,
    focusedContainerColor = ColorBase,
    unfocusedContainerColor = ColorBase,
)

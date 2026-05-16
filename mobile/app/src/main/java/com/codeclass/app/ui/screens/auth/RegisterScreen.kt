package com.codeclass.app.ui.screens.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onGoToLogin: () -> Unit,
    onBack: () -> Unit = onGoToLogin,
) {
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

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordConfirmation by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("student") }
    var showPassword by remember { mutableStateOf(false) }
    var showPasswordConfirmation by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf("") }

    LaunchedEffect(state) {
        if (state is AuthUiState.Success) {
            vm.resetState()
            onRegisterSuccess()
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
        // Contenido superior con scroll
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .padding(top = 16.dp)
                .padding(bottom = 120.dp),
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
                "Crea tu cuenta",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = ColorTitle,
            )
            Spacer(Modifier.height(6.dp))
            Text(
                "Empieza tu camino como future dev.",
                fontSize = 14.sp,
                color = ColorBody,
            )

            Spacer(Modifier.height(32.dp))

            // NOMBRE
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("NOMBRE", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = ColorMuted, letterSpacing = 1.sp)
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Ada Lovelace", color = ColorMuted) },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = authFieldColors(),
                )
            }

            Spacer(Modifier.height(20.dp))

            // EMAIL
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("EMAIL", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = ColorMuted, letterSpacing = 1.sp)
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

            // CONTRASEÑA
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("CONTRASEÑA", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = ColorMuted, letterSpacing = 1.sp)
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it; passwordError = "" },
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

            Spacer(Modifier.height(20.dp))

            // CONFIRMAR CONTRASEÑA (requerido por el backend)
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("CONFIRMAR CONTRASEÑA", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = ColorMuted, letterSpacing = 1.sp)
                OutlinedTextField(
                    value = passwordConfirmation,
                    onValueChange = { passwordConfirmation = it; passwordError = "" },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("••••••••", color = ColorMuted) },
                    trailingIcon = {
                        IconButton(onClick = { showPasswordConfirmation = !showPasswordConfirmation }) {
                            Icon(
                                if (showPasswordConfirmation) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = null,
                                tint = ColorMuted,
                            )
                        }
                    },
                    isError = passwordError.isNotEmpty(),
                    visualTransformation = if (showPasswordConfirmation) VisualTransformation.None else PasswordVisualTransformation(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = authFieldColors(),
                )
                if (passwordError.isNotEmpty()) {
                    Text(passwordError, fontSize = 11.sp, color = Color(0xFFEF4444))
                }
            }

            Spacer(Modifier.height(24.dp))

            // SOY — selector de rol
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("SOY", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = ColorMuted, letterSpacing = 1.sp)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    RoleToggleButton(
                        label = "Estudiante",
                        selected = role == "student",
                        onClick = { role = "student" },
                        modifier = Modifier.weight(1f),
                    )
                    RoleToggleButton(
                        label = "Profesor",
                        selected = role == "teacher",
                        onClick = { role = "teacher" },
                        modifier = Modifier.weight(1f),
                    )
                }
            }

            if (serverError.isNotEmpty()) {
                Spacer(Modifier.height(12.dp))
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
                    val emailOk = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
                    if (!emailOk) { emailError = "Email no válido"; return@Button }
                    if (password.length < 8) { passwordError = "Mínimo 8 caracteres"; return@Button }
                    if (password != passwordConfirmation) { passwordError = "Las contraseñas no coinciden"; return@Button }
                    vm.register(name.trim(), email.trim(), password, passwordConfirmation, role)
                },
                enabled = name.isNotBlank() && email.isNotBlank() && password.isNotBlank() && passwordConfirmation.isNotBlank() && !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(containerColor = ColorPrimary),
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = ColorTitle, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                } else {
                    Text("CREAR CUENTA", fontWeight = FontWeight.Bold, fontSize = 15.sp, letterSpacing = 1.sp)
                }
            }

            Spacer(Modifier.height(16.dp))

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("¿Ya tienes cuenta?", fontSize = 13.sp, color = ColorBody)
                TextButton(
                    onClick = onGoToLogin,
                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp),
                    modifier = Modifier.height(32.dp),
                ) {
                    Text("Inicia sesión", fontSize = 13.sp, color = ColorAccent, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun RoleToggleButton(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(48.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = if (selected) ColorPrimary else ColorSurface,
            contentColor = ColorTitle,
        ),
        border = BorderStroke(
            width = if (selected) 1.5.dp else 1.dp,
            color = if (selected) ColorPrimary else Color.White.copy(alpha = 0.12f),
        ),
    ) {
        Text(label, fontWeight = FontWeight.Bold, fontSize = 14.sp)
    }
}

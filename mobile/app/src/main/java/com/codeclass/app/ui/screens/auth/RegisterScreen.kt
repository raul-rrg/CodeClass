package com.codeclass.app.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
fun RegisterScreen(onRegisterSuccess: () -> Unit, onGoToLogin: () -> Unit) {
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
            .background(ColorBase),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()) // scroll por si el teclado empuja el contenido
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text("Crear cuenta", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = ColorTitle)
            Spacer(Modifier.height(4.dp))
            Text("Únete a CodeClass", fontSize = 14.sp, color = ColorBody)
            Spacer(Modifier.height(24.dp))

            Surface(
                color = ColorSurface,
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {

                    // Nombre
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Nombre", fontSize = 13.sp, color = ColorSubtitle)
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("Tu nombre", color = ColorMuted) },
                            leadingIcon = { Icon(Icons.Default.Person, null, tint = ColorMuted) },
                            singleLine = true,
                            colors = authFieldColors(),
                        )
                    }

                    // Email
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Email", fontSize = 13.sp, color = ColorSubtitle)
                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it; emailError = "" },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("tu@email.com", color = ColorMuted) },
                            leadingIcon = { Icon(Icons.Default.Email, null, tint = ColorMuted) },
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
                            onValueChange = { password = it; passwordError = "" },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("Mínimo 8 caracteres", color = ColorMuted) },
                            leadingIcon = { Icon(Icons.Default.Lock, null, tint = ColorMuted) },
                            trailingIcon = {
                                IconButton(onClick = { showPassword = !showPassword }) {
                                    Icon(if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility, null, tint = ColorMuted)
                                }
                            },
                            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                            singleLine = true,
                            colors = authFieldColors(),
                        )
                    }

                    // Confirmar contraseña
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Confirmar contraseña", fontSize = 13.sp, color = ColorSubtitle)
                        OutlinedTextField(
                            value = passwordConfirmation,
                            onValueChange = { passwordConfirmation = it; passwordError = "" },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("••••••••", color = ColorMuted) },
                            leadingIcon = { Icon(Icons.Default.Lock, null, tint = ColorMuted) },
                            trailingIcon = {
                                IconButton(onClick = { showPasswordConfirmation = !showPasswordConfirmation }) {
                                    Icon(if (showPasswordConfirmation) Icons.Default.VisibilityOff else Icons.Default.Visibility, null, tint = ColorMuted)
                                }
                            },
                            isError = passwordError.isNotEmpty(),
                            visualTransformation = if (showPasswordConfirmation) VisualTransformation.None else PasswordVisualTransformation(),
                            singleLine = true,
                            colors = authFieldColors(),
                        )
                        if (passwordError.isNotEmpty()) Text(passwordError, fontSize = 11.sp, color = Color(0xFFEF4444))
                    }

                    // Selector de rol — igual que la web: dos botones toggle
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Rol", fontSize = 13.sp, color = ColorSubtitle)
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Button(
                                onClick = { role = "student" },
                                modifier = Modifier.weight(1f).height(44.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (role == "student") ColorPrimary else ColorBase,
                                    contentColor = if (role == "student") ColorTitle else ColorSubtitle,
                                ),
                                shape = MaterialTheme.shapes.extraSmall,
                            ) { Text("Estudiante", fontSize = 13.sp, fontWeight = FontWeight.Bold) }

                            Button(
                                onClick = { role = "teacher" },
                                modifier = Modifier.weight(1f).height(44.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (role == "teacher") ColorPrimary else ColorBase,
                                    contentColor = if (role == "teacher") ColorTitle else ColorSubtitle,
                                ),
                                shape = MaterialTheme.shapes.extraSmall,
                            ) { Text("Profesor", fontSize = 13.sp, fontWeight = FontWeight.Bold) }
                        }
                    }

                    if (serverError.isNotEmpty()) {
                        Text(serverError, fontSize = 12.sp, color = Color(0xFFEF4444))
                    }

                    // Botón submit
                    Button(
                        onClick = {
                            // Validación local antes de llamar al backend
                            val emailOk = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
                            if (!emailOk) { emailError = "Email no válido"; return@Button }
                            if (password.length < 8) { passwordError = "Mínimo 8 caracteres"; return@Button }
                            if (password != passwordConfirmation) { passwordError = "Las contraseñas no coinciden"; return@Button }
                            vm.register(name.trim(), email.trim(), password, passwordConfirmation, role)
                        },
                        enabled = name.isNotBlank() && email.isNotBlank() && password.isNotBlank() && passwordConfirmation.isNotBlank() && !isLoading,
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ColorPrimary),
                    ) {
                        if (isLoading) CircularProgressIndicator(color = ColorTitle, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                        else Text("Crear cuenta", fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            Row {
                Text("¿Ya tienes cuenta? ", fontSize = 13.sp, color = ColorBody)
                TextButton(onClick = onGoToLogin, contentPadding = PaddingValues(0.dp)) {
                    Text("Inicia sesión", fontSize = 13.sp, color = ColorAccent, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

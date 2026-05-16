package com.codeclass.app.ui.screens.profile

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.codeclass.app.data.api.RetrofitClient
import com.codeclass.app.data.local.TokenDataStore
import com.codeclass.app.data.model.User
import com.codeclass.app.ui.components.AppBottomBar
import com.codeclass.app.ui.components.AppTab
import com.codeclass.app.ui.theme.*
import com.codeclass.app.viewmodel.ProfileUiState
import com.codeclass.app.viewmodel.ProfileViewModel
import java.text.SimpleDateFormat
import java.util.Locale

private val CardBg     = Color(0xFF111827)
private val CardBorder = Color(0xFF1F2937)
private val InputBg    = Color(0xFF1A2234)
private val BlueBg     = Color(0xFF1E3A5F)
private val BlueBorder = Color(0xFF2563EB)
private val BlueText   = Color(0xFF93C5FD)
private val GreenText  = Color(0xFF4ADE80)
private val RedText    = Color(0xFFF87171)
private val RedBg      = Color(0xFF7F1D1D).copy(alpha = 0.4f)

@Composable
fun ProfileScreen(
    onLogout:           () -> Unit,
    onChallengesClick:  () -> Unit,
    onRankingClick:     () -> Unit,
    onMyExercisesClick: () -> Unit,
    isTeacher:          Boolean = false,
) {
    val context = LocalContext.current
    val vm: ProfileViewModel = viewModel(factory = object : ViewModelProvider.Factory {
        override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
            val ds    = TokenDataStore(context)
            val prefs = context.getSharedPreferences("editor_prefs", Context.MODE_PRIVATE)
            @Suppress("UNCHECKED_CAST")
            return ProfileViewModel(RetrofitClient.create(ds), ds, prefs) as T
        }
    })

    val state by vm.state.collectAsState()

    var nameInput       by remember { mutableStateOf("") }
    var currentPassword by remember { mutableStateOf("") }
    var newPassword     by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    // Limpiar los campos de contraseña tras un cambio exitoso
    LaunchedEffect(state.passwordSuccess) {
        if (state.passwordSuccess) {
            currentPassword = ""
            newPassword     = ""
            confirmPassword = ""
        }
    }

    Scaffold(
        containerColor = ColorBase,
        bottomBar = {
            AppBottomBar(
                currentTab         = AppTab.PROFILE,
                onChallengesClick  = onChallengesClick,
                onRankingClick     = onRankingClick,
                onProfileClick     = {},
                onMyExercisesClick = onMyExercisesClick,
                isTeacher          = isTeacher,
                isAuthenticated    = true,
            )
        },
    ) { innerPadding ->
        if (state.isLoading && state.user == null) {
            Box(
                modifier         = Modifier.fillMaxSize().background(ColorBase).padding(innerPadding),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(color = ColorPrimary, strokeWidth = 2.dp)
            }
            return@Scaffold
        }

        LazyColumn(
            modifier            = Modifier.fillMaxSize().background(ColorBase).padding(innerPadding).systemBarsPadding(),
            contentPadding      = PaddingValues(horizontal = 16.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item { ProfileHeader(user = state.user) }
            item { ProfileStats(user = state.user) }
            item {
                ChangeNameSection(
                    state         = state,
                    vm            = vm,
                    nameInput     = nameInput,
                    onInputChange = { nameInput = it },
                )
            }
            item {
                ChangePasswordSection(
                    state           = state,
                    vm              = vm,
                    currentPassword = currentPassword,
                    newPassword     = newPassword,
                    confirmPassword = confirmPassword,
                    onCurrentChange = { currentPassword = it },
                    onNewChange     = { newPassword = it },
                    onConfirmChange = { confirmPassword = it },
                )
            }
            item { LogoutButton(onClick = { vm.logout(); onLogout() }) }
            item { Spacer(Modifier.height(8.dp)) }
        }
    }
}

// Header: avatar circular con inicial, nombre, email y badge de rol
@Composable
private fun ProfileHeader(user: User?) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(CardBg)
            .border(1.dp, CardBorder, RoundedCornerShape(16.dp))
            .padding(20.dp),
    ) {
        Row(
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(BlueBg)
                    .border(2.dp, BlueBorder.copy(alpha = 0.4f), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text       = user?.name?.firstOrNull()?.uppercaseChar()?.toString() ?: "?",
                    fontSize   = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color      = BlueText,
                    fontFamily = SpaceGroteskFamily,
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(user?.name ?: "—", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = ColorTitle, fontFamily = SpaceGroteskFamily)
                Text(user?.email ?: "—", fontSize = 13.sp, color = ColorBody, fontFamily = SpaceGroteskFamily)

                val roleLabel = if (user?.role?.lowercase() == "teacher") "PROFESOR" else "ESTUDIANTE"
                val roleColor = if (user?.role?.lowercase() == "teacher") Color(0xFFC084FC) else BlueText
                val roleBg    = if (user?.role?.lowercase() == "teacher") Color(0xFF581C87).copy(alpha = 0.4f) else BlueBg.copy(alpha = 0.5f)

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(roleBg)
                        .padding(horizontal = 8.dp, vertical = 3.dp),
                ) {
                    Text(roleLabel, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = roleColor, letterSpacing = 0.8.sp, fontFamily = SpaceGroteskFamily)
                }
            }
        }
    }
}

// Fila de 3 tarjetas: ejercicios resueltos, puntos y fecha de registro
@Composable
private fun ProfileStats(user: User?) {
    val memberSince = user?.createdAt?.let { raw ->
        runCatching {
            val sdf  = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", Locale("es", "ES"))
            val date = sdf.parse(raw)
            SimpleDateFormat("MMM yyyy", Locale("es", "ES")).format(date!!)
        }.getOrElse { raw.take(7) }
    } ?: "—"

    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        StatCard(label = "Resueltos", value = "${user?.solvedCount ?: 0}", modifier = Modifier.weight(1f))
        StatCard(label = "Puntos",    value = "${user?.points ?: 0}",      modifier = Modifier.weight(1f))
        StatCard(label = "Miembro",   value = memberSince,                 modifier = Modifier.weight(1f))
    }
}

// Sección para actualizar el nombre de perfil
@Composable
private fun ChangeNameSection(
    state:         ProfileUiState,
    vm:            ProfileViewModel,
    nameInput:     String,
    onInputChange: (String) -> Unit,
) {
    SectionCard {
        Text("Cambiar nombre", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = ColorTitle, fontFamily = SpaceGroteskFamily)
        Text("Actualiza el nombre que aparece en tu perfil", fontSize = 12.sp, color = ColorBody, fontFamily = SpaceGroteskFamily)
        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value         = nameInput,
            onValueChange = {
                onInputChange(it)
                if (state.nameSuccess || state.nameError.isNotEmpty()) vm.clearNameFeedback()
            },
            placeholder = { Text(state.user?.name ?: "Tu nombre", fontSize = 14.sp, color = ColorMuted, fontFamily = SpaceGroteskFamily) },
            singleLine  = true,
            modifier    = Modifier.fillMaxWidth(),
            shape       = RoundedCornerShape(10.dp),
            colors      = OutlinedTextFieldDefaults.colors(
                focusedContainerColor   = InputBg,
                unfocusedContainerColor = InputBg,
                focusedBorderColor      = BlueBorder.copy(alpha = 0.6f),
                unfocusedBorderColor    = CardBorder,
                focusedTextColor        = ColorTitle,
                unfocusedTextColor      = ColorTitle,
                cursorColor             = BlueText,
            ),
            textStyle = LocalTextStyle.current.copy(fontFamily = SpaceGroteskFamily, fontSize = 14.sp),
        )

        Spacer(Modifier.height(8.dp))

        Button(
            onClick  = { vm.updateName(nameInput) },
            enabled  = !state.nameLoading && nameInput.trim().isNotEmpty(),
            shape    = RoundedCornerShape(10.dp),
            colors   = ButtonDefaults.buttonColors(
                containerColor         = BlueBg,
                contentColor           = BlueText,
                disabledContainerColor = BlueBg.copy(alpha = 0.4f),
                disabledContentColor   = BlueText.copy(alpha = 0.4f),
            ),
            modifier = Modifier.fillMaxWidth(),
        ) {
            if (state.nameLoading) {
                CircularProgressIndicator(modifier = Modifier.size(16.dp), color = BlueText, strokeWidth = 2.dp)
            } else {
                Text("Guardar", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, fontFamily = SpaceGroteskFamily)
            }
        }

        if (state.nameSuccess) {
            Spacer(Modifier.height(4.dp))
            Text("Nombre actualizado correctamente", fontSize = 12.sp, color = GreenText, fontFamily = SpaceGroteskFamily)
        }
        if (state.nameError.isNotEmpty()) {
            Spacer(Modifier.height(4.dp))
            Text(state.nameError, fontSize = 12.sp, color = RedText, fontFamily = SpaceGroteskFamily)
        }
    }
}

// Sección para actualizar la contraseña
@Composable
private fun ChangePasswordSection(
    state:           ProfileUiState,
    vm:              ProfileViewModel,
    currentPassword: String,
    newPassword:     String,
    confirmPassword: String,
    onCurrentChange: (String) -> Unit,
    onNewChange:     (String) -> Unit,
    onConfirmChange: (String) -> Unit,
) {
    SectionCard {
        Text("Cambiar contraseña", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = ColorTitle, fontFamily = SpaceGroteskFamily)
        Text("Usa una contraseña de al menos 8 caracteres", fontSize = 12.sp, color = ColorBody, fontFamily = SpaceGroteskFamily)
        Spacer(Modifier.height(12.dp))

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            PasswordField(
                value         = currentPassword,
                onValueChange = {
                    onCurrentChange(it)
                    if (state.passwordSuccess || state.passwordError.isNotEmpty()) vm.clearPasswordFeedback()
                },
                placeholder = "Contraseña actual",
            )
            PasswordField(value = newPassword,     onValueChange = onNewChange,     placeholder = "Nueva contraseña")
            PasswordField(value = confirmPassword, onValueChange = onConfirmChange, placeholder = "Confirmar nueva contraseña")
        }

        Spacer(Modifier.height(8.dp))

        val canSubmit = currentPassword.isNotEmpty() && newPassword.isNotEmpty() && confirmPassword.isNotEmpty()
        Button(
            onClick  = { vm.updatePassword(currentPassword, newPassword, confirmPassword) },
            enabled  = !state.passwordLoading && canSubmit,
            shape    = RoundedCornerShape(10.dp),
            colors   = ButtonDefaults.buttonColors(
                containerColor         = BlueBg,
                contentColor           = BlueText,
                disabledContainerColor = BlueBg.copy(alpha = 0.4f),
                disabledContentColor   = BlueText.copy(alpha = 0.4f),
            ),
            modifier = Modifier.fillMaxWidth(),
        ) {
            if (state.passwordLoading) {
                CircularProgressIndicator(modifier = Modifier.size(16.dp), color = BlueText, strokeWidth = 2.dp)
            } else {
                Text("Cambiar contraseña", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, fontFamily = SpaceGroteskFamily)
            }
        }

        if (state.passwordSuccess) {
            Spacer(Modifier.height(4.dp))
            Text("Contraseña actualizada correctamente", fontSize = 12.sp, color = GreenText, fontFamily = SpaceGroteskFamily)
        }
        if (state.passwordError.isNotEmpty()) {
            Spacer(Modifier.height(4.dp))
            Text(state.passwordError, fontSize = 12.sp, color = RedText, fontFamily = SpaceGroteskFamily)
        }
    }
}

// Botón rojo de cierre de sesión
@Composable
private fun LogoutButton(onClick: () -> Unit) {
    Button(
        onClick  = onClick,
        shape    = RoundedCornerShape(12.dp),
        colors   = ButtonDefaults.buttonColors(containerColor = RedBg, contentColor = RedText),
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
    ) {
        Text("Cerrar sesión", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, fontFamily = SpaceGroteskFamily)
    }
}

// ── Helpers ────────────────────────────────────────────────────────

// Tarjeta contenedora de sección con borde y fondo oscuro
@Composable
private fun SectionCard(content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(CardBg)
            .border(1.dp, CardBorder, RoundedCornerShape(16.dp))
            .padding(20.dp),
        content = content,
    )
}

// Tarjeta de estadística individual: valor grande arriba, etiqueta abajo
@Composable
private fun StatCard(label: String, value: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(CardBg)
            .border(1.dp, CardBorder, RoundedCornerShape(12.dp))
            .padding(vertical = 14.dp, horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(value, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = BlueText, fontFamily = SpaceGroteskFamily, textAlign = TextAlign.Center)
        Text(label, fontSize = 11.sp, color = ColorBody, fontFamily = SpaceGroteskFamily, textAlign = TextAlign.Center)
    }
}

// Campo de texto para passwords con transformación visual oculta
@Composable
private fun PasswordField(value: String, onValueChange: (String) -> Unit, placeholder: String) {
    OutlinedTextField(
        value                = value,
        onValueChange        = onValueChange,
        placeholder          = { Text(placeholder, fontSize = 14.sp, color = ColorMuted, fontFamily = SpaceGroteskFamily) },
        singleLine           = true,
        visualTransformation = PasswordVisualTransformation(),
        keyboardOptions      = KeyboardOptions(keyboardType = KeyboardType.Password),
        modifier             = Modifier.fillMaxWidth(),
        shape                = RoundedCornerShape(10.dp),
        colors               = OutlinedTextFieldDefaults.colors(
            focusedContainerColor   = InputBg,
            unfocusedContainerColor = InputBg,
            focusedBorderColor      = BlueBorder.copy(alpha = 0.6f),
            unfocusedBorderColor    = CardBorder,
            focusedTextColor        = ColorTitle,
            unfocusedTextColor      = ColorTitle,
            cursorColor             = BlueText,
        ),
        textStyle = LocalTextStyle.current.copy(fontFamily = SpaceGroteskFamily, fontSize = 14.sp),
    )
}

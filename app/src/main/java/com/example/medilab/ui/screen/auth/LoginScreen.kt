package com.example.medilab.ui.screen.auth

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.medilab.ui.component.MediLabButton
import com.example.medilab.ui.component.MediLabButtonVariant
import com.example.medilab.ui.component.MediLabPasswordField
import com.example.medilab.ui.component.MediLabTextField
import com.example.medilab.ui.illustration.DoctorIllustration
import com.example.medilab.ui.theme.Spacing

@Composable
fun LoginScreen(
    onLoginSuccess: (role: String) -> Unit,
    onRegisterClick: () -> Unit,
    onForgotClick: () -> Unit,
    viewModel: AuthViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Spacing.lg),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(40.dp))
        Box(modifier = Modifier.height(180.dp), contentAlignment = Alignment.Center) {
            DoctorIllustration()
        }
        Spacer(Modifier.height(Spacing.xl))
        Text(
            text = "Masuk ke MediLab",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "Kelola atau akses hasil lab Anda",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = Spacing.xs)
        )
        Spacer(Modifier.height(Spacing.xxl))
        MediLabTextField(
            value = state.email,
            onValueChange = viewModel::onEmailChange,
            label = "Email",
            keyboardType = KeyboardType.Email,
            isError = state.emailError != null,
            errorMessage = state.emailError
        )
        Spacer(Modifier.height(Spacing.md))
        MediLabPasswordField(
            value = state.password,
            onValueChange = viewModel::onPasswordChange,
            label = "Password",
            isError = state.passwordError != null,
            errorMessage = state.passwordError
        )
        TextButton(
            onClick = onForgotClick,
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Lupa password?", style = MaterialTheme.typography.labelMedium)
        }
        Spacer(Modifier.height(Spacing.lg))
        MediLabButton(
            text = if (state.isLoading) "Memuat..." else "Masuk",
            onClick = { viewModel.login(onLoginSuccess) },
            enabled = !state.isLoading
        )
        state.errorMessage?.let { msg ->
            Spacer(Modifier.height(Spacing.sm))
            Text(msg, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
        }
        Spacer(Modifier.height(Spacing.lg))
        Text(
            text = "Belum punya akun?",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        MediLabButton(
            text = "Daftar sebagai Pasien",
            onClick = onRegisterClick,
            variant = MediLabButtonVariant.OUTLINED
        )
    }
}

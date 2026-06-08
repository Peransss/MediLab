package com.example.medilab.ui.screen.auth

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.medilab.ui.component.MediLabButton
import com.example.medilab.ui.component.MediLabButtonVariant
import com.example.medilab.ui.component.MediLabTextField
import com.example.medilab.ui.component.MediLabTopAppBar
import com.example.medilab.ui.navigation.SharedNavigationViewModel
import com.example.medilab.ui.theme.Spacing

@Composable
fun ForgotPasswordScreen(
    sharedNavigationViewModel: SharedNavigationViewModel = viewModel(),
    viewModel: AuthViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(state.successMessage) {
        if (state.successMessage != null && state.errorMessage == null && !state.isLoading) {
            sharedNavigationViewModel.popBackStack()
            viewModel.clearMessages()
        }
    }

    Scaffold(
        topBar = { MediLabTopAppBar(title = "Reset Password", onBackClick = { sharedNavigationViewModel.popBackStack() }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = Spacing.lg)
        ) {
            Text(
                text = "Lupa Password",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Masukkan email Anda. Kami akan kirim link untuk reset password.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = Spacing.xs)
            )
            Spacer(Modifier.height(Spacing.xl))
            MediLabTextField(
                value = state.email, onValueChange = viewModel::onEmailChange, label = "Email",
                keyboardType = KeyboardType.Email, isError = state.emailError != null, errorMessage = state.emailError
            )
            Spacer(Modifier.height(Spacing.xl))
            state.errorMessage?.let {
                Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.height(Spacing.sm))
            }
            state.successMessage?.let {
                Text(it, color = MaterialTheme.colorScheme.tertiary, style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.height(Spacing.sm))
                MediLabButton(
                    text = "Kembali ke Masuk",
                    onClick = { sharedNavigationViewModel.popBackStack() },
                    variant = MediLabButtonVariant.OUTLINED
                )
            } ?: run {
                MediLabButton(
                    text = if (state.isLoading) "Mengirim..." else "Kirim Email Reset",
                    onClick = viewModel::sendPasswordReset,
                    enabled = !state.isLoading,
                    variant = MediLabButtonVariant.CTA
                )
            }
        }
    }
}

package com.example.medilab.ui.screen.auth

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.medilab.ui.navigation.Route
import com.example.medilab.ui.navigation.SharedNavigationViewModel
import com.example.medilab.ui.theme.Spacing
import com.example.medilab.util.Constants

@Composable
fun LoginScreen(
    sharedNavigationViewModel: SharedNavigationViewModel = viewModel(),
    viewModel: AuthViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var passwordVisible by remember { mutableStateOf(false) }

    LaunchedEffect(state.isLoggedIn, state.userRole) {
        if (state.isLoggedIn && state.userRole != null) {
            val target = when (state.userRole) {
                Constants.ROLE_ADMIN, Constants.ROLE_PETUGAS, Constants.ROLE_DOKTER -> Route.StaffRoot.path
                Constants.ROLE_PASIEN -> Route.PatientRoot.path
                else -> Route.Login.path
            }
            sharedNavigationViewModel.navigateTo(target, popUpTo = "0", inclusive = true)
        }
    }

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
            text = "Selamat Datang Kembali 👋",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "Masuk untuk melanjutkan",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = Spacing.xs)
        )
        Spacer(Modifier.height(Spacing.xxl))
        MediLabTextField(
            value = state.email,
            onValueChange = viewModel::onEmailChange,
            label = "Email",
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.Email,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            keyboardType = KeyboardType.Email,
            isError = state.emailError != null,
            errorMessage = state.emailError
        )
        Spacer(Modifier.height(Spacing.md))
        MediLabPasswordField(
            value = state.password,
            onValueChange = viewModel::onPasswordChange,
            label = "Password",
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.Lock,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            passwordVisible = passwordVisible,
            onVisibilityToggle = { passwordVisible = !passwordVisible },
            isError = state.passwordError != null,
            errorMessage = state.passwordError
        )
        TextButton(
            onClick = { sharedNavigationViewModel.navigateTo(Route.ForgotPassword.path) },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Lupa password?", style = MaterialTheme.typography.labelMedium)
        }
        Spacer(Modifier.height(Spacing.lg))
        MediLabButton(
            text = "Masuk",
            onClick = { viewModel.login() },
            isLoading = state.isLoading,
            enabled = !state.isLoading,
            variant = MediLabButtonVariant.CTA
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
            onClick = { sharedNavigationViewModel.navigateTo(Route.Register.path) },
            variant = MediLabButtonVariant.OUTLINED
        )
    }
}

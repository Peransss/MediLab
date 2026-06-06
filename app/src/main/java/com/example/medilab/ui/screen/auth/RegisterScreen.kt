package com.example.medilab.ui.screen.auth

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.medilab.ui.component.MediLabButton
import com.example.medilab.ui.component.MediLabPasswordField
import com.example.medilab.ui.component.MediLabTextField
import com.example.medilab.ui.component.MediLabTopAppBar
import com.example.medilab.ui.theme.Spacing

@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onBack: () -> Unit,
    viewModel: AuthViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = { MediLabTopAppBar(title = "Daftar Pasien", onBackClick = onBack) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = Spacing.lg)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Buat Akun Baru",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Daftar untuk mengakses hasil lab Anda",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = Spacing.xs)
            )
            Spacer(Modifier.height(Spacing.xl))
            MediLabTextField(value = state.nama, onValueChange = viewModel::onNamaChange, label = "Nama Lengkap")
            Spacer(Modifier.height(Spacing.md))
            MediLabTextField(
                value = state.email, onValueChange = viewModel::onEmailChange, label = "Email",
                keyboardType = KeyboardType.Email, isError = state.emailError != null, errorMessage = state.emailError
            )
            Spacer(Modifier.height(Spacing.md))
            MediLabPasswordField(
                value = state.password, onValueChange = viewModel::onPasswordChange, label = "Password (min. 6 karakter)",
                isError = state.passwordError != null, errorMessage = state.passwordError
            )
            Spacer(Modifier.height(Spacing.md))
            MediLabTextField(value = state.noHP, onValueChange = viewModel::onNoHPChange, label = "No. HP", keyboardType = KeyboardType.Phone)
            Spacer(Modifier.height(Spacing.md))
            MediLabTextField(value = state.alamat, onValueChange = viewModel::onAlamatChange, label = "Alamat", singleLine = false, maxLines = 2)
            Spacer(Modifier.height(Spacing.md))
            MediLabTextField(value = state.tanggalLahir, onValueChange = viewModel::onTanggalLahirChange, label = "Tanggal Lahir (YYYY-MM-DD)")
            Spacer(Modifier.height(Spacing.xl))
            if (state.errorMessage != null) {
                Text(state.errorMessage!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.height(Spacing.sm))
            }
            MediLabButton(
                text = if (state.isLoading) "Memuat..." else "Daftar",
                onClick = { viewModel.registerPatient(onRegisterSuccess) },
                enabled = !state.isLoading
            )
            Spacer(Modifier.height(Spacing.xl))
        }
    }
}

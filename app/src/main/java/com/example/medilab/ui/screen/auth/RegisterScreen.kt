package com.example.medilab.ui.screen.auth

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.medilab.ui.component.MediLabButton
import com.example.medilab.ui.component.MediLabButtonVariant
import com.example.medilab.ui.component.MediLabPasswordField
import com.example.medilab.ui.component.MediLabTextField
import com.example.medilab.ui.component.MediLabTopAppBar
import com.example.medilab.ui.navigation.SharedNavigationViewModel
import com.example.medilab.ui.theme.Spacing
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    sharedNavigationViewModel: SharedNavigationViewModel = viewModel(),
    viewModel: AuthViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    LaunchedEffect(state.successMessage) {
        if (state.successMessage != null && state.errorMessage == null && !state.isLoading) {
            sharedNavigationViewModel.popBackStack()
            viewModel.clearMessages() // Clear message after navigating
        }
    }

    Scaffold(
        topBar = { MediLabTopAppBar(title = "Daftar Pasien", onBackClick = { sharedNavigationViewModel.popBackStack() }) }
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
            MediLabTextField(
                value = state.tanggalLahir,
                onValueChange = viewModel::onTanggalLahirChange,
                label = "Tanggal Lahir (YYYY-MM-DD)",
                readOnly = true, // Make it read-only
                onClick = { showDatePicker = true } // Show date picker on click
            )
            Spacer(Modifier.height(Spacing.xl))

            if (showDatePicker) {
                DatePickerDialog(
                    onDismissRequest = { showDatePicker = false },
                    confirmButton = {
                        MediLabButton(
                            text = "OK",
                            onClick = {
                                datePickerState.selectedDateMillis?.let { millis ->
                                    val date = Date(millis)
                                    val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                                    viewModel.onTanggalLahirChange(formatter.format(date))
                                }
                                showDatePicker = false
                            },
                            fullWidth = false
                        )
                    },
                    dismissButton = {
                        MediLabButton(
                            text = "Cancel",
                            onClick = { showDatePicker = false },
                            fullWidth = false
                        )
                    }
                ) {
                    DatePicker(state = datePickerState)
                }
            }
            if (state.errorMessage != null) {
                Text(state.errorMessage!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.height(Spacing.sm))
            }
            MediLabButton(
                text = if (state.isLoading) "Memuat..." else "Daftar",
                onClick = { viewModel.registerPatient() },
                enabled = !state.isLoading,
                variant = MediLabButtonVariant.CTA
            )
            Spacer(Modifier.height(Spacing.xl))
        }
    }
}

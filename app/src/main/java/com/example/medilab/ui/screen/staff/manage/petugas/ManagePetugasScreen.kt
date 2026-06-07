package com.example.medilab.ui.screen.staff.manage.petugas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.medilab.model.User
import com.example.medilab.ui.component.EmptyState
import com.example.medilab.ui.component.ErrorState
import com.example.medilab.ui.component.LoadingState
import com.example.medilab.ui.component.MediLabButton
import com.example.medilab.ui.component.MediLabCard
import com.example.medilab.ui.component.MediLabPasswordField
import com.example.medilab.ui.component.MediLabTextField
import com.example.medilab.ui.screen.staff.manage.ManageViewModel
import com.example.medilab.ui.theme.Spacing
import com.example.medilab.ui.util.UiState
import com.example.medilab.util.Constants
import com.example.medilab.util.Validators

@Composable
fun ManagePetugasScreen(viewModel: ManageViewModel = viewModel()) {
    val state by viewModel.petugas.collectAsStateWithLifecycle()
    val saveError by viewModel.petugasSaveError.collectAsStateWithLifecycle()
    val saving by viewModel.petugasSaving.collectAsStateWithLifecycle()
    var showSheet by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { viewModel.loadPetugas() }

    Box(modifier = Modifier.fillMaxSize()) {
        when (val s = state) {
            is UiState.Loading -> LoadingState()
            is UiState.Error -> ErrorState(message = s.message, onRetry = { viewModel.loadPetugas() })
            is UiState.Empty -> EmptyState(title = "Belum ada staff")
            is UiState.Success -> LazyColumn(
                contentPadding = androidx.compose.foundation.layout.PaddingValues(Spacing.lg),
                verticalArrangement = Arrangement.spacedBy(Spacing.sm)
            ) {
                items(s.data) { u -> PetugasRow(u) }
            }
        }
        FloatingActionButton(
            onClick = { showSheet = true },
            modifier = Modifier.align(Alignment.BottomEnd).padding(Spacing.lg)
        ) { Icon(Icons.Default.Add, contentDescription = "Tambah") }
    }

    if (showSheet) {
        PetugasFormSheet(
            saving = saving,
            errorMessage = saveError,
            onDismiss = {
                showSheet = false
                viewModel.clearPetugasSaveError()
            },
            onSave = { email, password, nama, role, noHP ->
                viewModel.savePetugas(email, password, nama, role, noHP)
            },
            onSuccess = { showSheet = false }
        )
    }
}

@Composable
private fun PetugasRow(user: User) {
    MediLabCard {
        Column(modifier = Modifier.fillMaxWidth().padding(Spacing.md)) {
            Text(
                text = user.nama.ifBlank { "-" },
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "${user.role.uppercase()} • ${user.email}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PetugasFormSheet(
    saving: Boolean,
    errorMessage: String?,
    onDismiss: () -> Unit,
    onSave: (String, String, String, String, String) -> Unit,
    onSuccess: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var nama by remember { mutableStateOf("") }
    var role by remember { mutableStateOf(Constants.ROLE_PETUGAS) }
    var noHP by remember { mutableStateOf("") }
    var submitted by remember { mutableStateOf(false) }

    val emailError = if (submitted && !Validators.isValidEmail(email)) "Email tidak valid" else null
    val passwordError = if (submitted && !Validators.isValidPassword(password)) "Password minimal 8 karakter" else null
    val noHPError = if (submitted && noHP.isNotBlank() && !Validators.isValidPhone(noHP)) "No HP tidak valid" else null
    val namaError = if (submitted && nama.isBlank()) "Nama harus diisi" else null
    val isValid = Validators.isValidEmail(email) && Validators.isValidPassword(password) && nama.isNotBlank() && noHP.isNotBlank()

    androidx.compose.runtime.LaunchedEffect(saving) {
        if (!saving && submitted && errorMessage == null) {
            onSuccess()
        }
    }

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(modifier = Modifier.padding(Spacing.lg)) {
            Text(
                text = "Tambah Staff",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(Modifier.height(Spacing.lg))
            MediLabTextField(
                value = nama, onValueChange = { nama = it }, label = "Nama",
                isError = namaError != null, errorMessage = namaError
            )
            Spacer(Modifier.height(Spacing.md))
            MediLabTextField(
                value = email, onValueChange = { email = it }, label = "Email",
                keyboardType = KeyboardType.Email,
                isError = emailError != null, errorMessage = emailError
            )
            Spacer(Modifier.height(Spacing.md))
            MediLabPasswordField(
                value = password, onValueChange = { password = it }, label = "Password (min. 6)",
                isError = passwordError != null, errorMessage = passwordError
            )
            Spacer(Modifier.height(Spacing.md))
            RoleSelector(selected = role, onSelect = { role = it })
            Spacer(Modifier.height(Spacing.md))
            MediLabTextField(
                value = noHP, onValueChange = { noHP = it }, label = "No HP",
                keyboardType = KeyboardType.Phone,
                isError = noHPError != null, errorMessage = noHPError
            )
            errorMessage?.let {
                Spacer(Modifier.height(Spacing.sm))
                Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
            }
            Spacer(Modifier.height(Spacing.lg))
            MediLabButton(
                text = if (saving) "Membuat akun..." else "Simpan",
                onClick = {
                    submitted = true
                    if (isValid) onSave(email, password, nama, role, noHP)
                },
                enabled = !saving
            )
        }
    }
}

@Composable
private fun RoleSelector(selected: String, onSelect: (String) -> Unit) {
    Column {
        Text(
            text = "Role",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(Spacing.xs))
        Row(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
            listOf(Constants.ROLE_ADMIN, Constants.ROLE_PETUGAS).forEach { r ->
                FilterChip(
                    selected = selected == r,
                    onClick = { onSelect(r) },
                    label = { Text(r.uppercase()) }
                )
            }
        }
    }
}

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
import com.example.medilab.repository.AuthRepository
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

@Composable
fun ManagePetugasScreen(viewModel: ManageViewModel = viewModel()) {
    val state by viewModel.petugas.collectAsStateWithLifecycle()
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
            onDismiss = { showSheet = false },
            onSave = { email, password, nama, role, noHP ->
                AuthRepository().createStaffAccount(email, password, nama, role, noHP) { ok, _ ->
                    if (ok) viewModel.loadPetugas()
                    showSheet = false
                }
            }
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
private fun PetugasFormSheet(onDismiss: () -> Unit, onSave: (String, String, String, String, String) -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var nama by remember { mutableStateOf("") }
    var role by remember { mutableStateOf(Constants.ROLE_PETUGAS) }
    var noHP by remember { mutableStateOf("") }

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(modifier = Modifier.padding(Spacing.lg)) {
            Text(
                text = "Tambah Staff",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(Modifier.height(Spacing.lg))
            MediLabTextField(value = nama, onValueChange = { nama = it }, label = "Nama")
            Spacer(Modifier.height(Spacing.md))
            MediLabTextField(value = email, onValueChange = { email = it }, label = "Email", keyboardType = KeyboardType.Email)
            Spacer(Modifier.height(Spacing.md))
            MediLabPasswordField(value = password, onValueChange = { password = it }, label = "Password (min. 6)")
            Spacer(Modifier.height(Spacing.md))
            RoleSelector(selected = role, onSelect = { role = it })
            Spacer(Modifier.height(Spacing.md))
            MediLabTextField(value = noHP, onValueChange = { noHP = it }, label = "No HP", keyboardType = KeyboardType.Phone)
            Spacer(Modifier.height(Spacing.lg))
            MediLabButton(text = "Simpan", onClick = { onSave(email, password, nama, role, noHP) })
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

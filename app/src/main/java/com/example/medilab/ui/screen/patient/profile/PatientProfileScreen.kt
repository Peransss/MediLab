package com.example.medilab.ui.screen.patient.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.medilab.ui.component.EmptyState
import com.example.medilab.ui.component.ErrorState
import com.example.medilab.ui.component.HeroCard
import com.example.medilab.ui.component.LoadingState
import com.example.medilab.ui.component.MediLabButton
import com.example.medilab.ui.component.MediLabButtonVariant
import com.example.medilab.ui.component.MediLabCard
import com.example.medilab.ui.component.MediLabPasswordField
import com.example.medilab.ui.component.MediLabTextField
import com.example.medilab.ui.component.MediLabTopAppBar
import com.example.medilab.ui.theme.Spacing
import com.example.medilab.ui.util.UiState

@Composable
fun PatientProfileScreen(
    onLogout: () -> Unit,
    isDark: Boolean,
    onToggleDark: () -> Unit,
    viewModel: PatientProfileViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var showEdit by remember { mutableStateOf(false) }
    var showPassword by remember { mutableStateOf(false) }
    var showLogoutConfirm by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { viewModel.load() }

    Scaffold(
        topBar = { MediLabTopAppBar(title = "Profile") }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when (val s = state) {
                is UiState.Loading -> LoadingState()
                is UiState.Error -> ErrorState(s.message, viewModel::load)
                is UiState.Empty -> EmptyState("Belum ada data")
                is UiState.Success -> {
                    val user = s.data
                    Column(
                        modifier = Modifier.fillMaxSize().padding(Spacing.lg),
                        verticalArrangement = Arrangement.spacedBy(Spacing.md)
                    ) {
                        HeroCard(title = user.nama.ifBlank { "User" }, subtitle = "${user.role.uppercase()} • ${user.id}") {
                            Box(
                                modifier = Modifier.size(80.dp).clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primaryContainer)
                                    .padding(Spacing.md),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = user.nama.firstOrNull()?.uppercase() ?: "U",
                                    style = MaterialTheme.typography.headlineLarge,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                        ProfileMenuItem(icon = Icons.Default.Edit, label = "Edit Profile") { showEdit = true }
                        ProfileMenuItem(icon = Icons.Default.Lock, label = "Ganti Password") { showPassword = true }
                        DarkModeToggleRow(enabled = isDark, onChange = { onToggleDark() })
                        Spacer(Modifier.height(Spacing.lg))
                        MediLabButton(
                            text = "Logout",
                            onClick = { showLogoutConfirm = true },
                            variant = MediLabButtonVariant.OUTLINED
                        )
                    }
                }
            }
        }
    }

    if (showEdit) {
        EditProfileBottomSheet(
            currentNama = (state as? UiState.Success)?.data?.nama ?: "",
            currentNoHP = (state as? UiState.Success)?.data?.noHP ?: "",
            currentAlamat = (state as? UiState.Success)?.data?.alamat ?: "",
            onDismiss = { showEdit = false },
            onSave = { nama, noHP, alamat ->
                viewModel.updateProfile(mapOf("nama" to nama, "noHP" to noHP, "alamat" to alamat)) { showEdit = false }
            }
        )
    }

    if (showPassword) {
        ChangePasswordDialog(
            onDismiss = { showPassword = false },
            onSubmit = { newPass ->
                viewModel.changePassword(newPass) { ok -> showPassword = false }
            }
        )
    }

    if (showLogoutConfirm) {
        AlertDialog(
            onDismissRequest = { showLogoutConfirm = false },
            title = { Text("Konfirmasi Logout") },
            text = { Text("Anda yakin ingin logout?") },
            confirmButton = {
                TextButton(onClick = { viewModel.logout(onLogout) }) { Text("Logout") }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutConfirm = false }) { Text("Batal") }
            }
        )
    }
}

@Composable
private fun ProfileMenuItem(icon: ImageVector, label: String, onClick: () -> Unit) {
    MediLabCard(onClick = onClick) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(Spacing.md),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.size(Spacing.md))
            Text(label, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun DarkModeToggleRow(enabled: Boolean, onChange: (Boolean) -> Unit) {
    MediLabCard {
        Row(
            modifier = Modifier.fillMaxWidth().padding(Spacing.md),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.DarkMode, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.size(Spacing.md))
            Text("Dark Mode", style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
            Switch(checked = enabled, onCheckedChange = onChange)
        }
    }
}

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
private fun EditProfileBottomSheet(
    currentNama: String,
    currentNoHP: String,
    currentAlamat: String,
    onDismiss: () -> Unit,
    onSave: (String, String, String) -> Unit
) {
    var nama by remember { mutableStateOf(currentNama) }
    var noHP by remember { mutableStateOf(currentNoHP) }
    var alamat by remember { mutableStateOf(currentAlamat) }
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(modifier = Modifier.padding(Spacing.lg)) {
            Text("Edit Profile", style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(Spacing.lg))
            MediLabTextField(value = nama, onValueChange = { nama = it }, label = "Nama")
            Spacer(Modifier.height(Spacing.md))
            MediLabTextField(value = noHP, onValueChange = { noHP = it }, label = "No HP", keyboardType = KeyboardType.Phone)
            Spacer(Modifier.height(Spacing.md))
            MediLabTextField(value = alamat, onValueChange = { alamat = it }, label = "Alamat", singleLine = false, maxLines = 3)
            Spacer(Modifier.height(Spacing.lg))
            MediLabButton(text = "Simpan", onClick = { onSave(nama, noHP, alamat) })
        }
    }
}

@Composable
private fun ChangePasswordDialog(onDismiss: () -> Unit, onSubmit: (String) -> Unit) {
    var newPass by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Ganti Password") },
        text = {
            MediLabPasswordField(value = newPass, onValueChange = { newPass = it }, label = "Password Baru")
        },
        confirmButton = {
            TextButton(onClick = { onSubmit(newPass) }) { Text("Simpan") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Batal") }
        }
    )
}

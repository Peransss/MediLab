package com.example.medilab.ui.screen.patient.profile

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.medilab.ui.component.EmptyState
import com.example.medilab.ui.component.ErrorState
import com.example.medilab.ui.component.LoadingState
import com.example.medilab.ui.component.MediLabButton
import com.example.medilab.ui.component.MediLabButtonVariant
import com.example.medilab.ui.component.MediLabCard
import com.example.medilab.ui.component.MediLabPasswordField
import com.example.medilab.ui.component.MediLabTextField
import com.example.medilab.ui.component.MediLabTopAppBar
import com.example.medilab.ui.theme.Spacing
import com.example.medilab.ui.util.UiState
import com.example.medilab.util.DateUtils
import java.util.Date

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
    var showTentang by remember { mutableStateOf(false) }
    var showBantuan by remember { mutableStateOf(false) }
    val context = LocalContext.current

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
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(Spacing.lg)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(Spacing.md)
                    ) {
                        // Profile Header Card
                        MediLabCard {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(64.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = user.nama.firstOrNull()?.uppercase() ?: "U",
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                                Spacer(Modifier.width(16.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = user.nama.ifBlank { "User" },
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onBackground
                                    )
                                    Spacer(Modifier.height(4.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "PASIEN",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onPrimary,
                                            modifier = Modifier
                                                .background(
                                                    color = MaterialTheme.colorScheme.primary,
                                                    shape = CircleShape
                                                )
                                                .padding(horizontal = 8.dp, vertical = 2.dp)
                                        )
                                    }
                                    Spacer(Modifier.height(4.dp))
                                    Text(
                                        text = "Member sejak ${DateUtils.formatDateOnly(Date(user.createdAtMillis))}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        // Settings List
                        ProfileMenuItem(icon = Icons.Default.Edit, label = "Edit Profile") { showEdit = true }
                        ProfileMenuItem(icon = Icons.Default.Lock, label = "Ganti Password") { showPassword = true }
                        DarkModeToggleRow(enabled = isDark, onChange = { onToggleDark() })
                        ProfileMenuItem(icon = Icons.Default.Info, label = "Tentang Aplikasi") { showTentang = true }
                        ProfileMenuItem(icon = Icons.Default.Help, label = "Bantuan") { showBantuan = true }
                        
                        Spacer(Modifier.height(Spacing.lg))
                        
                        // Redesigned Logout Button
                        OutlinedButton(
                            onClick = { showLogoutConfirm = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.error),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                            shape = MaterialTheme.shapes.small
                        ) {
                            Text("Logout", style = MaterialTheme.typography.labelLarge)
                        }
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
        val passwordError by viewModel.passwordError.collectAsStateWithLifecycle()
        val passwordSubmitting by viewModel.passwordSubmitting.collectAsStateWithLifecycle()
        ChangePasswordDialog(
            errorMessage = passwordError,
            submitting = passwordSubmitting,
            onDismiss = {
                showPassword = false
                viewModel.clearPasswordError()
            },
            onSubmit = { oldPass, newPass -> viewModel.changePassword(oldPass, newPass) }
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

    if (showTentang) {
        AlertDialog(
            onDismissRequest = { showTentang = false },
            title = { Text("Tentang Aplikasi") },
            text = { Text("MediLab App v1.0.0\nSistem informasi laboratorium medis yang modern dan mudah digunakan.\n\n© 2026 MediLab.") },
            confirmButton = {
                TextButton(onClick = { showTentang = false }) { Text("Tutup") }
            }
        )
    }

    if (showBantuan) {
        AlertDialog(
            onDismissRequest = { showBantuan = false },
            title = { Text("Bantuan") },
            text = { Text("Hubungi support kami melalui email di support@medilab.com atau hubungi Call Center kami di (021) 1234567.") },
            confirmButton = {
                TextButton(onClick = { showBantuan = false }) { Text("Mengerti") }
            }
        )
    }
}

@Composable
private fun ProfileMenuItem(icon: ImageVector, label: String, onClick: () -> Unit) {
    MediLabCard(onClick = onClick) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.md),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = label, tint = MaterialTheme.colorScheme.primary)
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
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.md),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.DarkMode, contentDescription = "Mode Gelap", tint = MaterialTheme.colorScheme.primary)
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
            MediLabButton(text = "Simpan", onClick = { onSave(nama, noHP, alamat) }, variant = MediLabButtonVariant.CTA)
        }
    }
}

@Composable
private fun ChangePasswordDialog(
    errorMessage: String?,
    submitting: Boolean,
    onDismiss: () -> Unit,
    onSubmit: (String, String) -> Unit
) {
    var oldPass by remember { mutableStateOf("") }
    var newPass by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Ganti Password") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                MediLabPasswordField(
                    value = oldPass,
                    onValueChange = { oldPass = it },
                    label = "Password Lama"
                )
                MediLabPasswordField(
                    value = newPass,
                    onValueChange = { newPass = it },
                    label = "Password Baru",
                    isError = errorMessage != null,
                    errorMessage = errorMessage
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onSubmit(oldPass, newPass) },
                enabled = !submitting && newPass.length >= 8 && oldPass.isNotBlank()
            ) { Text(if (submitting) "Menyimpan..." else "Simpan") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Batal") }
        }
    )
}

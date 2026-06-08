package com.example.medilab.ui.screen.staff.manage.dokter

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.medilab.model.Dokter
import com.example.medilab.ui.component.EmptyState
import com.example.medilab.ui.component.ErrorState
import com.example.medilab.ui.component.LoadingState
import com.example.medilab.ui.component.MediLabButton
import com.example.medilab.ui.component.MediLabCard
import com.example.medilab.ui.component.MediLabTextField
import com.example.medilab.ui.screen.staff.manage.ManageViewModel
import com.example.medilab.ui.theme.Spacing
import com.example.medilab.ui.util.UiState

@Composable
fun ManageDokterScreen(viewModel: ManageViewModel = viewModel()) {
    val state by viewModel.dokter.collectAsStateWithLifecycle()
    var showSheet by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        when (val s = state) {
            is UiState.Loading -> LoadingState()
            is UiState.Error -> ErrorState(message = s.message, onRetry = {})
            is UiState.Empty -> EmptyState(
                title = "Belum ada dokter",
                description = "Tap tombol + untuk menambah"
            )
            is UiState.Success -> LazyColumn(
                contentPadding = androidx.compose.foundation.layout.PaddingValues(Spacing.lg),
                verticalArrangement = Arrangement.spacedBy(Spacing.sm)
            ) {
                items(s.data) { d -> DokterRow(d) }
            }
        }
        FloatingActionButton(
            onClick = { showSheet = true },
            modifier = Modifier.align(Alignment.BottomEnd).padding(Spacing.lg)
        ) { Icon(Icons.Default.Add, contentDescription = "Tambah") }
    }

    if (showSheet) {
        DokterFormSheet(
            onDismiss = { showSheet = false },
            onSave = { d -> viewModel.saveDokter(d) { showSheet = false } }
        )
    }
}

@Composable
private fun DokterRow(d: Dokter) {
    MediLabCard {
        Column(modifier = Modifier.fillMaxWidth().padding(Spacing.md)) {
            Text(
                text = d.nama.ifBlank { "-" },
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = d.spesialis.ifBlank { "-" },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DokterFormSheet(onDismiss: () -> Unit, onSave: (Dokter) -> Unit) {
    var id by remember { mutableStateOf("D${System.currentTimeMillis() % 100000}") }
    var nama by remember { mutableStateOf("") }
    var spesialis by remember { mutableStateOf("") }
    var alamat by remember { mutableStateOf("") }
    var noHP by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(modifier = Modifier.padding(Spacing.lg)) {
            Text(
                text = "Tambah Dokter",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(Modifier.height(Spacing.lg))
            MediLabTextField(value = nama, onValueChange = { nama = it }, label = "Nama")
            Spacer(Modifier.height(Spacing.md))
            MediLabTextField(value = spesialis, onValueChange = { spesialis = it }, label = "Spesialis")
            Spacer(Modifier.height(Spacing.md))
            MediLabTextField(value = alamat, onValueChange = { alamat = it }, label = "Alamat")
            Spacer(Modifier.height(Spacing.md))
            MediLabTextField(value = noHP, onValueChange = { noHP = it }, label = "No HP", keyboardType = KeyboardType.Phone)
            Spacer(Modifier.height(Spacing.md))
            MediLabTextField(value = email, onValueChange = { email = it }, label = "Email", keyboardType = KeyboardType.Email)
            Spacer(Modifier.height(Spacing.lg))
            MediLabButton(
                text = "Simpan",
                onClick = {
                    onSave(Dokter(id = id, nama = nama, spesialis = spesialis, alamat = alamat, noHP = noHP, email = email))
                }
            )
        }
    }
}

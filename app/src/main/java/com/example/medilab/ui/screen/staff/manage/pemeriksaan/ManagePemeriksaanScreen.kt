package com.example.medilab.ui.screen.staff.manage.pemeriksaan

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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.medilab.model.Pemeriksaan
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
fun ManagePemeriksaanScreen(viewModel: ManageViewModel = viewModel()) {
    val state by viewModel.pemeriksaan.collectAsStateWithLifecycle()
    var showSheet by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { viewModel.loadPemeriksaan() }

    Box(modifier = Modifier.fillMaxSize()) {
        when (val s = state) {
            is UiState.Loading -> LoadingState()
            is UiState.Error -> ErrorState(message = s.message, onRetry = { viewModel.loadPemeriksaan() })
            is UiState.Empty -> EmptyState(title = "Belum ada master tes")
            is UiState.Success -> LazyColumn(
                contentPadding = androidx.compose.foundation.layout.PaddingValues(Spacing.lg),
                verticalArrangement = Arrangement.spacedBy(Spacing.sm)
            ) {
                items(s.data) { p -> PemeriksaanRow(p) }
            }
        }
        FloatingActionButton(
            onClick = { showSheet = true },
            modifier = Modifier.align(Alignment.BottomEnd).padding(Spacing.lg)
        ) { Icon(Icons.Default.Add, contentDescription = "Tambah") }
    }

    if (showSheet) {
        PemeriksaanFormSheet(
            onDismiss = { showSheet = false },
            onSave = { p -> viewModel.savePemeriksaan(p) { showSheet = false } }
        )
    }
}

@Composable
private fun PemeriksaanRow(p: Pemeriksaan) {
    MediLabCard {
        Column(modifier = Modifier.fillMaxWidth().padding(Spacing.md)) {
            Text(
                text = p.namaPemeriksaan.ifBlank { "-" },
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "${p.kategori} • ${p.parameter.size} parameter",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PemeriksaanFormSheet(onDismiss: () -> Unit, onSave: (Pemeriksaan) -> Unit) {
    var id by remember { mutableStateOf("PXS${System.currentTimeMillis() % 100000}") }
    var nama by remember { mutableStateOf("") }
    var kategori by remember { mutableStateOf("") }
    var deskripsi by remember { mutableStateOf("") }

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(modifier = Modifier.padding(Spacing.lg)) {
            Text(
                text = "Tambah Master Tes",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(Modifier.height(Spacing.lg))
            MediLabTextField(value = nama, onValueChange = { nama = it }, label = "Nama Pemeriksaan")
            Spacer(Modifier.height(Spacing.md))
            MediLabTextField(value = kategori, onValueChange = { kategori = it }, label = "Kategori")
            Spacer(Modifier.height(Spacing.md))
            MediLabTextField(
                value = deskripsi,
                onValueChange = { deskripsi = it },
                label = "Deskripsi",
                singleLine = false,
                maxLines = 3
            )
            Spacer(Modifier.height(Spacing.lg))
            MediLabButton(
                text = "Simpan",
                onClick = {
                    onSave(Pemeriksaan(id = id, namaPemeriksaan = nama, kategori = kategori, deskripsi = deskripsi))
                }
            )
        }
    }
}

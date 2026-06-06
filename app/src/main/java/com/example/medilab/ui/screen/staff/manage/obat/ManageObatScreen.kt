package com.example.medilab.ui.screen.staff.manage.obat

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
import com.example.medilab.model.Obat
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
fun ManageObatScreen(viewModel: ManageViewModel = viewModel()) {
    val state by viewModel.obat.collectAsStateWithLifecycle()
    var showSheet by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { viewModel.loadObat() }

    Box(modifier = Modifier.fillMaxSize()) {
        when (val s = state) {
            is UiState.Loading -> LoadingState()
            is UiState.Error -> ErrorState(message = s.message, onRetry = { viewModel.loadObat() })
            is UiState.Empty -> EmptyState(title = "Belum ada obat")
            is UiState.Success -> LazyColumn(
                contentPadding = androidx.compose.foundation.layout.PaddingValues(Spacing.lg),
                verticalArrangement = Arrangement.spacedBy(Spacing.sm)
            ) {
                items(s.data) { o -> ObatRow(o) }
            }
        }
        FloatingActionButton(
            onClick = { showSheet = true },
            modifier = Modifier.align(Alignment.BottomEnd).padding(Spacing.lg)
        ) { Icon(Icons.Default.Add, contentDescription = "Tambah") }
    }

    if (showSheet) {
        ObatFormSheet(
            onDismiss = { showSheet = false },
            onSave = { o -> viewModel.saveObat(o) { showSheet = false } }
        )
    }
}

@Composable
private fun ObatRow(o: Obat) {
    MediLabCard {
        Column(modifier = Modifier.fillMaxWidth().padding(Spacing.md)) {
            Text(
                text = o.namaObat.ifBlank { "-" },
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "${o.bentuk} • ${o.dosis} ${o.satuan}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ObatFormSheet(onDismiss: () -> Unit, onSave: (Obat) -> Unit) {
    var id by remember { mutableStateOf("OBT${System.currentTimeMillis() % 100000}") }
    var nama by remember { mutableStateOf("") }
    var bentuk by remember { mutableStateOf("Tablet") }
    var dosis by remember { mutableStateOf("") }
    var satuan by remember { mutableStateOf("mg") }
    var keterangan by remember { mutableStateOf("") }

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(modifier = Modifier.padding(Spacing.lg)) {
            Text(
                text = "Tambah Obat",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(Modifier.height(Spacing.lg))
            MediLabTextField(value = nama, onValueChange = { nama = it }, label = "Nama Obat")
            Spacer(Modifier.height(Spacing.md))
            BentukSelector(selected = bentuk, onSelect = { bentuk = it })
            Spacer(Modifier.height(Spacing.md))
            MediLabTextField(value = dosis, onValueChange = { dosis = it }, label = "Dosis", keyboardType = KeyboardType.Decimal)
            Spacer(Modifier.height(Spacing.md))
            MediLabTextField(value = satuan, onValueChange = { satuan = it }, label = "Satuan")
            Spacer(Modifier.height(Spacing.md))
            MediLabTextField(
                value = keterangan,
                onValueChange = { keterangan = it },
                label = "Keterangan",
                singleLine = false,
                maxLines = 2
            )
            Spacer(Modifier.height(Spacing.lg))
            MediLabButton(
                text = "Simpan",
                onClick = {
                    onSave(
                        Obat(
                            id = id,
                            namaObat = nama,
                            bentuk = bentuk,
                            dosis = dosis.toDoubleOrNull() ?: 0.0,
                            satuan = satuan,
                            keterangan = keterangan
                        )
                    )
                }
            )
        }
    }
}

@Composable
private fun BentukSelector(selected: String, onSelect: (String) -> Unit) {
    Column {
        Text(
            text = "Bentuk",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(Spacing.xs))
        Row(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
            listOf("Tablet", "Kapsul", "Sirup", "Injeksi").forEach { b ->
                FilterChip(
                    selected = selected == b,
                    onClick = { onSelect(b) },
                    label = { Text(b) }
                )
            }
        }
    }
}

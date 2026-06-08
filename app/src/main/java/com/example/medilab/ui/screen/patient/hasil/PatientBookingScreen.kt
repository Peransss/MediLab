package com.example.medilab.ui.screen.patient.hasil

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Science
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.medilab.ui.component.ErrorState
import com.example.medilab.ui.component.LoadingState
import com.example.medilab.ui.component.MediLabButton
import com.example.medilab.ui.component.MediLabTextField
import com.example.medilab.ui.component.MediLabTopAppBar
import com.example.medilab.ui.theme.Spacing

@Composable
fun PatientBookingScreen(
    onBack: () -> Unit,
    onSuccess: () -> Unit,
    viewModel: PatientBookingViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) { viewModel.load() }
    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) onSuccess()
    }

    Scaffold(
        topBar = {
            MediLabTopAppBar(
                title = "Pemeriksaan Baru",
                onBackClick = onBack
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = Spacing.lg)
        ) {
            when {
                state.isLoading -> LoadingState()
                state.pemeriksaanList.isEmpty() -> EmptyState(
                    title = "Belum ada pemeriksaan",
                    description = "Belum ada jenis pemeriksaan yang tersedia. Silakan hubungi admin."
                )
                else -> {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(Spacing.md),
                        modifier = Modifier.weight(1f)
                    ) {
                        item {
                            Spacer(Modifier.height(Spacing.md))
                            Text(
                                text = "Pilih Jenis Pemeriksaan",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(Modifier.height(Spacing.sm))
                        }
                        items(state.pemeriksaanList) { item ->
                            PemeriksaanCard(
                                nama = item.namaPemeriksaan,
                                kategori = item.kategori,
                                deskripsi = item.deskripsi,
                                isSelected = item.id == state.selectedPemeriksaanId,
                                onClick = { viewModel.selectPemeriksaan(item.id) }
                            )
                        }
                        item {
                            Spacer(Modifier.height(Spacing.md))
                            Text(
                                text = "Catatan (opsional)",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(Modifier.height(Spacing.sm))
                            MediLabTextField(
                                value = state.catatan,
                                onValueChange = viewModel::onCatatanChange,
                                label = "Catatan",
                                placeholder = "Tambahkan catatan untuk petugas..."
                            )
                            Spacer(Modifier.height(Spacing.lg))
                        }
                    }
                    state.errorMessage?.let { msg ->
                        Text(
                            text = msg,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(bottom = Spacing.sm)
                        )
                    }
                    MediLabButton(
                        text = "Ajukan Pemeriksaan",
                        onClick = viewModel::submit,
                        isLoading = state.isSubmitting,
                        enabled = !state.isSubmitting && state.selectedPemeriksaanId != null,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(Spacing.lg))
                }
            }
        }
    }
}

@Composable
private fun PemeriksaanCard(
    nama: String,
    kategori: String,
    deskripsi: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.md),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Science,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(end = Spacing.sm)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = nama,
                    style = MaterialTheme.typography.titleSmall
                )
                if (kategori.isNotBlank()) {
                    Text(
                        text = kategori,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                if (deskripsi.isNotBlank()) {
                    Text(
                        text = deskripsi,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2
                    )
                }
            }
            RadioButton(
                selected = isSelected,
                onClick = onClick
            )
        }
    }
}

@Composable
private fun EmptyState(
    title: String,
    description: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(Spacing.xl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.CalendarMonth,
            contentDescription = null,
            modifier = Modifier.height(64.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(Spacing.md))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(Spacing.sm))
        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

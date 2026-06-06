package com.example.medilab.ui.screen.patient.laporan

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.medilab.model.Laporan
import com.example.medilab.ui.component.EmptyState
import com.example.medilab.ui.component.ErrorState
import com.example.medilab.ui.component.LoadingState
import com.example.medilab.ui.component.MediLabCard
import com.example.medilab.ui.component.MediLabTopAppBar
import com.example.medilab.ui.component.StatusChip
import com.example.medilab.ui.theme.Spacing
import com.example.medilab.ui.util.UiState

@Composable
fun LaporanDetailScreen(
    laporanId: String,
    onBack: () -> Unit,
    viewModel: LaporanDetailViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(laporanId) { viewModel.load(laporanId) }

    Scaffold(
        topBar = { MediLabTopAppBar(title = "Detail Laporan", onBackClick = onBack) }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when (val s = state) {
                is UiState.Loading -> LoadingState()
                is UiState.Error -> ErrorState(message = s.message, onRetry = { viewModel.load(laporanId) })
                is UiState.Empty -> EmptyState(title = "Laporan kosong")
                is UiState.Success -> DetailContent(s.data)
            }
        }
    }
}

@Composable
private fun DetailContent(data: LaporanDetailData) {
    val l = data.laporan
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(Spacing.md)
    ) {
        DetailHeader(l)
        DetailInfoCard(
            "Info Pasien",
            listOf(
                "Nama" to (data.pasien?.nama?.ifBlank { null } ?: "Memuat..."),
                "No. RM" to (data.pasien?.noRekamMedis?.ifBlank { null } ?: "-"),
                "ID" to l.pasienId
            )
        )
        DetailInfoCard(
            "Info Dokter",
            listOf(
                "Nama" to (data.dokter?.nama?.ifBlank { null } ?: if (l.dokterId.isBlank()) "-" else "Memuat..."),
                "ID Dokter" to l.dokterId
            )
        )
        DetailParameterCard(l)
        DetailResepCard(l)
        DetailInfoCard(
            "Info Rumah Sakit",
            listOf(
                "Nama" to l.rumahSakit.nama,
                "Alamat" to l.rumahSakit.alamat,
                "Kota" to l.rumahSakit.kota
            )
        )
    }
}

@Composable
private fun DetailHeader(l: Laporan) {
    MediLabCard {
        Column(modifier = Modifier.padding(Spacing.lg)) {
            Text("Laporan ${l.id}", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(Spacing.xs))
            StatusChip(status = l.status)
        }
    }
}

@Composable
private fun DetailInfoCard(title: String, fields: List<Pair<String, String>>) {
    MediLabCard {
        Column(modifier = Modifier.padding(Spacing.lg)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(Spacing.sm))
            fields.forEach { (label, value) ->
                Row(modifier = Modifier.fillMaxWidth().padding(vertical = Spacing.xs)) {
                    Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.weight(1f))
                    Text(value.ifBlank { "-" }, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

@Composable
private fun DetailParameterCard(l: Laporan) {
    MediLabCard {
        Column(modifier = Modifier.padding(Spacing.lg)) {
            Text("Parameter Hasil", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(Spacing.sm))
            if (l.hasilParameter.isEmpty()) {
                Text("Belum ada hasil", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            } else {
                l.hasilParameter.forEach { p ->
                    Row(modifier = Modifier.fillMaxWidth().padding(vertical = Spacing.xs), verticalAlignment = Alignment.CenterVertically) {
                        Text(p.parameterNama, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
                        Text("${p.nilai} ${p.satuan}", style = MaterialTheme.typography.bodyMedium)
                    }
                    HorizontalDivider()
                }
            }
            if (l.diagnosa.isNotBlank()) {
                Spacer(Modifier.height(Spacing.sm))
                Text("Diagnosa", style = MaterialTheme.typography.titleSmall)
                Text(l.diagnosa, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
private fun DetailResepCard(l: Laporan) {
    if (l.resepObat.isEmpty()) return
    MediLabCard {
        Column(modifier = Modifier.padding(Spacing.lg)) {
            Text("Resep Obat", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(Spacing.sm))
            l.resepObat.forEach { r ->
                Column(modifier = Modifier.padding(vertical = Spacing.xs)) {
                    Text(r.namaObat, style = MaterialTheme.typography.bodyLarge)
                    Text("${r.dosis} ${r.satuan} • ${r.aturanPakai}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("Jumlah: ${r.jumlah}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                HorizontalDivider()
            }
        }
    }
}

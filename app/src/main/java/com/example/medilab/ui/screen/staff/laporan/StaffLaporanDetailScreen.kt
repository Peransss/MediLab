package com.example.medilab.ui.screen.staff.laporan

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.medilab.database.entity.LaporanEntity
import com.example.medilab.ui.component.MediLabButton
import com.example.medilab.ui.component.MediLabButtonVariant
import com.example.medilab.ui.component.MediLabCard
import com.example.medilab.ui.component.MediLabTopAppBar
import com.example.medilab.ui.component.StatusChip
import com.example.medilab.ui.theme.Spacing

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun StaffLaporanDetailScreen(
    laporanId: String,
    onBack: () -> Unit,
    userRole: String,
    viewModel: StaffLaporanViewModel = viewModel()
) {
    val laporan by viewModel.detail.collectAsStateWithLifecycle()
    LaunchedEffect(laporanId) { viewModel.loadDetail(laporanId) }

    Scaffold(
        topBar = { MediLabTopAppBar(title = "Detail Laporan", onBackClick = onBack) },
        bottomBar = {
            laporan?.let { l ->
                val actions = StatusActionHelper.actionsFor(userRole, l.status)
                if (actions.isNotEmpty()) {
                    Surface(tonalElevation = 4.dp) {
                        FlowRow(
                            modifier = Modifier.fillMaxWidth().padding(Spacing.md),
                            horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                            verticalArrangement = Arrangement.spacedBy(Spacing.sm)
                        ) {
                            actions.forEach { action ->
                                MediLabButton(
                                    text = action.label,
                                    onClick = { viewModel.updateStatus(laporanId, action.newStatus, userRole) {} },
                                    variant = if (action.isPrimary) MediLabButtonVariant.CTA else MediLabButtonVariant.OUTLINED,
                                    fullWidth = false
                                )
                            }
                        }
                    }
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            laporan?.let { l ->
                Column(
                    modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(Spacing.lg),
                    verticalArrangement = Arrangement.spacedBy(Spacing.md)
                ) {
                    HeaderCard(l)
                    InfoCard("Info Pasien", listOf("ID" to l.pasienId, "Status" to l.status))
                    InfoCard("Info Dokter", listOf("ID Dokter" to l.dokterId))
                    ParameterCard(l)
                    ResepCard(l)
                    InfoCard("Info RS", listOf("Nama" to l.rumahSakit.nama, "Alamat" to l.rumahSakit.alamat, "Kota" to l.rumahSakit.kota))
                    Spacer(Modifier.height(Spacing.xl))
                }
            } ?: Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Memuat...", style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}

@Composable
private fun HeaderCard(l: LaporanEntity) {
    MediLabCard {
        Column(modifier = Modifier.padding(Spacing.lg)) {
            Text("Laporan ${l.id}", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(Spacing.xs))
            StatusChip(status = l.status)
        }
    }
}

@Composable
private fun InfoCard(title: String, fields: List<Pair<String, String>>) {
    MediLabCard {
        Column(modifier = Modifier.padding(Spacing.lg)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(Spacing.sm))
            fields.forEach { (k, v) ->
                Row(modifier = Modifier.fillMaxWidth().padding(vertical = Spacing.xs)) {
                    Text(k, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.weight(1f))
                    Text(v.ifBlank { "-" }, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

@Composable
private fun ParameterCard(l: LaporanEntity) {
    MediLabCard {
        Column(modifier = Modifier.padding(Spacing.lg)) {
            Text("Parameter Hasil", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(Spacing.sm))
            if (l.hasilParameter.isEmpty()) {
                Text("Belum ada hasil", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            } else {
                l.hasilParameter.forEach { p ->
                    Row(modifier = Modifier.fillMaxWidth().padding(vertical = Spacing.xs), verticalAlignment = Alignment.CenterVertically) {
                        Text(p.parameterNama, modifier = Modifier.weight(1f))
                        Text("${p.nilai} ${p.satuan}")
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
private fun ResepCard(l: LaporanEntity) {
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

package com.example.medilab.ui.screen.patient.home

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
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.medilab.ui.component.EmptyState
import com.example.medilab.ui.component.ErrorState
import com.example.medilab.ui.component.LaporanCard
import com.example.medilab.ui.component.LoadingState
import com.example.medilab.ui.component.MediLabTopAppBar
import com.example.medilab.ui.component.NotifikasiItem
import com.example.medilab.ui.component.SummaryCard
import com.example.medilab.ui.theme.Spacing
import com.example.medilab.ui.util.UiState

@Composable
fun PatientHomeScreen(
    onLaporanClick: (String) -> Unit,
    onNotificationClick: () -> Unit,
    onProfileClick: () -> Unit,
    onSeeAllLaporanClick: () -> Unit = {},
    viewModel: PatientHomeViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) { viewModel.load() }

    Column(modifier = Modifier.fillMaxSize()) {
        MediLabTopAppBar(
            title = "Beranda",
            actions = {
                IconButton(onClick = onNotificationClick) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Notifikasi",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        )
        when (val s = state) {
            is UiState.Loading -> LoadingState()
            is UiState.Error -> ErrorState(message = s.message, onRetry = viewModel::load)
            is UiState.Empty -> EmptyState(title = "Belum ada data", description = "Belum ada laporan atau data")
            is UiState.Success -> {
                val data = s.data
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(Spacing.lg),
                    verticalArrangement = Arrangement.spacedBy(Spacing.md)
                ) {
                    item {
                        Column {
                            Text(
                                text = "Halo, ${data.user.nama} 👋",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = "Semoga harimu menyenangkan!",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    item {
                        SummaryCard(
                            pemeriksaanSelesai = data.pemeriksaanSelesai,
                            menungguHasil = data.menungguHasil,
                            totalRiwayat = data.totalRiwayat
                        )
                    }
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Pemeriksaan Terbaru",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            TextButton(onClick = onSeeAllLaporanClick) {
                                Text(
                                    text = "Lihat Semua",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                    if (data.recentLaporan.isEmpty()) {
                        item {
                            Text(
                                text = "Belum ada pemeriksaan terbaru",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        items(data.recentLaporan) { laporan ->
                            LaporanCard(
                                laporan = laporan,
                                onClick = { onLaporanClick(laporan.id) }
                            )
                        }
                    }
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Notifikasi",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            TextButton(onClick = onNotificationClick) {
                                Text(
                                    text = "Lihat Semua",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                    if (data.notifikasi.isEmpty()) {
                        item {
                            Text(
                                text = "Belum ada notifikasi",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        items(data.notifikasi) { notif ->
                            NotifikasiItem(notifikasi = notif, onClick = onNotificationClick)
                        }
                    }
                }
            }
        }
    }
}

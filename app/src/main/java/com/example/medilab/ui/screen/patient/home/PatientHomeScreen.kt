package com.example.medilab.ui.screen.patient.home

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
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.medilab.ui.component.EmptyState
import com.example.medilab.ui.component.ErrorState
import com.example.medilab.ui.component.HeroCard
import com.example.medilab.ui.component.LaporanCard
import com.example.medilab.ui.component.LoadingState
import com.example.medilab.ui.component.MediLabTopAppBar
import com.example.medilab.ui.component.NotifikasiItem
import com.example.medilab.ui.component.SectionHeader
import com.example.medilab.ui.illustration.DoctorIllustration
import com.example.medilab.ui.theme.Spacing
import com.example.medilab.ui.util.UiState
import com.example.medilab.util.Constants

@Composable
fun PatientHomeScreen(
    onLaporanClick: (String) -> Unit,
    onNotificationClick: () -> Unit,
    onProfileClick: () -> Unit,
    viewModel: PatientHomeViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) { viewModel.load() }

    Column(modifier = Modifier.fillMaxSize()) {
        MediLabTopAppBar(
            title = "Beranda",
            actions = {
                IconButton(onClick = onNotificationClick) {
                    Icon(Icons.Default.Notifications, contentDescription = "Notifikasi")
                }
                IconButton(onClick = onProfileClick) {
                    Icon(Icons.Default.Person, contentDescription = "Profile")
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
                        Text(
                            text = "Halo, ${data.user.nama}",
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                    item {
                        HeroCard(
                            title = "Selamat Datang di MediLab",
                            subtitle = "Pantau hasil lab Anda dengan mudah"
                        ) {
                            Box(modifier = Modifier.height(120.dp).padding(top = Spacing.md), contentAlignment = Alignment.Center) {
                                DoctorIllustration()
                            }
                        }
                    }
                    item { SectionHeader(title = "Status Laporan") }
                    item {
                        if (data.latestLaporan == null) {
                            Text("Belum ada laporan", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        } else {
                            Column {
                                LaporanCard(
                                    laporan = data.latestLaporan,
                                    onClick = { onLaporanClick(data.latestLaporan.id) }
                                )
                                Spacer(Modifier.height(Spacing.xs))
                                LinearProgressIndicator(
                                    progress = { laporanProgress(data.latestLaporan.status) },
                                    modifier = Modifier.fillMaxWidth().height(6.dp)
                                )
                            }
                        }
                    }
                    item { SectionHeader(title = "Notifikasi") }
                    if (data.notifikasi.isEmpty()) {
                        item { Text("Belum ada notifikasi", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant) }
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

private fun laporanProgress(status: String): Float = when (status.lowercase()) {
    Constants.STATUS_BARU -> 0.2f
    Constants.STATUS_PROSES -> 0.4f
    Constants.STATUS_VERIFIKASI -> 0.6f
    Constants.STATUS_REVISI -> 0.5f
    Constants.STATUS_SELESAI -> 1.0f
    Constants.STATUS_DITOLAK -> 0.0f
    Constants.STATUS_BATAL -> 0.0f
    else -> 0.0f
}

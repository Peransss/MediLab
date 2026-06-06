package com.example.medilab.ui.screen.staff.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PostAdd
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.medilab.ui.component.EmptyState
import com.example.medilab.ui.component.ErrorState
import com.example.medilab.ui.component.LaporanCard
import com.example.medilab.ui.component.LoadingState
import com.example.medilab.ui.component.MediLabTopAppBar
import com.example.medilab.ui.component.SectionHeader
import com.example.medilab.ui.component.StatCard
import com.example.medilab.ui.theme.Spacing
import com.example.medilab.ui.util.UiState

@Composable
fun StaffHomeScreen(
    onLaporanClick: (String) -> Unit,
    onNotificationClick: () -> Unit,
    onProfileClick: () -> Unit,
    viewModel: StaffHomeViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) { viewModel.load() }

    Column(modifier = Modifier.fillMaxSize()) {
        MediLabTopAppBar(
            title = "Beranda Staff",
            actions = {
                IconButton(onClick = onNotificationClick) { Icon(Icons.Default.Notifications, contentDescription = "Notifikasi") }
                IconButton(onClick = onProfileClick) { Icon(Icons.Default.Person, contentDescription = "Profile") }
            }
        )
        when (val s = state) {
            is UiState.Loading -> LoadingState()
            is UiState.Error -> ErrorState(s.message, viewModel::load)
            is UiState.Empty -> EmptyState(title = "Belum ada data")
            is UiState.Success -> LazyColumn(
                contentPadding = androidx.compose.foundation.layout.PaddingValues(Spacing.lg),
                verticalArrangement = Arrangement.spacedBy(Spacing.md)
            ) {
                item {
                    val user = s.data.user
                    Text(
                        text = "Halo, ${user.nama}",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Role: ${user.role.uppercase()}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(Spacing.md)) {
                        StatCard(
                            value = s.data.countBaru.toString(),
                            label = "Baru",
                            icon = Icons.Default.PostAdd,
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            value = s.data.countProses.toString(),
                            label = "Proses",
                            icon = Icons.Default.HourglassEmpty,
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            value = s.data.countSelesai.toString(),
                            label = "Selesai",
                            icon = Icons.Default.AssignmentTurnedIn,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                item { SectionHeader(title = "Perlu Verifikasi") }
                if (s.data.pendingVerifikasi.isEmpty()) {
                    item {
                        Text(
                            text = "Tidak ada yang perlu diverifikasi",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    items(s.data.pendingVerifikasi) { laporan ->
                        LaporanCard(laporan = laporan, onClick = { onLaporanClick(laporan.id) })
                    }
                }
            }
        }
    }
}

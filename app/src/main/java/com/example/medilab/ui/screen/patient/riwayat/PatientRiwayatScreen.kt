package com.example.medilab.ui.screen.patient.riwayat

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.medilab.ui.component.EmptyState
import com.example.medilab.ui.component.ErrorState
import com.example.medilab.ui.component.LoadingState
import com.example.medilab.ui.component.MediLabCard
import com.example.medilab.ui.component.MediLabTopAppBar
import com.example.medilab.ui.illustration.EmptyHistoryIllustration
import com.example.medilab.ui.theme.Spacing
import com.example.medilab.ui.util.UiState

@Composable
fun PatientRiwayatScreen(
    onItemClick: (String) -> Unit,
    viewModel: PatientRiwayatViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) { viewModel.load() }

    Scaffold(
        topBar = { MediLabTopAppBar(title = "Riwayat Rekam Medis") }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when (val s = state) {
                is UiState.Loading -> LoadingState()
                is UiState.Error -> ErrorState(s.message, viewModel::load)
                is UiState.Empty -> EmptyState(
                    title = "Belum ada riwayat",
                    description = "Riwayat rekam medis Anda akan tampil di sini.",
                    illustration = { EmptyHistoryIllustration(modifier = Modifier.size(100.dp)) }
                )
                is UiState.Success -> LazyColumn(
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(Spacing.lg),
                    verticalArrangement = Arrangement.spacedBy(Spacing.md)
                ) {
                    items(s.data) { rm ->
                        MediLabCard(onClick = { onItemClick(rm.id) }) {
                            Column(modifier = Modifier.padding(Spacing.md)) {
                                Text(rm.diagnosa.ifBlank { "-" }, style = MaterialTheme.typography.titleMedium)
                                Text(rm.rumahSakit.nama, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                if (rm.waktu.isNotBlank()) Text(rm.waktu, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }
        }
    }
}

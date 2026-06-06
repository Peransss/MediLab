package com.example.medilab.ui.screen.staff.laporan

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.medilab.ui.theme.Spacing
import com.example.medilab.ui.util.UiState

@Composable
fun StaffLaporanListScreen(
    status: String,
    onLaporanClick: (String) -> Unit,
    viewModel: StaffLaporanViewModel = viewModel()
) {
    val state by viewModel.listState.collectAsStateWithLifecycle()
    LaunchedEffect(status) { viewModel.loadByStatus(status) }

    Box(modifier = Modifier.fillMaxSize()) {
        when (val s = state) {
            is UiState.Loading -> LoadingState()
            is UiState.Error -> ErrorState(message = s.message, onRetry = { viewModel.loadByStatus(status) })
            is UiState.Empty -> EmptyState("Belum ada laporan", "Belum ada laporan dengan status $status")
            is UiState.Success -> LazyColumn(
                contentPadding = PaddingValues(Spacing.lg),
                verticalArrangement = Arrangement.spacedBy(Spacing.sm)
            ) {
                items(s.data) { laporan ->
                    LaporanCard(laporan = laporan, onClick = { onLaporanClick(laporan.id) })
                }
            }
        }
    }
}

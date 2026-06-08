package com.example.medilab.ui.screen.patient.hasil

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.medilab.ui.component.EmptyState
import com.example.medilab.ui.component.ErrorState
import com.example.medilab.ui.component.LaporanCard
import com.example.medilab.ui.component.LoadingState
import com.example.medilab.ui.component.MediLabTopAppBar
import com.example.medilab.ui.illustration.EmptyLabIllustration
import com.example.medilab.ui.theme.Spacing
import com.example.medilab.ui.util.UiState

@Composable
fun PatientHasilScreen(
    onLaporanClick: (String) -> Unit,
    viewModel: PatientHasilViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) { viewModel.load() }
    val context = LocalContext.current

    Scaffold(
        topBar = { MediLabTopAppBar(title = "Hasil Pemeriksaan") }
    ) { padding ->
        Box(
            modifier = Modifier.fillMaxSize().padding(padding)
        ) {
            when (val s = state) {
                is UiState.Loading -> LoadingState()
                is UiState.Error -> ErrorState(message = s.message, onRetry = viewModel::load)
                is UiState.Empty -> EmptyState(
                    title = "Belum ada hasil",
                    description = "Hasil pemeriksaan yang sudah selesai akan tampil di sini.",
                    illustration = { EmptyLabIllustration(modifier = Modifier.size(100.dp)) },
                    buttonText = "Lakukan Pemeriksaan",
                    onButtonClick = {
                        Toast.makeText(context, "Fitur pemesanan pemeriksaan belum tersedia", Toast.LENGTH_SHORT).show()
                    }
                )
                is UiState.Success -> LazyColumn(
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(Spacing.lg),
                    verticalArrangement = Arrangement.spacedBy(Spacing.md)
                ) {
                    items(s.data) { laporan ->
                        LaporanCard(laporan = laporan, onClick = { onLaporanClick(laporan.id) })
                    }
                }
            }
        }
    }
}

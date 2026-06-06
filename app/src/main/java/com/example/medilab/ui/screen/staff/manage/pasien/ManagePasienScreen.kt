package com.example.medilab.ui.screen.staff.manage.pasien

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.medilab.model.User
import com.example.medilab.ui.component.EmptyState
import com.example.medilab.ui.component.ErrorState
import com.example.medilab.ui.component.LoadingState
import com.example.medilab.ui.component.MediLabCard
import com.example.medilab.ui.screen.staff.manage.ManageViewModel
import com.example.medilab.ui.theme.Spacing
import com.example.medilab.ui.util.UiState

@Composable
fun ManagePasienScreen(viewModel: ManageViewModel = viewModel()) {
    val state by viewModel.pasien.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) { viewModel.loadPasien() }

    Box(modifier = Modifier.fillMaxSize()) {
        when (val s = state) {
            is UiState.Loading -> LoadingState()
            is UiState.Error -> ErrorState(message = s.message, onRetry = { viewModel.loadPasien() })
            is UiState.Empty -> EmptyState(title = "Belum ada pasien")
            is UiState.Success -> LazyColumn(
                contentPadding = androidx.compose.foundation.layout.PaddingValues(Spacing.lg),
                verticalArrangement = Arrangement.spacedBy(Spacing.sm)
            ) {
                items(s.data) { user -> UserRow(user) }
            }
        }
    }
}

@Composable
private fun UserRow(user: User) {
    MediLabCard {
        Column(modifier = Modifier.fillMaxWidth().padding(Spacing.md)) {
            Text(
                text = user.nama.ifBlank { "-" },
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "RM: ${user.noRekamMedis.ifBlank { user.id }}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

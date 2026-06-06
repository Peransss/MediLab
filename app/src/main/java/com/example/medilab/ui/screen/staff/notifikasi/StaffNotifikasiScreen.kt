package com.example.medilab.ui.screen.staff.notifikasi

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.medilab.ui.component.EmptyState
import com.example.medilab.ui.component.ErrorState
import com.example.medilab.ui.component.LoadingState
import com.example.medilab.ui.component.MediLabTopAppBar
import com.example.medilab.ui.component.NotifikasiItem
import com.example.medilab.ui.component.SectionHeader
import com.example.medilab.ui.theme.Spacing
import com.example.medilab.ui.util.UiState

@Composable
fun StaffNotifikasiScreen(viewModel: StaffNotifikasiViewModel = viewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) { viewModel.load() }

    Scaffold(
        topBar = {
            MediLabTopAppBar(
                title = "Notifikasi",
                actions = {
                    if (state is UiState.Success) {
                        val data = (state as UiState.Success).data
                        if (data.any { !it.dibaca }) {
                            IconButton(onClick = { viewModel.markAllAsRead() }) {
                                Icon(
                                    imageVector = Icons.Default.DoneAll,
                                    contentDescription = "Tandai semua sudah dibaca"
                                )
                            }
                        }
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when (val s = state) {
                is UiState.Loading -> LoadingState()
                is UiState.Error -> ErrorState(
                    message = s.message,
                    onRetry = { viewModel.load() }
                )
                is UiState.Empty -> EmptyState(
                    title = "Belum ada notifikasi",
                    description = "Notifikasi terbaru akan tampil di sini"
                )
                is UiState.Success -> {
                    val unread = s.data.filter { !it.dibaca }
                    val read = s.data.filter { it.dibaca }
                    LazyColumn(
                        contentPadding = PaddingValues(horizontal = Spacing.lg),
                        verticalArrangement = Arrangement.spacedBy(Spacing.sm)
                    ) {
                        if (unread.isNotEmpty()) {
                            item { SectionHeader(title = "Belum Dibaca (${unread.size})") }
                            items(unread) { notif ->
                                NotifikasiItem(
                                    notifikasi = notif,
                                    onClick = { viewModel.markAsRead(notif.id) }
                                )
                            }
                        }
                        if (read.isNotEmpty()) {
                            item { SectionHeader(title = "Semua Notifikasi") }
                            items(read) { notif ->
                                NotifikasiItem(
                                    notifikasi = notif,
                                    onClick = { viewModel.markAsRead(notif.id) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

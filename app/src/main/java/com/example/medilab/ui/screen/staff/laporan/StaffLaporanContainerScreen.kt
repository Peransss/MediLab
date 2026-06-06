package com.example.medilab.ui.screen.staff.laporan

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.medilab.ui.component.MediLabTopAppBar
import com.example.medilab.ui.component.PrimaryTabRow
import com.example.medilab.util.Constants

@Composable
fun StaffLaporanContainerScreen(onLaporanClick: (String) -> Unit) {
    val statuses = listOf(
        Constants.STATUS_BARU,
        Constants.STATUS_PROSES,
        Constants.STATUS_VERIFIKASI,
        Constants.STATUS_REVISI,
        Constants.STATUS_SELESAI,
        Constants.STATUS_DITOLAK
    )
    val labels = listOf("Baru", "Proses", "Verifikasi", "Revisi", "Selesai", "Ditolak")
    var selectedIndex by remember { mutableIntStateOf(0) }

    Scaffold(
        topBar = { MediLabTopAppBar(title = "Laporan Hasil Lab") }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            PrimaryTabRow(tabs = labels, selectedIndex = selectedIndex, onTabSelected = { selectedIndex = it })
            StaffLaporanListScreen(status = statuses[selectedIndex], onLaporanClick = onLaporanClick)
        }
    }
}

package com.example.medilab.ui.screen.staff.manage

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
import com.example.medilab.ui.screen.staff.manage.dokter.ManageDokterScreen
import com.example.medilab.ui.screen.staff.manage.obat.ManageObatScreen
import com.example.medilab.ui.screen.staff.manage.pasien.ManagePasienScreen
import com.example.medilab.ui.screen.staff.manage.pemeriksaan.ManagePemeriksaanScreen
import com.example.medilab.ui.screen.staff.manage.petugas.ManagePetugasScreen

@Composable
fun ManageContainerScreen() {
    var selectedIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Pasien", "Dokter", "Petugas", "Tes", "Obat")

    Scaffold(
        topBar = { MediLabTopAppBar(title = "Kelola Data") }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding)
        ) {
            PrimaryTabRow(
                tabs = tabs,
                selectedIndex = selectedIndex,
                onTabSelected = { selectedIndex = it }
            )
            when (selectedIndex) {
                0 -> ManagePasienScreen()
                1 -> ManageDokterScreen()
                2 -> ManagePetugasScreen()
                3 -> ManagePemeriksaanScreen()
                4 -> ManageObatScreen()
            }
        }
    }
}

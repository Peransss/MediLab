package com.example.medilab.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController

@Composable
fun PatientRootScreen(rootNavController: NavHostController) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Patient Root - Coming Soon", style = MaterialTheme.typography.headlineMedium)
    }
}

@Composable
fun PatientHomeScreen() { PlaceholderPatient("Patient Home") }
@Composable
fun PatientHasilScreen() { PlaceholderPatient("Patient Hasil") }
@Composable
fun PatientRiwayatScreen() { PlaceholderPatient("Patient Riwayat") }
@Composable
fun PatientNotifikasiScreen() { PlaceholderPatient("Patient Notifikasi") }
@Composable
fun PatientProfileScreen(onLogout: () -> Unit) { PlaceholderPatient("Patient Profile") }
@Composable
fun LaporanDetailScreen(laporanId: String, onBack: () -> Unit) { PlaceholderPatient("Laporan Detail: $laporanId") }

@Composable
private fun PlaceholderPatient(text: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text, style = MaterialTheme.typography.headlineMedium)
    }
}

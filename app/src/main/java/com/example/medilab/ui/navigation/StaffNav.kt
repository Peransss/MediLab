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
fun StaffRootScreen(rootNavController: NavHostController) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Staff Root - Coming Soon", style = MaterialTheme.typography.headlineMedium)
    }
}

@Composable
fun StaffHomeScreen() { PlaceholderStaff("Staff Home") }
@Composable
fun StaffManageScreen() { PlaceholderStaff("Staff Manage") }
@Composable
fun StaffLaporanScreen() { PlaceholderStaff("Staff Laporan") }
@Composable
fun StaffNotifikasiScreen() { PlaceholderStaff("Staff Notifikasi") }
@Composable
fun StaffProfileScreen(onLogout: () -> Unit) { PlaceholderStaff("Staff Profile") }
@Composable
fun StaffLaporanDetailScreen(laporanId: String, onBack: () -> Unit) { PlaceholderStaff("Staff Laporan Detail: $laporanId") }

@Composable
private fun PlaceholderStaff(text: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text, style = MaterialTheme.typography.headlineMedium)
    }
}

package com.example.medilab.ui.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Science
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

data class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector
)

object BottomNavItems {
    val Staff = listOf(
        BottomNavItem("staff/home", "Home", Icons.Default.Home),
        BottomNavItem("staff/manage", "Manage", Icons.Default.Folder),
        BottomNavItem("staff/laporan", "Laporan", Icons.Default.Description),
        BottomNavItem("staff/notifikasi", "Notif", Icons.Default.Notifications),
        BottomNavItem("staff/profile", "Profile", Icons.Default.Person)
    )
    val Patient = listOf(
        BottomNavItem("patient/home", "Home", Icons.Default.Home),
        BottomNavItem("patient/hasil", "Hasil", Icons.Default.Science),
        BottomNavItem("patient/riwayat", "Riwayat", Icons.Default.History),
        BottomNavItem("patient/profile", "Profile", Icons.Default.Person)
    )
}

@Composable
fun MediLabBottomBar(
    items: List<BottomNavItem>,
    currentRoute: String,
    onNavigate: (String) -> Unit
) {
    NavigationBar {
        items.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = { onNavigate(item.route) },
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) }
            )
        }
    }
}

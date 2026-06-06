package com.example.medilab.ui.screen.staff

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.medilab.ui.component.BottomNavItems
import com.example.medilab.ui.component.MediLabBottomBar
import com.example.medilab.ui.navigation.Route
import com.example.medilab.ui.screen.staff.home.StaffHomeScreen
import com.example.medilab.ui.screen.staff.laporan.StaffLaporanContainerScreen
import com.example.medilab.ui.screen.staff.manage.ManageContainerScreen

@Composable
fun StaffRootScreen(rootNavController: NavHostController) {
    val navController = rememberNavController()
    val backStack by navController.currentBackStackEntryAsState()
    val currentRoute = backStack?.destination?.route ?: Route.StaffHome.path

    androidx.compose.material3.Scaffold(
        bottomBar = {
            MediLabBottomBar(
                items = BottomNavItems.Staff,
                currentRoute = currentRoute,
                onNavigate = { route ->
                    navController.navigate(route) {
                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            NavHost(
                navController = navController,
                startDestination = Route.StaffHome.path
            ) {
                composable(Route.StaffHome.path) {
                    StaffHomeScreen(
                        onLaporanClick = { id -> rootNavController.navigate(Route.StaffLaporanDetail.build(id)) },
                        onNotificationClick = { navController.navigate(Route.StaffNotifikasi.path) },
                        onProfileClick = { navController.navigate(Route.StaffProfile.path) }
                    )
                }
                composable(Route.StaffManage.path) { ManageContainerScreen() }
                composable(Route.StaffLaporan.path) {
                    StaffLaporanContainerScreen(
                        onLaporanClick = { id -> rootNavController.navigate(Route.StaffLaporanDetail.build(id)) }
                    )
                }
                composable(Route.StaffNotifikasi.path) { StaffNotifikasiScreen() }
                composable(Route.StaffProfile.path) {
                    StaffProfileScreen(
                        onLogout = {
                            rootNavController.navigate(Route.Login.path) { popUpTo(0) { inclusive = true } }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun StaffNotifikasiScreen() {
    StaffPlaceholder("Notifikasi — coming in Task 8")
}

@Composable
fun StaffProfileScreen(onLogout: () -> Unit) {
    StaffPlaceholder("Profile — coming in Task 8")
}

@Composable
private fun StaffPlaceholder(text: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text, style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.onBackground)
    }
}

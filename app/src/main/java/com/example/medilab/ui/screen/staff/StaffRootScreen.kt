package com.example.medilab.ui.screen.staff

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
import com.example.medilab.ui.screen.staff.notifikasi.StaffNotifikasiScreen
import com.example.medilab.ui.screen.staff.profile.StaffProfileScreen
import com.example.medilab.ui.theme.DarkModeViewModel

@Composable
fun StaffRootScreen(rootNavController: NavHostController, darkModeViewModel: DarkModeViewModel) {
    val isDark by darkModeViewModel.isDarkMode.collectAsStateWithLifecycle()

    val navController = rememberNavController()
    val backStack by navController.currentBackStackEntryAsState()
    val currentRoute = backStack?.destination?.route ?: Route.StaffHome.path

    Scaffold(
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
        NavHost(
            navController = navController,
            startDestination = Route.StaffHome.path,
            modifier = Modifier.fillMaxSize().padding(padding)
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
                    },
                    isDark = isDark,
                    onToggleDark = { darkModeViewModel.toggle() }
                )
            }
        }
    }
}

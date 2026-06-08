package com.example.medilab.ui.screen.staff.home

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.PostAdd
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medilab.MediLabApp
import com.example.medilab.database.entity.LaporanEntity
import com.example.medilab.database.entity.UserEntity
import com.example.medilab.database.repository.LocalLaporanRepository
import com.example.medilab.database.repository.LocalUserRepository
import com.example.medilab.repository.AuthRepository
import com.example.medilab.ui.util.UiState
import com.example.medilab.util.Constants
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class StatCardData(val value: String, val label: String, val icon: ImageVector)

data class StaffHomeData(
    val user: UserEntity,
    val statCards: List<StatCardData>,
    val pendingItems: List<LaporanEntity>,
    val pendingTitle: String,
    val pendingEmptyText: String
)

class StaffHomeViewModel : ViewModel() {
    private val app by lazy { MediLabApp.instance }
    private val authRepo = AuthRepository()
    private val localLaporanRepo = LocalLaporanRepository(app.database, app.syncManager)
    private val localUserRepo = LocalUserRepository(app.database, app.syncManager)

    private val _uiState = MutableStateFlow<UiState<StaffHomeData>>(UiState.Loading)
    val uiState: StateFlow<UiState<StaffHomeData>> = _uiState.asStateFlow()

    private var loadJob: Job? = null

    fun load() {
        loadJob?.cancel()
        _uiState.value = UiState.Loading
        val uid = authRepo.getCurrentUid()
        if (uid.isEmpty()) {
            _uiState.value = UiState.Error("Tidak ada user login")
            return
        }
        loadJob = viewModelScope.launch {
            var userEntity = localUserRepo.getById(uid).first()
            if (userEntity == null) {
                app.syncManager.pullRemoteChanges()
                userEntity = localUserRepo.getById(uid).first()
            }
            if (userEntity == null) {
                userEntity = app.syncManager.pullUserById(uid)
                if (userEntity == null) {
                    _uiState.value = UiState.Error("Gagal memuat user")
                    return@launch
                }
            }
            val role = userEntity.role
            val all = localLaporanRepo.getAll().first()
            val byStatus = all.groupBy { it.status }
            val countBaru = byStatus[Constants.STATUS_BARU]?.size ?: 0
            val countProses = byStatus[Constants.STATUS_PROSES]?.size ?: 0
            val countSelesai = byStatus[Constants.STATUS_SELESAI]?.size ?: 0

            val statCards: List<StatCardData>
            val pendingItems: List<LaporanEntity>
            val pendingTitle: String
            val pendingEmptyText: String

            when (role) {
                Constants.ROLE_ADMIN -> {
                    val pending = localLaporanRepo.getByStatus(Constants.STATUS_VERIFIKASI).first()
                    statCards = listOf(
                        StatCardData(countBaru.toString(), "Baru", Icons.Default.PostAdd),
                        StatCardData(countProses.toString(), "Proses", Icons.Default.HourglassEmpty),
                        StatCardData(countSelesai.toString(), "Selesai", Icons.Default.AssignmentTurnedIn)
                    )
                    pendingItems = pending.sortedByDescending { it.createdAt }.take(5)
                    pendingTitle = "Perlu Verifikasi"
                    pendingEmptyText = "Tidak ada yang perlu diverifikasi"
                }
                Constants.ROLE_PETUGAS -> {
                    val baru = localLaporanRepo.getByStatus(Constants.STATUS_BARU).first()
                    val revisi = localLaporanRepo.getByStatus(Constants.STATUS_REVISI).first()
                    statCards = listOf(
                        StatCardData(countBaru.toString(), "Baru", Icons.Default.PostAdd),
                        StatCardData(countProses.toString(), "Proses", Icons.Default.HourglassEmpty),
                        StatCardData(countSelesai.toString(), "Selesai", Icons.Default.AssignmentTurnedIn)
                    )
                    pendingItems = (baru + revisi).sortedByDescending { it.createdAt }.take(5)
                    pendingTitle = "Perlu Diproses"
                    pendingEmptyText = "Tidak ada yang perlu diproses"
                }
                Constants.ROLE_DOKTER -> {
                    val selesaiList = localLaporanRepo.getByStatus(Constants.STATUS_SELESAI).first()
                    statCards = listOf(
                        StatCardData(countSelesai.toString(), "Selesai", Icons.Default.AssignmentTurnedIn)
                    )
                    pendingItems = selesaiList.sortedByDescending { it.createdAt }.take(5)
                    pendingTitle = "Laporan Terbaru"
                    pendingEmptyText = "Belum ada laporan"
                }
                else -> error("Unknown role")
            }

            _uiState.value = UiState.Success(
                StaffHomeData(
                    user = userEntity,
                    statCards = statCards,
                    pendingItems = pendingItems,
                    pendingTitle = pendingTitle,
                    pendingEmptyText = pendingEmptyText
                )
            )
        }
    }
}

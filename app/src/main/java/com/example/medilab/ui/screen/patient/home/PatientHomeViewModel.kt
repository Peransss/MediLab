package com.example.medilab.ui.screen.patient.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medilab.MediLabApp
import com.example.medilab.database.entity.LaporanEntity
import com.example.medilab.database.entity.NotifikasiEntity
import com.example.medilab.database.entity.UserEntity
import com.example.medilab.database.repository.LocalLaporanRepository
import com.example.medilab.database.repository.LocalNotifikasiRepository
import com.example.medilab.database.repository.LocalUserRepository
import com.example.medilab.repository.AuthRepository
import com.example.medilab.ui.util.UiState
import com.example.medilab.util.Constants
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class PatientHomeData(
    val user: UserEntity,
    val latestLaporan: LaporanEntity?,
    val recentLaporan: List<LaporanEntity>,
    val notifikasi: List<NotifikasiEntity>,
    val pemeriksaanSelesai: Int,
    val menungguHasil: Int,
    val totalRiwayat: Int
)

class PatientHomeViewModel : ViewModel() {
    private val app = MediLabApp.instance
    private val authRepo = AuthRepository()
    private val localUserRepo = LocalUserRepository(app.database, app.syncManager)
    private val localLaporanRepo = LocalLaporanRepository(app.database, app.syncManager)
    private val localNotifikasiRepo = LocalNotifikasiRepository(app.database, app.syncManager)

    private val _uiState = MutableStateFlow<UiState<PatientHomeData>>(UiState.Loading)
    val uiState: StateFlow<UiState<PatientHomeData>> = _uiState.asStateFlow()

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
            localUserRepo.getById(uid).collect { user ->
                if (user == null) return@collect
                launch {
                    localLaporanRepo.getByPasienId(uid).collect { laporanList ->
                        val sorted = laporanList.sortedByDescending { it.createdAt }
                        val selesai = laporanList.count { it.status == Constants.STATUS_SELESAI }
                        val menunggu = laporanList.count {
                            it.status != Constants.STATUS_SELESAI &&
                            it.status != Constants.STATUS_BATAL &&
                            it.status != Constants.STATUS_DITOLAK
                        }
                        val total = laporanList.size
                        launch {
                            localNotifikasiRepo.getByUserId(uid).collect { notifList ->
                                _uiState.value = UiState.Success(
                                    PatientHomeData(
                                        user = user,
                                        latestLaporan = sorted.firstOrNull(),
                                        recentLaporan = sorted.take(3),
                                        notifikasi = notifList.take(5),
                                        pemeriksaanSelesai = selesai,
                                        menungguHasil = menunggu,
                                        totalRiwayat = total
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

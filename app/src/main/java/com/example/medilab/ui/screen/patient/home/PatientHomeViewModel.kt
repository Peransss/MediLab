package com.example.medilab.ui.screen.patient.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medilab.model.Laporan
import com.example.medilab.model.Notifikasi
import com.example.medilab.model.User
import com.example.medilab.repository.AuthRepository
import com.example.medilab.repository.LaporanRepository
import com.example.medilab.repository.NotifikasiRepository
import com.example.medilab.repository.UserRepository
import com.example.medilab.ui.util.UiState
import com.example.medilab.util.Constants
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class PatientHomeData(
    val user: User,
    val latestLaporan: Laporan?,
    val recentLaporan: List<Laporan>,
    val notifikasi: List<Notifikasi>,
    val pemeriksaanSelesai: Int,
    val menungguHasil: Int,
    val totalRiwayat: Int
)

class PatientHomeViewModel : ViewModel() {
    private val authRepo = AuthRepository()
    private val userRepo = UserRepository()
    private val laporanRepo = LaporanRepository()
    private val notifikasiRepo = NotifikasiRepository()

    private val _uiState = MutableStateFlow<UiState<PatientHomeData>>(UiState.Loading)
    val uiState: StateFlow<UiState<PatientHomeData>> = _uiState.asStateFlow()

    fun load() {
        _uiState.value = UiState.Loading
        val uid = authRepo.getCurrentUid()
        if (uid.isEmpty()) {
            _uiState.value = UiState.Error("Tidak ada user login")
            return
        }
        viewModelScope.launch {
            userRepo.getUser(uid) { user ->
                if (user == null) {
                    _uiState.value = UiState.Error("Gagal memuat data user")
                    return@getUser
                }
                laporanRepo.getByPasienId(uid) { laporanList ->
                    val sorted = laporanList.sortedByDescending { it.createdAt }
                    val selesai = laporanList.count { it.status == Constants.STATUS_SELESAI }
                    val menunggu = laporanList.count { it.status != Constants.STATUS_SELESAI && it.status != Constants.STATUS_BATAL && it.status != Constants.STATUS_DITOLAK }
                    val total = laporanList.size
                    notifikasiRepo.getByUserId(uid) { notifList ->
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

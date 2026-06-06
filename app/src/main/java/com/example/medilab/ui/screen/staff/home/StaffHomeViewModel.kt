package com.example.medilab.ui.screen.staff.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medilab.model.Laporan
import com.example.medilab.model.User
import com.example.medilab.repository.AuthRepository
import com.example.medilab.repository.LaporanRepository
import com.example.medilab.repository.UserRepository
import com.example.medilab.ui.util.UiState
import com.example.medilab.util.Constants
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class StaffHomeData(
    val user: User,
    val countBaru: Int,
    val countProses: Int,
    val countSelesai: Int,
    val pendingVerifikasi: List<Laporan>
)

class StaffHomeViewModel : ViewModel() {
    private val authRepo = AuthRepository()
    private val userRepo = UserRepository()
    private val laporanRepo = LaporanRepository()

    private val _uiState = MutableStateFlow<UiState<StaffHomeData>>(UiState.Loading)
    val uiState: StateFlow<UiState<StaffHomeData>> = _uiState.asStateFlow()

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
                    _uiState.value = UiState.Error("Gagal memuat user")
                    return@getUser
                }
                laporanRepo.getAll { all ->
                    val byStatus = all.groupBy { it.status }
                    laporanRepo.getByStatus(Constants.STATUS_VERIFIKASI) { pending ->
                        _uiState.value = UiState.Success(
                            StaffHomeData(
                                user = user,
                                countBaru = byStatus[Constants.STATUS_BARU]?.size ?: 0,
                                countProses = byStatus[Constants.STATUS_PROSES]?.size ?: 0,
                                countSelesai = byStatus[Constants.STATUS_SELESAI]?.size ?: 0,
                                pendingVerifikasi = pending.sortedByDescending { it.createdAt }.take(5)
                            )
                        )
                    }
                }
            }
        }
    }
}

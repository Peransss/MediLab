package com.example.medilab.ui.screen.staff.home

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

data class StaffHomeData(
    val user: UserEntity,
    val countBaru: Int,
    val countProses: Int,
    val countSelesai: Int,
    val pendingVerifikasi: List<LaporanEntity>
)

class StaffHomeViewModel : ViewModel() {
    private val app = MediLabApp.instance
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
            val userEntity = localUserRepo.getById(uid).first()
            if (userEntity == null) {
                _uiState.value = UiState.Error("Gagal memuat user")
                return@launch
            }
            val all = localLaporanRepo.getAll().first()
            val byStatus = all.groupBy { it.status }
            val pending = localLaporanRepo.getByStatus(Constants.STATUS_VERIFIKASI).first()
            _uiState.value = UiState.Success(
                StaffHomeData(
                    user = userEntity,
                    countBaru = byStatus[Constants.STATUS_BARU]?.size ?: 0,
                    countProses = byStatus[Constants.STATUS_PROSES]?.size ?: 0,
                    countSelesai = byStatus[Constants.STATUS_SELESAI]?.size ?: 0,
                    pendingVerifikasi = pending.sortedByDescending { it.createdAt }.take(5)
                )
            )
        }
    }
}

package com.example.medilab.ui.screen.patient.hasil

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medilab.MediLabApp
import com.example.medilab.database.SyncStatus
import com.example.medilab.database.entity.LaporanEntity
import com.example.medilab.database.entity.PemeriksaanEntity
import com.example.medilab.database.repository.LocalLaporanRepository
import com.example.medilab.database.repository.LocalPemeriksaanRepository
import com.example.medilab.repository.AuthRepository
import com.example.medilab.util.Constants
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.UUID

data class BookingUiState(
    val pemeriksaanList: List<PemeriksaanEntity> = emptyList(),
    val selectedPemeriksaanId: String? = null,
    val catatan: String = "",
    val isLoading: Boolean = false,
    val isSubmitting: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)

class PatientBookingViewModel : ViewModel() {
    private val app by lazy { MediLabApp.instance }
    private val authRepo = AuthRepository()
    private val pemeriksaanRepo = LocalPemeriksaanRepository(app.database, app.syncManager)
    private val laporanRepo = LocalLaporanRepository(app.database, app.syncManager)

    private val _uiState = MutableStateFlow(BookingUiState())
    val uiState: StateFlow<BookingUiState> = _uiState.asStateFlow()

    fun load() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val list = pemeriksaanRepo.getAll().first()
            _uiState.value = _uiState.value.copy(
                pemeriksaanList = list,
                isLoading = false
            )
        }
    }

    fun selectPemeriksaan(id: String) {
        _uiState.value = _uiState.value.copy(selectedPemeriksaanId = id)
    }

    fun onCatatanChange(catatan: String) {
        _uiState.value = _uiState.value.copy(catatan = catatan)
    }

    fun submit() {
        val state = _uiState.value
        if (state.selectedPemeriksaanId == null) {
            _uiState.value = state.copy(errorMessage = "Pilih jenis pemeriksaan")
            return
        }
        _uiState.value = state.copy(isSubmitting = true, errorMessage = null)
        val uid = authRepo.getCurrentUid()
        if (uid.isEmpty()) {
            _uiState.value = _uiState.value.copy(isSubmitting = false, errorMessage = "Tidak ada user login")
            return
        }
        viewModelScope.launch {
            val laporan = LaporanEntity(
                id = UUID.randomUUID().toString(),
                pasienId = uid,
                status = Constants.STATUS_BARU,
                pemeriksaanId = state.selectedPemeriksaanId,
                catatanRevisi = state.catatan,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis(),
                syncStatus = SyncStatus.PENDING_CREATE
            )
            laporanRepo.upsert(laporan)
            _uiState.value = _uiState.value.copy(isSubmitting = false, isSuccess = true)
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}

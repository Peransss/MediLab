package com.example.medilab.ui.screen.patient.laporan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medilab.model.Laporan
import com.example.medilab.model.User
import com.example.medilab.repository.LaporanRepository
import com.example.medilab.repository.UserRepository
import com.example.medilab.ui.util.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class LaporanDetailData(
    val laporan: Laporan,
    val pasien: User?,
    val dokter: User?
)

class LaporanDetailViewModel : ViewModel() {
    private val laporanRepo = LaporanRepository()
    private val userRepo = UserRepository()
    private val _uiState = MutableStateFlow<UiState<LaporanDetailData>>(UiState.Loading)
    val uiState: StateFlow<UiState<LaporanDetailData>> = _uiState.asStateFlow()

    fun load(id: String) {
        _uiState.value = UiState.Loading
        viewModelScope.launch {
            laporanRepo.getById(id) { laporan ->
                if (laporan == null) {
                    _uiState.value = UiState.Error("Laporan tidak ditemukan")
                    return@getById
                }
                userRepo.getUser(laporan.pasienId) { pasien ->
                    if (laporan.dokterId.isBlank()) {
                        _uiState.value = UiState.Success(LaporanDetailData(laporan, pasien, null))
                        return@getUser
                    }
                    userRepo.getUser(laporan.dokterId) { dokter ->
                        _uiState.value = UiState.Success(LaporanDetailData(laporan, pasien, dokter))
                    }
                }
            }
        }
    }
}

package com.example.medilab.ui.screen.patient.riwayat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medilab.MediLabApp
import com.example.medilab.database.entity.RekamMedisEntity
import com.example.medilab.database.repository.LocalRekamMedisRepository
import com.example.medilab.model.RekamMedis
import com.example.medilab.repository.AuthRepository
import com.example.medilab.ui.util.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class PatientRiwayatViewModel : ViewModel() {
    private val app by lazy { MediLabApp.instance }
    private val authRepo = AuthRepository()
    private val uid = authRepo.getCurrentUid()

    val uiState: StateFlow<UiState<List<RekamMedis>>> = if (uid.isEmpty()) {
        kotlinx.coroutines.flow.MutableStateFlow(UiState.Empty).asStateFlow()
    } else {
        LocalRekamMedisRepository(app.database, app.syncManager)
            .getByPasienId(uid)
            .map { entities ->
                if (entities.isEmpty()) UiState.Empty
                else UiState.Success(entities.map { it.toModel() })
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UiState.Loading)
    }
}

private fun RekamMedisEntity.toModel() = RekamMedis(
    id = id, pasienId = pasienId, laporanId = laporanId,
    diagnosa = diagnosa, hasilRingkasan = hasilRingkasan,
    rumahSakit = rumahSakit, waktu = waktu, createdAt = createdAt
)

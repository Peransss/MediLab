package com.example.medilab.ui.screen.patient.riwayat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medilab.model.RekamMedis
import com.example.medilab.repository.AuthRepository
import com.example.medilab.ui.util.UiState
import com.example.medilab.util.Constants
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PatientRiwayatViewModel : ViewModel() {
    private val authRepo = AuthRepository()
    private val firestore = FirebaseFirestore.getInstance()
    private val _uiState = MutableStateFlow<UiState<List<RekamMedis>>>(UiState.Loading)
    val uiState: StateFlow<UiState<List<RekamMedis>>> = _uiState.asStateFlow()

    fun load() {
        _uiState.value = UiState.Loading
        val uid = authRepo.getCurrentUid()
        if (uid.isEmpty()) {
            _uiState.value = UiState.Empty
            return
        }
        viewModelScope.launch {
            firestore.collection(Constants.COLLECTION_REKAM_MEDIS)
                .whereEqualTo("pasienId", uid)
                .get()
                .addOnSuccessListener { snap ->
                    val list = snap.documents.mapNotNull { it.toObject(RekamMedis::class.java) }
                        .sortedByDescending { it.createdAt }
                    _uiState.value = if (list.isEmpty()) UiState.Empty else UiState.Success(list)
                }
                .addOnFailureListener { _uiState.value = UiState.Error(it.message ?: "Gagal memuat") }
        }
    }
}

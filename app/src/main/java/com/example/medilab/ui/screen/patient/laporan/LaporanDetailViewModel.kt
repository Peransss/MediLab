package com.example.medilab.ui.screen.patient.laporan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medilab.model.Laporan
import com.example.medilab.util.Constants
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LaporanDetailViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()
    private val _laporan = MutableStateFlow<Laporan?>(null)
    val laporan: StateFlow<Laporan?> = _laporan.asStateFlow()

    fun load(id: String) {
        viewModelScope.launch {
            firestore.collection(Constants.COLLECTION_LAPORAN).document(id).get()
                .addOnSuccessListener { doc ->
                    _laporan.value = doc.toObject(Laporan::class.java)
                }
        }
    }
}

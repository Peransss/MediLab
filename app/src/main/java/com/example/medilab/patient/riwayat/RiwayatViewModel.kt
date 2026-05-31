package com.example.medilab.patient.riwayat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.medilab.model.RekamMedis
import com.example.medilab.util.Constants
import com.google.firebase.firestore.FirebaseFirestore

class RiwayatViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()

    private val _riwayatList = MutableLiveData<List<RekamMedis>>()
    val riwayatList: LiveData<List<RekamMedis>> = _riwayatList

    fun load(pasienId: String) {
        firestore.collection(Constants.COLLECTION_REKAM_MEDIS)
            .whereEqualTo("pasienId", pasienId)
            .orderBy("createdAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { snapshot ->
                val list = snapshot.documents.mapNotNull { it.toObject(RekamMedis::class.java) }
                _riwayatList.value = list
            }
            .addOnFailureListener { _riwayatList.value = emptyList() }
    }
}

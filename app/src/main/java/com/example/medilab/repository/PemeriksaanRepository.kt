package com.example.medilab.repository

import com.example.medilab.model.Pemeriksaan
import com.example.medilab.util.Constants
import com.google.firebase.firestore.FirebaseFirestore

class PemeriksaanRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val collection = firestore.collection(Constants.COLLECTION_PEMERIKSAAN)

    fun getAll(onResult: (List<Pemeriksaan>) -> Unit) {
        collection.get()
            .addOnSuccessListener { snapshot ->
                val list = snapshot.documents.mapNotNull { it.toObject(Pemeriksaan::class.java) }
                onResult(list)
            }
            .addOnFailureListener { onResult(emptyList()) }
    }

    fun add(pemeriksaan: Pemeriksaan, onResult: (Boolean) -> Unit) {
        collection.document(pemeriksaan.id).set(pemeriksaan)
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }

    fun update(id: String, data: Map<String, Any>, onResult: (Boolean) -> Unit) {
        collection.document(id).update(data)
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }

    fun delete(id: String, onResult: (Boolean) -> Unit) {
        collection.document(id).delete()
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }
}

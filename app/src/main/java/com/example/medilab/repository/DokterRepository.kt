package com.example.medilab.repository

import com.example.medilab.model.Dokter
import com.example.medilab.util.Constants
import com.google.firebase.firestore.FirebaseFirestore

class DokterRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val collection = firestore.collection(Constants.COLLECTION_DOKTER)

    fun getAll(onResult: (List<Dokter>) -> Unit) {
        collection.get()
            .addOnSuccessListener { snapshot ->
                val list = snapshot.documents.mapNotNull { it.toObject(Dokter::class.java) }
                onResult(list)
            }
            .addOnFailureListener { onResult(emptyList()) }
    }

    fun add(dokter: Dokter, onResult: (Boolean) -> Unit) {
        collection.document(dokter.id).set(dokter)
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

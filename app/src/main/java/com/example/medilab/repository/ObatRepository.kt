package com.example.medilab.repository

import com.example.medilab.model.Obat
import com.example.medilab.util.Constants
import com.google.firebase.firestore.FirebaseFirestore

class ObatRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val collection = firestore.collection(Constants.COLLECTION_OBAT)

    fun getAll(onResult: (List<Obat>) -> Unit) {
        collection.get()
            .addOnSuccessListener { snapshot ->
                val list = snapshot.documents.mapNotNull { it.toObject(Obat::class.java) }
                onResult(list)
            }
            .addOnFailureListener { onResult(emptyList()) }
    }

    fun add(obat: Obat, onResult: (Boolean) -> Unit) {
        collection.document(obat.id).set(obat)
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

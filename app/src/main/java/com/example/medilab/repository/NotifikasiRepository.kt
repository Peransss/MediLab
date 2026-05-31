package com.example.medilab.repository

import com.example.medilab.model.Notifikasi
import com.example.medilab.util.Constants
import com.google.firebase.firestore.FirebaseFirestore

class NotifikasiRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val collection = firestore.collection(Constants.COLLECTION_NOTIFIKASI)

    fun getByUserId(userId: String, onResult: (List<Notifikasi>) -> Unit) {
        collection.whereEqualTo("userId", userId)
            .orderBy("createdAt", com.google.firebase.firestore.Query.Direction.DESCENDING).get()
            .addOnSuccessListener { snapshot ->
                val list = snapshot.documents.mapNotNull { it.toObject(Notifikasi::class.java) }
                onResult(list)
            }
            .addOnFailureListener { onResult(emptyList()) }
    }

    fun add(notifikasi: Notifikasi, onResult: (Boolean) -> Unit) {
        collection.document(notifikasi.id).set(notifikasi)
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }

    fun markAsRead(id: String, onResult: (Boolean) -> Unit) {
        collection.document(id).update("dibaca", true)
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }
}

package com.example.medilab.repository

import com.example.medilab.database.entity.AntrianEntity
import com.example.medilab.util.Constants
import com.google.firebase.firestore.FirebaseFirestore

class AntrianRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val collection = firestore.collection(Constants.COLLECTION_ANTRIAN)

    fun addToQueue(antrian: AntrianEntity, onResult: (Boolean) -> Unit) {
        collection.document().set(antrian)
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }
}

package com.example.medilab.repository

import com.example.medilab.model.Laporan
import com.example.medilab.util.Constants
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

class LaporanRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val collection = firestore.collection(Constants.COLLECTION_LAPORAN)

    fun getAll(onResult: (List<Laporan>) -> Unit) {
        collection.orderBy("createdAt", com.google.firebase.firestore.Query.Direction.DESCENDING).get()
            .addOnSuccessListener { snapshot: QuerySnapshot ->
                val list = snapshot.documents.mapNotNull { it.toObject(Laporan::class.java) }
                onResult(list)
            }
            .addOnFailureListener { onResult(emptyList()) }
    }

    fun getByStatus(status: String, onResult: (List<Laporan>) -> Unit) {
        collection.whereEqualTo("status", status)
            .orderBy("createdAt", com.google.firebase.firestore.Query.Direction.DESCENDING).get()
            .addOnSuccessListener { snapshot ->
                val list = snapshot.documents.mapNotNull { it.toObject(Laporan::class.java) }
                onResult(list)
            }
            .addOnFailureListener { onResult(emptyList()) }
    }

    fun getByPasienId(pasienId: String, onResult: (List<Laporan>) -> Unit) {
        collection.whereEqualTo("pasienId", pasienId)
            .orderBy("createdAt", com.google.firebase.firestore.Query.Direction.DESCENDING).get()
            .addOnSuccessListener { snapshot ->
                val list = snapshot.documents.mapNotNull { it.toObject(Laporan::class.java) }
                onResult(list)
            }
            .addOnFailureListener { onResult(emptyList()) }
    }

    fun getByPasienIdAndStatus(pasienId: String, status: String, onResult: (List<Laporan>) -> Unit) {
        collection.whereEqualTo("pasienId", pasienId)
            .whereEqualTo("status", status)
            .orderBy("createdAt", com.google.firebase.firestore.Query.Direction.DESCENDING).get()
            .addOnSuccessListener { snapshot ->
                val list = snapshot.documents.mapNotNull { it.toObject(Laporan::class.java) }
                onResult(list)
            }
            .addOnFailureListener { onResult(emptyList()) }
    }

    fun getById(id: String, onResult: (Laporan?) -> Unit) {
        collection.document(id).get()
            .addOnSuccessListener { doc -> onResult(doc.toObject(Laporan::class.java)) }
            .addOnFailureListener { onResult(null) }
    }

    fun add(laporan: Laporan, onResult: (Boolean) -> Unit) {
        collection.document(laporan.id).set(laporan)
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }

    fun updateStatus(id: String, status: String, adminId: String = "", onResult: (Boolean) -> Unit) {
        val data = mutableMapOf<String, Any>("status" to status, "updatedAt" to System.currentTimeMillis())
        if (adminId.isNotEmpty()) data["adminId"] = adminId
        if (status == Constants.STATUS_SELESAI) data["tanggalSelesai"] = System.currentTimeMillis()
        collection.document(id).update(data)
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }

    fun update(id: String, data: Map<String, Any>, onResult: (Boolean) -> Unit) {
        collection.document(id).update(data)
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }
}

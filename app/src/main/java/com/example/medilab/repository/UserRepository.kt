package com.example.medilab.repository

import com.example.medilab.model.User
import com.example.medilab.util.Constants
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

class UserRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val collection = firestore.collection(Constants.COLLECTION_USERS)

    fun getUser(uid: String, onResult: (User?) -> Unit) {
        collection.document(uid).get()
            .addOnSuccessListener { doc ->
                val user = doc.toObject(User::class.java)
                onResult(user)
            }
            .addOnFailureListener { onResult(null) }
    }

    fun getUsersByRole(role: String, onResult: (List<User>) -> Unit) {
        collection.whereEqualTo("role", role).get()
            .addOnSuccessListener { snapshot: QuerySnapshot ->
                val users = snapshot.documents.mapNotNull { it.toObject(User::class.java) }
                onResult(users)
            }
            .addOnFailureListener { onResult(emptyList()) }
    }

    fun getAllUsers(onResult: (List<User>) -> Unit) {
        collection.get()
            .addOnSuccessListener { snapshot: QuerySnapshot ->
                val users = snapshot.documents.mapNotNull { it.toObject(User::class.java) }
                onResult(users)
            }
            .addOnFailureListener { onResult(emptyList()) }
    }

    fun updateUser(uid: String, data: Map<String, Any>, onResult: (Boolean) -> Unit) {
        collection.document(uid).update(data)
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }

    fun deleteUser(uid: String, onResult: (Boolean) -> Unit) {
        collection.document(uid).delete()
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }
}

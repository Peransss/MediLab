package com.example.medilab.repository

import com.example.medilab.model.User
import com.example.medilab.util.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AuthRepository {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    fun login(email: String, password: String, onResult: (Boolean, String, String?) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uid = auth.currentUser?.uid ?: ""
                    firestore.collection(Constants.COLLECTION_USERS).document(uid).get()
                        .addOnSuccessListener { doc ->
                            val role = doc.getString("role") ?: ""
                            onResult(true, "Login berhasil", role)
                        }
                        .addOnFailureListener {
                            onResult(false, "Gagal memuat data user", null)
                        }
                } else {
                    onResult(false, task.exception?.message ?: "Login gagal", null)
                }
            }
    }

    fun registerPatient(email: String, password: String, nama: String, noHP: String, alamat: String, tanggalLahir: String, onResult: (Boolean, String) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uid = auth.currentUser?.uid ?: ""
                    val user = User(
                        id = uid,
                        nama = nama,
                        email = email,
                        role = Constants.ROLE_PASIEN,
                        noHP = noHP,
                        alamat = alamat,
                        tanggalLahir = tanggalLahir,
                        noRekamMedis = generateNoRM()
                    )
                    firestore.collection(Constants.COLLECTION_USERS).document(uid).set(user)
                        .addOnSuccessListener { onResult(true, "Registrasi berhasil") }
                        .addOnFailureListener { onResult(false, "Gagal menyimpan data") }
                } else {
                    onResult(false, task.exception?.message ?: "Registrasi gagal")
                }
            }
    }

    fun createStaffAccount(email: String, password: String, nama: String, role: String, noHP: String, onResult: (Boolean, String) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uid = auth.currentUser?.uid ?: ""
                    val user = User(
                        id = uid,
                        nama = nama,
                        email = email,
                        role = role,
                        noHP = noHP
                    )
                    firestore.collection(Constants.COLLECTION_USERS).document(uid).set(user)
                        .addOnSuccessListener { onResult(true, "Akun $role berhasil dibuat") }
                        .addOnFailureListener { onResult(false, "Gagal menyimpan data") }
                } else {
                    onResult(false, task.exception?.message ?: "Gagal membuat akun")
                }
            }
    }

    fun getCurrentUid(): String = auth.currentUser?.uid ?: ""

    fun logout() {
        auth.signOut()
    }

    fun sendPasswordReset(email: String, onResult: (Boolean, String) -> Unit) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) onResult(true, "Email reset terkirim")
                else onResult(false, task.exception?.message ?: "Gagal mengirim email")
            }
    }

    private fun generateNoRM(): String {
        val timestamp = System.currentTimeMillis() % 100000
        return "RM-$timestamp"
    }
}

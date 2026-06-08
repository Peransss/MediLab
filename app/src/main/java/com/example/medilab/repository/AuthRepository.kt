package com.example.medilab.repository

import com.example.medilab.model.User
import com.example.medilab.util.Constants
import com.google.firebase.auth.EmailAuthProvider
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
        // Use a secondary FirebaseApp instance so the primary auth session
        // (the currently signed-in staff member) is NOT replaced by the new
        // account being created. createUserWithEmailAndPassword on the default
        // app implicitly signs in the new user.
        val secondaryApp = try {
            com.google.firebase.FirebaseApp.getInstance("staffCreate")
        } catch (e: IllegalStateException) {
            val options = com.google.firebase.FirebaseApp.getInstance().options
            com.google.firebase.FirebaseApp.initializeApp(
                com.example.medilab.MediLabApp.instance.applicationContext,
                options,
                "staffCreate"
            )
        }
        val secondaryAuth = FirebaseAuth.getInstance(secondaryApp)
        val secondaryDb = FirebaseFirestore.getInstance(secondaryApp)

        secondaryAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uid = secondaryAuth.currentUser?.uid ?: ""
                    val user = User(
                        id = uid,
                        nama = nama,
                        email = email,
                        role = role,
                        noHP = noHP
                    )
                    secondaryDb.collection(Constants.COLLECTION_USERS).document(uid).set(user)
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

    fun changePassword(oldPassword: String, newPassword: String, onResult: (Boolean, String) -> Unit) {
        val user = auth.currentUser ?: return onResult(false, "Tidak ada user login")
        val email = user.email ?: return onResult(false, "Email tidak tersedia")
        val credential = EmailAuthProvider.getCredential(email, oldPassword)
        user.reauthenticate(credential)
            .addOnSuccessListener {
                user.updatePassword(newPassword)
                    .addOnSuccessListener { onResult(true, "Password diubah") }
                    .addOnFailureListener { onResult(false, it.message ?: "Gagal mengubah password") }
            }
            .addOnFailureListener { onResult(false, "Password lama salah") }
    }

    private fun generateNoRM(): String {
        val uuid = java.util.UUID.randomUUID().toString().take(8)
        return "RM-${System.currentTimeMillis() % 100000}-$uuid"
    }
}

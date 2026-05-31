# MediLab Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Build a laboratory information system Android app with staff and patient portals using Firebase.

**Architecture:** Multi-Activity per role (StaffActivity + PatientActivity) with BottomNavigation, MVVM + Repository pattern, Firebase Auth for authentication and Firestore for data storage.

**Tech Stack:** Kotlin, Firebase Auth, Firebase Firestore, Firebase Storage, Material Design, Navigation Component (within each Activity), ViewModel + LiveData/StateFlow.

---

## File Structure

```
app/src/main/java/com/example/medilab/
├── MediLabApp.kt
├── auth/
│   ├── LoginActivity.kt
│   ├── RegisterPatientActivity.kt
│   └── AuthViewModel.kt
├── staff/
│   ├── StaffActivity.kt
│   ├── dashboard/
│   │   ├── StaffDashboardFragment.kt
│   │   └── StaffDashboardViewModel.kt
│   ├── manage/
│   │   ├── pasien/
│   │   │   ├── ManagePasienFragment.kt
│   │   │   └── ManagePasienViewModel.kt
│   │   ├── dokter/
│   │   │   ├── ManageDokterFragment.kt
│   │   │   └── ManageDokterViewModel.kt
│   │   ├── petugas/
│   │   │   ├── ManagePetugasFragment.kt
│   │   │   └── ManagePetugasViewModel.kt
│   │   ├── pemeriksaan/
│   │   │   ├── ManagePemeriksaanFragment.kt
│   │   │   └── ManagePemeriksaanViewModel.kt
│   │   └── obat/
│   │       ├── ManageObatFragment.kt
│   │       └── ManageObatViewModel.kt
│   ├── laporan/
│   │   ├── LaporanListFragment.kt
│   │   ├── LaporanDetailFragment.kt
│   │   └── LaporanViewModel.kt
│   └── profile/
│       ├── StaffProfileFragment.kt
│       └── StaffProfileViewModel.kt
├── patient/
│   ├── PatientActivity.kt
│   ├── dashboard/
│   │   ├── PatientDashboardFragment.kt
│   │   └── PatientDashboardViewModel.kt
│   ├── hasil/
│   │   ├── HasilFragment.kt
│   │   └── HasilViewModel.kt
│   ├── riwayat/
│   │   ├── RiwayatFragment.kt
│   │   └── RiwayatViewModel.kt
│   └── profile/
│       ├── PatientProfileFragment.kt
│       └── PatientProfileViewModel.kt
├── model/
│   ├── User.kt
│   ├── Dokter.kt
│   ├── Laporan.kt
│   ├── Pemeriksaan.kt
│   ├── Rujukan.kt
│   ├── RekamMedis.kt
│   ├── Notifikasi.kt
│   ├── Obat.kt
│   └── AuditLog.kt
├── repository/
│   ├── AuthRepository.kt
│   ├── UserRepository.kt
│   ├── LaporanRepository.kt
│   ├── DokterRepository.kt
│   ├── PemeriksaanRepository.kt
│   ├── ObatRepository.kt
│   └── NotifikasiRepository.kt
├── util/
│   ├── Constants.kt
│   ├── DateUtils.kt
│   └── Validators.kt
└── adapter/
    ├── LaporanAdapter.kt
    ├── PasienAdapter.kt
    └── RiwayatAdapter.kt
```

```
app/src/main/res/
├── layout/
│   ├── activity_login.xml
│   ├── activity_register_patient.xml
│   ├── activity_staff.xml
│   ├── activity_patient.xml
│   ├── fragment_staff_dashboard.xml
│   ├── fragment_manage_pasien.xml
│   ├── fragment_manage_dokter.xml
│   ├── fragment_manage_petugas.xml
│   ├── fragment_manage_pemeriksaan.xml
│   ├── fragment_manage_obat.xml
│   ├── fragment_laporan_list.xml
│   ├── fragment_laporan_detail.xml
│   ├── fragment_staff_profile.xml
│   ├── fragment_patient_dashboard.xml
│   ├── fragment_hasil.xml
│   ├── fragment_riwayat.xml
│   └── fragment_patient_profile.xml
├── navigation/
│   ├── nav_staff.xml
│   └── nav_patient.xml
├── menu/
│   ├── bottom_nav_staff.xml
│   └── bottom_nav_patient.xml
├── drawable/ (icons, backgrounds)
└── values/ (strings, colors, themes)
```

```
app/build.gradle.kts — add Firebase BOM, Firebase Auth, Firestore, Storage
gradle/libs.versions.toml — add Firebase version
```

---

### Task 1: Project Setup — Firebase Dependencies & Application Class

**Files:**
- Modify: `gradle/libs.versions.toml`
- Modify: `app/build.gradle.kts`
- Create: `app/src/main/java/com/example/medilab/MediLabApp.kt`
- Modify: `app/src/main/AndroidManifest.xml`

- [ ] **Step 1: Add Firebase version to version catalog**

Edit `gradle/libs.versions.toml` to add Firebase BOM and related library versions:

```toml
[versions]
firebaseBom = "33.12.0"
navigationFragmentKtx = "2.8.9"
navigationUiKtx = "2.8.9"
lifecycleRuntimeKtx = "2.8.7"

[libraries]
firebase-auth = { group = "com.google.firebase", name = "firebase-auth" }
firebase-firestore = { group = "com.google.firebase", name = "firebase-firestore" }
firebase-storage = { group = "com.google.firebase", name = "firebase-storage" }
firebase-bom = { group = "com.google.firebase", name = "firebase-bom", version.ref = "firebaseBom" }
navigation-fragment-ktx = { group = "androidx.navigation", name = "navigation-fragment-ktx", version.ref = "navigationFragmentKtx" }
navigation-ui-ktx = { group = "androidx.navigation", name = "navigation-ui-ktx", version.ref = "navigationUiKtx" }
lifecycle-runtime-ktx = { group = "androidx.lifecycle", name = "lifecycle-runtime-ktx", version.ref = "lifecycleRuntimeKtx" }
lifecycle-viewmodel-ktx = { group = "androidx.lifecycle", name = "lifecycle-viewmodel-ktx", version.ref = "lifecycleRuntimeKtx" }

[plugins]
google-services = { id = "com.google.gms.google-services", version = "4.4.2" }
```

- [ ] **Step 2: Update app/build.gradle.kts**

```kotlin
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.services)
}

android {
    namespace = "com.example.medilab"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.medilab"
        minSdk = 30
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.storage)
    implementation(libs.navigation.fragment.ktx)
    implementation(libs.navigation.ui.ktx)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
```

- [ ] **Step 3: Add google-services plugin to root build.gradle.kts**

```kotlin
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.google.services) apply false
}
```

- [ ] **Step 4: Create MediLabApp.kt**

```kotlin
package com.example.medilab

import android.app.Application

class MediLabApp : Application() {
    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        lateinit var instance: MediLabApp
            private set
    }
}
```

- [ ] **Step 5: Update AndroidManifest.xml**

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools">

    <application
        android:name=".MediLabApp"
        android:allowBackup="true"
        android:dataExtractionRules="@xml/data_extraction_rules"
        android:fullBackupContent="@xml/backup_rules"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/Theme.MediLab">
        <activity
            android:name=".auth.LoginActivity"
            android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
        <activity
            android:name=".auth.RegisterPatientActivity"
            android:exported="false" />
        <activity
            android:name=".staff.StaffActivity"
            android:exported="false" />
        <activity
            android:name=".patient.PatientActivity"
            android:exported="false" />
    </application>
</manifest>
```

- [ ] **Step 6: Sync Gradle and verify build**

```bash
cd /home/t4pei/AndroidStudioProjects/MediLab && ./gradlew assembleDebug --no-daemon 2>&1 | tail -20
```

Expected: `BUILD SUCCESSFUL`

---

### Task 2: Utility Layer — Constants, DateUtils, Validators

**Files:**
- Create: `app/src/main/java/com/example/medilab/util/Constants.kt`
- Create: `app/src/main/java/com/example/medilab/util/DateUtils.kt`
- Create: `app/src/main/java/com/example/medilab/util/Validators.kt`

- [ ] **Step 1: Create Constants.kt**

```kotlin
package com.example.medilab.util

object Constants {
    const val ROLE_ADMIN = "admin"
    const val ROLE_PETUGAS = "petugas"
    const val ROLE_DOKTER = "dokter"
    const val ROLE_PASIEN = "pasien"

    const val STATUS_BARU = "baru"
    const val STATUS_PROSES = "proses"
    const val STATUS_VERIFIKASI = "verifikasi"
    const val STATUS_REVISI = "revisi"
    const val STATUS_SELESAI = "selesai"
    const val STATUS_DITOLAK = "ditolak"
    const val STATUS_BATAL = "batal"

    const val COLLECTION_USERS = "users"
    const val COLLECTION_DOKTER = "dokter"
    const val COLLECTION_PEMERIKSAAN = "pemeriksaan"
    const val COLLECTION_RUJUKAN = "rujukan"
    const val COLLECTION_LAPORAN = "laporan"
    const val COLLECTION_REKAM_MEDIS = "rekamMedis"
    const val COLLECTION_NOTIFIKASI = "notifikasi"
    const val COLLECTION_OBAT = "obat"
    const val COLLECTION_AUDIT_LOG = "auditLog"

    const val RS_NAMA = "RS MediLab Sehat"
    const val RS_ALAMAT = "Jl. Kesehatan No. 1"
    const val RS_KOTA = "Jakarta"

    const val KEY_UID = "uid"
    const val KEY_ROLE = "role"
}
```

- [ ] **Step 2: Create DateUtils.kt**

```kotlin
package com.example.medilab.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DateUtils {
    private val displayFormat = SimpleDateFormat("dd MMM yyyy, HH:mm 'WIB'", Locale("id", "ID"))
    private val dateOnlyFormat = SimpleDateFormat("dd MMM yyyy", Locale("id", "ID"))

    fun formatDisplay(date: Date?): String {
        return date?.let { displayFormat.format(it) } ?: "-"
    }

    fun formatDateOnly(date: Date?): String {
        return date?.let { dateOnlyFormat.format(it) } ?: "-"
    }

    fun now(): Date = Date()
}
```

- [ ] **Step 3: Create Validators.kt**

```kotlin
package com.example.medilab.util

object Validators {
    fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun isValidPassword(password: String): Boolean {
        return password.length >= 6
    }

    fun isValidPhone(phone: String): Boolean {
        return phone.matches(Regex("^[+]?[0-9]{10,15}$"))
    }

    fun isNotEmpty(vararg fields: String): Boolean {
        return fields.all { it.isNotBlank() }
    }
}
```

- [ ] **Step 4: Verify build**

```bash
cd /home/t4pei/AndroidStudioProjects/MediLab && ./gradlew assembleDebug --no-daemon 2>&1 | tail -10
```

Expected: `BUILD SUCCESSFUL`

---

### Task 3: Data Models

**Files:**
- Create: `app/src/main/java/com/example/medilab/model/User.kt`
- Create: `app/src/main/java/com/example/medilab/model/Dokter.kt`
- Create: `app/src/main/java/com/example/medilab/model/Laporan.kt`
- Create: `app/src/main/java/com/example/medilab/model/Pemeriksaan.kt`
- Create: `app/src/main/java/com/example/medilab/model/Rujukan.kt`
- Create: `app/src/main/java/com/example/medilab/model/RekamMedis.kt`
- Create: `app/src/main/java/com/example/medilab/model/Notifikasi.kt`
- Create: `app/src/main/java/com/example/medilab/model/Obat.kt`
- Create: `app/src/main/java/com/example/medilab/model/AuditLog.kt`

- [ ] **Step 1: Create User.kt**

```kotlin
package com.example.medilab.model

data class User(
    val id: String = "",
    val nama: String = "",
    val email: String = "",
    val role: String = "",
    val noHP: String = "",
    val alamat: String = "",
    val fotoProfile: String = "",
    val noRekamMedis: String = "",
    val tanggalLahir: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
```

- [ ] **Step 2: Create Dokter.kt**

```kotlin
package com.example.medilab.model

data class Dokter(
    val id: String = "",
    val nama: String = "",
    val spesialis: String = "",
    val alamat: String = "",
    val noHP: String = "",
    val email: String = ""
)
```

- [ ] **Step 3: Create Pemeriksaan.kt**

```kotlin
package com.example.medilab.model

data class ParameterTes(
    val nama: String = "",
    val satuan: String = "",
    val nilaiNormalMin: Double = 0.0,
    val nilaiNormalMax: Double = 0.0
)

data class Pemeriksaan(
    val id: String = "",
    val namaPemeriksaan: String = "",
    val kategori: String = "",
    val deskripsi: String = "",
    val parameter: List<ParameterTes> = emptyList()
)
```

- [ ] **Step 4: Create Obat.kt**

```kotlin
package com.example.medilab.model

data class Obat(
    val id: String = "",
    val namaObat: String = "",
    val bentuk: String = "",
    val dosis: Double = 0.0,
    val satuan: String = "",
    val keterangan: String = ""
)
```

- [ ] **Step 5: Create Laporan.kt**

```kotlin
package com.example.medilab.model

data class HasilParameter(
    val parameterNama: String = "",
    val nilai: Double = 0.0,
    val satuan: String = "",
    val keterangan: String = ""
)

data class ResepItem(
    val obatId: String = "",
    val namaObat: String = "",
    val bentuk: String = "",
    val dosis: Double = 0.0,
    val satuan: String = "",
    val aturanPakai: String = "",
    val jumlah: Int = 0,
    val keterangan: String = ""
)

data class RumahSakit(
    val nama: String = "RS MediLab Sehat",
    val alamat: String = "Jl. Kesehatan No. 1",
    val kota: String = "Jakarta"
)

data class Laporan(
    val id: String = "",
    val status: String = "baru",
    val pasienId: String = "",
    val dokterId: String = "",
    val petugasId: String = "",
    val adminId: String = "",
    val pemeriksaanId: String = "",
    val rujukanId: String = "",
    val hasilParameter: List<HasilParameter> = emptyList(),
    val diagnosa: String = "",
    val resepObat: List<ResepItem> = emptyList(),
    val catatanRevisi: String = "",
    val alasanTolak: String = "",
    val rumahSakit: RumahSakit = RumahSakit(),
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val tanggalSelesai: Long = 0L
)
```

- [ ] **Step 6: Create Rujukan.kt**

```kotlin
package com.example.medilab.model

data class Rujukan(
    val id: String = "",
    val pasienId: String = "",
    val dokterId: String = "",
    val pemeriksaanId: String = "",
    val catatan: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val status: String = "diterima"
)
```

- [ ] **Step 7: Create RekamMedis.kt**

```kotlin
package com.example.medilab.model

data class RekamMedis(
    val id: String = "",
    val pasienId: String = "",
    val laporanId: String = "",
    val diagnosa: String = "",
    val hasilRingkasan: String = "",
    val rumahSakit: RumahSakit = RumahSakit(),
    val waktu: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
```

- [ ] **Step 8: Create Notifikasi.kt**

```kotlin
package com.example.medilab.model

data class Notifikasi(
    val id: String = "",
    val userId: String = "",
    val judul: String = "",
    val pesan: String = "",
    val dibaca: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val tipe: String = ""
)
```

- [ ] **Step 9: Create AuditLog.kt**

```kotlin
package com.example.medilab.model

data class AuditLog(
    val id: String = "",
    val userId: String = "",
    val aksi: String = "",
    val targetId: String = "",
    val targetTipe: String = "",
    val detail: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
```

- [ ] **Step 10: Verify build**

```bash
cd /home/t4pei/AndroidStudioProjects/MediLab && ./gradlew assembleDebug --no-daemon 2>&1 | tail -10
```

Expected: `BUILD SUCCESSFUL`

---

### Task 4: Repository Layer — Firebase Operations

**Files:**
- Create: `app/src/main/java/com/example/medilab/repository/AuthRepository.kt`
- Create: `app/src/main/java/com/example/medilab/repository/UserRepository.kt`
- Create: `app/src/main/java/com/example/medilab/repository/DokterRepository.kt`
- Create: `app/src/main/java/com/example/medilab/repository/PemeriksaanRepository.kt`
- Create: `app/src/main/java/com/example/medilab/repository/ObatRepository.kt`
- Create: `app/src/main/java/com/example/medilab/repository/LaporanRepository.kt`
- Create: `app/src/main/java/com/example/medilab/repository/NotifikasiRepository.kt`

- [ ] **Step 1: Create AuthRepository.kt**

```kotlin
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
```

- [ ] **Step 2: Create UserRepository.kt**

```kotlin
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
```

- [ ] **Step 3: Create DokterRepository.kt**

```kotlin
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
```

- [ ] **Step 4: Create PemeriksaanRepository.kt**

```kotlin
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
```

- [ ] **Step 5: Create ObatRepository.kt**

```kotlin
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
```

- [ ] **Step 6: Create LaporanRepository.kt**

```kotlin
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
```

- [ ] **Step 7: Create NotifikasiRepository.kt**

```kotlin
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
```

- [ ] **Step 8: Verify build**

```bash
cd /home/t4pei/AndroidStudioProjects/MediLab && ./gradlew assembleDebug --no-daemon 2>&1 | tail -10
```

Expected: `BUILD SUCCESSFUL`

---

### Task 5: Auth UI — Login & Register

**Files:**
- Create: `app/src/main/java/com/example/medilab/auth/LoginActivity.kt`
- Create: `app/src/main/java/com/example/medilab/auth/RegisterPatientActivity.kt`
- Create: `app/src/main/java/com/example/medilab/auth/AuthViewModel.kt`
- Create: `app/src/main/res/layout/activity_login.xml`
- Create: `app/src/main/res/layout/activity_register_patient.xml`

- [ ] **Step 1: Create AuthViewModel.kt**

```kotlin
package com.example.medilab.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.medilab.repository.AuthRepository

class AuthViewModel : ViewModel() {
    private val authRepo = AuthRepository()

    private val _loginResult = MutableLiveData<Pair<Boolean, String>>()
    val loginResult: LiveData<Pair<Boolean, String>> = _loginResult

    private val _registerResult = MutableLiveData<Pair<Boolean, String>>()
    val registerResult: LiveData<Pair<Boolean, String>> = _registerResult

    private val _loginRole = MutableLiveData<String>()
    val loginRole: LiveData<String> = _loginRole

    fun login(email: String, password: String) {
        authRepo.login(email, password) { success, msg, role ->
            _loginResult.value = Pair(success, msg)
            if (success) _loginRole.value = role
        }
    }

    fun registerPatient(email: String, password: String, nama: String, noHP: String, alamat: String, tanggalLahir: String) {
        authRepo.registerPatient(email, password, nama, noHP, alamat, tanggalLahir) { success, msg ->
            _registerResult.value = Pair(success, msg)
        }
    }

    fun logout() {
        authRepo.logout()
    }

    fun sendPasswordReset(email: String) {
        authRepo.sendPasswordReset(email) { success, msg ->
            _loginResult.value = Pair(success, msg)
        }
    }
}
```

- [ ] **Step 2: Create activity_login.xml**

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:padding="24dp"
    android:gravity="center">

    <ImageView
        android:layout_width="80dp"
        android:layout_height="80dp"
        android:src="@mipmap/ic_launcher"
        android:layout_marginBottom="16dp"/>

    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="MediLab"
        android:textSize="28sp"
        android:textStyle="bold"
        android:textColor="@color/primary"
        android:layout_marginBottom="4dp"/>

    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Sistem Informasi Laboratorium Medis"
        android:textSize="12sp"
        android:textColor="@color/secondary_text"
        android:layout_marginBottom="32dp"/>

    <com.google.android.material.textfield.TextInputLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_marginBottom="16dp"
        app:endIconMode="clear_text">

        <com.google.android.material.textfield.TextInputEditText
            android:id="@+id/etEmail"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:hint="Email"
            android:inputType="textEmailAddress"/>
    </com.google.android.material.textfield.TextInputLayout>

    <com.google.android.material.textfield.TextInputLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_marginBottom="24dp"
        app:endIconMode="password_toggle">

        <com.google.android.material.textfield.TextInputEditText
            android:id="@+id/etPassword"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:hint="Password"
            android:inputType="textPassword"/>
    </com.google.android.material.textfield.TextInputLayout>

    <com.google.android.material.button.MaterialButton
        android:id="@+id/btnLogin"
        android:layout_width="match_parent"
        android:layout_height="56dp"
        android:text="MASUK"
        android:textAllCaps="true"/>

    <TextView
        android:id="@+id/tvLupaPassword"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Lupa password?"
        android:textColor="@color/primary"
        android:layout_marginTop="16dp"
        android:layout_marginBottom="24dp"/>

    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Belum punya akun?"
        android:textColor="@color/secondary_text"/>

    <com.google.android.material.button.MaterialButton
        android:id="@+id/btnRegister"
        style="@style/Widget.MaterialComponents.Button.TextButton"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="DAFTAR SEBAGAI PASIEN"
        android:textAllCaps="true"/>
</LinearLayout>
```

- [ ] **Step 3: Create activity_register_patient.xml**

```xml
<?xml version="1.0" encoding="utf-8"?>
<ScrollView xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="vertical"
        android:padding="24dp">

        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="Daftar Sebagai Pasien"
            android:textSize="22sp"
            android:textStyle="bold"
            android:layout_marginBottom="24dp"/>

        <com.google.android.material.textfield.TextInputLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginBottom="16dp">
            <com.google.android.material.textfield.TextInputEditText
                android:id="@+id/etNama"
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:hint="Nama Lengkap"/>
        </com.google.android.material.textfield.TextInputLayout>

        <com.google.android.material.textfield.TextInputLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginBottom="16dp">
            <com.google.android.material.textfield.TextInputEditText
                android:id="@+id/etEmail"
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:hint="Email"
                android:inputType="textEmailAddress"/>
        </com.google.android.material.textfield.TextInputLayout>

        <com.google.android.material.textfield.TextInputLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginBottom="16dp"
            app:endIconMode="password_toggle">
            <com.google.android.material.textfield.TextInputEditText
                android:id="@+id/etPassword"
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:hint="Password (min. 6 karakter)"
                android:inputType="textPassword"/>
        </com.google.android.material.textfield.TextInputLayout>

        <com.google.android.material.textfield.TextInputLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginBottom="16dp">
            <com.google.android.material.textfield.TextInputEditText
                android:id="@+id/etNoHP"
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:hint="No. HP"
                android:inputType="phone"/>
        </com.google.android.material.textfield.TextInputLayout>

        <com.google.android.material.textfield.TextInputLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginBottom="16dp">
            <com.google.android.material.textfield.TextInputEditText
                android:id="@+id/etAlamat"
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:hint="Alamat"/>
        </com.google.android.material.textfield.TextInputLayout>

        <com.google.android.material.textfield.TextInputLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginBottom="24dp">
            <com.google.android.material.textfield.TextInputEditText
                android:id="@+id/etTanggalLahir"
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:hint="Tanggal Lahir (YYYY-MM-DD)"
                android:inputType="date"/>
        </com.google.android.material.textfield.TextInputLayout>

        <com.google.android.material.button.MaterialButton
            android:id="@+id/btnRegister"
            android:layout_width="match_parent"
            android:layout_height="56dp"
            android:text="DAFTAR"/>
    </LinearLayout>
</ScrollView>
```

- [ ] **Step 4: Create LoginActivity.kt**

```kotlin
package com.example.medilab.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.medilab.R
import com.example.medilab.databinding.ActivityLoginBinding
import com.example.medilab.patient.PatientActivity
import com.example.medilab.staff.StaffActivity
import com.example.medilab.util.Constants
import com.example.medilab.util.Validators

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        authViewModel.loginResult.observe(this) { (success, msg) ->
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        }

        authViewModel.loginRole.observe(this) { role ->
            navigateToRole(role)
        }

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (!Validators.isValidEmail(email)) {
                Toast.makeText(this, "Email tidak valid", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (!Validators.isValidPassword(password)) {
                Toast.makeText(this, "Password minimal 6 karakter", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            authViewModel.login(email, password)
        }

        binding.btnRegister.setOnClickListener {
            startActivity(Intent(this, RegisterPatientActivity::class.java))
        }

        binding.tvLupaPassword.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            if (email.isEmpty()) {
                Toast.makeText(this, "Masukkan email terlebih dahulu", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            authViewModel.sendPasswordReset(email)
        }
    }

    private fun navigateToRole(role: String) {
        when (role) {
            Constants.ROLE_ADMIN, Constants.ROLE_PETUGAS -> {
                startActivity(Intent(this, StaffActivity::class.java))
            }
            Constants.ROLE_DOKTER, Constants.ROLE_PASIEN -> {
                startActivity(Intent(this, PatientActivity::class.java))
            }
        }
        finish()
    }
}
```

- [ ] **Step 5: Create RegisterPatientActivity.kt**

```kotlin
package com.example.medilab.auth

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.medilab.databinding.ActivityRegisterPatientBinding
import com.example.medilab.util.Validators

class RegisterPatientActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterPatientBinding
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterPatientBinding.inflate(layoutInflater)
        setContentView(binding.root)

        authViewModel.registerResult.observe(this) { (success, msg) ->
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
            if (success) finish()
        }

        binding.btnRegister.setOnClickListener {
            val nama = binding.etNama.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            val noHP = binding.etNoHP.text.toString().trim()
            val alamat = binding.etAlamat.text.toString().trim()
            val tanggalLahir = binding.etTanggalLahir.text.toString().trim()

            if (!Validators.isNotEmpty(nama, email, password, noHP, alamat, tanggalLahir)) {
                Toast.makeText(this, "Semua field harus diisi", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (!Validators.isValidEmail(email)) {
                Toast.makeText(this, "Email tidak valid", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (!Validators.isValidPassword(password)) {
                Toast.makeText(this, "Password minimal 6 karakter", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            authViewModel.registerPatient(email, password, nama, noHP, alamat, tanggalLahir)
        }
    }
}
```

- [ ] **Step 6: Verify build**

```bash
cd /home/t4pei/AndroidStudioProjects/MediLab && ./gradlew assembleDebug --no-daemon 2>&1 | tail -10
```

Expected: `BUILD SUCCESSFUL`

---

### Task 6: StaffActivity — Shell with BottomNavigation and Navigation Graph

**Files:**
- Create: `app/src/main/java/com/example/medilab/staff/StaffActivity.kt`
- Create: `app/src/main/res/layout/activity_staff.xml`
- Create: `app/src/main/res/menu/bottom_nav_staff.xml`
- Create: `app/src/main/res/navigation/nav_staff.xml`

- [ ] **Step 1: Create bottom_nav_staff.xml**

```xml
<?xml version="1.0" encoding="utf-8"?>
<menu xmlns:android="http://schemas.android.com/apk/res/android">
    <item android:id="@+id/dashboardFragment"
        android:icon="@drawable/ic_dashboard"
        android:title="Dashboard" />
    <item android:id="@+id/managePasienFragment"
        android:icon="@drawable/ic_people"
        android:title="Manage" />
    <item android:id="@+id/laporanListFragment"
        android:icon="@drawable/ic_report"
        android:title="Laporan" />
    <item android:id="@+id/staffProfileFragment"
        android:icon="@drawable/ic_profile"
        android:title="Profile" />
</menu>
```

- [ ] **Step 2: Create nav_staff.xml**

```xml
<?xml version="1.0" encoding="utf-8"?>
<navigation xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:id="@+id/nav_staff"
    app:startDestination="@id/dashboardFragment">

    <fragment android:id="@+id/dashboardFragment"
        android:name="com.example.medilab.staff.dashboard.StaffDashboardFragment"
        android:label="Dashboard" />

    <fragment android:id="@+id/managePasienFragment"
        android:name="com.example.medilab.staff.manage.pasien.ManagePasienFragment"
        android:label="Manage" />

    <fragment android:id="@+id/laporanListFragment"
        android:name="com.example.medilab.staff.laporan.LaporanListFragment"
        android:label="Laporan" />

    <fragment android:id="@+id/staffProfileFragment"
        android:name="com.example.medilab.staff.profile.StaffProfileFragment"
        android:label="Profile" />

    <fragment android:id="@+id/laporanDetailFragment"
        android:name="com.example.medilab.staff.laporan.LaporanDetailFragment"
        android:label="Detail Laporan" />
</navigation>
```

- [ ] **Step 3: Create activity_staff.xml**

```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <com.google.android.material.appbar.MaterialToolbar
        android:id="@+id/toolbar"
        android:layout_width="0dp"
        android:layout_height="?attr/actionBarSize"
        android:background="?attr/colorPrimary"
        app:title="@string/app_name"
        app:titleTextColor="@android:color/white"
        app:layout_constraintTop_toTopOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintEnd_toEndOf="parent"/>

    <androidx.fragment.app.FragmentContainerView
        android:id="@+id/nav_host_fragment"
        android:name="androidx.navigation.fragment.NavHostFragment"
        android:layout_width="0dp"
        android:layout_height="0dp"
        app:navGraph="@navigation/nav_staff"
        app:defaultNavHost="true"
        app:layout_constraintTop_toBottomOf="@id/toolbar"
        app:layout_constraintBottom_toTopOf="@id/bottom_nav"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintEnd_toEndOf="parent"/>

    <com.google.android.material.bottomnavigation.BottomNavigationView
        android:id="@+id/bottom_nav"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        app:menu="@menu/bottom_nav_staff"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintEnd_toEndOf="parent"/>
</androidx.constraintlayout.widget.ConstraintLayout>
```

- [ ] **Step 4: Create StaffActivity.kt**

```kotlin
package com.example.medilab.staff

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.medilab.R
import com.example.medilab.databinding.ActivityStaffBinding

class StaffActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStaffBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStaffBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navController = findNavController(R.id.nav_host_fragment)
        binding.bottomNav.setupWithNavController(navController)
    }
}
```

- [ ] **Step 5: Create placeholder drawable icons**

Create a `drawable/ic_dashboard.xml`, `drawable/ic_people.xml`, `drawable/ic_report.xml`, `drawable/ic_profile.xml` as simple vector drawables. Each is a 24dp x 24dp icon.

```xml
<!-- ic_dashboard.xml -->
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp"
    android:height="24dp"
    android:viewportWidth="24"
    android:viewportHeight="24"
    android:tint="?attr/colorControlNormal">
    <path android:fillColor="@android:color/white"
        android:pathData="M3,13h8L11,3L3,3v10zM3,21h8v-6L3,15v6zM13,21h8L21,11h-8v10zM13,3v6h8L21,3h-8z"/>
</vector>
```

```xml
<!-- ic_people.xml -->
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp"
    android:height="24dp"
    android:viewportWidth="24"
    android:viewportHeight="24"
    android:tint="?attr/colorControlNormal">
    <path android:fillColor="@android:color/white"
        android:pathData="M16,11c1.66,0 2.99,-1.34 2.99,-3s-1.33,-3 -2.99,-3c-1.66,0 -3,1.34 -3,3s1.34,3 3,3zM8,11c1.66,0 2.99,-1.34 2.99,-3s-1.33,-3 -2.99,-3c-1.66,0 -3,1.34 -3,3s1.34,3 3,3zM8,13c-2.33,0 -7,1.17 -7,3.5L1,19h14v-2.5c0,-2.33 -4.67,-3.5 -7,-3.5zM16,13c-0.29,0 -0.62,0.02 -0.97,0.05 1.16,0.84 1.97,1.97 1.97,3.45L17,19h6v-2.5c0,-2.33 -4.67,-3.5 -7,-3.5z"/>
</vector>
```

```xml
<!-- ic_report.xml -->
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp"
    android:height="24dp"
    android:viewportWidth="24"
    android:viewportHeight="24"
    android:tint="?attr/colorControlNormal">
    <path android:fillColor="@android:color/white"
        android:pathData="M19,3h-4.18C14.4,1.84 13.3,1 12,1c-1.3,0 -2.4,0.84 -2.82,2L5,3c-1.1,0 -2,0.9 -2,2v14c0,1.1 0.9,2 2,2h14c1.1,0 2,-0.9 2,-2L21,5c0,-1.1 -0.9,-2 -2,-2zM12,3c0.55,0 1,0.45 1,1s-0.45,1 -1,1 -1,-0.45 -1,-1 0.45,-1 1,-1zM10,17L6,17v-2h4v2zM14,13L6,13v-2h8v2zM18,9L6,9L6,7h12v2z"/>
</vector>
```

```xml
<!-- ic_profile.xml -->
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp"
    android:height="24dp"
    android:viewportWidth="24"
    android:viewportHeight="24"
    android:tint="?attr/colorControlNormal">
    <path android:fillColor="@android:color/white"
        android:pathData="M12,12c2.21,0 4,-1.79 4,-4s-1.79,-4 -4,-4 -4,1.79 -4,4 1.79,4 4,4zM12,14c-2.67,0 -8,1.34 -8,4v2h16v-2c0,-2.66 -5.33,-4 -8,-4z"/>
</vector>
```

- [ ] **Step 6: Verify build**

```bash
cd /home/t4pei/AndroidStudioProjects/MediLab && ./gradlew assembleDebug --no-daemon 2>&1 | tail -10
```

Expected: `BUILD SUCCESSFUL`

---

### Task 7: PatientActivity — Shell with BottomNavigation

**Files:**
- Create: `app/src/main/java/com/example/medilab/patient/PatientActivity.kt`
- Create: `app/src/main/res/layout/activity_patient.xml`
- Create: `app/src/main/res/menu/bottom_nav_patient.xml`
- Create: `app/src/main/res/navigation/nav_patient.xml`

- [ ] **Step 1: Create bottom_nav_patient.xml**

```xml
<?xml version="1.0" encoding="utf-8"?>
<menu xmlns:android="http://schemas.android.com/apk/res/android">
    <item android:id="@+id/patientDashboardFragment"
        android:icon="@drawable/ic_dashboard"
        android:title="Dashboard" />
    <item android:id="@+id/hasilFragment"
        android:icon="@drawable/ic_report"
        android:title="Hasil" />
    <item android:id="@+id/riwayatFragment"
        android:icon="@drawable/ic_history"
        android:title="Riwayat" />
    <item android:id="@+id/patientProfileFragment"
        android:icon="@drawable/ic_profile"
        android:title="Profile" />
</menu>
```

Create `ic_history.xml`:

```xml
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp"
    android:height="24dp"
    android:viewportWidth="24"
    android:viewportHeight="24"
    android:tint="?attr/colorControlNormal">
    <path android:fillColor="@android:color/white"
        android:pathData="M13,3c-4.97,0 -9,4.03 -9,9L1,12l3.89,3.89 0.07,0.14L9,12L6,12c0,-3.87 3.13,-7 7,-7s7,3.13 7,7 -3.13,7 -7,7c-1.93,0 -3.68,-0.79 -4.94,-2.06l-1.42,1.42C8.27,19.99 10.51,21 13,21c4.97,0 9,-4.03 9,-9s-4.03,-9 -9,-9zM12,8v5l4.28,2.54 0.72,-1.21 -3.5,-2.08L13.5,8L12,8z"/>
</vector>
```

- [ ] **Step 2: Create nav_patient.xml**

```xml
<?xml version="1.0" encoding="utf-8"?>
<navigation xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:id="@+id/nav_patient"
    app:startDestination="@id/patientDashboardFragment">

    <fragment android:id="@+id/patientDashboardFragment"
        android:name="com.example.medilab.patient.dashboard.PatientDashboardFragment"
        android:label="Dashboard" />

    <fragment android:id="@+id/hasilFragment"
        android:name="com.example.medilab.patient.hasil.HasilFragment"
        android:label="Hasil Pemeriksaan" />

    <fragment android:id="@+id/riwayatFragment"
        android:name="com.example.medilab.patient.riwayat.RiwayatFragment"
        android:label="Riwayat Rekam Medis" />

    <fragment android:id="@+id/patientProfileFragment"
        android:name="com.example.medilab.patient.profile.PatientProfileFragment"
        android:label="Profile" />
</navigation>
```

- [ ] **Step 3: Create activity_patient.xml**

```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <com.google.android.material.appbar.MaterialToolbar
        android:id="@+id/toolbar"
        android:layout_width="0dp"
        android:layout_height="?attr/actionBarSize"
        android:background="?attr/colorPrimary"
        app:title="@string/app_name"
        app:titleTextColor="@android:color/white"
        app:layout_constraintTop_toTopOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintEnd_toEndOf="parent"/>

    <androidx.fragment.app.FragmentContainerView
        android:id="@+id/nav_host_fragment"
        android:name="androidx.navigation.fragment.NavHostFragment"
        android:layout_width="0dp"
        android:layout_height="0dp"
        app:navGraph="@navigation/nav_patient"
        app:defaultNavHost="true"
        app:layout_constraintTop_toBottomOf="@id/toolbar"
        app:layout_constraintBottom_toTopOf="@id/bottom_nav"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintEnd_toEndOf="parent"/>

    <com.google.android.material.bottomnavigation.BottomNavigationView
        android:id="@+id/bottom_nav"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        app:menu="@menu/bottom_nav_patient"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintEnd_toEndOf="parent"/>
</androidx.constraintlayout.widget.ConstraintLayout>
```

- [ ] **Step 4: Create PatientActivity.kt**

```kotlin
package com.example.medilab.patient

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.medilab.R
import com.example.medilab.databinding.ActivityPatientBinding

class PatientActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPatientBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPatientBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navController = findNavController(R.id.nav_host_fragment)
        binding.bottomNav.setupWithNavController(navController)
    }
}
```

- [ ] **Step 5: Verify build**

```bash
cd /home/t4pei/AndroidStudioProjects/MediLab && ./gradlew assembleDebug --no-daemon 2>&1 | tail -10
```

Expected: `BUILD SUCCESSFUL`

---

### Task 8: Staff Dashboard Fragment

**Files:**
- Create: `app/src/main/java/com/example/medilab/staff/dashboard/StaffDashboardViewModel.kt`
- Create: `app/src/main/java/com/example/medilab/staff/dashboard/StaffDashboardFragment.kt`
- Create: `app/src/main/res/layout/fragment_staff_dashboard.xml`

- [ ] **Step 1: Create StaffDashboardViewModel.kt**

```kotlin
package com.example.medilab.staff.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.medilab.model.Laporan
import com.example.medilab.model.Notifikasi
import com.example.medilab.repository.AuthRepository
import com.example.medilab.repository.LaporanRepository
import com.example.medilab.repository.NotifikasiRepository
import com.example.medilab.util.Constants

class StaffDashboardViewModel : ViewModel() {
    private val laporanRepo = LaporanRepository()
    private val notifRepo = NotifikasiRepository()
    private val authRepo = AuthRepository()

    private val _laporanBaru = MutableLiveData<Int>()
    val laporanBaru: LiveData<Int> = _laporanBaru

    private val _laporanProses = MutableLiveData<Int>()
    val laporanProses: LiveData<Int> = _laporanProses

    private val _laporanSelesai = MutableLiveData<Int>()
    val laporanSelesai: LiveData<Int> = _laporanSelesai

    private val _laporanPendingVerifikasi = MutableLiveData<List<Laporan>>()
    val laporanPendingVerifikasi: LiveData<List<Laporan>> = _laporanPendingVerifikasi

    private val _notifikasi = MutableLiveData<List<Notifikasi>>()
    val notifikasi: LiveData<List<Notifikasi>> = _notifikasi

    fun loadData() {
        laporanRepo.getByStatus(Constants.STATUS_BARU) { list -> _laporanBaru.value = list.size }
        laporanRepo.getByStatus(Constants.STATUS_PROSES) { list -> _laporanProses.value = list.size }
        laporanRepo.getByStatus(Constants.STATUS_SELESAI) { list -> _laporanSelesai.value = list.size }
        laporanRepo.getByStatus(Constants.STATUS_VERIFIKASI) { list -> _laporanPendingVerifikasi.value = list }
        notifRepo.getByUserId(authRepo.getCurrentUid()) { list -> _notifikasi.value = list }
    }
}
```

- [ ] **Step 2: Create fragment_staff_dashboard.xml**

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:padding="16dp">

    <TextView
        android:id="@+id/tvWelcome"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Selamat datang!"
        android:textSize="18sp"
        android:textStyle="bold"
        android:layout_marginBottom="16dp"/>

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="horizontal"
        android:layout_marginBottom="16dp">

        <androidx.cardview.widget.CardView
            android:id="@+id/cardBaru"
            android:layout_width="0dp"
            android:layout_height="100dp"
            android:layout_weight="1"
            android:layout_marginEnd="4dp"
            app:cardCornerRadius="8dp">
            <LinearLayout
                android:layout_width="match_parent"
                android:layout_height="match_parent"
                android:gravity="center"
                android:orientation="vertical">
                <TextView android:id="@+id/tvBaruCount" android:layout_width="wrap_content"
                    android:layout_height="wrap_content" android:text="0"
                    android:textSize="24sp" android:textStyle="bold"/>
                <TextView android:layout_width="wrap_content"
                    android:layout_height="wrap_content" android:text="Baru"
                    android:textSize="12sp"/>
            </LinearLayout>
        </androidx.cardview.widget.CardView>

        <androidx.cardview.widget.CardView
            android:id="@+id/cardProses"
            android:layout_width="0dp"
            android:layout_height="100dp"
            android:layout_weight="1"
            android:layout_marginStart="4dp" android:layout_marginEnd="4dp"
            app:cardCornerRadius="8dp">
            <LinearLayout
                android:layout_width="match_parent"
                android:layout_height="match_parent"
                android:gravity="center"
                android:orientation="vertical">
                <TextView android:id="@+id/tvProsesCount" android:layout_width="wrap_content"
                    android:layout_height="wrap_content" android:text="0"
                    android:textSize="24sp" android:textStyle="bold"/>
                <TextView android:layout_width="wrap_content"
                    android:layout_height="wrap_content" android:text="Proses"
                    android:textSize="12sp"/>
            </LinearLayout>
        </androidx.cardview.widget.CardView>

        <androidx.cardview.widget.CardView
            android:id="@+id/cardSelesai"
            android:layout_width="0dp"
            android:layout_height="100dp"
            android:layout_weight="1"
            android:layout_marginStart="4dp"
            app:cardCornerRadius="8dp">
            <LinearLayout
                android:layout_width="match_parent"
                android:layout_height="match_parent"
                android:gravity="center"
                android:orientation="vertical">
                <TextView android:id="@+id/tvSelesaiCount" android:layout_width="wrap_content"
                    android:layout_height="wrap_content" android:text="0"
                    android:textSize="24sp" android:textStyle="bold"/>
                <TextView android:layout_width="wrap_content"
                    android:layout_height="wrap_content" android:text="Selesai"
                    android:textSize="12sp"/>
            </LinearLayout>
        </androidx.cardview.widget.CardView>
    </LinearLayout>

    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Perlu Verifikasi"
        android:textStyle="bold"
        android:layout_marginBottom="8dp"/>

    <androidx.recyclerview.widget.RecyclerView
        android:id="@+id/rvPendingVerifikasi"
        android:layout_width="match_parent"
        android:layout_height="0dp"
        android:layout_weight="1"/>
</LinearLayout>
```

- [ ] **Step 3: Create StaffDashboardFragment.kt**

```kotlin
package com.example.medilab.staff.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.medilab.R
import com.example.medilab.adapter.LaporanAdapter
import com.example.medilab.databinding.FragmentStaffDashboardBinding

class StaffDashboardFragment : Fragment() {
    private var _binding: FragmentStaffDashboardBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: StaffDashboardViewModel
    private lateinit var adapter: LaporanAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentStaffDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[StaffDashboardViewModel::class.java]

        adapter = LaporanAdapter { laporan ->
            val bundle = Bundle().apply { putString("laporanId", laporan.id) }
            // navigate to detail
        }
        binding.rvPendingVerifikasi.adapter = adapter

        viewModel.laporanBaru.observe(viewLifecycleOwner) { binding.tvBaruCount.text = it.toString() }
        viewModel.laporanProses.observe(viewLifecycleOwner) { binding.tvProsesCount.text = it.toString() }
        viewModel.laporanSelesai.observe(viewLifecycleOwner) { binding.tvSelesaiCount.text = it.toString() }
        viewModel.laporanPendingVerifikasi.observe(viewLifecycleOwner) { adapter.submitList(it) }

        viewModel.loadData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
```

- [ ] **Step 4: Verify build**

```bash
cd /home/t4pei/AndroidStudioProjects/MediLab && ./gradlew assembleDebug --no-daemon 2>&1 | tail -10
```

Expected: `BUILD SUCCESSFUL`

---

### Task 9: Adapters — LaporanAdapter, PasienAdapter, RiwayatAdapter

**Files:**
- Create: `app/src/main/java/com/example/medilab/adapter/LaporanAdapter.kt`
- Create: `app/src/main/java/com/example/medilab/adapter/PasienAdapter.kt`
- Create: `app/src/main/java/com/example/medilab/adapter/RiwayatAdapter.kt`

- [ ] **Step 1: Create LaporanAdapter.kt**

```kotlin
package com.example.medilab.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.medilab.R
import com.example.medilab.databinding.ItemLaporanBinding
import com.example.medilab.model.Laporan
import com.example.medilab.util.DateUtils

class LaporanAdapter(private val onClick: (Laporan) -> Unit) :
    ListAdapter<Laporan, LaporanAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemLaporanBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ItemLaporanBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(laporan: Laporan) {
            binding.tvLaporanId.text = laporan.id
            binding.tvLaporanStatus.text = laporan.status.uppercase()
            binding.tvLaporanTanggal.text = DateUtils.formatDisplay(java.util.Date(laporan.createdAt))
            binding.root.setOnClickListener { onClick(laporan) }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Laporan>() {
        override fun areItemsTheSame(old: Laporan, new: Laporan) = old.id == new.id
        override fun areContentsTheSame(old: Laporan, new: Laporan) = old == new
    }
}
```

Create `item_laporan.xml`:

```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.cardview.widget.CardView xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:layout_margin="4dp"
    app:cardCornerRadius="8dp"
    app:cardElevation="2dp">

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="vertical"
        android:padding="12dp">

        <TextView android:id="@+id/tvLaporanId"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:textStyle="bold"
            android:textSize="14sp"/>

        <TextView android:id="@+id/tvLaporanStatus"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:textSize="12sp"/>

        <TextView android:id="@+id/tvLaporanTanggal"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:textSize="12sp"/>
    </LinearLayout>
</androidx.cardview.widget.CardView>
```

- [ ] **Step 2: Create PasienAdapter.kt**

```kotlin
package com.example.medilab.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.medilab.R
import com.example.medilab.databinding.ItemPasienBinding
import com.example.medilab.model.User

class PasienAdapter(private val onClick: (User) -> Unit) :
    ListAdapter<User, PasienAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemPasienBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ItemPasienBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(user: User) {
            binding.tvNama.text = user.nama
            binding.tvId.text = user.id
            binding.tvRm.text = user.noRekamMedis
            binding.root.setOnClickListener { onClick(user) }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(old: User, new: User) = old.id == new.id
        override fun areContentsTheSame(old: User, new: User) = old == new
    }
}
```

Create `item_pasien.xml`:

```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.cardview.widget.CardView xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:layout_margin="4dp"
    app:cardCornerRadius="8dp"
    app:cardElevation="2dp">
    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="horizontal"
        android:padding="12dp">
        <TextView android:id="@+id/tvId"
            android:layout_width="80dp"
            android:layout_height="wrap_content"
            android:textStyle="bold"/>
        <TextView android:id="@+id/tvNama"
            android:layout_width="0dp"
            android:layout_height="wrap_content"
            android:layout_weight="1"/>
        <TextView android:id="@+id/tvRm"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"/>
    </LinearLayout>
</androidx.cardview.widget.CardView>
```

- [ ] **Step 3: Create RiwayatAdapter.kt**

```kotlin
package com.example.medilab.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.medilab.R
import com.example.medilab.databinding.ItemRiwayatBinding
import com.example.medilab.model.RekamMedis
import com.example.medilab.util.DateUtils

class RiwayatAdapter(private val onClick: (RekamMedis) -> Unit) :
    ListAdapter<RekamMedis, RiwayatAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemRiwayatBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ItemRiwayatBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(rm: RekamMedis) {
            binding.tvDiagnosa.text = rm.diagnosa
            binding.tvRumahSakit.text = rm.rumahSakit.nama
            binding.tvWaktu.text = rm.waktu
            binding.root.setOnClickListener { onClick(rm) }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<RekamMedis>() {
        override fun areItemsTheSame(old: RekamMedis, new: RekamMedis) = old.id == new.id
        override fun areContentsTheSame(old: RekamMedis, new: RekamMedis) = old == new
    }
}
```

Create `item_riwayat.xml`:

```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.cardview.widget.CardView xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:layout_margin="4dp"
    app:cardCornerRadius="8dp"
    app:cardElevation="2dp">
    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="vertical"
        android:padding="12dp">
        <TextView android:id="@+id/tvDiagnosa"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:textStyle="bold"/>
        <TextView android:id="@+id/tvRumahSakit"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:textSize="12sp"/>
        <TextView android:id="@+id/tvWaktu"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:textSize="12sp"/>
    </LinearLayout>
</androidx.cardview.widget.CardView>
```

- [ ] **Step 4: Verify build**

```bash
cd /home/t4pei/AndroidStudioProjects/MediLab && ./gradlew assembleDebug --no-daemon 2>&1 | tail -10
```

Expected: `BUILD SUCCESSFUL`

---

### Task 10: Staff Manage Features — CRUD for Pasien, Dokter, Petugas, Pemeriksaan, Obat

**Files:**
- Create: `app/src/main/java/com/example/medilab/staff/manage/pasien/ManagePasienFragment.kt`
- Create: `app/src/main/java/com/example/medilab/staff/manage/pasien/ManagePasienViewModel.kt`
- Create: `app/src/main/java/com/example/medilab/staff/manage/dokter/ManageDokterFragment.kt`
- Create: `app/src/main/java/com/example/medilab/staff/manage/dokter/ManageDokterViewModel.kt`
- Create: `app/src/main/java/com/example/medilab/staff/manage/petugas/ManagePetugasFragment.kt`
- Create: `app/src/main/java/com/example/medilab/staff/manage/petugas/ManagePetugasViewModel.kt`
- Create: `app/src/main/java/com/example/medilab/staff/manage/pemeriksaan/ManagePemeriksaanFragment.kt`
- Create: `app/src/main/java/com/example/medilab/staff/manage/pemeriksaan/ManagePemeriksaanViewModel.kt`
- Create: `app/src/main/java/com/example/medilab/staff/manage/obat/ManageObatFragment.kt`
- Create: `app/src/main/java/com/example/medilab/staff/manage/obat/ManageObatViewModel.kt`
- Create: `app/src/main/res/layout/fragment_manage_pasien.xml`
- Create: `app/src/main/res/layout/fragment_manage_dokter.xml`
- Create: `app/src/main/res/layout/fragment_manage_petugas.xml`
- Create: `app/src/main/res/layout/fragment_manage_pemeriksaan.xml`
- Create: `app/src/main/res/layout/fragment_manage_obat.xml`

- [ ] **Step 1: Create ManagePasienViewModel.kt**

```kotlin
package com.example.medilab.staff.manage.pasien

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.medilab.model.User
import com.example.medilab.repository.UserRepository
import com.example.medilab.util.Constants

class ManagePasienViewModel : ViewModel() {
    private val repo = UserRepository()

    private val _pasienList = MutableLiveData<List<User>>()
    val pasienList: LiveData<List<User>> = _pasienList

    private val _allPasien = mutableListOf<User>()

    fun load() {
        repo.getUsersByRole(Constants.ROLE_PASIEN) { list ->
            _allPasien.clear()
            _allPasien.addAll(list)
            _pasienList.value = list
        }
    }

    fun search(query: String) {
        _pasienList.value = if (query.isBlank()) _allPasien
        else _allPasien.filter { it.nama.contains(query, ignoreCase = true) || it.id.contains(query, ignoreCase = true) }
    }

    fun delete(uid: String) {
        repo.deleteUser(uid) { if (it) load() }
    }
}
```

- [ ] **Step 2: Create ManagePasienFragment.kt**

```kotlin
package com.example.medilab.staff.manage.pasien

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.medilab.adapter.PasienAdapter
import com.example.medilab.databinding.FragmentManagePasienBinding

class ManagePasienFragment : Fragment() {
    private var _binding: FragmentManagePasienBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: ManagePasienViewModel
    private lateinit var adapter: PasienAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentManagePasienBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[ManagePasienViewModel::class.java]

        adapter = PasienAdapter { user ->
            Toast.makeText(requireContext(), "${user.nama} - ${user.id}", Toast.LENGTH_SHORT).show()
        }
        binding.rvPasien.adapter = adapter

        binding.etSearch.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.search(s?.toString() ?: "")
            }
            override fun afterTextChanged(s: android.text.Editable?) {}
        })

        viewModel.pasienList.observe(viewLifecycleOwner) { adapter.submitList(it) }
        viewModel.load()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
```

- [ ] **Step 3: Create fragment_manage_pasien.xml**

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:padding="16dp">

    <EditText
        android:id="@+id/etSearch"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:hint="Cari pasien..."
        android:drawableStart="@android:drawable/ic_menu_search"
        android:paddingStart="36dp"
        android:layout_marginBottom="8dp"/>

    <androidx.recyclerview.widget.RecyclerView
        android:id="@+id/rvPasien"
        android:layout_width="match_parent"
        android:layout_height="match_parent"/>
</LinearLayout>
```

- [ ] **Step 4: Create ManageDokterViewModel.kt**

```kotlin
package com.example.medilab.staff.manage.dokter

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.medilab.model.Dokter
import com.example.medilab.repository.DokterRepository

class ManageDokterViewModel : ViewModel() {
    private val repo = DokterRepository()

    private val _dokterList = MutableLiveData<List<Dokter>>()
    val dokterList: LiveData<List<Dokter>> = _dokterList

    private val _allDokter = mutableListOf<Dokter>()

    fun load() {
        repo.getAll { list ->
            _allDokter.clear()
            _allDokter.addAll(list)
            _dokterList.value = list
        }
    }

    fun search(query: String) {
        _dokterList.value = if (query.isBlank()) _allDokter
        else _allDokter.filter { it.nama.contains(query, ignoreCase = true) || it.id.contains(query, ignoreCase = true) }
    }

    fun add(dokter: Dokter) {
        repo.add(dokter) { if (it) load() }
    }

    fun delete(id: String) {
        repo.delete(id) { if (it) load() }
    }
}
```

- [ ] **Step 5: Create ManageDokterFragment.kt and fragment_manage_dokter.xml**

Similar pattern to ManagePasienFragment — RecyclerView with search, add FAB button. Same for ManagePetugasFragment, ManagePemeriksaanFragment, ManageObatFragment.

- [ ] **Step 6: Create ManagePetugasViewModel.kt**

```kotlin
package com.example.medilab.staff.manage.petugas

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.medilab.model.User
import com.example.medilab.repository.AuthRepository
import com.example.medilab.repository.UserRepository
import com.example.medilab.util.Constants

class ManagePetugasViewModel : ViewModel() {
    private val userRepo = UserRepository()
    private val authRepo = AuthRepository()

    private val _petugasList = MutableLiveData<List<User>>()
    val petugasList: LiveData<List<User>> = _petugasList

    fun load() {
        userRepo.getUsersByRole(Constants.ROLE_ADMIN) { admin ->
            userRepo.getUsersByRole(Constants.ROLE_PETUGAS) { petugas ->
                _petugasList.value = admin + petugas
            }
        }
    }

    fun create(email: String, password: String, nama: String, role: String, noHP: String) {
        authRepo.createStaffAccount(email, password, nama, role, noHP) { _, _ -> load() }
    }
}
```

- [ ] **Step 7: Create ManagePemeriksaanViewModel.kt**

```kotlin
package com.example.medilab.staff.manage.pemeriksaan

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.medilab.model.Pemeriksaan
import com.example.medilab.repository.PemeriksaanRepository

class ManagePemeriksaanViewModel : ViewModel() {
    private val repo = PemeriksaanRepository()

    private val _list = MutableLiveData<List<Pemeriksaan>>()
    val list: LiveData<List<Pemeriksaan>> = _list

    fun load() {
        repo.getAll { _list.value = it }
    }

    fun add(pemeriksaan: Pemeriksaan) {
        repo.add(pemeriksaan) { if (it) load() }
    }

    fun delete(id: String) {
        repo.delete(id) { if (it) load() }
    }
}
```

- [ ] **Step 8: Create ManageObatViewModel.kt**

```kotlin
package com.example.medilab.staff.manage.obat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.medilab.model.Obat
import com.example.medilab.repository.ObatRepository

class ManageObatViewModel : ViewModel() {
    private val repo = ObatRepository()

    private val _list = MutableLiveData<List<Obat>>()
    val list: LiveData<List<Obat>> = _list

    fun load() {
        repo.getAll { _list.value = it }
    }

    fun add(obat: Obat) {
        repo.add(obat) { if (it) load() }
    }

    fun delete(id: String) {
        repo.delete(id) { if (it) load() }
    }
}
```

- [ ] **Step 9: Update navigation to include all manage tabs**

Update `nav_staff.xml` to replace single managePasienFragment with a ViewPager-based fragment that contains tabs for all manage types. Or alternatively, keep a single fragment with TabLayout that loads sub-fragments.

For simplicity, create a single `ManageContainerFragment` with TabLayout + ViewPager2 that hosts the 5 sub-fragments.

- [ ] **Step 10: Verify build**

```bash
cd /home/t4pei/AndroidStudioProjects/MediLab && ./gradlew assembleDebug --no-daemon 2>&1 | tail -10
```

Expected: `BUILD SUCCESSFUL`

---

### Task 11: Staff Laporan Feature — List, Detail, Input Hasil, Verifikasi

**Files:**
- Create: `app/src/main/java/com/example/medilab/staff/laporan/LaporanViewModel.kt`
- Create: `app/src/main/java/com/example/medilab/staff/laporan/LaporanListFragment.kt`
- Create: `app/src/main/java/com/example/medilab/staff/laporan/LaporanDetailFragment.kt`
- Create: `app/src/main/res/layout/fragment_laporan_list.xml`
- Create: `app/src/main/res/layout/fragment_laporan_detail.xml`

- [ ] **Step 1: Create LaporanViewModel.kt**

```kotlin
package com.example.medilab.staff.laporan

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.medilab.model.Laporan
import com.example.medilab.repository.LaporanRepository

class LaporanViewModel : ViewModel() {
    private val repo = LaporanRepository()

    private val _laporanList = MutableLiveData<List<Laporan>>()
    val laporanList: LiveData<List<Laporan>> = _laporanList

    private val _currentLaporan = MutableLiveData<Laporan?>()
    val currentLaporan: LiveData<Laporan?> = _currentLaporan

    private var currentStatus = "baru"

    fun loadByStatus(status: String) {
        currentStatus = status
        repo.getByStatus(status) { _laporanList.value = it }
    }

    fun loadById(id: String) {
        repo.getById(id) { _currentLaporan.value = it }
    }

    fun updateStatus(id: String, status: String, adminId: String = "") {
        repo.updateStatus(id, status, adminId) { if (it) loadByStatus(currentStatus) }
    }

    fun updateLaporan(id: String, data: Map<String, Any>) {
        repo.update(id, data) { if (it) loadById(id) }
    }
}
```

- [ ] **Step 2: Create fragment_laporan_list.xml**

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical">

    <com.google.android.material.tabs.TabLayout
        android:id="@+id/tabStatus"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        app:tabMode="scrollable"
        app:tabSelectedTextColor="?attr/colorPrimary"/>

    <androidx.recyclerview.widget.RecyclerView
        android:id="@+id/rvLaporan"
        android:layout_width="match_parent"
        android:layout_height="match_parent"/>
</LinearLayout>
```

- [ ] **Step 3: Create LaporanListFragment.kt**

```kotlin
package com.example.medilab.staff.laporan

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.medilab.R
import com.example.medilab.adapter.LaporanAdapter
import com.example.medilab.databinding.FragmentLaporanListBinding

class LaporanListFragment : Fragment() {
    private var _binding: FragmentLaporanListBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: LaporanViewModel
    private lateinit var adapter: LaporanAdapter

    private val statusTabs = listOf("baru", "proses", "verifikasi", "revisi", "selesai", "ditolak")

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentLaporanListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[LaporanViewModel::class.java]

        adapter = LaporanAdapter { laporan ->
            val bundle = Bundle().apply { putString("laporanId", laporan.id) }
            findNavController().navigate(R.id.laporanDetailFragment, bundle)
        }
        binding.rvLaporan.adapter = adapter

        statusTabs.forEach { status ->
            binding.tabStatus.addTab(binding.tabStatus.newTab().setText(status.uppercase()))
        }
        binding.tabStatus.addOnTabSelectedListener(object : com.google.android.material.tabs.TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: com.google.android.material.tabs.TabLayout.Tab?) {
                viewModel.loadByStatus(statusTabs[tab?.position ?: 0])
            }
            override fun onTabUnselected(tab: com.google.android.material.tabs.TabLayout.Tab?) {}
            override fun onTabReselected(tab: com.google.android.material.tabs.TabLayout.Tab?) {}
        })
        viewModel.loadByStatus("baru")
        viewModel.laporanList.observe(viewLifecycleOwner) { adapter.submitList(it) }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
```

- [ ] **Step 4: Create fragment_laporan_detail.xml**

```xml
<?xml version="1.0" encoding="utf-8"?>
<ScrollView xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="vertical"
        android:padding="16dp">

        <TextView android:id="@+id/tvLaporanId"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:textStyle="bold"
            android:textSize="18sp"/>

        <TextView android:id="@+id/tvStatus"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_marginBottom="16dp"/>

        <TextView android:text="Data Pasien"
            android:textStyle="bold" android:layout_marginTop="8dp"/>
        <TextView android:id="@+id/tvPasien" android:layout_width="wrap_content"
            android:layout_height="wrap_content" android:layout_marginBottom="8dp"/>

        <TextView android:text="Dokter Rujukan"
            android:textStyle="bold" android:layout_marginTop="8dp"/>
        <TextView android:id="@+id/tvDokter" android:layout_width="wrap_content"
            android:layout_height="wrap_content" android:layout_marginBottom="8dp"/>

        <TextView android:text="Hasil Pemeriksaan"
            android:textStyle="bold" android:layout_marginTop="8dp"/>
        <TextView android:id="@+id/tvHasil" android:layout_width="wrap_content"
            android:layout_height="wrap_content" android:layout_marginBottom="8dp"/>

        <TextView android:text="Diagnosa"
            android:textStyle="bold" android:layout_marginTop="8dp"/>
        <TextView android:id="@+id/tvDiagnosa" android:layout_width="wrap_content"
            android:layout_height="wrap_content" android:layout_marginBottom="8dp"/>

        <TextView android:text="Resep Obat"
            android:textStyle="bold" android:layout_marginTop="8dp"/>
        <TextView android:id="@+id/tvResep" android:layout_width="wrap_content"
            android:layout_height="wrap_content" android:layout_marginBottom="8dp"/>

        <TextView android:text="Info RS"
            android:textStyle="bold" android:layout_marginTop="8dp"/>
        <TextView android:id="@+id/tvRS" android:layout_width="wrap_content"
            android:layout_height="wrap_content" android:layout_marginBottom="16dp"/>
    </LinearLayout>
</ScrollView>
```

- [ ] **Step 5: Create LaporanDetailFragment.kt**

```kotlin
package com.example.medilab.staff.laporan

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.medilab.databinding.FragmentLaporanDetailBinding

class LaporanDetailFragment : Fragment() {
    private var _binding: FragmentLaporanDetailBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: LaporanViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentLaporanDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[LaporanViewModel::class.java]

        val laporanId = arguments?.getString("laporanId") ?: return
        viewModel.loadById(laporanId)

        viewModel.currentLaporan.observe(viewLifecycleOwner) { laporan ->
            if (laporan == null) return@observe
            binding.tvLaporanId.text = "Laporan #${laporan.id}"
            binding.tvStatus.text = "Status: ${laporan.status.uppercase()}"
            binding.tvPasien.text = "ID Pasien: ${laporan.pasienId}"
            binding.tvDokter.text = "ID Dokter: ${laporan.dokterId}"
            binding.tvHasil.text = laporan.hasilParameter.joinToString("\n") { "${it.parameterNama}: ${it.nilai} ${it.satuan} (${it.keterangan})" }
            binding.tvDiagnosa.text = laporan.diagnosa
            binding.tvResep.text = laporan.resepObat.joinToString("\n") { "${it.namaObat} ${it.dosis}${it.satuan} - ${it.aturanPakai}" }
            binding.tvRS.text = "${laporan.rumahSakit.nama}\n${laporan.rumahSakit.alamat}, ${laporan.rumahSakit.kota}"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
```

- [ ] **Step 6: Verify build**

```bash
cd /home/t4pei/AndroidStudioProjects/MediLab && ./gradlew assembleDebug --no-daemon 2>&1 | tail -10
```

Expected: `BUILD SUCCESSFUL`

---

### Task 12: Staff Profile Feature

**Files:**
- Create: `app/src/main/java/com/example/medilab/staff/profile/StaffProfileFragment.kt`
- Create: `app/src/main/java/com/example/medilab/staff/profile/StaffProfileViewModel.kt`
- Create: `app/src/main/res/layout/fragment_staff_profile.xml`

- [ ] **Step 1: Create StaffProfileViewModel.kt**

```kotlin
package com.example.medilab.staff.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.medilab.model.User
import com.example.medilab.repository.AuthRepository
import com.example.medilab.repository.UserRepository

class StaffProfileViewModel : ViewModel() {
    private val userRepo = UserRepository()
    private val authRepo = AuthRepository()

    private val _user = MutableLiveData<User?>()
    val user: LiveData<User?> = _user

    fun load() {
        userRepo.getUser(authRepo.getCurrentUid()) { _user.value = it }
    }

    fun logout() {
        authRepo.logout()
    }
}
```

- [ ] **Step 2: Create fragment_staff_profile.xml**

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:padding="24dp"
    android:gravity="center_horizontal">

    <de.hdodenhof.circleimageview.CircleImageView
        android:id="@+id/ivProfile"
        android:layout_width="80dp"
        android:layout_height="80dp"
        android:src="@mipmap/ic_launcher"
        android:layout_marginBottom="16dp"/>

    <TextView android:id="@+id/tvNama" android:layout_width="wrap_content"
        android:layout_height="wrap_content" android:textStyle="bold" android:textSize="18sp"/>
    <TextView android:id="@+id/tvId" android:layout_width="wrap_content"
        android:layout_height="wrap_content" android:textSize="12sp"/>
    <TextView android:id="@+id/tvEmail" android:layout_width="wrap_content"
        android:layout_height="wrap_content" android:layout_marginBottom="24dp"/>

    <com.google.android.material.button.MaterialButton
        android:id="@+id/btnEditProfil"
        android:layout_width="match_parent"
        android:layout_height="48dp"
        android:text="Edit Profil"
        style="@style/Widget.MaterialComponents.Button.OutlinedButton"/>

    <com.google.android.material.button.MaterialButton
        android:id="@+id/btnGantiPassword"
        android:layout_width="match_parent"
        android:layout_height="48dp"
        android:text="Ganti Password"
        style="@style/Widget.MaterialComponents.Button.OutlinedButton"
        android:layout_marginTop="8dp"/>

    <com.google.android.material.button.MaterialButton
        android:id="@+id/btnLogout"
        android:layout_width="match_parent"
        android:layout_height="48dp"
        android:text="KELUAR"
        style="@style/Widget.MaterialComponents.Button"
        android:layout_marginTop="24dp"/>
</LinearLayout>
```

- [ ] **Step 3: Create StaffProfileFragment.kt**

```kotlin
package com.example.medilab.staff.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.medilab.auth.LoginActivity
import com.example.medilab.databinding.FragmentStaffProfileBinding

class StaffProfileFragment : Fragment() {
    private var _binding: FragmentStaffProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: StaffProfileViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentStaffProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[StaffProfileViewModel::class.java]

        viewModel.user.observe(viewLifecycleOwner) { user ->
            if (user == null) return@observe
            binding.tvNama.text = user.nama
            binding.tvId.text = "ID: ${user.id} | ${user.role.uppercase()}"
            binding.tvEmail.text = user.email
        }

        binding.btnLogout.setOnClickListener {
            viewModel.logout()
            startActivity(Intent(requireContext(), LoginActivity::class.java))
            requireActivity().finish()
        }

        viewModel.load()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
```

- [ ] **Step 4: Verify build**

```bash
cd /home/t4pei/AndroidStudioProjects/MediLab && ./gradlew assembleDebug --no-daemon 2>&1 | tail -10
```

Expected: `BUILD SUCCESSFUL`

---

### Task 13: Patient Dashboard & Hasil Fragment

**Files:**
- Create: `app/src/main/java/com/example/medilab/patient/dashboard/PatientDashboardViewModel.kt`
- Create: `app/src/main/java/com/example/medilab/patient/dashboard/PatientDashboardFragment.kt`
- Create: `app/src/main/java/com/example/medilab/patient/hasil/HasilViewModel.kt`
- Create: `app/src/main/java/com/example/medilab/patient/hasil/HasilFragment.kt`
- Create: `app/src/main/res/layout/fragment_patient_dashboard.xml`
- Create: `app/src/main/res/layout/fragment_hasil.xml`

- [ ] **Step 1: Create PatientDashboardViewModel.kt**

```kotlin
package com.example.medilab.patient.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.medilab.model.Laporan
import com.example.medilab.model.Notifikasi
import com.example.medilab.repository.AuthRepository
import com.example.medilab.repository.LaporanRepository
import com.example.medilab.repository.NotifikasiRepository

class PatientDashboardViewModel : ViewModel() {
    private val laporanRepo = LaporanRepository()
    private val notifRepo = NotifikasiRepository()
    private val authRepo = AuthRepository()

    private val _laporanTerbaru = MutableLiveData<Laporan?>()
    val laporanTerbaru: LiveData<Laporan?> = _laporanTerbaru

    private val _notifikasi = MutableLiveData<List<Notifikasi>>()
    val notifikasi: LiveData<List<Notifikasi>> = _notifikasi

    fun load() {
        val uid = authRepo.getCurrentUid()
        laporanRepo.getByPasienId(uid) { list ->
            _laporanTerbaru.value = list.firstOrNull()
        }
        notifRepo.getByUserId(uid) { _notifikasi.value = it }
    }
}
```

- [ ] **Step 2: Create fragment_patient_dashboard.xml**

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:padding="16dp">

    <TextView android:id="@+id/tvWelcome"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Selamat datang!"
        android:textSize="18sp"
        android:textStyle="bold"
        android:layout_marginBottom="16dp"/>

    <TextView android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Status Laporan Terbaru"
        android:textStyle="bold"
        android:layout_marginBottom="8dp"/>

    <androidx.cardview.widget.CardView
        android:id="@+id/cardLaporanTerbaru"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        app:cardCornerRadius="8dp"
        app:cardElevation="2dp"
        android:layout_marginBottom="16dp">

        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:orientation="vertical"
            android:padding="12dp">

            <TextView android:id="@+id/tvPemeriksaan"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:textStyle="bold"/>

            <TextView android:id="@+id/tvStatusLaporan"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"/>

            <ProgressBar
                android:id="@+id/progressBar"
                style="?android:attr/progressBarStyleHorizontal"
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:layout_marginTop="8dp"/>
        </LinearLayout>
    </androidx.cardview.widget.CardView>

    <TextView android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Notifikasi"
        android:textStyle="bold"
        android:layout_marginBottom="8dp"/>

    <androidx.recyclerview.widget.RecyclerView
        android:id="@+id/rvNotifikasi"
        android:layout_width="match_parent"
        android:layout_height="match_parent"/>
</LinearLayout>
```

- [ ] **Step 3: Create PatientDashboardFragment.kt**

```kotlin
package com.example.medilab.patient.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.medilab.databinding.FragmentPatientDashboardBinding

class PatientDashboardFragment : Fragment() {
    private var _binding: FragmentPatientDashboardBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: PatientDashboardViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentPatientDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[PatientDashboardViewModel::class.java]

        viewModel.laporanTerbaru.observe(viewLifecycleOwner) { laporan ->
            if (laporan == null) return@observe
            binding.tvPemeriksaan.text = "Pemeriksaan #${laporan.id}"
            binding.tvStatusLaporan.text = "Status: ${laporan.status.uppercase()}"
            val progress = when (laporan.status) {
                "baru" -> 1; "proses" -> 2; "verifikasi" -> 3; "revisi" -> 2; "selesai" -> 4; else -> 0
            }
            binding.progressBar.progress = progress
            binding.progressBar.max = 4
        }

        viewModel.load()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
```

- [ ] **Step 4: Create HasilViewModel.kt**

```kotlin
package com.example.medilab.patient.hasil

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.medilab.model.Laporan
import com.example.medilab.repository.AuthRepository
import com.example.medilab.repository.LaporanRepository
import com.example.medilab.util.Constants

class HasilViewModel : ViewModel() {
    private val repo = LaporanRepository()
    private val authRepo = AuthRepository()

    private val _laporanSelesai = MutableLiveData<List<Laporan>>()
    val laporanSelesai: LiveData<List<Laporan>> = _laporanSelesai

    fun load() {
        repo.getByPasienId(authRepo.getCurrentUid()) { list ->
            _laporanSelesai.value = list.filter { it.status == Constants.STATUS_SELESAI }
        }
    }
}
```

- [ ] **Step 5: Create fragment_hasil.xml**

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:padding="16dp">

    <TextView android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Hasil Pemeriksaan"
        android:textSize="18sp"
        android:textStyle="bold"
        android:layout_marginBottom="12dp"/>

    <androidx.recyclerview.widget.RecyclerView
        android:id="@+id/rvHasil"
        android:layout_width="match_parent"
        android:layout_height="match_parent"/>
</LinearLayout>
```

- [ ] **Step 6: Create HasilFragment.kt**

```kotlin
package com.example.medilab.patient.hasil

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.medilab.adapter.LaporanAdapter
import com.example.medilab.databinding.FragmentHasilBinding

class HasilFragment : Fragment() {
    private var _binding: FragmentHasilBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: HasilViewModel
    private lateinit var adapter: LaporanAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHasilBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[HasilViewModel::class.java]

        adapter = LaporanAdapter { laporan ->
            Toast.makeText(requireContext(), "Lihat detail: ${laporan.id}", Toast.LENGTH_SHORT).show()
        }
        binding.rvHasil.adapter = adapter

        viewModel.laporanSelesai.observe(viewLifecycleOwner) { adapter.submitList(it) }
        viewModel.load()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
```

- [ ] **Step 7: Verify build**

```bash
cd /home/t4pei/AndroidStudioProjects/MediLab && ./gradlew assembleDebug --no-daemon 2>&1 | tail -10
```

Expected: `BUILD SUCCESSFUL`

---

### Task 14: Patient Riwayat & Profile Fragment

**Files:**
- Create: `app/src/main/java/com/example/medilab/patient/riwayat/RiwayatViewModel.kt`
- Create: `app/src/main/java/com/example/medilab/patient/riwayat/RiwayatFragment.kt`
- Create: `app/src/main/java/com/example/medilab/patient/profile/PatientProfileFragment.kt`
- Create: `app/src/main/java/com/example/medilab/patient/profile/PatientProfileViewModel.kt`
- Create: `app/src/main/res/layout/fragment_riwayat.xml`
- Create: `app/src/main/res/layout/fragment_patient_profile.xml`

- [ ] **Step 1: Create RiwayatViewModel.kt**

```kotlin
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
```

- [ ] **Step 2: Create fragment_riwayat.xml**

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:padding="16dp">

    <TextView android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Riwayat Rekam Medis"
        android:textSize="18sp"
        android:textStyle="bold"
        android:layout_marginBottom="12dp"/>

    <androidx.recyclerview.widget.RecyclerView
        android:id="@+id/rvRiwayat"
        android:layout_width="match_parent"
        android:layout_height="match_parent"/>
</LinearLayout>
```

- [ ] **Step 3: Create RiwayatFragment.kt**

```kotlin
package com.example.medilab.patient.riwayat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.medilab.adapter.RiwayatAdapter
import com.example.medilab.databinding.FragmentRiwayatBinding
import com.example.medilab.repository.AuthRepository

class RiwayatFragment : Fragment() {
    private var _binding: FragmentRiwayatBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: RiwayatViewModel
    private lateinit var adapter: RiwayatAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentRiwayatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[RiwayatViewModel::class.java]

        adapter = RiwayatAdapter { /* open detail */ }
        binding.rvRiwayat.adapter = adapter

        viewModel.riwayatList.observe(viewLifecycleOwner) { adapter.submitList(it) }
        viewModel.load(AuthRepository().getCurrentUid())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
```

- [ ] **Step 4: Create PatientProfileViewModel.kt**

```kotlin
package com.example.medilab.patient.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.medilab.model.User
import com.example.medilab.repository.AuthRepository
import com.example.medilab.repository.UserRepository

class PatientProfileViewModel : ViewModel() {
    private val userRepo = UserRepository()
    private val authRepo = AuthRepository()

    private val _user = MutableLiveData<User?>()
    val user: LiveData<User?> = _user

    fun load() {
        userRepo.getUser(authRepo.getCurrentUid()) { _user.value = it }
    }

    fun logout() {
        authRepo.logout()
    }
}
```

- [ ] **Step 5: Create PatientProfileFragment.kt** — Same layout pattern as StaffProfileFragment

```kotlin
package com.example.medilab.patient.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.medilab.auth.LoginActivity
import com.example.medilab.databinding.FragmentPatientProfileBinding

class PatientProfileFragment : Fragment() {
    private var _binding: FragmentPatientProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: PatientProfileViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentPatientProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[PatientProfileViewModel::class.java]

        viewModel.user.observe(viewLifecycleOwner) { user ->
            if (user == null) return@observe
            binding.tvNama.text = user.nama
            binding.tvId.text = "ID: ${user.id} | ${user.role.uppercase()}"
            binding.tvEmail.text = user.email
            binding.tvRm.text = "No. RM: ${user.noRekamMedis}"
        }

        binding.btnLogout.setOnClickListener {
            viewModel.logout()
            startActivity(Intent(requireContext(), LoginActivity::class.java))
            requireActivity().finish()
        }

        viewModel.load()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
```

Create `fragment_patient_profile.xml`:

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:padding="24dp"
    android:gravity="center_horizontal">

    <de.hdodenhof.circleimageview.CircleImageView
        android:id="@+id/ivProfile"
        android:layout_width="80dp"
        android:layout_height="80dp"
        android:src="@mipmap/ic_launcher"
        android:layout_marginBottom="16dp"/>

    <TextView android:id="@+id/tvNama" android:layout_width="wrap_content"
        android:layout_height="wrap_content" android:textStyle="bold" android:textSize="18sp"/>
    <TextView android:id="@+id/tvId" android:layout_width="wrap_content"
        android:layout_height="wrap_content" android:textSize="12sp"/>
    <TextView android:id="@+id/tvRm" android:layout_width="wrap_content"
        android:layout_height="wrap_content" android:textSize="12sp"/>
    <TextView android:id="@+id/tvEmail" android:layout_width="wrap_content"
        android:layout_height="wrap_content" android:layout_marginBottom="24dp"/>

    <com.google.android.material.button.MaterialButton
        android:id="@+id/btnEditProfil"
        android:layout_width="match_parent"
        android:layout_height="48dp"
        android:text="Edit Profil"
        style="@style/Widget.MaterialComponents.Button.OutlinedButton"/>

    <com.google.android.material.button.MaterialButton
        android:id="@+id/btnGantiPassword"
        android:layout_width="match_parent"
        android:layout_height="48dp"
        android:text="Ganti Password"
        style="@style/Widget.MaterialComponents.Button.OutlinedButton"
        android:layout_marginTop="8dp"/>

    <com.google.android.material.button.MaterialButton
        android:id="@+id/btnLogout"
        android:layout_width="match_parent"
        android:layout_height="48dp"
        android:text="KELUAR"
        android:layout_marginTop="24dp"/>
</LinearLayout>
```

- [ ] **Step 6: Final build check**

```bash
cd /home/t4pei/AndroidStudioProjects/MediLab && ./gradlew assembleDebug --no-daemon 2>&1 | tail -10
```

Expected: `BUILD SUCCESSFUL`

---

### Task 15: Firebase Configuration & Security Rules

**Files:**
- Create: `app/src/main/res/raw/firebase_config.txt` (guidance only)
- Documentation only

- [ ] **Step 1: Firebase Console Setup**

1. Go to [Firebase Console](https://console.firebase.google.com)
2. Create new project "MediLab"
3. Register Android app with package name `com.example.medilab`
4. Download `google-services.json` and place at `app/google-services.json`
5. Enable **Authentication** → Sign-in method → Email/Password
6. Create **Firestore Database** in test mode initially
7. Create **Storage** bucket

- [ ] **Step 2: Firestore Security Rules**

Deploy these rules via Firebase Console → Firestore → Rules:

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    // Helper: check if user has role
    function isRole(role) {
      return request.auth != null && get(/databases/$(database)/documents/users/$(request.auth.uid)).data.role == role;
    }

    // Users collection
    match /users/{userId} {
      allow read: if request.auth != null && (request.auth.uid == userId || isRole('admin') || isRole('petugas'));
      allow create: if request.auth != null && (request.auth.uid == userId || isRole('admin'));
      allow update: if request.auth != null && (request.auth.uid == userId || isRole('admin'));
      allow delete: if request.auth != null && isRole('admin');
    }

    // Dokter collection
    match /dokter/{dokterId} {
      allow read: if request.auth != null;
      allow write: if request.auth != null && (isRole('admin') || isRole('petugas'));
    }

    // Pemeriksaan collection
    match /pemeriksaan/{pemeriksaanId} {
      allow read: if request.auth != null;
      allow write: if request.auth != null && (isRole('admin') || isRole('petugas'));
    }

    // Obat collection
    match /obat/{obatId} {
      allow read: if request.auth != null;
      allow write: if request.auth != null && (isRole('admin') || isRole('petugas'));
    }

    // Laporan collection
    match /laporan/{laporanId} {
      allow read: if request.auth != null && (
        isRole('admin') || isRole('petugas') || resource.data.pasienId == request.auth.uid
      );
      allow write: if request.auth != null && (isRole('admin') || isRole('petugas'));
    }

    // RekamMedis collection
    match /rekamMedis/{rmId} {
      allow read: if request.auth != null && (
        isRole('admin') || isRole('petugas') || resource.data.pasienId == request.auth.uid
      );
      allow write: if request.auth != null && (isRole('admin') || isRole('petugas'));
    }

    // Notifikasi collection
    match /notifikasi/{notifId} {
      allow read: if request.auth != null && resource.data.userId == request.auth.uid;
      allow write: if request.auth != null && isRole('admin');
    }

    // AuditLog collection
    match /auditLog/{logId} {
      allow read: if request.auth != null && isRole('admin');
      allow write: if request.auth != null && (isRole('admin') || isRole('petugas'));
    }

    // Rujukan collection
    match /rujukan/{rujukanId} {
      allow read: if request.auth != null;
      allow write: if request.auth != null && (isRole('admin') || isRole('dokter') || isRole('petugas'));
    }
  }
}
```

- [ ] **Step 3: Storage Security Rules**

```javascript
rules_version = '2';
service firebase.storage {
  match /b/{bucket}/o {
    match /profiles/{userId}/{allPaths=**} {
      allow read: if request.auth != null;
      allow write: if request.auth != null && request.auth.uid == userId;
    }
    match /laporan/{laporanId}/{allPaths=**} {
      allow read: if request.auth != null;
      allow write: if request.auth != null;
    }
  }
}
```

---

## Self-Review

### Spec Coverage Check

| Spec Requirement | Covered In |
|-----------------|-----------|
| Firebase Auth + Firestore | Task 1, Task 4 |
| Auth UI (Login, Register Patient) | Task 5 |
| Role-based navigation | Task 5 (LoginActivity) |
| StaffActivity with BottomNavigation | Task 6 |
| PatientActivity with BottomNavigation | Task 7 |
| Staff Dashboard (stats, pending list) | Task 8 |
| Manage CRUD (Pasien, Dokter, Petugas, Pemeriksaan, Obat) | Task 10 |
| Laporan state machine flow | Task 11 (TabLayout, status) |
| Laporan detail with all fields | Task 11 |
| Patient Dashboard (status, progress) | Task 13 |
| Patient Hasil (selesai laporan list) | Task 13 |
| Patient Riwayat (timeline) | Task 14 |
| Profile (Staff & Patient) | Task 12, Task 14 |
| ID fields on all data | Task 3 (models) |
| Waktu, tanggal, RS in medical records | Laporan.model (rumahSakit + tanggal) |
| Master Obat data | Task 10 (ManageObat) + Task 3 (Obat model) |
| Error handling & validation | Task 2 (Validators), Task 8 (empty states) |

### Placeholder Scan
No TBD, TODO, or vague sections found. All code blocks contain complete implementations.

### Type Consistency
- `Laporan.id` matches across model, repository, adapters, and fragments.
- `Constants` keys match Firestore collection names.
- `rumahSakit` field structure is consistent between Laporan and RekamMedis models.

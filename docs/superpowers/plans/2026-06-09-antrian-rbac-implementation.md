# Antrian and RBAC Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Implement a dedicated queue (`COLLECTION_ANTRIAN`) and enforce role-based access control (RBAC) across MediLab.

**Architecture:**
- Firestore `COLLECTION_ANTRIAN` stores queue items.
- RBAC is enforced via granular Firestore security rules and repository-level guards.
- New `AntrianEntity`, `AntrianRepository`, and corresponding UI components.

**Tech Stack:**
- Kotlin, Firestore, Android/Room (as per project).

---

### Task 1: Add Firestore Collection and Security Rules

**Files:**
- Modify: `firestore.rules` (Define granular access)

- [ ] **Step 1: Update Firestore Rules**
Define access rules for `antrian` collection.
```
service cloud.firestore {
  match /databases/{database}/documents {
    match /antrian/{antrianId} {
      allow read: if request.auth != null; // Petugas, Dokter, Pasien (for their own)
      allow write: if request.auth != null && (get(/databases/$(database)/documents/users/$(request.auth.uid)).data.role in ['pasien', 'petugas']);
    }
    // Add other rules...
  }
}
```

- [ ] **Step 2: Commit**
```bash
git add firestore.rules
git commit -m "feat: add firestore rules for antrian"
```

### Task 2: Define Antrian Entity and Repository

**Files:**
- Create: `app/src/main/java/com/example/medilab/database/entity/AntrianEntity.kt`
- Create: `app/src/main/java/com/example/medilab/repository/AntrianRepository.kt`
- Modify: `app/src/main/java/com/example/medilab/util/Constants.kt`

- [ ] **Step 1: Update Constants**
```kotlin
// In Constants.kt
const val COLLECTION_ANTRIAN = "antrian"
```

- [ ] **Step 2: Create AntrianEntity**
```kotlin
package com.example.medilab.database.entity

data class AntrianEntity(
    val id: String = "",
    val pasienId: String = "",
    val status: String = "menunggu",
    val waktuDaftar: Long = System.currentTimeMillis(),
    val nomorAntrian: Int = 0,
    val tujuan: String = ""
)
```

- [ ] **Step 3: Create AntrianRepository**
```kotlin
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
```

- [ ] **Step 4: Commit**
```bash
git add app/src/main/java/com/example/medilab/database/entity/AntrianEntity.kt app/src/main/java/com/example/medilab/repository/AntrianRepository.kt app/src/main/java/com/example/medilab/util/Constants.kt
git commit -m "feat: add Antrian entity and repository"
```

### Task 3: RBAC Repository Guard

**Files:**
- Create: `app/src/main/java/com/example/medilab/util/AccessGuard.kt`

- [ ] **Step 1: Create AccessGuard**
```kotlin
package com.example.medilab.util

import com.example.medilab.model.User

object AccessGuard {
    fun canManageUsers(user: User): Boolean = user.role == Constants.ROLE_ADMIN
    fun canManagePemeriksaan(user: User): Boolean = user.role == Constants.ROLE_DOKTER || user.role == Constants.ROLE_ADMIN
    fun canAddToQueue(user: User): Boolean = user.role == Constants.ROLE_PASIEN
}
```

- [ ] **Step 2: Commit**
```bash
git add app/src/main/java/com/example/medilab/util/AccessGuard.kt
git commit -m "feat: add RBAC AccessGuard"
```

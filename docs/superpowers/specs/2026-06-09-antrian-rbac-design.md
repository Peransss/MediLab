# Design Specification: Antrian Feature and Role-Based Access Control (RBAC)

## 1. Overview
Implement a robust queue management system ("Antrian") and enforce strict role-based access control (RBAC) across the MediLab application. This ensures data integrity and operational efficiency by restricting access based on user roles (Admin, Dokter, Petugas, Pasien).

## 2. Queue Management (Antrian)
A dedicated `COLLECTION_ANTRIAN` will be implemented to track patient queue status.

### Data Model
```kotlin
data class AntrianEntity(
    val id: String = "",
    val pasienId: String = "",
    val status: String = "menunggu", // menunggu, dipanggil, selesai, batal
    val waktuDaftar: Long = System.currentTimeMillis(),
    val nomorAntrian: Int = 0,
    val tujuan: String = ""
)
```

## 3. RBAC Enforcement Matrix

| Role | Access Scope |
| :--- | :--- |
| **Admin** | Full management of Users (all roles), Dokter, Petugas. |
| **Dokter** | Manage Obat, Pemeriksaan, RekamMedis. Access limited Pasien info. |
| **Petugas** | Manage Pasien, view Dokter schedule/data. |
| **Pasien** | Access personal Hasil Pemeriksaan, add to Antrian. |

## 4. Implementation Strategy
1. **Firestore Rules:** Define granular security rules enforcing the above matrix.
2. **Repository Guard:** Implement a role-check wrapper in the repository layer to provide UI feedback before attempting server requests.
3. **Queue Logic:** Create `AntrianRepository` and UI components for patients to join the queue and petugas/dokter to monitor it.

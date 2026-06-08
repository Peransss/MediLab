# MediLab – Room Database Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) for syntax tracking.

**Goal:** Add Room (SQLite) as local source of truth with Firebase Firestore sync, phased per collection.

**Architecture:** Room-first data layer where all reads/writes hit local SQLite, then sync to Firestore in background. Firebase Auth remains for login. Sync uses last-write-wins by timestamp. Implemented in 3 phases per collection group.

**Tech Stack:** Room (SQLite), Firebase Auth/Firestore, WorkManager, Kotlin Flow, Gson (TypeConverters)

**Design Principles:**
- Room is single source of truth — ViewModels observe Room Flows
- Writes go to Room first with PENDING sync status
- SyncManager pushes pending changes to Firestore, pulls remote updates
- Conflict resolution: last-write-wins by `lastModifiedAt` timestamp
- Phase 1: User, Laporan, Notifikasi (core business objects)
- Phase 2: Pemeriksaan, Obat, Dokter (reference data)
- Phase 3: RekamMedis, Rujukan, AuditLog

---

## File Structure

```
app/src/main/java/com/example/medilab/database/
├── AppDatabase.kt           — Room database (@Database)
├── SyncStatus.kt            — enum: SYNCED, PENDING_CREATE, PENDING_UPDATE, PENDING_DELETE
├── Converters.kt            — Gson TypeConverters for nested objects
├── entity/                  (Phase 1)
│   ├── UserEntity.kt
│   ├── LaporanEntity.kt
│   └── NotifikasiEntity.kt
├── dao/                     (Phase 1)
│   ├── UserDao.kt
│   ├── LaporanDao.kt
│   └── NotifikasiDao.kt
├── repository/
│   ├── LocalUserRepository.kt
│   ├── LocalLaporanRepository.kt
│   └── LocalNotifikasiRepository.kt
└── sync/
    ├── SyncManager.kt       — Orchestrates push/pull sync
    ├── SyncWorker.kt        — WorkManager periodic worker
    └── NetworkMonitor.kt    — Connectivity StateFlow
```

**Modified files:**
- `gradle/libs.versions.toml` — add Room + Gson versions
- `app/build.gradle.kts` — add Room + Gson dependencies, KSP plugin
- `app/src/main/java/com/example/medilab/MediLabApp.kt` — init Room database + SyncManager
- Existing ViewModels — observe Room Flow instead of Firebase callbacks
- Existing repositories — unchanged; serve as remote sync layer for SyncManager

---

## Database Schema Design

### Entity Relationship Diagram (Phase 1)

```
┌─────────────┐       ┌─────────────────┐       ┌──────────────────┐
│  UserEntity │       │ LaporanEntity   │       │ NotifikasiEntity │
├─────────────┤       ├─────────────────┤       ├──────────────────┤
│ id (PK)     │──┐    │ id (PK)         │       │ id (PK)          │
│ nama        │  │    │ status          │       │ userId           │
│ email       │  │    │ pasienId        │──┐    │ judul            │
│ role        │  │    │ dokterId        │  │    │ pesan            │
│ noHP        │  │    │ petugasId       │  │    │ dibaca           │
│ alamat      │  │    │ adminId         │  │    │ createdAt        │
│ fotoProfile │  │    │ pemeriksaanId   │  │    │ tipe             │
│ noRekamMedis│  │    │ diagnosa        │  │    │ syncStatus       │
│ tanggalLahir│  │    │ catatanRevisi   │  │    │ lastModifiedAt   │
│ createdAt   │  │    │ alasanTolak     │  │    └──────────────────┘
│ syncStatus  │  │    │ createdAt       │  │
│ lastModAt   │  │    │ updatedAt       │  │
└─────────────┘  │    │ tanggalSelesai  │  │
                 │    │ syncStatus      │  │
                 │    │ lastModifiedAt  │  │
                 │    └─────────────────┘  │
                 │                         │
                 └─────────────────────────┘
```

### SyncStatus Enum

```kotlin
enum class SyncStatus {
    SYNCED,           // Data match with Firestore
    PENDING_CREATE,   // New local data, not yet sent to Firestore
    PENDING_UPDATE,   // Local changes not yet sent to Firestore
    PENDING_DELETE    // Locally deleted, not yet removed from Firestore
}
```

### TypeConverters

Nested objects stored as JSON strings via Gson:

| Room Field | Kotlin Type | Stored As |
|------------|-------------|-----------|
| `hasilParameter` | `List<HasilParameter>` | JSON string |
| `resepObat` | `List<ResepItem>` | JSON string |
| `rumahSakit` | `RumahSakit` | JSON string |

### Key Design Decisions

- **String IDs** — Room uses String primary keys matching Firestore document IDs
- **Timestamps** — Stored as `Long` (epoch millis), matching current `createdAt`/`updatedAt` pattern
- **Sync marker** — Each entity has `syncStatus: SyncStatus` + `lastModifiedAt: Long`
- **No foreign key constraints** — Room allows them but we skip to keep schema simple; app-level integrity
- **No cascade deletes** — Manual delete handling in sync layer

---

## Sync Architecture

```
                    ┌──────────────────────┐
                    │     ViewModel         │
                    │ (observes Room Flow)  │
                    └──────────┬───────────┘
                               │
                    ┌──────────▼───────────┐
                    │  LocalRepository      │
                    │  (Room DAO → Flow)   │
                    └──────────┬───────────┘
                               │
              ┌────────────────┴────────────────┐
              │                                  │
    ┌─────────▼──────────┐            ┌─────────▼──────────┐
    │   Write             │            │   Read             │
    │   → Room            │            │   ← Room Flow     │
    │   → mark PENDING    │            │                    │
    │   → trigger sync    │            │                    │
    └─────────┬───────────┘            └────────────────────┘
              │
    ┌─────────▼───────────┐
    │   SyncManager        │
    │                      │
    │   pushPendingChanges │──→ Firestore (batch writes)
    │   pullRemoteChanges  │──← Firestore (timestamp-based)
    │   conflictResolve    │──→ last-write-wins by timestamp
    │   upsert into Room   │
    └─────────────────────┘
```

### Sync Flow (Push)

```
1. Local write → Room upsert with syncStatus = PENDING_CREATE/UPDATE/DELETE
2. SyncManager.listenForPending() — collects pending records from DAOs
3. When online (NetworkMonitor):
   a. Read pending records from Room
   b. Write to Firestore (set/delete batch)
   c. On success: update Room syncStatus = SYNCED
   d. On failure: leave as PENDING (retry later)
```

### Sync Flow (Pull)

```
1. SyncManager.pullRemoteChanges() called periodically:
   a. Get lastSyncTimestamp from DataStore
   b. Firestore: .whereGreaterThan("updatedAt", lastSyncTimestamp)
   c. For each returned document:
      - Compare Room.lastModifiedAt vs Firestore.updatedAt
      - If Firestore timestamp > Room timestamp: upsert into Room
      - If Room timestamp > Firestore timestamp: push to Firestore (local wins)
   d. Update lastSyncTimestamp
2. WorkManager triggers pull every 15 minutes (minimum interval)
3. Manual pull on app foreground + after each push
```

### NetworkMonitor

```kotlin
class NetworkMonitor(context: Context) {
    val isOnline: Flow<Boolean> = // ConnectivityManager callback → callbackFlow
}
```

---

## Task Breakdown

### Phase 1: Foundation + Core Collections (User, Laporan, Notifikasi)

---

### Task 1: Add Room + Gson Dependencies

**Files:**
- Modify: `gradle/libs.versions.toml`
- Modify: `app/build.gradle.kts`
- Modify: root `build.gradle.kts`

- [ ] **Add Room + Gson + KSP to libs.versions.toml**

In `[versions]`:
```toml
room = "2.6.1"
gson = "2.11.0"
```

In `[libraries]`:
```toml
androidx-room-runtime = { group = "androidx.room", name = "room-runtime", version.ref = "room" }
androidx-room-ktx = { group = "androidx.room", name = "room-ktx", version.ref = "room" }
androidx-room-compiler = { group = "androidx.room", name = "room-compiler", version.ref = "room" }
gson = { group = "com.google.code.gson", name = "gson", version.ref = "gson" }
```

In `[plugins]`:
```toml
ksp = { id = "com.google.devtools.ksp", version = "2.0.21-1.0.28" }
```

- [ ] **Add KSP plugin to root build.gradle.kts**

```kotlin
plugins {
    // ... existing
    alias(libs.plugins.ksp) apply false
}
```

- [ ] **Add Room + Gson dependencies to app/build.gradle.kts**

```kotlin
plugins {
    // ... existing
    alias(libs.plugins.ksp)
}

dependencies {
    // ... existing

    // Room
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // Gson
    implementation(libs.gson)
}
```

Build: `cd /home/t4pei/AndroidStudioProjects/MediLab && ./gradlew assembleDebug --no-daemon 2>&1 | tail -10`

Expected: BUILD SUCCESSFUL

- [ ] **Commit**

```bash
git add gradle/libs.versions.toml build.gradle.kts app/build.gradle.kts
git commit -m "chore: add Room, Gson, KSP dependencies"
```

---

### Task 2: Create Database Infrastructure

**Files:**
- Create: `app/src/main/java/com/example/medilab/database/SyncStatus.kt`
- Create: `app/src/main/java/com/example/medilab/database/Converters.kt`
- Create: `app/src/main/java/com/example/medilab/database/AppDatabase.kt`
- Modify: `app/src/main/java/com/example/medilab/MediLabApp.kt`

- [ ] **Create SyncStatus.kt**

```kotlin
package com.example.medilab.database

enum class SyncStatus {
    SYNCED,
    PENDING_CREATE,
    PENDING_UPDATE,
    PENDING_DELETE
}
```

- [ ] **Create Converters.kt**

```kotlin
package com.example.medilab.database

import androidx.room.TypeConverter
import com.example.medilab.model.HasilParameter
import com.example.medilab.model.ResepItem
import com.example.medilab.model.RumahSakit
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromHasilParameterList(value: List<HasilParameter>): String = gson.toJson(value)

    @TypeConverter
    fun toHasilParameterList(value: String): List<HasilParameter> {
        val type = object : TypeToken<List<HasilParameter>>() {}.type
        return gson.fromJson(value, type) ?: emptyList()
    }

    @TypeConverter
    fun fromResepItemList(value: List<ResepItem>): String = gson.toJson(value)

    @TypeConverter
    fun toResepItemList(value: String): List<ResepItem> {
        val type = object : TypeToken<List<ResepItem>>() {}.type
        return gson.fromJson(value, type) ?: emptyList()
    }

    @TypeConverter
    fun fromRumahSakit(value: RumahSakit): String = gson.toJson(value)

    @TypeConverter
    fun toRumahSakit(value: String): RumahSakit {
        return gson.fromJson(value, RumahSakit::class.java) ?: RumahSakit()
    }
}
```

- [ ] **Create AppDatabase.kt**

```kotlin
package com.example.medilab.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.medilab.database.dao.LaporanDao
import com.example.medilab.database.dao.NotifikasiDao
import com.example.medilab.database.dao.UserDao
import com.example.medilab.database.entity.LaporanEntity
import com.example.medilab.database.entity.NotifikasiEntity
import com.example.medilab.database.entity.UserEntity

@Database(
    entities = [UserEntity::class, LaporanEntity::class, NotifikasiEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun laporanDao(): LaporanDao
    abstract fun notifikasiDao(): NotifikasiDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "medilab_database"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
```

- [ ] **Update MediLabApp.kt**

```kotlin
package com.example.medilab

import android.app.Application
import com.example.medilab.database.AppDatabase

class MediLabApp : Application() {
    lateinit var database: AppDatabase
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this
        database = AppDatabase.getInstance(this)
    }

    companion object {
        lateinit var instance: MediLabApp
            private set
    }
}
```

Build: `cd /home/t4pei/AndroidStudioProjects/MediLab && ./gradlew assembleDebug --no-daemon 2>&1 | tail -10`

Expected: BUILD SUCCESSFUL

- [ ] **Commit**

```bash
git add app/src/main/java/com/example/medilab/database/ app/src/main/java/com/example/medilab/MediLabApp.kt
git commit -m "feat: add Room database infrastructure"
```

---

### Task 3: Create Room Entities (User, Laporan, Notifikasi)

**Files:**
- Create: `app/src/main/java/com/example/medilab/database/entity/UserEntity.kt`
- Create: `app/src/main/java/com/example/medilab/database/entity/LaporanEntity.kt`
- Create: `app/src/main/java/com/example/medilab/database/entity/NotifikasiEntity.kt`

- [ ] **Create UserEntity.kt**

```kotlin
package com.example.medilab.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.medilab.database.SyncStatus

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val nama: String = "",
    val email: String = "",
    val role: String = "",
    val noHP: String = "",
    val alamat: String = "",
    val fotoProfile: String = "",
    val noRekamMedis: String = "",
    val tanggalLahir: String = "",
    val createdAt: Long = 0L,
    val syncStatus: SyncStatus = SyncStatus.SYNCED,
    val lastModifiedAt: Long = System.currentTimeMillis()
)
```

- [ ] **Create LaporanEntity.kt**

```kotlin
package com.example.medilab.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.medilab.database.SyncStatus
import com.example.medilab.model.HasilParameter
import com.example.medilab.model.ResepItem
import com.example.medilab.model.RumahSakit

@Entity(tableName = "laporan")
data class LaporanEntity(
    @PrimaryKey val id: String,
    val status: String = "",
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
    val createdAt: Long = 0L,
    val updatedAt: Long = 0L,
    val tanggalSelesai: Long = 0L,
    val syncStatus: SyncStatus = SyncStatus.SYNCED,
    val lastModifiedAt: Long = System.currentTimeMillis()
)
```

- [ ] **Create NotifikasiEntity.kt**

```kotlin
package com.example.medilab.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.medilab.database.SyncStatus

@Entity(tableName = "notifikasi")
data class NotifikasiEntity(
    @PrimaryKey val id: String,
    val userId: String = "",
    val judul: String = "",
    val pesan: String = "",
    val dibaca: Boolean = false,
    val createdAt: Long = 0L,
    val tipe: String = "",
    val syncStatus: SyncStatus = SyncStatus.SYNCED,
    val lastModifiedAt: Long = System.currentTimeMillis()
)
```

Build: `cd /home/t4pei/AndroidStudioProjects/MediLab && ./gradlew assembleDebug --no-daemon 2>&1 | tail -10`

Expected: BUILD SUCCESSFUL

- [ ] **Commit**

```bash
git add app/src/main/java/com/example/medilab/database/entity/
git commit -m "feat: add Room entities for User, Laporan, Notifikasi"
```

---

### Task 4: Create DAOs

**Files:**
- Create: `app/src/main/java/com/example/medilab/database/dao/UserDao.kt`
- Create: `app/src/main/java/com/example/medilab/database/dao/LaporanDao.kt`
- Create: `app/src/main/java/com/example/medilab/database/dao/NotifikasiDao.kt`

- [ ] **Create UserDao.kt**

```kotlin
package com.example.medilab.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.medilab.database.SyncStatus
import com.example.medilab.database.entity.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users ORDER BY nama ASC")
    fun getAll(): Flow<List<UserEntity>>

    @Query("SELECT * FROM users WHERE id = :id")
    fun getById(id: String): Flow<UserEntity?>

    @Query("SELECT * FROM users WHERE role = :role")
    fun getByRole(role: String): Flow<List<UserEntity>>

    @Query("SELECT * FROM users WHERE syncStatus != 'SYNCED'")
    suspend fun getPendingSync(): List<UserEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(user: UserEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(users: List<UserEntity>)

    @Query("DELETE FROM users WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("UPDATE users SET syncStatus = :status WHERE id = :id")
    suspend fun updateSyncStatus(id: String, status: SyncStatus)

    @Query("SELECT COUNT(*) FROM users")
    suspend fun count(): Int
}
```

- [ ] **Create LaporanDao.kt**

```kotlin
package com.example.medilab.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.medilab.database.SyncStatus
import com.example.medilab.database.entity.LaporanEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LaporanDao {
    @Query("SELECT * FROM laporan ORDER BY createdAt DESC")
    fun getAll(): Flow<List<LaporanEntity>>

    @Query("SELECT * FROM laporan WHERE id = :id")
    fun getById(id: String): Flow<LaporanEntity?>

    @Query("SELECT * FROM laporan WHERE status = :status ORDER BY createdAt DESC")
    fun getByStatus(status: String): Flow<List<LaporanEntity>>

    @Query("SELECT * FROM laporan WHERE pasienId = :pasienId ORDER BY createdAt DESC")
    fun getByPasienId(pasienId: String): Flow<List<LaporanEntity>>

    @Query("SELECT * FROM laporan WHERE pasienId = :pasienId AND status = :status ORDER BY createdAt DESC")
    fun getByPasienIdAndStatus(pasienId: String, status: String): Flow<List<LaporanEntity>>

    @Query("SELECT * FROM laporan WHERE syncStatus != 'SYNCED'")
    suspend fun getPendingSync(): List<LaporanEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(laporan: LaporanEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(laporans: List<LaporanEntity>)

    @Query("DELETE FROM laporan WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("UPDATE laporan SET syncStatus = :status WHERE id = :id")
    suspend fun updateSyncStatus(id: String, status: SyncStatus)

    @Query("SELECT COUNT(*) FROM laporan")
    suspend fun count(): Int
}
```

- [ ] **Create NotifikasiDao.kt**

```kotlin
package com.example.medilab.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.medilab.database.SyncStatus
import com.example.medilab.database.entity.NotifikasiEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NotifikasiDao {
    @Query("SELECT * FROM notifikasi WHERE userId = :userId ORDER BY createdAt DESC")
    fun getByUserId(userId: String): Flow<List<NotifikasiEntity>>

    @Query("SELECT * FROM notifikasi WHERE id = :id")
    fun getById(id: String): Flow<NotifikasiEntity?>

    @Query("SELECT * FROM notifikasi WHERE syncStatus != 'SYNCED'")
    suspend fun getPendingSync(): List<NotifikasiEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(notifikasi: NotifikasiEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(notifikasis: List<NotifikasiEntity>)

    @Query("UPDATE notifikasi SET dibaca = 1, syncStatus = 'PENDING_UPDATE' WHERE id = :id")
    suspend fun markAsRead(id: String)

    @Query("UPDATE notifikasi SET dibaca = 1, syncStatus = 'PENDING_UPDATE' WHERE userId = :userId AND dibaca = 0")
    suspend fun markAllAsRead(userId: String)

    @Query("DELETE FROM notifikasi WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("UPDATE notifikasi SET syncStatus = :status WHERE id = :id")
    suspend fun updateSyncStatus(id: String, status: SyncStatus)
}
```

Build: `cd /home/t4pei/AndroidStudioProjects/MediLab && ./gradlew assembleDebug --no-daemon 2>&1 | tail -10`

Expected: BUILD SUCCESSFUL

- [ ] **Commit**

```bash
git add app/src/main/java/com/example/medilab/database/dao/
git commit -m "feat: add Room DAOs for User, Laporan, Notifikasi"
```

---

### Task 5: Create NetworkMonitor

**Files:**
- Create: `app/src/main/java/com/example/medilab/database/sync/NetworkMonitor.kt`

- [ ] **Create NetworkMonitor.kt**

```kotlin
package com.example.medilab.database.sync

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged

class NetworkMonitor(context: Context) {
    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    val isOnline: Flow<Boolean> = callbackFlow {
        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) { trySend(true) }
            override fun onLost(network: Network) { trySend(false) }
            override fun onCapabilitiesChanged(network: Network, caps: NetworkCapabilities) {
                trySend(caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET))
            }
        }
        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        connectivityManager.registerNetworkCallback(request, callback)

        val activeNet = connectivityManager.activeNetwork
        val caps = connectivityManager.getNetworkCapabilities(activeNet)
        trySend(caps?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true)

        awaitClose { connectivityManager.unregisterNetworkCallback(callback) }
    }.distinctUntilChanged()
}
```

- [ ] **Commit**

```bash
git add app/src/main/java/com/example/medilab/database/sync/NetworkMonitor.kt
git commit -m "feat: add NetworkMonitor for connectivity state"
```

---

### Task 6: Create SyncManager

**Files:**
- Create: `app/src/main/java/com/example/medilab/database/sync/SyncManager.kt`

- [ ] **Create SyncManager.kt**

```kotlin
package com.example.medilab.database.sync

import com.example.medilab.database.AppDatabase
import com.example.medilab.database.SyncStatus
import com.example.medilab.database.entity.LaporanEntity
import com.example.medilab.database.entity.NotifikasiEntity
import com.example.medilab.database.entity.UserEntity
import com.example.medilab.model.Laporan
import com.example.medilab.model.Notifikasi
import com.example.medilab.repository.LaporanRepository
import com.example.medilab.repository.NotifikasiRepository
import com.example.medilab.repository.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class SyncManager(
    private val database: AppDatabase,
    private val userRepository: UserRepository,
    private val laporanRepository: LaporanRepository,
    private val notifikasiRepository: NotifikasiRepository,
    private val networkMonitor: NetworkMonitor,
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
) {
    var lastSyncTimestamp: Long = 0L

    fun startListening() {
        scope.launch {
            networkMonitor.isOnline.collect { online ->
                if (online) {
                    pushPendingChanges()
                    pullRemoteChanges()
                }
            }
        }
    }

    suspend fun pushPendingChanges() {
        pushPendingUsers()
        pushPendingLaporans()
        pushPendingNotifikasis()
    }

    private suspend fun pushPendingUsers() {
        val pending = database.userDao().getPendingSync()
        for (entity in pending) {
            when (entity.syncStatus) {
                SyncStatus.PENDING_CREATE, SyncStatus.PENDING_UPDATE -> {
                    val data = mapOf<String, Any?>(
                        "nama" to entity.nama,
                        "email" to entity.email,
                        "role" to entity.role,
                        "noHP" to entity.noHP,
                        "alamat" to entity.alamat,
                        "fotoProfile" to entity.fotoProfile,
                        "noRekamMedis" to entity.noRekamMedis,
                        "tanggalLahir" to entity.tanggalLahir,
                        "createdAt" to entity.createdAt
                    )
                    userRepository.updateUser(entity.id, data) { success ->
                        if (success) {
                            scope.launch {
                                database.userDao().updateSyncStatus(entity.id, SyncStatus.SYNCED)
                            }
                        }
                    }
                }
                SyncStatus.PENDING_DELETE -> {
                    userRepository.deleteUser(entity.id) { success ->
                        if (success) {
                            scope.launch {
                                database.userDao().deleteById(entity.id)
                            }
                        }
                    }
                }
                else -> {}
            }
        }
    }

    private suspend fun pushPendingLaporans() {
        val pending = database.laporanDao().getPendingSync()
        for (entity in pending) {
            when (entity.syncStatus) {
                SyncStatus.PENDING_CREATE -> {
                    val laporan = Laporan(
                        id = entity.id,
                        status = entity.status,
                        pasienId = entity.pasienId,
                        dokterId = entity.dokterId,
                        petugasId = entity.petugasId,
                        adminId = entity.adminId,
                        pemeriksaanId = entity.pemeriksaanId,
                        rujukanId = entity.rujukanId,
                        hasilParameter = entity.hasilParameter,
                        diagnosa = entity.diagnosa,
                        resepObat = entity.resepObat,
                        catatanRevisi = entity.catatanRevisi,
                        alasanTolak = entity.alasanTolak,
                        rumahSakit = entity.rumahSakit,
                        createdAt = entity.createdAt,
                        updatedAt = entity.updatedAt,
                        tanggalSelesai = entity.tanggalSelesai
                    )
                    laporanRepository.add(laporan) { success ->
                        if (success) scope.launch {
                            database.laporanDao().updateSyncStatus(entity.id, SyncStatus.SYNCED)
                        }
                    }
                }
                SyncStatus.PENDING_UPDATE -> {
                    val data = mapOf<String, Any?>(
                        "status" to entity.status,
                        "hasilParameter" to entity.hasilParameter,
                        "diagnosa" to entity.diagnosa,
                        "resepObat" to entity.resepObat,
                        "catatanRevisi" to entity.catatanRevisi,
                        "alasanTolak" to entity.alasanTolak,
                        "updatedAt" to entity.lastModifiedAt,
                        "tanggalSelesai" to entity.tanggalSelesai
                    )
                    laporanRepository.update(entity.id, data) { success ->
                        if (success) scope.launch {
                            database.laporanDao().updateSyncStatus(entity.id, SyncStatus.SYNCED)
                        }
                    }
                }
                else -> {}
            }
        }
    }

    private suspend fun pushPendingNotifikasis() {
        val pending = database.notifikasiDao().getPendingSync()
        for (entity in pending) {
            when (entity.syncStatus) {
                SyncStatus.PENDING_CREATE -> {
                    val notifikasi = Notifikasi(
                        id = entity.id,
                        userId = entity.userId,
                        judul = entity.judul,
                        pesan = entity.pesan,
                        dibaca = entity.dibaca,
                        createdAt = entity.createdAt,
                        tipe = entity.tipe
                    )
                    notifikasiRepository.add(notifikasi) { success ->
                        if (success) scope.launch {
                            database.notifikasiDao().updateSyncStatus(entity.id, SyncStatus.SYNCED)
                        }
                    }
                }
                SyncStatus.PENDING_UPDATE -> {
                    notifikasiRepository.markAsRead(entity.id) { success ->
                        if (success) scope.launch {
                            database.notifikasiDao().updateSyncStatus(entity.id, SyncStatus.SYNCED)
                        }
                    }
                }
                else -> {}
            }
        }
    }

    suspend fun pullRemoteChanges() {
        pullUpdatedUsers()
        pullUpdatedLaporans()
        lastSyncTimestamp = System.currentTimeMillis()
    }

    private suspend fun pullUpdatedUsers() {
        userRepository.getAllUsers { users ->
            scope.launch {
                val entities = users.map { user ->
                    UserEntity(
                        id = user.id,
                        nama = user.nama,
                        email = user.email,
                        role = user.role,
                        noHP = user.noHP,
                        alamat = user.alamat,
                        fotoProfile = user.fotoProfile,
                        noRekamMedis = user.noRekamMedis,
                        tanggalLahir = user.tanggalLahir,
                        createdAt = user.createdAt,
                        syncStatus = SyncStatus.SYNCED,
                        lastModifiedAt = user.createdAt
                    )
                }
                database.userDao().upsertAll(entities)
            }
        }
    }

    private suspend fun pullUpdatedLaporans() {
        laporanRepository.getAll { laporans ->
            scope.launch {
                val entities = laporans.map { l ->
                    LaporanEntity(
                        id = l.id, status = l.status, pasienId = l.pasienId,
                        dokterId = l.dokterId, petugasId = l.petugasId,
                        adminId = l.adminId, pemeriksaanId = l.pemeriksaanId,
                        rujukanId = l.rujukanId, hasilParameter = l.hasilParameter,
                        diagnosa = l.diagnosa, resepObat = l.resepObat,
                        catatanRevisi = l.catatanRevisi, alasanTolak = l.alasanTolak,
                        rumahSakit = l.rumahSakit, createdAt = l.createdAt,
                        updatedAt = l.updatedAt, tanggalSelesai = l.tanggalSelesai,
                        syncStatus = SyncStatus.SYNCED, lastModifiedAt = l.updatedAt
                    )
                }
                database.laporanDao().upsertAll(entities)
            }
        }
    }
}
```

- [ ] **Commit**

```bash
git add app/src/main/java/com/example/medilab/database/sync/SyncManager.kt
git commit -m "feat: add SyncManager for push/pull sync with Firestore"
```

---

### Task 7: Create SyncWorker (WorkManager)

**Files:**
- Create: `app/src/main/java/com/example/medilab/database/sync/SyncWorker.kt`
- Modify: `app/src/main/java/com/example/medilab/MediLabApp.kt`

- [ ] **Create SyncWorker.kt**

```kotlin
package com.example.medilab.database.sync

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.example.medilab.MediLabApp
import java.util.concurrent.TimeUnit

class SyncWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val app = applicationContext as MediLabApp
        val syncManager = app.syncManager
        return try {
            syncManager.pushPendingChanges()
            syncManager.pullRemoteChanges()
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    companion object {
        private const val WORK_NAME = "medilab_sync"

        fun schedule(context: Context) {
            val request = PeriodicWorkRequestBuilder<SyncWorker>(
                15, TimeUnit.MINUTES
            ).build()
            WorkManager.getInstance(context)
                .enqueueUniquePeriodicWork(
                    WORK_NAME,
                    ExistingPeriodicWorkPolicy.KEEP,
                    request
                )
        }
    }
}
```

- [ ] **Update MediLabApp.kt**

```kotlin
package com.example.medilab

import android.app.Application
import com.example.medilab.database.AppDatabase
import com.example.medilab.database.sync.NetworkMonitor
import com.example.medilab.database.sync.SyncManager
import com.example.medilab.database.sync.SyncWorker
import com.example.medilab.repository.LaporanRepository
import com.example.medilab.repository.NotifikasiRepository
import com.example.medilab.repository.UserRepository

class MediLabApp : Application() {
    lateinit var database: AppDatabase
        private set
    lateinit var networkMonitor: NetworkMonitor
        private set
    lateinit var syncManager: SyncManager
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this
        database = AppDatabase.getInstance(this)
        networkMonitor = NetworkMonitor(this)
        syncManager = SyncManager(
            database = database,
            userRepository = UserRepository(),
            laporanRepository = LaporanRepository(),
            notifikasiRepository = NotifikasiRepository(),
            networkMonitor = networkMonitor
        )
        SyncWorker.schedule(this)
        syncManager.startListening()
    }

    companion object {
        lateinit var instance: MediLabApp
            private set
    }
}
```

Build: `cd /home/t4pei/AndroidStudioProjects/MediLab && ./gradlew assembleDebug --no-daemon 2>&1 | tail -10`

Expected: BUILD SUCCESSFUL

- [ ] **Commit**

```bash
git add app/src/main/java/com/example/medilab/database/sync/SyncWorker.kt app/src/main/java/com/example/medilab/MediLabApp.kt
git commit -m "feat: add SyncWorker for periodic background sync"
```

---

### Task 8: Create LocalRepositories (Room-first APIs for ViewModels)

**Files:**
- Create: `app/src/main/java/com/example/medilab/database/repository/LocalUserRepository.kt`
- Create: `app/src/main/java/com/example/medilab/database/repository/LocalLaporanRepository.kt`
- Create: `app/src/main/java/com/example/medilab/database/repository/LocalNotifikasiRepository.kt`

- [ ] **Create LocalUserRepository.kt**

```kotlin
package com.example.medilab.database.repository

import com.example.medilab.database.AppDatabase
import com.example.medilab.database.SyncStatus
import com.example.medilab.database.entity.UserEntity
import com.example.medilab.database.sync.SyncManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class LocalUserRepository(
    private val database: AppDatabase,
    private val syncManager: SyncManager
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val dao = database.userDao()

    fun getAll(): Flow<List<UserEntity>> = dao.getAll()

    fun getById(id: String): Flow<UserEntity?> = dao.getById(id)

    fun getByRole(role: String): Flow<List<UserEntity>> = dao.getByRole(role)

    fun upsert(user: UserEntity) {
        scope.launch {
            dao.upsert(user.copy(syncStatus = SyncStatus.PENDING_CREATE))
            syncManager.pushPendingChanges()
        }
    }

    fun update(id: String, nama: String, noHP: String, alamat: String) {
        scope.launch {
            val current = database.userDao().getById(id)
            val entity = current?.copy(
                nama = nama, noHP = noHP, alamat = alamat,
                syncStatus = SyncStatus.PENDING_UPDATE,
                lastModifiedAt = System.currentTimeMillis()
            ) ?: return@launch
            dao.upsert(entity)
            syncManager.pushPendingChanges()
        }
    }

    fun delete(id: String) {
        scope.launch {
            dao.updateSyncStatus(id, SyncStatus.PENDING_DELETE)
            syncManager.pushPendingChanges()
        }
    }
}
```

- [ ] **Create LocalLaporanRepository.kt**

```kotlin
package com.example.medilab.database.repository

import com.example.medilab.database.AppDatabase
import com.example.medilab.database.SyncStatus
import com.example.medilab.database.entity.LaporanEntity
import com.example.medilab.database.sync.SyncManager
import com.example.medilab.model.HasilParameter
import com.example.medilab.model.ResepItem
import com.example.medilab.model.RumahSakit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class LocalLaporanRepository(
    private val database: AppDatabase,
    private val syncManager: SyncManager
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val dao = database.laporanDao()

    fun getAll(): Flow<List<LaporanEntity>> = dao.getAll()

    fun getById(id: String): Flow<LaporanEntity?> = dao.getById(id)

    fun getByStatus(status: String): Flow<List<LaporanEntity>> = dao.getByStatus(status)

    fun getByPasienId(pasienId: String): Flow<List<LaporanEntity>> = dao.getByPasienId(pasienId)

    fun getByPasienIdAndStatus(pasienId: String, status: String): Flow<List<LaporanEntity>> =
        dao.getByPasienIdAndStatus(pasienId, status)

    fun upsert(laporan: LaporanEntity) {
        scope.launch {
            dao.upsert(laporan.copy(syncStatus = SyncStatus.PENDING_CREATE))
            syncManager.pushPendingChanges()
        }
    }

    fun updateStatus(id: String, newStatus: String) {
        scope.launch {
            val current = database.laporanDao().getById(id)
            val entity = current?.copy(
                status = newStatus,
                syncStatus = SyncStatus.PENDING_UPDATE,
                lastModifiedAt = System.currentTimeMillis()
            ) ?: return@launch
            dao.upsert(entity)
            syncManager.pushPendingChanges()
        }
    }

    fun update(
        id: String,
        hasilParameter: List<HasilParameter>? = null,
        diagnosa: String? = null,
        resepObat: List<ResepItem>? = null,
        catatanRevisi: String? = null,
        alasanTolak: String? = null,
        rumahSakit: RumahSakit? = null
    ) {
        scope.launch {
            val current = database.laporanDao().getById(id) ?: return@launch
            val entity = current.copy(
                hasilParameter = hasilParameter ?: current.hasilParameter,
                diagnosa = diagnosa ?: current.diagnosa,
                resepObat = resepObat ?: current.resepObat,
                catatanRevisi = catatanRevisi ?: current.catatanRevisi,
                alasanTolak = alasanTolak ?: current.alasanTolak,
                rumahSakit = rumahSakit ?: current.rumahSakit,
                syncStatus = SyncStatus.PENDING_UPDATE,
                lastModifiedAt = System.currentTimeMillis()
            )
            dao.upsert(entity)
            syncManager.pushPendingChanges()
        }
    }
}
```

- [ ] **Create LocalNotifikasiRepository.kt**

```kotlin
package com.example.medilab.database.repository

import com.example.medilab.database.AppDatabase
import com.example.medilab.database.entity.NotifikasiEntity
import com.example.medilab.database.sync.SyncManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class LocalNotifikasiRepository(
    private val database: AppDatabase,
    private val syncManager: SyncManager
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val dao = database.notifikasiDao()

    fun getByUserId(userId: String): Flow<List<NotifikasiEntity>> = dao.getByUserId(userId)

    fun upsert(notifikasi: NotifikasiEntity) {
        scope.launch {
            dao.upsert(notifikasi)
            syncManager.pushPendingChanges()
        }
    }

    fun markAsRead(id: String) {
        scope.launch {
            dao.markAsRead(id)
            syncManager.pushPendingChanges()
        }
    }

    fun markAllAsRead(userId: String) {
        scope.launch {
            dao.markAllAsRead(userId)
            syncManager.pushPendingChanges()
        }
    }
}
```

Build: `cd /home/t4pei/AndroidStudioProjects/MediLab && ./gradlew assembleDebug --no-daemon 2>&1 | tail -10`

Expected: BUILD SUCCESSFUL

- [ ] **Commit**

```bash
git add app/src/main/java/com/example/medilab/database/repository/
git commit -m "feat: add LocalRepositories wrapping Room DAOs"
```

---

### Task 9: ViewModel Refactor — PatientHomeViewModel

**Files:**
- Modify: `app/src/main/java/com/example/medilab/ui/screen/patient/home/PatientHomeViewModel.kt`

- [ ] **Refactor PatientHomeViewModel to observe Room Flow**

Current pattern (Firebase callback):
```kotlin
laporanRepository.getByPasienId(pasienId) { list ->
    _uiState.update { it.copy(laporanList = list) }
}
```

New pattern (Room Flow):
```kotlin
class PatientHomeViewModel(
    private val localLaporanRepo: LocalLaporanRepository,
    private val localUserRepo: LocalUserRepository,
    private val authRepo: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PatientHomeUiState())
    val uiState: StateFlow<PatientHomeUiState> = _uiState.asStateFlow()

    init {
        val uid = authRepo.getCurrentUid()
        if (uid.isNotEmpty()) {
            viewModelScope.launch {
                localLaporanRepo.getByPasienId(uid).collect { list ->
                    _uiState.update { it.copy(laporanList = list) }
                }
            }
            viewModelScope.launch {
                localLaporanRepo.getByPasienIdAndStatus(uid, "selesai").collect { list ->
                    _uiState.update { it.copy(jumlahSelesai = list.size) }
                }
            }
        }
    }
}
```

Build: `cd /home/t4pei/AndroidStudioProjects/MediLab && ./gradlew assembleDebug --no-daemon 2>&1 | tail -10`

Expected: BUILD SUCCESSFUL

- [ ] **Commit**

```bash
git add app/src/main/java/com/example/medilab/ui/screen/patient/home/PatientHomeViewModel.kt
git commit -m "refactor: PatientHomeViewModel uses Room Flow"
```

---

### Task 10: ViewModel Refactor — Staff Laporan + Notifikasi

**Files:**
- Modify: `app/src/main/java/com/example/medilab/ui/screen/staff/laporan/StaffLaporanViewModel.kt`
- Modify: `app/src/main/java/com/example/medilab/ui/screen/staff/notifikasi/StaffNotifikasiScreen.kt`
- Modify: `app/src/main/java/com/example/medilab/ui/screen/patient/notifikasi/PatientNotifikasiScreen.kt`

- [ ] **Refactor each view/screen to use local repository + Flow**

For StaffLaporanViewModel:
- Inject `LocalLaporanRepository`
- Replace `laporanRepository.getByStatus(status) { }` with `localLaporanRepo.getByStatus(status).collect { }`

For StaffNotifikasiScreen:
- Inject `LocalNotifikasiRepository`
- Replace `notifikasiRepository.getByUserId(uid) { }` with `localNotifikasiRepo.getByUserId(uid).collect { }`

For PatientNotifikasiScreen:
- Same pattern as StaffNotifikasiScreen

Build after each change.

- [ ] **Commit**

---

### Task 11: Initial Seed — Pull from Firestore on First Launch

**Files:**
- Modify: `app/src/main/java/com/example/medilab/MediLabApp.kt`

- [ ] **Add seed logic after database + syncManager init**

```kotlin
// In onCreate(), after syncManager init:
scope.launch {
    if (database.userDao().count() == 0) {
        syncManager.pullRemoteChanges()
    }
}

// Add scope to MediLabApp:
private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
```

- [ ] **Commit**

---

### Phase 2: Reference Data (Pemeriksaan, Obat, Dokter)

Repeat Tasks 3-8 pattern for these collections. Each follows identical entity → DAO → LocalRepository pattern.

- [ ] **Task 12: Create entities, DAOs, repositories for Pemeriksaan, Obat, Dokter**
- [ ] **Task 13: Update AppDatabase (version 2) + SyncManager push/pull**
- [ ] **Task 14: Update ViewModels that reference these collections**

---

### Phase 3: Remaining Collections (RekamMedis, Rujukan, AuditLog)

- [ ] **Task 15: Repeat pattern for RekamMedis, Rujukan, AuditLog**
- [ ] **Task 16: Update AppDatabase (version 3) + SyncManager**
- [ ] **Task 17: Update ViewModels**

---

## Verification Plan

After each phase:
1. `./gradlew assembleDebug` — must compile
2. Test on device with airplane mode:
   - App loads cached data without internet
   - Writes queue locally (PENDING status)
3. Re-enable network:
   - Pending writes sync to Firestore
   - Pull updates synced to Room
4. Kill app and reopen:
   - Room persists all data
   - SyncWorker runs periodically

## State After Implementation

```
┌──────────────────────────────────────────────┐
│                  ViewModel                    │
│         observes Flow<List<Entity>>           │
└─────────────────┬────────────────────────────┘
                  │
┌─────────────────▼────────────────────────────┐
│          LocalRepository                      │
│     Room DAO (Flow) ← write → mark PENDING   │
└─────────────────┬────────────────────────────┘
                  │
┌─────────────────▼────────────────────────────┐
│              Room Database                    │
│  (SQLite — single source of truth)           │
└────────┬────────────────────────┬────────────┘
         │                        │
┌────────▼────────┐    ┌─────────▼───────────┐
│   Write path    │    │    Read path         │
│   mark PENDING  │    │    Room query → Flow │
│   SyncManager   │    │                      │
│   push to FS    │    │                      │
└─────────────────┘    └──────────────────────┘
```

---

*Plan generated — June 2026*

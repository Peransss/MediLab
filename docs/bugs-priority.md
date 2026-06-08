# Prioritas Perbaikan Bug

Daftar bug berdasarkan severity. Build sukses, error bersifat runtime/logic.

---

## PRIORITAS 1 — CRASH / DATA CORRUPTION (fix segera)

### 1.1 `MediLabApp.instance` NPE di ViewModel

**Severity:** HIGH
**File:** Semua ViewModel yang mengakses `MediLabApp.instance` di property declaration
**Affected:** `StaffHomeViewModel`, `PatientHomeViewModel`, `StaffLaporanViewModel`, `StaffNotifikasiViewModel`, `PatientNotifikasiViewModel`, `PatientHasilViewModel`, `ManageViewModel`

**Problem:**
```kotlin
class ManageViewModel : ViewModel() {
    private val app = MediLabApp.instance  // Crash jika onCreate() belum selesai
```
Akses `MediLabApp.instance` di property level → `UninitializedPropertyAccessException` jika ViewModel di-instantiate sebelum `Application.onCreate()` selesai (misal: Activity recreation race).

**Fix:** Pindah ke `lazy {}`:
```kotlin
private val app by lazy { MediLabApp.instance }
```

---

### 1.2 SyncManager Push/Pull Race Condition

**Severity:** HIGH
**Files:** `SyncManager.kt:63-73` (push), `SyncManager.kt:369-379` (pull)

**Problem:**
```
pushPendingChanges() → fire-and-forget Firebase callbacks (async)
     ↓ (return segera)
pullRemoteChanges() → mulai tanpa tunggu push selesai
     ↓
Data yang masih PENDING_* di Room ketimpa SYNCED dari pull
     ↓
Callback push selesai → updateSyncStatus pada baris yang sudah berubah
```
Data sync status corrupt → perubahan lokal hilang atau gagal di-push ke Firestore.

**Fix:**
1. Tambah `@Volatile` ke `lastSyncTimestamp` atau ganti `AtomicLong`
2. Tambah `Mutex` agar push/pull tidak bisa jalan bersamaan
3. Push methods pakai `Tasks.await()` atau callback chaining agar benar-benar selesai sebelum pull jalan

---

### 1.3 SyncManager Thread Safety (`lastSyncTimestamp`)

**Severity:** HIGH
**File:** `SyncManager.kt:50`

**Problem:**
```kotlin
var lastSyncTimestamp: Long = 0L
// Diakses dari multiple coroutines tanpa sinkronisasi
```

**Fix:** Tambah `@Volatile`.

---

### 1.4 SyncWorker Concurrent dengan startListening()

**Severity:** HIGH
**Files:** `SyncWorker.kt:17-27`, `SyncManager.kt:52-61`

**Problem:**
`startListening()` jalan tiap connectivity change (foreground), `SyncWorker` jalan tiap 15 menit (background). Keduanya bisa memanggil `pushPendingChanges()` + `pullRemoteChanges()` secara bersamaan.

**Fix:** Gunakan `Mutex` yang sama di SyncManager.

---

### 1.5 `SyncWorker` Unsafe Cast

**Severity:** HIGH
**File:** `SyncWorker.kt:18`

**Problem:**
```kotlin
val app = applicationContext as MediLabApp  // ClassCastException jika WorkManager pakai Application class berbeda
```

**Fix:**
```kotlin
val app = applicationContext as? MediLabApp ?: return
```

---

### 1.6 Room DAO Crash di Tab Dokter/Tes/Obat (Manage Screen)

**Severity:** HIGH (force close + crash loop)
**Files:** `ManageViewModel.kt:40-59`, `AppDatabase.kt:29`

**Problem:**
Tab Pasien & Petugas di Manage screen pakai `UserRepository` (Firestore langsung) → aman.
Tab Dokter/Tes/Obat pakai `localDokterRepo.getAll()` → `dao.getAll()` (Room Flow) → crash jika tabel `dokter`/`pemeriksaan`/`obat` tidak ada di database.

Ini terjadi ketika database dibuat sebelum Phase 2 (hanya punya tabel `users` + `laporan`), lalu versi mismatch tidak trigger `fallbackToDestructiveMigration()` — sehingga tabel baru tidak pernah dibuat.

**Dampak:** Force close saat klik tab Dokter/Tes/Obat. Navigation `saveState = true` menyebabkan crash loop pada restart. Clear data = satu-satunya jalan.

**Fix:**
1. Increment `AppDatabase` version 3→4 — memicu `fallbackToDestructiveMigration()` → semua tabel dibuat ulang
2. Tambah `.catch {}` di pipeline Flow — error Room tidak force close, tapi tampilkan `ErrorState` ke user

```kotlin
// AppDatabase.kt
version = 4  // trigger destructive migration

// ManageViewModel.kt — setiap stateIn flow
.catch { e -> emit(UiState.Error("Gagal memuat: ${e.message}")) }
```

**Verifikasi:** Klik tab Dokter/Tes/Obat — harus tampil data atau empty state, bukan force close.

---

## PRIORITAS 2 — LOGIC / FUNCTIONAL BUGS

### 2.1 `seedStaffAccounts()` Race dengan `pullRemoteChanges()`

**Severity:** MEDIUM
**File:** `MediLabApp.kt:59-64`

**Problem:**
```kotlin
scope.launch {
    if (database.userDao().count() == 0) {
        syncManager.pullRemoteChanges()  // async, return segera
        seedStaffAccounts()              // jalan tanpa tunggu pull selesai
    }
}
```

**Fix:** Pindah `seedStaffAccounts()` ke dalam callback pull.

---

### 2.2 Staff Home "Perlu Verifikasi" Tidak Role-Aware

**Severity:** MEDIUM
**File:** `StaffHomeViewModel.kt:55`

**Problem:**
Semua role (admin, petugas, dokter) lihat section "Perlu Verifikasi" yang isinya laporan status `verifikasi`. Tapi hanya admin yang bisa approve/reject. Petugas dan dokter melihat data yang tidak relevan.

| Role | Seharusnya melihat |
|---|---|
| Admin | Laporan status `verifikasi` |
| Petugas | Laporan status `baru` atau `revisi` |
| Dokter | (kosong — read-only) |

**Fix:** Filter `pendingVerifikasi` berdasarkan role user.

---

### 2.3 Staff Home Data Tidak Reaktif

**Severity:** MEDIUM
**File:** `StaffHomeViewModel.kt:39-66`

**Problem:**
`load()` dipanggil sekali via `.first()` pada Flow. Perubahan data di Room setelah `load()` tidak ter-reflect di UI sampai user navigasi keluar-masuk screen.

| ViewModel | Reaktif? |
|---|---|
| ManageViewModel (dokter/pemeriksaan/obat) | ✅ stateIn Flow |
| PatientRiwayatViewModel | ✅ stateIn Flow |
| StaffHomeViewModel | ❌ one-shot .first() |

**Fix:** Refactor ke `stateIn` seperti ManageViewModel.

---

### 2.4 Staff Home Screen — Bedakan Tampilan per Role

**Severity:** MEDIUM
**Files:** `StaffHomeViewModel.kt`, `StaffHomeScreen.kt`

**Problem:**
Saat ini 3 role (admin, petugas, dokter) lihat screen yang sama persis: 3 stat card (Baru/Proses/Selesai) + section "Perlu Verifikasi". Padahal:
- **Admin** perlu lihat laporan `verifikasi` untuk disetujui/direvisi (✅ cocok)
- **Petugas** perlu lihat laporan `baru`/`revisi` untuk diproses (❌ lihat verifikasi yang bukan wewenangnya)
- **Dokter** read-only — tidak perlu section action (❌ lihat data yang tidak relevan)

**Rencana perbaikan:**

#### Perubahan di `StaffHomeViewModel.kt`

Tambah data class:
```kotlin
data class StatCardData(val value: Int, val label: String, val icon: ImageVector)
```

Perluas `StaffHomeData`:
```kotlin
data class StaffHomeData(
    val user: UserEntity,
    val statCards: List<StatCardData>,    // ← dinamis per role
    val pendingItems: List<LaporanEntity>,
    val pendingTitle: String,             // ← dinamis per role
    val pendingEmptyText: String          // ← dinamis per role
)
```

Logic role-aware di `load()`:
```kotlin
val (statCards, pendingItems, pendingTitle, pendingEmptyText) = when (role) {
    Constants.ROLE_ADMIN -> {
        val pending = localLaporanRepo.getByStatus(STATUS_VERIFIKASI).first()
        listOf(
            StatCard(countBaru, "Baru", PostAdd),
            StatCard(countProses, "Proses", HourglassEmpty),
            StatCard(countSelesai, "Selesai", AssignmentTurnedIn)
        ) to (pending to ("Perlu Verifikasi" to "Tidak ada yang perlu diverifikasi"))
    }
    Constants.ROLE_PETUGAS -> {
        val baru = localLaporanRepo.getByStatus(STATUS_BARU).first()
        val revisi = localLaporanRepo.getByStatus(STATUS_REVISI).first()
        val pending = (baru + revisi).sortedByDescending { it.createdAt }.take(5)
        listOf(
            StatCard(countBaru, "Baru", PostAdd),
            StatCard(countProses, "Proses", HourglassEmpty),
            StatCard(countSelesai, "Selesai", AssignmentTurnedIn)
        ) to (pending to ("Perlu Diproses" to "Tidak ada yang perlu diproses"))
    }
    Constants.ROLE_DOKTER -> {
        val selesaiList = localLaporanRepo.getByStatus(STATUS_SELESAI).first()
        listOf(
            StatCard(countSelesai, "Selesai", AssignmentTurnedIn)
        ) to (selesaiList.sortedByDescending { it.createdAt }.take(5)
            to ("Laporan Terbaru" to "Belum ada laporan"))
    }
    else -> error("Unknown role")
}
```

#### Perubahan di `StaffHomeScreen.kt`

```kotlin
// Sebelum (hardcoded 3 stat card):
Row {
    StatCard(value = s.data.countBaru, label = "Baru", ...)
    StatCard(value = s.data.countProses, label = "Proses", ...)
    StatCard(value = s.data.countSelesai, label = "Selesai", ...)
}

// Sesudah (dinamis):
Row(horizontalArrangement = spacedBy(Spacing.md)) {
    s.data.statCards.forEach { card ->
        StatCard(
            value = card.value.toString(),
            label = card.label,
            icon = card.icon,
            modifier = Modifier.weight(1f)
        )
    }
}
```

Section header dan empty text juga dinamis:
```kotlin
item { SectionHeader(title = s.data.pendingTitle) }
if (s.data.pendingItems.isEmpty()) {
    item { Text(text = s.data.pendingEmptyText, ...) }
} else {
    items(s.data.pendingItems) { laporan ->
        LaporanCard(laporan = laporan, onClick = { onLaporanClick(laporan.id) })
    }
}
```

#### Tidak berubah
- `StaffRootScreen.kt` — tetap panggil `StaffHomeScreen()` tanpa passing role
- `StatusActionHelper.kt` — action buttons tetap di detail screen
- Room DAOs — query sudah tersedia

**Hasil akhir:**

| Role | Stat Card | Section Title | Isi Section |
|---|---|---|---|
| Admin | Baru, Proses, Selesai | Perlu Verifikasi | Laporan `verifikasi` |
| Petugas | Baru, Proses, Selesai | Perlu Diproses | Laporan `baru` + `revisi` |
| Dokter | Selesai | Laporan Terbaru | Laporan `selesai` |

---

### 2.5 Staff Home Error "Gagal memuat user" Setelah Seed Akun

**Severity:** HIGH (blocking login flow)
**File:** `StaffHomeViewModel.kt:47-52`

**Problem:**
```
1. Aplikasi first launch → Room kosong → pullRemoteChanges()
2. Firestore saat itu masih kosong → Room tetap kosong
3. Akun admin/petugas/dokter dibuat via REST API → masuk Firestore
4. Tapi Room TIDAK pernah di-refresh → data akun tidak ada di Room
5. Login sukses → StaffHomeViewModel cari user di Room → null → "Gagal memuat user"
```

Akar masalah: `pullRemoteChanges()` hanya jalan **sekali** saat `count() == 0`. Setelah akun dibuat di Firestore via REST API, Room tidak pernah sync ulang.

**Fix — Opsi A (fallback di ViewModel):**
`StaffHomeViewModel.load()` — saat user tidak ditemukan di Room, pull dari Firestore dulu, baru coba lagi:
```kotlin
var userEntity = localUserRepo.getById(uid).first()
if (userEntity == null) {
    syncManager.pullRemoteChanges()
    userEntity = localUserRepo.getById(uid).first()
}
if (userEntity == null) {
    _uiState.value = UiState.Error("Gagal memuat user")
    return@launch
}
```

**Fix — Opsi B (preventif di login flow):**
Di `AuthViewModel` atau `LoginScreen`, setelah login sukses, trigger `syncManager.pullRemoteChanges()` sebelum navigasi ke home. Data selalu fresh sebelum masuk screen.

```kotlin
// Di AuthViewModel, setelah login sukses:
syncManager.pullRemoteChanges()
// lalu navigasi ke StaffRoot / PatientRoot
```

**Fix — Opsi C (preventif di SplashScreen):**
SplashScreen sudah cek auth dan routing. Tambah pull di situ sebelum navigasi.

**Rekomendasi: Opsi A + B** — pull pas login (preventif) + pull pas ViewModel gagal (fallback).

---

### 2.6 CoroutineScope Leak di LocalRepositories

**Severity:** MEDIUM
**Files:** Semua `Local*Repository.kt` (9 file)

**Problem:**
```kotlin
class LocalPemeriksaanRepository(...) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    // scope ini tidak pernah di-cancel
}
```
Repository dibuat inline di ViewModel, scope tetap hidup setelah ViewModel di-clear.

**Fix:** Tambah parameter `scope: CoroutineScope` dan pakai `viewModelScope` dari caller:
```kotlin
class LocalPemeriksaanRepository(
    ...
    private val scope: CoroutineScope
)
```

---

### 2.7 `RegisterScreen` — Non-null Assertion

**Severity:** MEDIUM
**File:** `RegisterScreen.kt:131`

**Problem:**
```kotlin
state.errorMessage!!  // NPE jika errorMessage null
```

**Fix:**
```kotlin
state.errorMessage ?: "Terjadi kesalahan"
```

---

### 2.8 HorizontalPager Deprecated

**Severity:** MEDIUM
**File:** `OnboardingScreen.kt:26-28, 56, 81`

**Problem:**
Pakai `com.google.accompanist.pager.HorizontalPager` (deprecated). `rememberPagerState()` dipanggil tanpa `pageCount` parameter — crash di accompanist versi lebih baru.

**Fix:** Migrasi ke `androidx.compose.foundation.pager.Pager`.

---

### 2.9 SplashScreen — `popUpTo("0")` Route Tidak Ada

**Severity:** MEDIUM
**File:** `SplashScreen.kt:50`

**Problem:**
```kotlin
sharedNavigationViewModel.navigateTo(target, popUpTo = "0", inclusive = true)
```
Tidak ada route `"0"` di NavHost. `popUpTo("0")` adalah no-op — splash screen tetap di backstack. Setelah login masuk StaffRoot, tekan back → kembali ke SplashScreen.

Akar masalah: `popUpTo` pakai string route literal `"0"` bukan `Route.Splash.path`.

**Fix:**
```kotlin
sharedNavigationViewModel.navigateTo(target, popUpTo = Route.Splash.path, inclusive = true)
```

---

### 2.10 SplashScreen — `getUser()` Callback Lepas dari Lifecycle

**Severity:** LOW
**File:** `SplashScreen.kt:44`

**Problem:**
```kotlin
LaunchedEffect(Unit) {
    delay(2500)
    userRepo.getUser(uid) { user ->        // ← callback async
        navigateTo(...)                      // ← jalan di luar coroutine scope
    }
    // LaunchedEffect selesai di sini
}
```
`getUser()` callback-based, bukan coroutine. Callback bisa jalan setelah `LaunchedEffect` dibatalkan (misal navigasi sudah terjadi). `navigateTo()` di dalam callback tetap jalan via `viewModelScope` → navigasi ganda atau ke route salah.

**Fix:** Pindah ke coroutine dengan `suspendCancellableCoroutine`.

---

### 2.11 SplashScreen — Tidak Ada Loading Indicator Saat Auth Check

**Severity:** LOW
**File:** `SplashScreen.kt:37-52`

**Problem:**
`delay(2500)` hardcoded. Setelah itu Firestore query bisa lambat. User lihat layar statis tanpa feedback. Jika Firestore timeout, user tidak tahu sedang menunggu apa.

**Fix:** Tambah loading indicator setelah `delay` selesai. Atau ganti jadi polling dengan timeout.

---

### 2.12 `pushPendingChanges()` Crash di Background Coroutine (Role-dependent)

**Severity:** HIGH (force close spesifik role Petugas)
**Files:** `Local*Repository.kt` (upsert/delete methods), `SyncManager.kt`

**Problem:**
Saat Petugas menyimpan data di tab Obat (atau tab lain), `LocalObatRepository.upsert()` menjalankan coroutine:

```kotlin
scope.launch {
    dao.upsert(entity.copy(syncStatus = SyncStatus.PENDING_CREATE))
    syncManager.pushPendingChanges()  // ← exception di sini → force close
}
```

`pushPendingChanges()` memanggil SEMUA `pushPending*()` — termasuk `pushPendingUsers()`, `pushPendingLaporans()`, dll, yang melakukan Firestore write. Jika salah satu throw exception (misal Firestore permission denied yang tidak tertangani, `ConcurrentModificationException`, atau `IllegalStateException` dari coroutine), tidak ada `try/catch` atau `CoroutineExceptionHandler` → app crash.

**Mengapa role-dependent:**
- Dokter: tidak punya user pending records → `pushPendingUsers()` tidak jalan atau tidak crash
- Petugas: mungkin punya pending records sendiri → `pushPendingUsers()` jalan → Firestore write gagal karena security rules → exception tidak tertangani → crash

**Fix — Opsi A (minimal):** `try/catch` wrapper `syncManager.pushPendingChanges()` di semua `Local*Repository`:
```kotlin
scope.launch {
    try {
        dao.upsert(...)
        syncManager.pushPendingChanges()
    } catch (e: Exception) {
        android.util.Log.e("LocalRepo", "Sync error", e)
    }
}
```

**Fix — Opsi B (clean):** Tambah `CoroutineExceptionHandler` di scope constructor:
```kotlin
private val scope = CoroutineScope(
    SupervisorJob() + Dispatchers.IO +
    CoroutineExceptionHandler { _, e -> Log.e("LocalRepo", "Sync error", e) }
)
```

**Rekomendasi: Opsi A + B** — `try/catch` untuk immediate safety + handler untuk global safety net.

---

## PRIORITAS 3 — CLEANUP / CODE QUALITY

### 3.1 Dead Navigation Files

**Severity:** LOW
**Files:** `ui/navigation/AuthNav.kt`, `PatientNav.kt`, `StaffNav.kt`, `OnboardingNav.kt`

**Problem:** Berisi import doang, tidak dipakai di mana pun.

**Fix:** Hapus 4 file.

---

### 3.2 Hardcoded Strings

**Severity:** LOW
**Files:** Semua screen

**Problem:** Semua UI text hardcoded di composable, tidak pakai `strings.xml`.

**Fix:** Pindah ke resource string. (Nice-to-have, bukan blocker.)

---

### 3.3 `generateNoRM()` Collision Risk

**Severity:** LOW
**File:** `AuthRepository.kt:122-124`

**Problem:**
```kotlin
private fun generateNoRM(): String {
    val timestamp = System.currentTimeMillis() % 100000
    return "RM-$timestamp"
}
```
1/100000 collision chance per millisecond jika 2 pasien daftar bersamaan.

**Fix:** Tambah `UUID` atau counter.

---

### 3.4 Unused Imports

**Severity:** LOW
**File:** `PatientHomeScreen.kt:32-33`

**Problem:** Import `Laporan` dan `Notifikasi` dari model tidak dipakai.

**Fix:** Hapus import.

# Database Design — MediLab

**Teknologi:** Room (SQLite + WAL mode) sebagai source of truth lokal, Firebase Firestore sebagai remote backup/sync.

**File DB:** `/data/data/com.example.medilab/databases/medilab_database`

**Version:** 3 (`fallbackToDestructiveMigration()` untuk development)

---

## Arsitektur Aliran Data

```
UI (Composable)
  ↑ collectAsStateWithLifecycle()
ViewModel (StateFlow)
  ↑ .map { ... }.stateIn()
LocalRepository (Room-first wrapper)
  ↑ Flow<List<Entity>>
DAO (Room Query — SELECT, INSERT, UPDATE, DELETE)
  ↑
Room Database (9 tabel)
  ↕ SyncManager (push pending changes → Firestore, pull remote → Room)
Firebase Firestore
```

**Prinsip:** Semua read/write → Room. Firebase hanya sebagai remote backup sinkronisasi.

---

## ERD

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                          MEDILAB DATABASE (9 TABEL)                        │
└─────────────────────────────────────────────────────────────────────────────┘

┌──────────────┐       ┌──────────────────┐       ┌──────────────────┐
│    user      │       │   notifikasi     │       │   audit_log      │
├──────────────┤       ├──────────────────┤       ├──────────────────┤
│ PK id:String │──1:N──│ PK id:String     │       │ PK id:String     │
│ nama         │       │ FK userId        │       │ FK userId        │
│ email        │       │ judul            │       │ aksi             │
│ role         │       │ pesan            │       │ targetId         │
│ noHP         │       │ dibaca:Boolean   │       │ targetTipe       │
│ alamat       │       │ createdAt:Long   │       │ detail           │
│ fotoProfile  │       │ tipe             │       │ timestamp:Long   │
│ noRekamMedis │       └──────────────────┘       └──────────────────┘
│ tanggalLahir │
└──┬───┬───┬───┘
   │   │   │
   │   │   └──────────────────────────────────┐
   │   │                                      │
   │   │   ┌──────────────────┐               │
   │   └───│   rujukan       │               │
   │       ├──────────────────┤               │
   │   1:N │ PK id:String     │               │
   │       │ FK pasienId ─────┤               │
   │       │ FK dokterId ─────┼───────────────┘
   │       │ FK pemeriksaanId │
   │       │ catatan          │
   │       │ createdAt:Long   │
   │       │ status           │
   │       └────────┬─────────┘
   │                │ 1:N
   │                │
   │       ┌────────▼─────────┐
   │       │    laporan       │
   │       ├──────────────────┤
   ├──1:N──│ PK id:String     │
   │       │ FK pasienId      │
   │       │ FK dokterId      │
   │       │ FK petugasId     │
   │       │ FK adminId       │
   │       │ FK pemeriksaanId │──1:1──┐
   │       │ FK rujukanId     │──┐    │
   │       │ status           │  │    │
   │       │ hasilParameter   │  │    │
   │       │ diagnosa         │  │    │
   │       │ resepObat        │  │    │
   │       │ rumahSakit       │  │    │
   │       │ createdAt:Long   │  │    │
   │       │ updatedAt:Long   │  │    │
   │       │ tglSelesai:Long  │  │    │
   │       └──────────────────┘  │    │
   │                             │    │
   │       ┌──────────────────┐  │    │
   │       │  rekam_medis     │  │    │
   │       ├──────────────────┤  │    │
   ├──1:N──│ PK id:String     │  │    │
   │       │ FK pasienId      │  │    │
   │       │ FK laporanId ────┼──┼────┘
   │       │ diagnosa         │  │
   │       │ hasilRingkasan   │  │
   │       │ rumahSakit(JSON) │  │
   │       │ waktu:String     │  │
   │       │ createdAt:Long   │  │
   │       └──────────────────┘  │
   │                             │
   │       ┌──────────────────┐  │
   │       │  pemeriksaan     │  │
   │       ├──────────────────┤  │
   │   1:N │ PK id:String     │  │
   │       │ namaPemeriksaan  │  │
   │       │ kategori         │  │
   │       │ deskripsi        │  │
   │       │ parameter(JSON)  │  │
   │       └────────┬─────────┘  │
   │                │            │
   │                └──1:N───────┘
   │
   │       ┌──────────────────┐
   │       │   dokter         │
   │       ├──────────────────┤
   │       │ PK id:String     │
   │       │ nama             │
   │       │ spesialis        │
   │       │ alamat           │
   │       │ noHP             │
   │       │ email            │
   │       └──────────────────┘
   │
   │       ┌──────────────────┐
   │       │   obat           │
   │       ├──────────────────┤
   │       │ PK id:String     │
   │       │ namaObat         │
   │       │ bentuk           │
   │       │ dosis:Double     │
   │       │ satuan           │
   │       │ keterangan       │
   │       └──────────────────┘
```

---

## Detail 9 Tabel

### 1. `user` — Pengguna aplikasi (pasien, admin, petugas)

| Field | Tipe | Keterangan |
|---|---|---|
| `id` | String (PK) | Firebase UID |
| `nama` | String | Nama lengkap |
| `email` | String | Email login |
| `role` | String | `pasien`, `admin`, `petugas` |
| `noHP` | String | Nomor handphone |
| `alamat` | String | Alamat |
| `fotoProfile` | String | URL foto profil |
| `noRekamMedis` | String | Nomor rekam medis (untuk role pasien) |
| `tanggalLahir` | String | Tanggal lahir |
| `syncStatus` | SyncStatus | Status sinkronisasi |
| `lastModifiedAt` | Long | Timestamp perubahan terakhir |

### 2. `laporan` — Dokumen inti pemeriksaan

| Field | Tipe | Keterangan |
|---|---|---|
| `id` | String (PK) | |
| `status` | String | `diajukan`, `direvisi`, `disetujui`, `selesai`, `ditolak`, `batal` |
| `pasienId` | String | FK → user.id |
| `dokterId` | String | FK → user.id |
| `petugasId` | String | FK → user.id |
| `adminId` | String | FK → user.id |
| `pemeriksaanId` | String | FK → pemeriksaan.id |
| `rujukanId` | String | FK → rujukan.id |
| `hasilParameter` | JSON | `List<HasilParameter>` via TypeConverter |
| `diagnosa` | String | Diagnosa dari dokter |
| `resepObat` | JSON | `List<ResepItem>` via TypeConverter |
| `catatanRevisi` | String | Catatan revisi dari admin |
| `alasanTolak` | String | Alasan penolakan |
| `rumahSakit` | JSON | `RumahSakit` via TypeConverter |
| `createdAt` | Long | Timestamp dibuat |
| `updatedAt` | Long | Timestamp diupdate |
| `tanggalSelesai` | Long | Timestamp selesai |
| `syncStatus` | SyncStatus | Status sinkronisasi |
| `lastModifiedAt` | Long | Timestamp perubahan terakhir |

### 3. `notifikasi` — Notifikasi pengguna

| Field | Tipe | Keterangan |
|---|---|---|
| `id` | String (PK) | |
| `userId` | String | FK → user.id |
| `judul` | String | Judul notifikasi |
| `pesan` | String | Isi pesan |
| `dibaca` | Boolean | Status baca |
| `createdAt` | Long | Timestamp |
| `tipe` | String | Tipe notifikasi |
| `syncStatus` | SyncStatus | Status sinkronisasi |
| `lastModifiedAt` | Long | Timestamp perubahan terakhir |

### 4. `pemeriksaan` — Master data jenis pemeriksaan

| Field | Tipe | Keterangan |
|---|---|---|
| `id` | String (PK) | |
| `namaPemeriksaan` | String | Nama pemeriksaan |
| `kategori` | String | Kategori |
| `deskripsi` | String | Deskripsi |
| `parameter` | JSON | `List<ParameterTes>` via TypeConverter |
| `syncStatus` | SyncStatus | Status sinkronisasi |
| `lastModifiedAt` | Long | Timestamp perubahan terakhir |

### 5. `obat` — Master data obat

| Field | Tipe | Keterangan |
|---|---|---|
| `id` | String (PK) | |
| `namaObat` | String | Nama obat |
| `bentuk` | String | Tablet/Kapsul/Sirup/Injeksi |
| `dosis` | Double | Dosis |
| `satuan` | String | mg/ml |
| `keterangan` | String | Keterangan tambahan |
| `syncStatus` | SyncStatus | Status sinkronisasi |
| `lastModifiedAt` | Long | Timestamp perubahan terakhir |

### 6. `dokter` — Master data dokter

| Field | Tipe | Keterangan |
|---|---|---|
| `id` | String (PK) | |
| `nama` | String | Nama dokter |
| `spesialis` | String | Spesialisasi |
| `alamat` | String | Alamat |
| `noHP` | String | Nomor handphone |
| `email` | String | Email |
| `syncStatus` | SyncStatus | Status sinkronisasi |
| `lastModifiedAt` | Long | Timestamp perubahan terakhir |

### 7. `rekam_medis` — Riwayat rekam medis pasien

| Field | Tipe | Keterangan |
|---|---|---|
| `id` | String (PK) | |
| `pasienId` | String | FK → user.id |
| `laporanId` | String | FK → laporan.id |
| `diagnosa` | String | Diagnosa |
| `hasilRingkasan` | String | Ringkasan hasil |
| `rumahSakit` | JSON | `RumahSakit` via TypeConverter |
| `waktu` | String | Waktu pemeriksaan |
| `createdAt` | Long | Timestamp |
| `syncStatus` | SyncStatus | Status sinkronisasi |
| `lastModifiedAt` | Long | Timestamp perubahan terakhir |

### 8. `rujukan` — Data rujukan

| Field | Tipe | Keterangan |
|---|---|---|
| `id` | String (PK) | |
| `pasienId` | String | FK → user.id |
| `dokterId` | String | FK → user.id |
| `pemeriksaanId` | String | FK → pemeriksaan.id |
| `catatan` | String | Catatan rujukan |
| `createdAt` | Long | Timestamp |
| `status` | String | Default `diterima` |
| `syncStatus` | SyncStatus | Status sinkronisasi |
| `lastModifiedAt` | Long | Timestamp perubahan terakhir |

### 9. `audit_log` — Log aktivitas sistem

| Field | Tipe | Keterangan |
|---|---|---|
| `id` | String (PK) | |
| `userId` | String | FK → user.id |
| `aksi` | String | Aksi yang dilakukan |
| `targetId` | String | ID target |
| `targetTipe` | String | Tipe target |
| `detail` | String | Detail aksi |
| `timestamp` | Long | Timestamp |
| `syncStatus` | SyncStatus | Status sinkronisasi |
| `lastModifiedAt` | Long | Timestamp perubahan terakhir |

---

## Ringkasan Relasi

| Entity | Relasi | Entity Lain | Melalui Field |
|---|---|---|---|
| **user** | 1:N | notifikasi | `notifikasi.userId` → `user.id` |
| **user** | 1:N | audit_log | `audit_log.userId` → `user.id` |
| **user (pasien)** | 1:N | laporan | `laporan.pasienId` → `user.id` |
| **user (dokter)** | 1:N | laporan | `laporan.dokterId` → `user.id` |
| **user (petugas)** | 1:N | laporan | `laporan.petugasId` → `user.id` |
| **user (admin)** | 1:N | laporan | `laporan.adminId` → `user.id` |
| **user (pasien)** | 1:N | rekam_medis | `rekam_medis.pasienId` → `user.id` |
| **user (pasien)** | 1:N | rujukan | `rujukan.pasienId` → `user.id` |
| **user (dokter)** | 1:N | rujukan | `rujukan.dokterId` → `user.id` |
| **pemeriksaan** | 1:N | laporan | `laporan.pemeriksaanId` → `pemeriksaan.id` |
| **pemeriksaan** | 1:N | rujukan | `rujukan.pemeriksaanId` → `pemeriksaan.id` |
| **laporan** | 1:1 | rekam_medis | `rekam_medis.laporanId` → `laporan.id` |
| **rujukan** | 1:N | laporan | `laporan.rujukanId` → `rujukan.id` |

**Tabel mandiri (tanpa FK ke tabel lain):** `dokter`, `obat`

---

## SyncStatus — 2 Field Umum

Setiap entity memiliki 2 field ini:

| Field | Tipe | Nilai | Fungsi |
|---|---|---|---|
| `syncStatus` | `SyncStatus` (enum) | `SYNCED`, `PENDING_CREATE`, `PENDING_UPDATE`, `PENDING_DELETE` | Menandakan apakah data perlu disinkronkan ke Firestore |
| `lastModifiedAt` | `Long` | `System.currentTimeMillis()` | Timestamp untuk conflict resolution (last-write-wins) |

### Enum SyncStatus

```kotlin
enum class SyncStatus {
    SYNCED,            // Data sudah sinkron dengan Firestore
    PENDING_CREATE,    // Data baru perlu di-create ke Firestore
    PENDING_UPDATE,    // Data perlu di-update ke Firestore
    PENDING_DELETE     // Data perlu di-delete dari Firestore
}
```

---

## Sync Mechanism

```
┌───────────────────────────────────────────────────────────────┐
│                        SYNC FLOW                              │
├───────────────────────────────────────────────────────────────┤
│                                                               │
│  PUSH (saat ada perubahan lokal):                             │
│    Room write → set syncStatus = PENDING_*                    │
│    → SyncManager.pushPendingChanges()                         │
│      → untuk setiap entity pending:                           │
│        - PENDING_CREATE → Firestore.add()                     │
│        - PENDING_UPDATE → Firestore.update()                  │
│        - PENDING_DELETE → Firestore.delete()                  │
│        - sukses → set syncStatus = SYNCED                     │
│                                                               │
│  PULL (saat online / periodik):                               │
│    SyncManager.pullRemoteChanges()                            │
│      → Firestore.getAll() → Room.upsertAll(syncStatus=SYNCED) │
│                                                               │
│  TRIGGER:                                                     │
│    - NetworkMonitor: callback saat online → push + pull       │
│    - SyncWorker: WorkManager tiap 15 menit (periodic)         │
│    - Manual: dipanggil dari LocalRepository.upsert/delete     │
│                                                               │
│  CONFLICT RESOLUTION:                                         │
│    Last-write-wins by lastModifiedAt timestamp                │
│                                                               │
└───────────────────────────────────────────────────────────────┘
```

---

## Type Converters (Gson JSON)

Room menyimpan nested object sebagai JSON string via `@TypeConverter`:

| Type Asli | JSON | Digunakan di Entity |
|---|---|---|
| `List<HasilParameter>` | JSON Array | LaporanEntity |
| `List<ResepItem>` | JSON Array | LaporanEntity |
| `RumahSakit` | JSON Object | LaporanEntity, RekamMedisEntity |
| `List<ParameterTes>` | JSON Array | PemeriksaanEntity |
| `SyncStatus` | String (enum name) | Semua entity |

---

## ViewModels Ter-refactor ke Room Flow

| ViewModel | Koleksi | Sebelum | Sesudah |
|---|---|---|---|
| PatientHomeViewModel | User, Laporan, Notifikasi | Firebase callback | Room Flow |
| StaffLaporanViewModel | Laporan | Firebase callback | Room Flow |
| StaffNotifikasiViewModel | Notifikasi | Firebase callback | Room Flow |
| PatientNotifikasiViewModel | Notifikasi | Firebase callback | Room Flow |
| ManageViewModel | Dokter, Pemeriksaan, Obat | Firebase callback | Room Flow |
| PatientRiwayatViewModel | RekamMedis | Firebase langsung | Room Flow |

**Sisa (masih Firebase langsung):** LaporanDetailViewModel, StaffHomeViewModel, PatientHasilViewModel, dll.

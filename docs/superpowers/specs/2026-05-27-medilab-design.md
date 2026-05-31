# MediLab — Sistem Informasi Laboratorium Medis

**Tanggal:** 27 Mei 2026
**Platform:** Android (Kotlin) + Firebase
**Min SDK:** 30 | **Target SDK:** 36

---

## 1. Tujuan

Aplikasi Android yang terhubung dengan database untuk membantu admin dan petugas laboratorium dalam mengelola proses layanan Laporan Hasil Lab, serta memberikan transparansi progress layanan kepada pasien secara real-time.

## 2. Role Pengguna

| Role | Registrasi | Hak Akses |
|------|-----------|-----------|
| **Admin** | Dibuat oleh Admin lain | Manage petugas/dokter/pasien, verifikasi laporan final, oversight sistem |
| **Petugas Lab** | Dibuat oleh Admin | Verifikasi sampel, input hasil pemeriksaan, draft laporan |
| **Dokter** | Dibuat oleh Admin | Membuat rujukan, melihat hasil pasien |
| **Pasien** | Registrasi mandiri | Melihat hasil pemeriksaan, riwayat rekam medis, status progress |

Setiap data pengguna memiliki field **ID** sebagai penanda unik.

## 3. Arsitektur Aplikasi

### Pendekatan: Multi Activity per Role

Dua Activity terpisah untuk masing-masing role utama, dengan BottomNavigation masing-masing.

### Navigasi

```
Splash Screen
      │
      ▼
 AuthActivity (Login / Register Pasien)
      │
      ├── StaffActivity (Admin & Petugas Lab)
      │     ├── Dashboard
      │     ├── Manage Data (Pasien, Dokter, Petugas, Master Tes, Obat)
      │     ├── Laporan Hasil Lab (per status: Baru/Proses/Verifikasi/Revisi/Selesai/Ditolak/Batal)
      │     └── Profile
      │
      └── PatientActivity (Pasien)
            ├── Dashboard
            ├── Hasil Pemeriksaan
            ├── Riwayat Rekam Medis
            └── Profile
```

### Pola Arsitektur: MVVM + Repository

```
View (Activity/Fragment) → ViewModel → Repository → Firebase (Auth/Firestore/Storage)
```

## 4. Alur Proses Layanan (State Machine)

```
                    ┌─────────┐
                    │  BARU   │ ← Dokter buat rujukan
                    └────┬────┘
                         │ Petugas Lab verifikasi sampel & data
                    ┌────▼────┐
                    │ PROSES  │ ← Petugas Lab input hasil & diagnosa
                    └────┬────┘
                    ┌────▼──────┐
              ┌─────│VERIFIKASI │ ← Admin review
              │     └────┬──────┘
              │          │ Setuju?
              │     ┌────▼────┐
              │     │ SELESAI │ → Notif pasien, laporan terbit
              │     └─────────┘
              │
              │ Ditampilkan:
              ├── DITOLAK (dari BARU) — sampel tidak valid
              └── REVISI (dari VERIFIKASI) — admin minta perbaikan
                  → kembali ke PROSES → VERIFIKASI ulang

BATAL — kapan saja jika rujukan dibatalkan
```

Setiap transisi status mencatat: userId, role, timestamp, dan catatan.

## 5. Struktur Database (Firestore)

### `users/{userId}`
```json
{
  "id": "USR001",
  "nama": "Andi Pratama",
  "email": "andi@email.com",
  "role": "admin | petugas | dokter | pasien",
  "noHP": "0812xxxx",
  "alamat": "Jl. Contoh No. 1",
  "createdAt": Timestamp,
  "fotoProfile": "url (opsional)",
  "noRekamMedis": "RM-001 (khusus pasien)",
  "tanggalLahir": "1999-01-01 (khusus pasien)"
}
```

### `dokter/{dokterId}`
```json
{
  "id": "D001",
  "nama": "dr. Andi Setiawan",
  "spesialis": "Penyakit Dalam",
  "alamat": "Jl. Medis No. 5",
  "noHP": "0813xxxx",
  "email": "dr.andi@email.com"
}
```

### `pemeriksaan/{pemeriksaanId}`
```json
{
  "id": "PXS001",
  "namaPemeriksaan": "Hematologi Lengkap",
  "kategori": "Hematologi",
  "deskripsi": "Pemeriksaan darah lengkap",
  "parameter": [
    {
      "nama": "Hemoglobin",
      "satuan": "g/dL",
      "nilaiNormalMin": 12.0,
      "nilaiNormalMax": 16.0
    },
    {
      "nama": "Leukosit",
      "satuan": "/μL",
      "nilaiNormalMin": 4000,
      "nilaiNormalMax": 11000
    }
  ]
}
```

### `rujukan/{rujukanId}`
```json
{
  "id": "RJ001",
  "pasienId": "USR002",
  "dokterId": "D001",
  "pemeriksaanId": "PXS001",
  "catatan": "Periksa keluhan demam",
  "createdAt": Timestamp,
  "status": "diterima | diproses"
}
```

### `laporan/{laporanId}`
```json
{
  "id": "LP001",
  "status": "baru | proses | verifikasi | revisi | selesai | ditolak | batal",
  "pasienId": "USR002",
  "dokterId": "D001",
  "petugasId": "USR003",
  "adminId": "USR001 (verifikator)",
  "pemeriksaanId": "PXS001",
  "rujukanId": "RJ001",
  "hasilParameter": [
    {
      "parameterNama": "Hemoglobin",
      "nilai": 13.5,
      "satuan": "g/dL",
      "keterangan": "normal"
    }
  ],
  "diagnosa": "Infeksi bakteri ringan",
  "resepObat": [
    {
      "obatId": "OBT001",
      "namaObat": "Amoxicillin",
      "bentuk": "Tablet",
      "dosis": 500,
      "satuan": "mg",
      "aturanPakai": "3x1 sehari",
      "jumlah": 21,
      "keterangan": "Setelah makan"
    }
  ],
  "catatanRevisi": "Perbaiki nilai leukosit (opsional untuk status revisi)",
  "alasanTolak": "Sampel hemolisis (opsional untuk status ditolak)",
  "rumahSakit": {
    "nama": "RS MediLab Sehat",
    "alamat": "Jl. Kesehatan No. 1",
    "kota": "Jakarta"
  },
  "createdAt": Timestamp,
  "updatedAt": Timestamp,
  "tanggalSelesai": Timestamp
}
```

### `rekamMedis/{rekamMedisId}`
```json
{
  "id": "RM001",
  "pasienId": "USR002",
  "laporanId": "LP001",
  "diagnosa": "Infeksi bakteri ringan",
  "hasilRingkasan": "Hemoglobin normal, Leukosit meningkat",
  "rumahSakit": {
    "nama": "RS MediLab Sehat",
    "alamat": "Jl. Kesehatan No. 1",
    "kota": "Jakarta",
    "waktu": "27 Mei 2026, 14:30 WIB"
  },
  "createdAt": Timestamp
}
```

### `notifikasi/{notifikasiId}`
```json
{
  "id": "NOTIF001",
  "userId": "USR002",
  "judul": "Laporan Baru",
  "pesan": "Hasil pemeriksaan Anda sudah terbit",
  "dibaca": false,
  "createdAt": Timestamp,
  "tipe": "laporan_baru | progress"
}
```

### `obat/{obatId}`
```json
{
  "id": "OBT001",
  "namaObat": "Amoxicillin",
  "bentuk": "Tablet | Kapsul | Sirup | Injeksi",
  "dosis": 500,
  "satuan": "mg",
  "keterangan": "Antibiotik spektrum luas"
}
```

### `auditLog/{logId}`
```json
{
  "id": "LOG001",
  "userId": "USR003",
  "aksi": "buat | ubah | verifikasi | tolak | hapus",
  "targetId": "LP001",
  "targetTipe": "laporan | pasien | dll",
  "detail": "Input hasil parameter Hemoglobin: 13.5",
  "timestamp": Timestamp
}
```

## 6. Package Structure

```
com.example.medilab/
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
│   │   ├── dokter/
│   │   ├── petugas/
│   │   └── pemeriksaan/
│   ├── laporan/
│   │   ├── LaporanListFragment.kt
│   │   ├── LaporanDetailFragment.kt
│   │   └── LaporanViewModel.kt
│   └── profile/
├── patient/
│   ├── PatientActivity.kt
│   ├── dashboard/
│   ├── hasil/
│   ├── riwayat/
│   └── profile/
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

## 7. Daftar Halaman & Fitur

### AuthActivity
- Login (email/password) — untuk semua role
- Daftar sebagai Pasien — form registrasi mandiri
- Lupa password — Firebase password reset

### StaffActivity — Dashboard
- Kartu statistik: jumlah laporan Baru, Proses, Selesai
- Daftar verifikasi pending (untuk Admin)
- Notifikasi terbaru

### StaffActivity — Manage
- Tab: Pasien | Dokter | Petugas | Master Tes | Obat
- CRUD dengan form dialog
- Search & filter
- Setiap data memiliki ID unik

### StaffActivity — Laporan
- Tab per status: Baru | Proses | Verifikasi | Revisi | Selesai | Ditolak
- Aksi sesuai status: Terima/Tolak, Input Hasil, Kirim Verifikasi, Setujui/Revisi
- Detail laporan menampilkan: data pasien, dokter rujukan, parameter hasil, diagnosa, resep, info RS (nama, alamat, kota, waktu)

### StaffActivity — Profile
- Info akun (nama, ID, email, role)
- Edit profil
- Ganti password
- Logout

### PatientActivity — Dashboard
- Status laporan terbaru dengan progress bar
- Riwayat terakhir (3 item)

### PatientActivity — Hasil
- Detail laporan yang sudah selesai
- Parameter hasil dengan nilai normal & keterangan
- Diagnosa & resep
- Info RS (nama, alamat, kota, waktu)

### PatientActivity — Riwayat
- Timeline kronologis rekam medis
- Setiap item: tanggal, RS, pemeriksaan, dokter

### PatientActivity — Profile
- Sama seperti Staff Profile

## 8. Error Handling & Validasi

- Validasi form (email, password minimal 6 karakter, no HP)
- Firestore security rules — user hanya bisa akses data sesuai role
- Network error handling dengan retry & toast
- Loading state di setiap operasi Firestore
- Empty state untuk list kosong

## 9. Tech Stack

| Komponen | Teknologi |
|----------|-----------|
| Bahasa | Kotlin |
| UI | XML Layout + Material Design |
| Auth | Firebase Authentication |
| Database | Firebase Firestore |
| Storage | Firebase Storage |
| Arsitektur | MVVM + Repository |
| Dependency | ViewModel, LiveData/StateFlow, Firebase SDK |

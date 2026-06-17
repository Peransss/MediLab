# MediLab

A comprehensive medical laboratory management Android application built with **Kotlin** and **Jetpack Compose**, backed by **Firebase** for authentication, database, and storage.

## Features

### 👤 Patient
- **Dashboard** — overview of recent lab results and notifications
- **Lab Results** — view detailed test parameters and results
- **Medical History** — browse past medical records
- **Reports** — view detailed lab reports and referrals
- **Notifications** — real-time updates on new results and status changes
- **Profile** — manage personal information

### 🏥 Staff
- **Dashboard** — statistics, recent activity, and quick actions
- **Management** — tab-based CRUD for:
  - Patients, Doctors, Staff, Medicines, Examinations
- **Reports** — list, filter, update status (submitted → revised → approved → completed)
- **Notifications** — send and manage notifications
- **Profile** — manage staff profile

### ⚙️ General
- Role-based authentication (Patient / Admin / Staff)
- 3-step onboarding flow for first-time users
- Dark mode toggle (persisted via DataStore)
- Offline-first architecture with Room + Firebase Firestore sync
- Shimmer loading, empty states, and error states throughout

## Tech Stack

| Layer | Technology |
|---|---|
| **Language** | Kotlin 2.0.21 |
| **UI** | Jetpack Compose (BOM 2024.10.01) + Material 3 |
| **Architecture** | MVVM with Repository pattern |
| **Navigation** | Navigation Compose 2.8.4 |
| **Local DB** | Room (SQLite) with WAL mode |
| **Backend** | Firebase Auth, Firestore, Storage (BOM 33.12.0) |
| **State** | ViewModel + StateFlow + `collectAsStateWithLifecycle` |
| **Preferences** | DataStore Preferences (dark mode) |
| **Min SDK** | 30 (Android 11) |
| **Target SDK** | 36 |

## Screenshots

<!-- TODO: Add screenshots of key screens (onboarding, login, patient dashboard, staff management, dark mode) -->

## Project Structure

```
app/src/main/java/com/example/medilab/
├── MediLabApp.kt              # Application class
├── MainActivity.kt            # Single-activity Compose host
├── model/                     # Domain models
├── repository/                # Firebase-backed repositories
├── util/                      # Constants, DateUtils, Validators
└── ui/
    ├── theme/                 # Color, Type, Shape, Theme, DarkMode
    ├── component/             # Reusable Compose components
    ├── illustration/          # Custom Compose illustrations
    ├── navigation/            # Routes, AppNavHost, AuthNav, PatientNav, StaffNav
    └── screen/
        ├── SplashScreen.kt
        ├── onboarding/        # 3-step onboarding
        ├── auth/              # Login, Register, Forgot Password
        ├── patient/           # Patient screens (home, hasil, riwayat, laporan, notifikasi, profile)
        └── staff/             # Staff screens (home, manage, laporan, notifikasi, profile)
```

## Getting Started

### Prerequisites

- Android Studio Ladybug or later
- A Firebase project

### Firebase Setup

1. Go to [Firebase Console](https://console.firebase.google.com)
2. Create a new project (or use existing)
3. Register Android app with package name `com.example.medilab`
4. Download `google-services.json` and place it at `app/google-services.json`
5. Enable **Authentication** → Sign-in method → **Email/Password**
6. Create **Cloud Firestore** database (start in test mode for development)
7. Create **Storage** bucket

### Firestore Security Rules

Deploy via Firebase Console → Firestore → Rules:

```
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    function isRole(role) {
      return request.auth != null && get(/databases/$(database)/documents/users/$(request.auth.uid)).data.role == role;
    }

    match /users/{userId} {
      allow read: if request.auth != null && (request.auth.uid == userId || isRole('admin') || isRole('petugas'));
      allow create: if request.auth != null && (request.auth.uid == userId || isRole('admin'));
      allow update: if request.auth != null && (request.auth.uid == userId || isRole('admin'));
      allow delete: if request.auth != null && isRole('admin');
    }

    match /dokter/{dokterId} {
      allow read: if request.auth != null;
      allow write: if request.auth != null && (isRole('admin') || isRole('petugas'));
    }

    match /pemeriksaan/{pemeriksaanId} {
      allow read: if request.auth != null;
      allow write: if request.auth != null && (isRole('admin') || isRole('petugas'));
    }

    match /obat/{obatId} {
      allow read: if request.auth != null;
      allow write: if request.auth != null && (isRole('admin') || isRole('petugas'));
    }

    match /laporan/{laporanId} {
      allow read: if request.auth != null && (
        isRole('admin') || isRole('petugas') || resource.data.pasienId == request.auth.uid
      );
      allow write: if request.auth != null && (isRole('admin') || isRole('petugas'));
    }

    match /rekamMedis/{rmId} {
      allow read: if request.auth != null && (
        isRole('admin') || isRole('petugas') || resource.data.pasienId == request.auth.uid
      );
      allow write: if request.auth != null && (isRole('admin') || isRole('petugas'));
    }

    match /notifikasi/{notifId} {
      allow read: if request.auth != null && resource.data.userId == request.auth.uid;
      allow write: if request.auth != null && isRole('admin');
    }

    match /auditLog/{logId} {
      allow read: if request.auth != null && isRole('admin');
      allow write: if request.auth != null && (isRole('admin') || isRole('petugas'));
    }

    match /rujukan/{rujukanId} {
      allow read: if request.auth != null;
      allow write: if request.auth != null && (isRole('admin') || isRole('dokter') || isRole('petugas'));
    }
  }
}
```

### Storage Security Rules

Deploy via Firebase Console → Storage → Rules:

```
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

### Firestore Composite Indexes

Create these indexes in Firebase Console → Firestore → Indexes:

| Collection | Fields |
|---|---|
| `laporan` | `status` ASC, `createdAt` DESC |
| `laporan` | `pasienId` ASC, `createdAt` DESC |
| `laporan` | `pasienId` ASC, `status` ASC, `createdAt` DESC |
| `notifikasi` | `userId` ASC, `createdAt` DESC |
| `notifikasi` | `userId` ASC, `dibaca` ASC |

### Run the App

1. Clone the repository
2. Open the project in Android Studio
3. Place your `google-services.json` in the `app/` directory
4. Sync Gradle and run on a device or emulator (API 30+)

## Database Architecture

MediLab uses an **offline-first** approach with **Room** as the source of truth and **Firebase Firestore** as the remote sync layer.

```
UI (Composable)
  ↑ collectAsStateWithLifecycle()
ViewModel (StateFlow)
  ↑
Repository (Room-first wrapper)
  ↑
Room Database (9 tables)
  ↕ SyncManager (push ↔ Firestore)
Firebase Firestore
```

**9 Tables:** user, laporan (reports), pemeriksaan (examinations), obat (medicines), dokter (doctors), rekam_medis (medical records), rujukan (referrals), notifikasi (notifications), audit_log.

## Roles

| Role | Access |
|---|---|
| `pasien` (Patient) | View own results, history, reports, notifications |
| `petugas` (Staff) | Manage patients, doctors, medicines, examinations; process reports |
| `admin` | Full access including user management and audit logs |

## License

MIT

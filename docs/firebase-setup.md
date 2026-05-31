# Firebase Setup for MediLab

## 1. Firebase Console Setup

1. Go to https://console.firebase.google.com
2. Create new project "MediLab"
3. Register Android app with package name `com.example.medilab`
4. Download `google-services.json` and place at `app/google-services.json`
5. Enable **Authentication** → Sign-in method → Email/Password
6. Create **Cloud Firestore** database (start in test mode for development)
7. Create **Storage** bucket

## 2. Firestore Security Rules

Deploy these rules via Firebase Console → Firestore → Rules:

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

## 3. Storage Security Rules

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

# MediLab — UI/UX Review & Recommendations

## Design System Analysis (Healthcare)

| Aspek | Rekomendasi | MediLab Sekarang | Gap |
|-------|-------------|------------------|-----|
| Primary | #0891B2 (Teal) | #5B5FED (Indigo) | ❌ Indigo ≠ healthcare |
| Secondary | #22D3EE (Cyan) | #FF6B9D (Pink) | ❌ |
| CTA | #22C55E (Green) | #5B5FED (Primary) | ❌ CTA = Primary |
| Background | #F0FDFA (Soft teal) | #FAFAFE (White-ish) | ✅ OK |
| Text | #134E4A (Dark teal) | #0F172A (Slate-900) | ✅ OK |
| Heading Font | Figtree | Plus Jakarta Sans | ✅ OK (similar) |
| Body Font | Noto Sans | Inter | ✅ OK (Inter lebih baik) |
| Style | Accessible & Ethical | Material 3 | ✅ |
| Anti-pattern | ❌ Purple/pink gradients | ⚠️ Indigo + Pink | Flagged |

## Anti-pattern — Purple/Pink

Skill secara eksplisit menyatakan:

> **Avoid:** Bright neon colors, Motion-heavy animations, **AI purple/pink gradients**

Palette MediLab (`#5B5FED` Indigo + `#FF6B9D` Pink) masuk kategori ini.
Untuk healthcare, **Teal/Cyan + Green** adalah standar industri (trustworthy, healing, clean).

## WCAG Contrast Audit

| Token | FG | BG | Ratio | Status |
|-------|-----|-----|-------|--------|
| OnPrimary | White (#FFF) | Primary (#5B5FED) | 4.2:1 | ⚠️ Pass AA (3:1) |
| OnSurface | #0F172A | Surface #FFF | 18:1 | ✅ |
| OnSurfaceVariant | #475569 | Surface #FFF | 5.8:1 | ✅ |
| OnPrimaryContainer | #1A1A66 | PrimaryContainer #E8E8FF | 3.5:1 | ⚠️ Borderline AA text (4.5:1) |

## Remaining Issues

| Issue | Severity | Detail |
|-------|----------|--------|
| DarkMode 3 instance | Medium | VM terpisah di Activity + StaffRoot + PatientRoot |
| ROLE_DOKTER tanpa action | Medium | Dokter lihat tombol "Batalkan" saja — no feedback |
| validatePassword() dead code | Low | Fungsi didefinisikan tidak dipanggil |
| contentDescription kurang | Medium | Banyak Icon tanpa contentDescription di component |
| CTA = Primary, tidak ada CTA distinct | Low | Tombol aksi utama warna sama dengan tema |
| Unused dependencies | Low | viewpager2, navigation-fragment-ktx, dll masih ada |

## Recommendations

1. **Ganti palette ke teal/cyan** (sesuai standar healthcare) — optional, tergantung preference
2. **Tambah contentDescription** di semua Icon untuk aksesibilitas
3. **Fix DarkMode VM** — shared single instance di Activity level
4. **ROLE_DOKTER** — tambah aksi atau feedback jika tidak ada aksi tersedia
5. **Hapus dead code** — `validatePassword()` di AuthViewModel
6. **Hapus unused dependencies** di build.gradle.kts

## Pre-Delivery Checklist

- [ ] No emojis as icons → perlu verifikasi
- [ ] Hover states with transitions → Material 3 otomatis
- [ ] Light mode text contrast 4.5:1 → ✅ mostly OK
- [ ] Focus states visible → perlu cek custom component
- [ ] prefers-reduced-motion → perlu verifikasi
- [ ] Responsive 375-1440px → Android sudah responsive
- [ ] contentDescription di semua Icon → ❌ belum semua

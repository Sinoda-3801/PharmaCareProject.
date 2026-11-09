# PharmaCare - Sistem Manajemen Apotek (Java Console)

Tugas: COSC6047035 - Introduction to Programming for Business (Soal No. 3).
Aplikasi apotek digital dengan dua role: **Apoteker (Admin)** dan **Pelanggan**.

## Persyaratan
- JDK 11 atau lebih baru (`javac -version` untuk mengecek)

## Akun contoh
| Role | Username | Password |
|---|---|---|
| Apoteker | `apoteker` | `admin123` |
| Pelanggan | `budi` | `budi123` |

Pelanggan baru bisa mendaftar lewat menu "Daftar sebagai pelanggan".

## Struktur folder
```
PharmaCareProject/
├── src/pharmacare/
│   ├── app/       Main.java                      (menu login & registrasi)
│   ├── model/     Obat (abstract), ObatBebas, ObatBebasTerbatas, ObatKeras,
│   │              ItemKeranjang, Transaksi, StatusTransaksi (enum)
│   ├── payment/   Pembayaran (interface), Tunai, KartuDebitKredit, QRIS
│   ├── user/      User (abstract), Apoteker, Pelanggan
│   ├── service/   PharmaCareSystem                (data di memori & logika checkout)
│   └── util/      Util                            (input aman, format rupiah)
├── docs/          contoh_output.txt
├── screenshots/   tempat screenshot program berjalan
├── diagrams/      tempat file diagram nomor 4
└── README.md
```

## Penerapan konsep OOP
| Konsep | Penerapan |
|---|---|
| Abstract Class | `Obat`, `User` |
| Inheritance | `ObatBebas`/`ObatBebasTerbatas`/`ObatKeras` extends `Obat`; `Apoteker`/`Pelanggan` extends `User` |
| Polymorphism | `validasiPembelian()` per jenis obat; `hitungFee()` per metode bayar; `jalankanMenu()` per role |
| Interface | `Pembayaran` diimplementasikan `Tunai`, `KartuDebitKredit`, `QRIS` |
| Encapsulation | Atribut `private` + getter/setter tervalidasi; password tanpa getter |

## Fitur
**Apoteker:** CRUD obat (atribut khusus per jenis), stok menipis & restock, validasi resep,
penyelesaian pesanan, laporan harian/bulanan (total transaksi, obat terlaris, rincian metode bayar).

**Pelanggan:** lihat daftar obat, unggah resep (simulasi nama file), tambah ke keranjang dengan
validasi otomatis, checkout dengan pilihan pembayaran, riwayat transaksi beserta status.

## Aturan bisnis
- **Obat Bebas**: tanpa syarat khusus.
- **Obat Bebas Terbatas**: ada batas jumlah beli per transaksi dan peringatan penggunaan.
- **Obat Keras**: ditolak masuk keranjang tanpa resep; transaksi berstatus *Menunggu Validasi Resep*
  sampai apoteker menyetujui.
- **Fee pembayaran**: Tunai 0%, Kartu Debit/Kredit 2,5%, QRIS 0,7% dari subtotal.
- **Alur status**: Menunggu Validasi Resep -> Diproses -> Selesai (atau Ditolak, stok dikembalikan).
- Pembayaran hanya simulasi (dianggap sukses setelah metode dipilih).
- Data disimpan di memori (ArrayList/HashMap), hilang saat program ditutup.

## Skenario uji (lihat `docs/input_demo.txt`)
1. Pelanggan menambah Obat Keras tanpa resep -> ditolak.
2. Menambah CTM 3 unit (batas 2) -> ditolak.
3. Unggah resep, tambah Amoxicillin, checkout QRIS -> status Menunggu Validasi Resep.
4. Apoteker validasi resep -> Diproses -> Selesai.
5. Apoteker melihat stok menipis dan laporan penjualan.


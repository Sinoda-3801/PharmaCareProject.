package pharmacare.user;

import pharmacare.model.*;
import pharmacare.payment.*;
import pharmacare.service.*;
import pharmacare.util.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** INHERITANCE: Apoteker adalah User dengan hak kelola obat, stok, resep, dan laporan. */
public class Apoteker extends User {
    private static final int AMBANG_STOK_MENIPIS = 10;

    // Konstruktor meneruskan data akun ke induk
    public Apoteker(String username, String password, String nama) {
        super(username, password, nama);
    }

    @Override
    public String getRole() {
        return "Apoteker";
    }

    // Menu utama apoteker (override dari User)
    @Override
    public void jalankanMenu(PharmaCareSystem sistem) {
        boolean jalan = true;
        while (jalan) {
            Util.garis();
            System.out.println(" MENU APOTEKER - " + getNama());
            Util.garis();
            System.out.println(" 1. Kelola data obat");
            System.out.println(" 2. Stok menipis & restock");
            System.out.println(" 3. Validasi resep dokter");
            System.out.println(" 4. Selesaikan pesanan (Diproses -> Selesai)");
            System.out.println(" 5. Laporan penjualan");
            System.out.println(" 0. Logout");
            int pilih = Util.bacaInt("Pilih: ");
            switch (pilih) {
                case 1: kelolaObat(sistem); break;
                case 2: kelolaStok(sistem); break;
                case 3: validasiResep(sistem); break;
                case 4: selesaikanPesanan(sistem); break;
                case 5: laporan(sistem); break;
                case 0: jalan = false; break;
                default: System.out.println("  Pilihan tidak valid.");
            }
        }
    }

    // Submenu CRUD data obat
    private void kelolaObat(PharmaCareSystem sistem) {
        System.out.println("\n 1. Lihat semua  2. Tambah  3. Ubah  4. Hapus  0. Kembali");
        int p = Util.bacaInt("Pilih: ");
        switch (p) {
            case 1:
                for (Obat o : sistem.getDaftarObat()) System.out.println(o);
                break;
            case 2: tambahObat(sistem); break;
            case 3: ubahObat(sistem); break;
            case 4: hapusObat(sistem); break;
            default: break;
        }
    }

    // Menambah obat baru; objek dibuat sesuai jenis (atribut khusus berbeda tiap jenis)
    private void tambahObat(PharmaCareSystem sistem) {
        System.out.println(" Jenis: 1. Obat Bebas  2. Obat Bebas Terbatas  3. Obat Keras");
        int jenis = Util.bacaInt("Pilih jenis: ");
        if (jenis < 1 || jenis > 3) {
            System.out.println("  Jenis tidak valid.");
            return;
        }
        try {
            String nama = Util.bacaTeks("Nama: ");
            String desk = Util.bacaTeks("Deskripsi: ");
            double harga = Util.bacaDouble("Harga: ");
            int stok = Util.bacaInt("Stok awal: ");
            String id = sistem.buatIdObat();
            Obat baru;
            if (jenis == 1) {
                baru = new ObatBebas(id, nama, desk, harga, stok);
            } else if (jenis == 2) {
                int batas = Util.bacaInt("Batas maksimal beli per transaksi: ");
                String warn = Util.bacaTeks("Peringatan penggunaan: ");
                baru = new ObatBebasTerbatas(id, nama, desk, harga, stok, batas, warn);
            } else {
                baru = new ObatKeras(id, nama, desk, harga, stok);
            }
            sistem.getDaftarObat().add(baru);
            System.out.println("  Obat ditambahkan dengan ID " + id);
        } catch (IllegalArgumentException e) {
            System.out.println("  Gagal: " + e.getMessage());
        }
    }

    // Mengubah nama, harga, dan (jika Obat Bebas Terbatas) batas beli
    private void ubahObat(PharmaCareSystem sistem) {
        Obat o = sistem.cariObat(Util.bacaTeks("ID obat yang diubah: "));
        if (o == null) {
            System.out.println("  Obat tidak ditemukan.");
            return;
        }
        try {
            String nama = Util.bacaTeks("Nama baru (kosong = tetap): ");
            if (!nama.isEmpty()) o.setNama(nama);
            String harga = Util.bacaTeks("Harga baru (kosong = tetap): ");
            if (!harga.isEmpty()) o.setHarga(Double.parseDouble(harga));
            if (o instanceof ObatBebasTerbatas) {
                String batas = Util.bacaTeks("Batas beli baru (kosong = tetap): ");
                if (!batas.isEmpty()) ((ObatBebasTerbatas) o).setBatasBeli(Integer.parseInt(batas));
            }
            System.out.println("  Data obat diperbarui.");
        } catch (IllegalArgumentException e) {
            System.out.println("  Gagal: " + e.getMessage());
        }
    }

    // Menghapus obat dari daftar
    private void hapusObat(PharmaCareSystem sistem) {
        Obat o = sistem.cariObat(Util.bacaTeks("ID obat yang dihapus: "));
        if (o == null) {
            System.out.println("  Obat tidak ditemukan.");
            return;
        }
        sistem.getDaftarObat().remove(o);
        System.out.println("  " + o.getNama() + " dihapus.");
    }

    // Menampilkan obat dengan stok di bawah ambang, lalu menawarkan restock
    private void kelolaStok(PharmaCareSystem sistem) {
        System.out.println("\n--- STOK MENIPIS (< " + AMBANG_STOK_MENIPIS + ") ---");
        boolean ada = false;
        for (Obat o : sistem.getDaftarObat()) {
            if (o.getStok() < AMBANG_STOK_MENIPIS) {
                System.out.printf(" %-6s %-20s sisa %d%n", o.getId(), o.getNama(), o.getStok());
                ada = true;
            }
        }
        if (!ada) System.out.println(" Semua stok aman.");
        String id = Util.bacaTeks("ID obat untuk restock (kosong = lewati): ");
        if (id.isEmpty()) return;
        Obat o = sistem.cariObat(id);
        if (o == null) {
            System.out.println("  Obat tidak ditemukan.");
            return;
        }
        try {
            o.tambahStok(Util.bacaInt("Jumlah tambahan: "));
            System.out.println("  Stok " + o.getNama() + " sekarang " + o.getStok());
        } catch (IllegalArgumentException e) {
            System.out.println("  Gagal: " + e.getMessage());
        }
    }

    // Memvalidasi resep pada transaksi berstatus MENUNGGU_VALIDASI_RESEP
    private void validasiResep(PharmaCareSystem sistem) {
        List<Transaksi> menunggu = new ArrayList<>();
        for (Transaksi t : sistem.getDaftarTransaksi()) {
            if (t.getStatus() == StatusTransaksi.MENUNGGU_VALIDASI_RESEP) menunggu.add(t);
        }
        if (menunggu.isEmpty()) {
            System.out.println("  Tidak ada resep yang menunggu validasi.");
            return;
        }
        System.out.println("\n--- RESEP MENUNGGU VALIDASI ---");
        for (Transaksi t : menunggu) {
            t.cetak();
            System.out.println("  ------------------------------");
        }
        String id = Util.bacaTeks("ID transaksi yang divalidasi (kosong = batal): ");
        for (Transaksi t : menunggu) {
            if (t.getId().equalsIgnoreCase(id)) {
                String k = Util.bacaTeks("Resep sah? (y/n): ");
                if (k.equalsIgnoreCase("y")) {
                    t.setStatus(StatusTransaksi.DIPROSES);
                    System.out.println("  Resep disetujui. Status -> Diproses.");
                } else {
                    t.setStatus(StatusTransaksi.DITOLAK);
                    sistem.kembalikanStok(t);
                    System.out.println("  Resep ditolak. Stok dikembalikan.");
                }
                return;
            }
        }
        if (!id.isEmpty()) System.out.println("  ID transaksi tidak ada di daftar menunggu.");
    }

    // Menandai pesanan berstatus DIPROSES menjadi SELESAI
    private void selesaikanPesanan(PharmaCareSystem sistem) {
        boolean ada = false;
        for (Transaksi t : sistem.getDaftarTransaksi()) {
            if (t.getStatus() == StatusTransaksi.DIPROSES) {
                System.out.printf(" %s | %s | %s%n", t.getId(), t.getPelanggan().getNama(), Util.rupiah(t.getTotal()));
                ada = true;
            }
        }
        if (!ada) {
            System.out.println("  Tidak ada pesanan berstatus Diproses.");
            return;
        }
        String id = Util.bacaTeks("ID transaksi yang diselesaikan (kosong = batal): ");
        for (Transaksi t : sistem.getDaftarTransaksi()) {
            if (t.getId().equalsIgnoreCase(id) && t.getStatus() == StatusTransaksi.DIPROSES) {
                t.setStatus(StatusTransaksi.SELESAI);
                System.out.println("  Pesanan " + t.getId() + " selesai.");
                return;
            }
        }
        if (!id.isEmpty()) System.out.println("  ID tidak valid.");
    }

    // Laporan harian (yyyy-MM-dd) atau bulanan (yyyy-MM): total transaksi, terlaris, rincian pembayaran
    private void laporan(PharmaCareSystem sistem) {
        System.out.println("\nLaporan: ketik tanggal (yyyy-MM-dd) untuk harian atau bulan (yyyy-MM) untuk bulanan.");
        String periode = Util.bacaTeks("Periode (kosong = bulan ini): ");
        if (periode.isEmpty()) {
            periode = java.time.LocalDate.now().toString().substring(0, 7);
        }
        int jumlahTrx = 0;
        double omzet = 0;
        Map<String, Integer> terjual = new HashMap<>();
        Map<String, Integer> perMetode = new HashMap<>();
        Map<String, Double> nominalMetode = new HashMap<>();
        for (Transaksi t : sistem.getDaftarTransaksi()) {
            if (t.getStatus() == StatusTransaksi.DITOLAK) continue;
            if (!t.getTanggal().toString().startsWith(periode)) continue;
            jumlahTrx++;
            omzet += t.getTotal();
            for (ItemKeranjang it : t.getItems()) {
                terjual.merge(it.getObat().getNama(), it.getJumlah(), Integer::sum);
            }
            String m = t.getMetode().getNama();
            perMetode.merge(m, 1, Integer::sum);
            nominalMetode.merge(m, t.getTotal(), Double::sum);
        }
        System.out.println("\n--- LAPORAN PENJUALAN " + periode + " ---");
        System.out.println(" Total transaksi : " + jumlahTrx);
        System.out.println(" Total pendapatan: " + Util.rupiah(omzet));
        if (jumlahTrx == 0) return;
        String terlaris = null;
        for (Map.Entry<String, Integer> e : terjual.entrySet()) {
            if (terlaris == null || e.getValue() > terjual.get(terlaris)) terlaris = e.getKey();
        }
        System.out.println(" Obat terlaris   : " + terlaris + " (" + terjual.get(terlaris) + " unit)");
        System.out.println(" Rincian metode pembayaran:");
        for (String m : perMetode.keySet()) {
            System.out.printf("   - %-20s %d transaksi, %s%n", m, perMetode.get(m), Util.rupiah(nominalMetode.get(m)));
        }
    }
}

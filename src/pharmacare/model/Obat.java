package pharmacare.model;

import pharmacare.payment.*;
import pharmacare.user.*;
import pharmacare.service.*;
import pharmacare.util.*;

/**
 * ABSTRACT CLASS: induk semua jenis obat.
 * ENCAPSULATION: semua atribut private, diakses lewat getter/setter yang memvalidasi data.
 */
public abstract class Obat {
    private String id;
    private String nama;
    private String deskripsi;
    private double harga;
    private int stok;

    // Konstruktor: menyimpan data dasar obat lewat setter agar tervalidasi
    public Obat(String id, String nama, String deskripsi, double harga, int stok) {
        this.id = id;
        setNama(nama);
        setDeskripsi(deskripsi);
        setHarga(harga);
        setStok(stok);
    }

    // ---------- Getter & Setter (Encapsulation) ----------
    public String getId() { return id; }
    public String getNama() { return nama; }
    public String getDeskripsi() { return deskripsi; }
    public double getHarga() { return harga; }
    public int getStok() { return stok; }

    // Nama tidak boleh kosong
    public void setNama(String nama) {
        if (nama == null || nama.trim().isEmpty()) {
            throw new IllegalArgumentException("Nama obat tidak boleh kosong.");
        }
        this.nama = nama.trim();
    }

    // Deskripsi opsional
    public void setDeskripsi(String deskripsi) {
        this.deskripsi = (deskripsi == null) ? "" : deskripsi;
    }

    // Harga harus lebih dari 0
    public void setHarga(double harga) {
        if (harga <= 0) {
            throw new IllegalArgumentException("Harga harus lebih dari 0.");
        }
        this.harga = harga;
    }

    // Stok tidak boleh negatif
    public void setStok(int stok) {
        if (stok < 0) {
            throw new IllegalArgumentException("Stok tidak boleh negatif.");
        }
        this.stok = stok;
    }

    // Menambah stok (restock)
    public void tambahStok(int jumlah) {
        if (jumlah <= 0) {
            throw new IllegalArgumentException("Jumlah restock harus lebih dari 0.");
        }
        this.stok += jumlah;
    }

    // Mengurangi stok saat transaksi
    public void kurangiStok(int jumlah) {
        if (jumlah > stok) {
            throw new IllegalStateException("Stok " + nama + " tidak cukup.");
        }
        this.stok -= jumlah;
    }

    // Validasi umum (jumlah > 0 dan stok cukup); dipakai oleh subclass
    protected String validasiDasar(int jumlah) {
        if (jumlah <= 0) return "Jumlah harus lebih dari 0.";
        if (jumlah > stok) return "Stok tidak cukup (tersedia: " + stok + ").";
        return null;
    }

    // ---------- Method abstract: WAJIB di-override tiap jenis obat (Polymorphism) ----------

    // Nama jenis obat
    public abstract String getJenis();

    // Teks aturan penjualan yang ditampilkan ke pelanggan
    public abstract String getAturan();

    // Validasi pembelian. Return null jika valid, atau pesan error jika ditolak.
    // jumlahDiKeranjang = jumlah obat ini yang sudah ada di keranjang
    public abstract String validasiPembelian(int jumlahBaru, int jumlahDiKeranjang, boolean punyaResep);

    // Apakah obat ini mewajibkan resep dokter
    public boolean perluResep() {
        return false;
    }

    // Menampilkan ringkasan obat dalam satu baris
    @Override
    public String toString() {
        return String.format("%-6s | %-20s | %-18s | %12s | stok %3d | %s",
                id, nama, getJenis(), Util.rupiah(harga), stok, getAturan());
    }
}

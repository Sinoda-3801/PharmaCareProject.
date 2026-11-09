package pharmacare.service;

import pharmacare.model.*;
import pharmacare.payment.*;
import pharmacare.user.*;
import pharmacare.util.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Penyimpan data utama aplikasi (di memori): obat, user, dan transaksi.
 * Juga berisi logika checkout yang dipakai Pelanggan.
 */
public class PharmaCareSystem {
    private final List<Obat> daftarObat = new ArrayList<>();
    private final List<User> daftarUser = new ArrayList<>();
    private final List<Transaksi> daftarTransaksi = new ArrayList<>();
    private int counterObat = 0;
    private int counterTrx = 0;

    public List<Obat> getDaftarObat() { return daftarObat; }
    public List<Transaksi> getDaftarTransaksi() { return daftarTransaksi; }

    // Membuat ID obat otomatis: OB001, OB002, ...
    public String buatIdObat() {
        counterObat++;
        return String.format("OB%03d", counterObat);
    }

    // Mencari obat berdasarkan ID (tidak case-sensitive); null jika tidak ada
    public Obat cariObat(String id) {
        for (Obat o : daftarObat) {
            if (o.getId().equalsIgnoreCase(id)) return o;
        }
        return null;
    }

    // Mendaftarkan pelanggan baru; false jika username sudah dipakai
    public boolean daftarPelanggan(String username, String password, String nama) {
        for (User u : daftarUser) {
            if (u.getUsername().equalsIgnoreCase(username)) return false;
        }
        daftarUser.add(new Pelanggan(username, password, nama));
        return true;
    }

    // Login: mengembalikan objek User (Apoteker/Pelanggan) atau null
    public User login(String username, String password) {
        for (User u : daftarUser) {
            if (u.cekLogin(username, password)) return u;
        }
        return null;
    }

    /**
     * Proses checkout keranjang pelanggan.
     * - Validasi ulang tiap item (stok bisa berubah).
     * - Jika ada Obat Keras -> status MENUNGGU_VALIDASI_RESEP, selain itu DIPROSES.
     * - Stok langsung dikurangi; pembayaran diasumsikan sukses.
     */
    public Transaksi checkout(Pelanggan p, Pembayaran metode) {
        List<ItemKeranjang> items = new ArrayList<>(p.getKeranjang());
        boolean punyaResep = p.getResepFile() != null;
        for (ItemKeranjang it : items) {
            String err = it.getObat().validasiPembelian(0, it.getJumlah(), punyaResep);
            if (err != null) {
                System.out.println("  Checkout gagal pada " + it.getObat().getNama() + ": " + err);
                return null;
            }
        }
        boolean adaKeras = false;
        for (ItemKeranjang it : items) {
            it.getObat().kurangiStok(it.getJumlah());
            if (it.getObat().perluResep()) adaKeras = true;
        }
        counterTrx++;
        StatusTransaksi awal = adaKeras ? StatusTransaksi.MENUNGGU_VALIDASI_RESEP : StatusTransaksi.DIPROSES;
        Transaksi t = new Transaksi(String.format("TRX%03d", counterTrx), p, items, metode,
                adaKeras ? p.getResepFile() : null, LocalDate.now(), awal);
        daftarTransaksi.add(t);
        p.kosongkanKeranjang();
        if (adaKeras) p.setResepFile(null);
        return t;
    }

    // Mengembalikan stok bila transaksi ditolak (resep tidak valid)
    public void kembalikanStok(Transaksi t) {
        for (ItemKeranjang it : t.getItems()) {
            it.getObat().tambahStok(it.getJumlah());
        }
    }

    // Mengisi data awal (akun & obat contoh) agar program bisa langsung dicoba
    public void isiDataAwal() {
        daftarUser.add(new Apoteker("apoteker", "admin123", "Apt. Sari"));
        daftarUser.add(new Pelanggan("budi", "budi123", "Budi Santoso"));

        daftarObat.add(new ObatBebas(buatIdObat(), "Paracetamol 500mg", "Pereda demam & nyeri", 5000, 50));
        daftarObat.add(new ObatBebas(buatIdObat(), "Vitamin C 500mg", "Suplemen daya tahan tubuh", 12000, 8));
        daftarObat.add(new ObatBebasTerbatas(buatIdObat(), "Antasida Doen", "Meredakan sakit maag", 8000, 30,
                3, "Jangan melebihi dosis, hentikan bila gejala berlanjut"));
        daftarObat.add(new ObatBebasTerbatas(buatIdObat(), "CTM 4mg", "Antihistamin alergi", 3000, 40,
                2, "Dapat menyebabkan kantuk"));
        daftarObat.add(new ObatKeras(buatIdObat(), "Amoxicillin 500mg", "Antibiotik", 25000, 20));
        daftarObat.add(new ObatKeras(buatIdObat(), "Metformin 500mg", "Obat diabetes tipe 2", 18000, 5));
    }
}

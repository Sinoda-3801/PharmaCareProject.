package pharmacare.model;

import pharmacare.payment.*;
import pharmacare.user.*;
import pharmacare.service.*;
import pharmacare.util.*;

/** INHERITANCE: Obat Bebas Terbatas, ada batas jumlah beli dan peringatan penggunaan. */
public class ObatBebasTerbatas extends Obat {
    private int batasBeli;
    private String peringatan;

    // Konstruktor: selain data dasar, menyimpan batas beli & peringatan
    public ObatBebasTerbatas(String id, String nama, String deskripsi, double harga, int stok,
                             int batasBeli, String peringatan) {
        super(id, nama, deskripsi, harga, stok);
        setBatasBeli(batasBeli);
        this.peringatan = peringatan;
    }

    public int getBatasBeli() { return batasBeli; }
    public String getPeringatan() { return peringatan; }

    // Batas beli minimal 1
    public void setBatasBeli(int batasBeli) {
        if (batasBeli < 1) {
            throw new IllegalArgumentException("Batas beli minimal 1.");
        }
        this.batasBeli = batasBeli;
    }

    // Jenis obat
    @Override
    public String getJenis() {
        return "Obat Bebas Terbatas";
    }

    // Aturan penjualan: batas jumlah + peringatan
    @Override
    public String getAturan() {
        return "Maks " + batasBeli + " per transaksi. PERINGATAN: " + peringatan;
    }

    // Validasi: total di keranjang tidak boleh melebihi batas beli
    @Override
    public String validasiPembelian(int jumlahBaru, int jumlahDiKeranjang, boolean punyaResep) {
        int total = jumlahBaru + jumlahDiKeranjang;
        if (total > batasBeli) {
            return "Melebihi batas pembelian (maks " + batasBeli + ", di keranjang sudah "
                    + jumlahDiKeranjang + ").";
        }
        return validasiDasar(total);
    }
}

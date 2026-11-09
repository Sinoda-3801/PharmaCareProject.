package pharmacare.model;

import pharmacare.payment.*;
import pharmacare.user.*;
import pharmacare.service.*;
import pharmacare.util.*;

/** INHERITANCE: Obat Keras, hanya boleh dibeli dengan resep dokter yang sah. */
public class ObatKeras extends Obat {

    // Konstruktor meneruskan data ke kelas induk
    public ObatKeras(String id, String nama, String deskripsi, double harga, int stok) {
        super(id, nama, deskripsi, harga, stok);
    }

    // Jenis obat
    @Override
    public String getJenis() {
        return "Obat Keras";
    }

    // Aturan penjualan
    @Override
    public String getAturan() {
        return "WAJIB resep dokter";
    }

    // Obat Keras selalu memerlukan resep
    @Override
    public boolean perluResep() {
        return true;
    }

    // Validasi: tolak jika pelanggan belum melampirkan resep
    @Override
    public String validasiPembelian(int jumlahBaru, int jumlahDiKeranjang, boolean punyaResep) {
        if (!punyaResep) {
            return "Obat Keras wajib melampirkan resep dokter. Unggah resep terlebih dahulu.";
        }
        return validasiDasar(jumlahBaru + jumlahDiKeranjang);
    }
}

package pharmacare.model;

import pharmacare.payment.*;
import pharmacare.user.*;
import pharmacare.service.*;
import pharmacare.util.*;

/** INHERITANCE: Obat Bebas, dapat dibeli tanpa syarat khusus. */
public class ObatBebas extends Obat {

    // Konstruktor meneruskan data ke kelas induk
    public ObatBebas(String id, String nama, String deskripsi, double harga, int stok) {
        super(id, nama, deskripsi, harga, stok);
    }

    // Jenis obat
    @Override
    public String getJenis() {
        return "Obat Bebas";
    }

    // Aturan penjualan
    @Override
    public String getAturan() {
        return "Tanpa syarat khusus";
    }

    // Hanya cek jumlah dan stok
    @Override
    public String validasiPembelian(int jumlahBaru, int jumlahDiKeranjang, boolean punyaResep) {
        return validasiDasar(jumlahBaru + jumlahDiKeranjang);
    }
}

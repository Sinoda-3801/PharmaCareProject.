package pharmacare.model;

import pharmacare.payment.*;
import pharmacare.user.*;
import pharmacare.service.*;
import pharmacare.util.*;

/** Satu baris item di keranjang / transaksi. Menyimpan harga saat dibeli (snapshot). */
public class ItemKeranjang {
    private final Obat obat;
    private int jumlah;
    private final double hargaSatuan;

    // Konstruktor: mengunci harga saat item dimasukkan
    public ItemKeranjang(Obat obat, int jumlah) {
        this.obat = obat;
        this.jumlah = jumlah;
        this.hargaSatuan = obat.getHarga();
    }

    public Obat getObat() { return obat; }
    public int getJumlah() { return jumlah; }
    public double getHargaSatuan() { return hargaSatuan; }

    // Menambah jumlah item yang sama
    public void tambahJumlah(int tambahan) {
        this.jumlah += tambahan;
    }

    // Subtotal baris = harga x jumlah
    public double getSubtotal() {
        return hargaSatuan * jumlah;
    }
}

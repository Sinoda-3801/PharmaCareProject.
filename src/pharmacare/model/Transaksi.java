package pharmacare.model;

import pharmacare.payment.*;
import pharmacare.user.*;
import pharmacare.service.*;
import pharmacare.util.*;

import java.time.LocalDate;
import java.util.List;

/** Data satu transaksi pembelian beserta statusnya. */
public class Transaksi {
    private final String id;
    private final Pelanggan pelanggan;
    private final List<ItemKeranjang> items;
    private final Pembayaran metode;
    private final double subtotal;
    private final double fee;
    private final double total;
    private final LocalDate tanggal;
    private final String resepFile;
    private StatusTransaksi status;

    // Konstruktor: fee & total dihitung polimorfik lewat objek Pembayaran
    public Transaksi(String id, Pelanggan pelanggan, List<ItemKeranjang> items, Pembayaran metode,
                     String resepFile, LocalDate tanggal, StatusTransaksi statusAwal) {
        this.id = id;
        this.pelanggan = pelanggan;
        this.items = items;
        this.metode = metode;
        this.resepFile = resepFile;
        this.tanggal = tanggal;
        this.status = statusAwal;
        double sum = 0;
        for (ItemKeranjang it : items) sum += it.getSubtotal();
        this.subtotal = sum;
        this.fee = metode.hitungFee(sum);   // POLYMORPHISM: fee berbeda per metode
        this.total = sum + fee;
    }

    public String getId() { return id; }
    public Pelanggan getPelanggan() { return pelanggan; }
    public List<ItemKeranjang> getItems() { return items; }
    public Pembayaran getMetode() { return metode; }
    public double getSubtotal() { return subtotal; }
    public double getFee() { return fee; }
    public double getTotal() { return total; }
    public LocalDate getTanggal() { return tanggal; }
    public String getResepFile() { return resepFile; }
    public StatusTransaksi getStatus() { return status; }

    // Mengubah status transaksi
    public void setStatus(StatusTransaksi status) {
        this.status = status;
    }

    // Apakah transaksi memuat Obat Keras (butuh validasi resep)
    public boolean adaObatKeras() {
        for (ItemKeranjang it : items) {
            if (it.getObat().perluResep()) return true;
        }
        return false;
    }

    // Mencetak rincian transaksi
    public void cetak() {
        System.out.println("  ID        : " + id + "  (" + tanggal + ")");
        System.out.println("  Pelanggan : " + pelanggan.getNama());
        for (ItemKeranjang it : items) {
            System.out.printf("   - %-20s x%-3d %12s%n", it.getObat().getNama(), it.getJumlah(),
                    Util.rupiah(it.getSubtotal()));
        }
        System.out.println("  Subtotal  : " + Util.rupiah(subtotal));
        System.out.println("  Fee (" + metode.getNama() + "): " + Util.rupiah(fee));
        System.out.println("  TOTAL     : " + Util.rupiah(total));
        if (resepFile != null) System.out.println("  Resep     : " + resepFile);
        System.out.println("  Status    : " + status.getLabel());
    }
}

package pharmacare.model;

import pharmacare.payment.*;
import pharmacare.user.*;
import pharmacare.service.*;
import pharmacare.util.*;

/**
 * Enum status transaksi. Dipakai untuk melacak alur pesanan
 * (terutama alur validasi resep untuk Obat Keras).
 */
public enum StatusTransaksi {
    MENUNGGU_VALIDASI_RESEP("Menunggu Validasi Resep"),
    DIPROSES("Diproses"),
    SELESAI("Selesai"),
    DITOLAK("Ditolak (Resep Tidak Valid)");

    private final String label;

    StatusTransaksi(String label) {
        this.label = label;
    }

    // Mengembalikan teks status yang mudah dibaca pengguna
    public String getLabel() {
        return label;
    }
}

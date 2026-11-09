package pharmacare.payment;

import pharmacare.model.*;
import pharmacare.user.*;
import pharmacare.service.*;
import pharmacare.util.*;

/**
 * INTERFACE: kontrak untuk semua metode pembayaran.
 * Setiap metode wajib menentukan nama dan cara menghitung fee-nya sendiri.
 */
public interface Pembayaran {

    // Nama metode pembayaran (untuk ditampilkan & laporan)
    String getNama();

    // Menghitung biaya tambahan (fee/admin) dari subtotal belanja
    double hitungFee(double subtotal);

    // Menghitung total akhir = subtotal + fee (default method pada interface)
    default double hitungTotal(double subtotal) {
        return subtotal + hitungFee(subtotal);
    }

    // Simulasi proses pembayaran; diasumsikan selalu sukses (tanpa payment gateway)
    String bayar(double total);
}

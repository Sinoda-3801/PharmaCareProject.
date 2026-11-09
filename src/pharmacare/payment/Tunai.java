package pharmacare.payment;

import pharmacare.model.*;
import pharmacare.user.*;
import pharmacare.service.*;
import pharmacare.util.*;

/** Pembayaran Tunai: tidak ada biaya tambahan. */
public class Tunai implements Pembayaran {

    // Nama metode
    @Override
    public String getNama() {
        return "Tunai";
    }

    // Tunai bebas biaya admin
    @Override
    public double hitungFee(double subtotal) {
        return 0;
    }

    // Simulasi pembayaran tunai berhasil
    @Override
    public String bayar(double total) {
        return "Pembayaran TUNAI sebesar " + Util.rupiah(total) + " diterima di kasir. [BERHASIL]";
    }
}

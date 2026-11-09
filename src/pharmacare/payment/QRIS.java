package pharmacare.payment;

import pharmacare.model.*;
import pharmacare.user.*;
import pharmacare.service.*;
import pharmacare.util.*;

/** Pembayaran QRIS: fee 0,7% dari subtotal. */
public class QRIS implements Pembayaran {
    private static final double PERSEN_FEE = 0.007;

    // Nama metode
    @Override
    public String getNama() {
        return "QRIS";
    }

    // Fee QRIS = 0,7% dari subtotal
    @Override
    public double hitungFee(double subtotal) {
        return subtotal * PERSEN_FEE;
    }

    // Simulasi scan QR berhasil
    @Override
    public String bayar(double total) {
        return "Pembayaran QRIS sebesar " + Util.rupiah(total) + " terkonfirmasi. [BERHASIL]";
    }
}

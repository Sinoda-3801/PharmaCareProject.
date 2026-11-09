package pharmacare.payment;

import pharmacare.model.*;
import pharmacare.user.*;
import pharmacare.service.*;
import pharmacare.util.*;

/** Pembayaran Kartu Debit/Kredit: fee 2,5% dari subtotal. */
public class KartuDebitKredit implements Pembayaran {
    private static final double PERSEN_FEE = 0.025;

    // Nama metode
    @Override
    public String getNama() {
        return "Kartu Debit/Kredit";
    }

    // Fee kartu = 2,5% dari subtotal
    @Override
    public double hitungFee(double subtotal) {
        return subtotal * PERSEN_FEE;
    }

    // Simulasi gesek kartu berhasil
    @Override
    public String bayar(double total) {
        return "Pembayaran KARTU sebesar " + Util.rupiah(total) + " disetujui mesin EDC. [BERHASIL]";
    }
}

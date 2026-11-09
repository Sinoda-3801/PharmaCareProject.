package pharmacare.util;

import pharmacare.model.*;
import pharmacare.payment.*;
import pharmacare.user.*;
import pharmacare.service.*;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.Scanner;

/** Kumpulan fungsi bantu: input aman dan format rupiah. */
public class Util {
    private static final Scanner SC = new Scanner(System.in);

    // Memformat angka menjadi "Rp 12.500"
    public static String rupiah(double nilai) {
        NumberFormat nf = NumberFormat.getNumberInstance(Locale.forLanguageTag("id-ID"));
        nf.setMaximumFractionDigits(0);
        return "Rp " + nf.format(Math.round(nilai));
    }

    // Membaca satu baris teks
    public static String bacaTeks(String prompt) {
        System.out.print(prompt);
        return SC.hasNextLine() ? SC.nextLine().trim() : "";
    }

    // Membaca angka bulat; mengulang sampai input valid
    public static int bacaInt(String prompt) {
        while (true) {
            String s = bacaTeks(prompt);
            try {
                return Integer.parseInt(s);
            } catch (NumberFormatException e) {
                System.out.println("  Input harus berupa angka bulat.");
            }
        }
    }

    // Membaca angka desimal; mengulang sampai input valid
    public static double bacaDouble(String prompt) {
        while (true) {
            String s = bacaTeks(prompt);
            try {
                return Double.parseDouble(s);
            } catch (NumberFormatException e) {
                System.out.println("  Input harus berupa angka.");
            }
        }
    }

    // Mencetak garis pemisah
    public static void garis() {
        System.out.println("==================================================================");
    }
}

package pharmacare.app;

import pharmacare.model.*;
import pharmacare.payment.*;
import pharmacare.user.*;
import pharmacare.service.*;
import pharmacare.util.*;

/**
 * PharmaCare - Sistem Manajemen Apotek (console).
 * Akun contoh: apoteker/admin123 (Apoteker), budi/budi123 (Pelanggan).
 */
public class Main {

    // Titik masuk program: menu login / registrasi, lalu menjalankan menu sesuai role
    public static void main(String[] args) {
        PharmaCareSystem sistem = new PharmaCareSystem();
        sistem.isiDataAwal();

        boolean jalan = true;
        while (jalan) {
            Util.garis();
            System.out.println("        PHARMACARE - SISTEM APOTEK DIGITAL");
            Util.garis();
            System.out.println(" 1. Login");
            System.out.println(" 2. Daftar sebagai pelanggan");
            System.out.println(" 0. Keluar");
            int pilih = Util.bacaInt("Pilih: ");
            switch (pilih) {
                case 1: login(sistem); break;
                case 2: daftar(sistem); break;
                case 0: jalan = false; break;
                default: System.out.println("  Pilihan tidak valid.");
            }
        }
        System.out.println("Terima kasih telah menggunakan PharmaCare.");
    }

    // Proses login; menu yang tampil ditentukan polimorfik oleh tipe User
    private static void login(PharmaCareSystem sistem) {
        String u = Util.bacaTeks("Username: ");
        String p = Util.bacaTeks("Password: ");
        User user = sistem.login(u, p);
        if (user == null) {
            System.out.println("  Username atau password salah.");
            return;
        }
        System.out.println("  Selamat datang, " + user.getNama() + " (" + user.getRole() + ")");
        user.jalankanMenu(sistem);   // POLYMORPHISM
    }

    // Registrasi pelanggan baru
    private static void daftar(PharmaCareSystem sistem) {
        String nama = Util.bacaTeks("Nama lengkap: ");
        String u = Util.bacaTeks("Username: ");
        String p = Util.bacaTeks("Password: ");
        if (nama.isEmpty() || u.isEmpty() || p.isEmpty()) {
            System.out.println("  Semua kolom wajib diisi.");
            return;
        }
        if (sistem.daftarPelanggan(u, p, nama)) {
            System.out.println("  Registrasi berhasil. Silakan login.");
        } else {
            System.out.println("  Username sudah dipakai.");
        }
    }
}

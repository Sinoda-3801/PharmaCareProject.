package pharmacare.user;

import pharmacare.model.*;
import pharmacare.payment.*;
import pharmacare.service.*;
import pharmacare.util.*;

/**
 * ABSTRACT CLASS: induk bagi semua pengguna sistem (Apoteker & Pelanggan).
 * ENCAPSULATION: password disimpan private dan tidak punya getter.
 */
public abstract class User {
    private final String username;
    private final String password;
    private String nama;

    // Konstruktor data akun
    public User(String username, String password, String nama) {
        this.username = username;
        this.password = password;
        this.nama = nama;
    }

    public String getUsername() { return username; }
    public String getNama() { return nama; }
    public void setNama(String nama) { this.nama = nama; }

    // Mencocokkan kredensial login tanpa membuka password ke luar kelas
    public boolean cekLogin(String u, String p) {
        return username.equals(u) && password.equals(p);
    }

    // Nama role (Apoteker/Pelanggan)
    public abstract String getRole();

    // POLYMORPHISM: tiap role punya menu sendiri
    public abstract void jalankanMenu(PharmaCareSystem sistem);
}

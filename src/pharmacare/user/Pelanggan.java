package pharmacare.user;

import pharmacare.model.*;
import pharmacare.payment.*;
import pharmacare.service.*;
import pharmacare.util.*;

import java.util.ArrayList;
import java.util.List;

/** INHERITANCE: Pelanggan adalah User dengan keranjang, resep, dan riwayat belanja. */
public class Pelanggan extends User {
    private final List<ItemKeranjang> keranjang = new ArrayList<>();
    private String resepFile;   // nama file resep yang diunggah (null jika belum)

    // Konstruktor meneruskan data akun ke induk
    public Pelanggan(String username, String password, String nama) {
        super(username, password, nama);
    }

    public List<ItemKeranjang> getKeranjang() { return keranjang; }
    public String getResepFile() { return resepFile; }
    public void setResepFile(String resepFile) { this.resepFile = resepFile; }

    // Mengosongkan keranjang
    public void kosongkanKeranjang() {
        keranjang.clear();
    }

    @Override
    public String getRole() {
        return "Pelanggan";
    }

    // Menghitung jumlah obat tertentu yang sudah ada di keranjang
    private int jumlahDiKeranjang(Obat o) {
        for (ItemKeranjang it : keranjang) {
            if (it.getObat() == o) return it.getJumlah();
        }
        return 0;
    }

    // Menu utama pelanggan (override dari User)
    @Override
    public void jalankanMenu(PharmaCareSystem sistem) {
        boolean jalan = true;
        while (jalan) {
            Util.garis();
            System.out.println(" MENU PELANGGAN - " + getNama());
            Util.garis();
            System.out.println(" 1. Lihat daftar obat");
            System.out.println(" 2. Unggah resep dokter");
            System.out.println(" 3. Tambah obat ke keranjang");
            System.out.println(" 4. Lihat keranjang");
            System.out.println(" 5. Kosongkan keranjang");
            System.out.println(" 6. Checkout & bayar");
            System.out.println(" 7. Riwayat transaksi");
            System.out.println(" 0. Logout");
            int pilih = Util.bacaInt("Pilih: ");
            switch (pilih) {
                case 1: lihatObat(sistem); break;
                case 2: unggahResep(); break;
                case 3: tambahKeKeranjang(sistem); break;
                case 4: lihatKeranjang(); break;
                case 5: kosongkanKeranjang(); System.out.println("  Keranjang dikosongkan."); break;
                case 6: checkout(sistem); break;
                case 7: riwayat(sistem); break;
                case 0: jalan = false; break;
                default: System.out.println("  Pilihan tidak valid.");
            }
        }
    }

    // Menampilkan semua obat beserta jenis, harga, deskripsi, dan aturan beli
    private void lihatObat(PharmaCareSystem sistem) {
        System.out.println("\n--- DAFTAR OBAT ---");
        for (Obat o : sistem.getDaftarObat()) {
            System.out.println(o);
            System.out.println("         Deskripsi: " + o.getDeskripsi());
        }
    }

    // Simulasi unggah resep: pelanggan mengetik nama file resep
    private void unggahResep() {
        String f = Util.bacaTeks("Nama file resep (mis. resep_budi.jpg): ");
        if (f.isEmpty()) {
            System.out.println("  Nama file tidak boleh kosong.");
            return;
        }
        resepFile = f;
        System.out.println("  Resep '" + f + "' berhasil diunggah. Akan divalidasi apoteker saat checkout.");
    }

    // Menambah obat ke keranjang dengan validasi sesuai jenis (polimorfik)
    private void tambahKeKeranjang(PharmaCareSystem sistem) {
        String id = Util.bacaTeks("ID obat: ");
        Obat o = sistem.cariObat(id);
        if (o == null) {
            System.out.println("  Obat tidak ditemukan.");
            return;
        }
        int qty = Util.bacaInt("Jumlah: ");
        String err = o.validasiPembelian(qty, jumlahDiKeranjang(o), resepFile != null);
        if (err != null) {
            System.out.println("  DITOLAK: " + err);
            return;
        }
        boolean ada = false;
        for (ItemKeranjang it : keranjang) {
            if (it.getObat() == o) { it.tambahJumlah(qty); ada = true; }
        }
        if (!ada) keranjang.add(new ItemKeranjang(o, qty));
        System.out.println("  " + qty + " x " + o.getNama() + " ditambahkan ke keranjang.");
        if (o instanceof ObatBebasTerbatas) {
            System.out.println("  PERINGATAN: " + ((ObatBebasTerbatas) o).getPeringatan());
        }
    }

    // Menampilkan isi keranjang dan subtotalnya
    private void lihatKeranjang() {
        if (keranjang.isEmpty()) {
            System.out.println("  Keranjang kosong.");
            return;
        }
        System.out.println("\n--- KERANJANG ---");
        double sum = 0;
        for (ItemKeranjang it : keranjang) {
            System.out.printf(" %-20s x%-3d %12s%n", it.getObat().getNama(), it.getJumlah(),
                    Util.rupiah(it.getSubtotal()));
            sum += it.getSubtotal();
        }
        System.out.println(" Subtotal: " + Util.rupiah(sum));
        System.out.println(" Resep terunggah: " + (resepFile == null ? "(belum ada)" : resepFile));
    }

    // Checkout: pilih metode bayar (objek Pembayaran dipilih polimorfik), lalu proses
    private void checkout(PharmaCareSystem sistem) {
        if (keranjang.isEmpty()) {
            System.out.println("  Keranjang kosong.");
            return;
        }
        lihatKeranjang();
        Pembayaran[] metode = { new Tunai(), new KartuDebitKredit(), new QRIS() };
        double sub = 0;
        for (ItemKeranjang it : keranjang) sub += it.getSubtotal();
        System.out.println("\nPilih metode pembayaran:");
        for (int i = 0; i < metode.length; i++) {
            System.out.println(" " + (i + 1) + ". " + metode[i].getNama()
                    + "  -> total " + Util.rupiah(metode[i].hitungTotal(sub)));
        }
        int p = Util.bacaInt("Pilih (0 = batal): ");
        if (p < 1 || p > metode.length) {
            System.out.println("  Checkout dibatalkan.");
            return;
        }
        Pembayaran dipilih = metode[p - 1];
        Transaksi t = sistem.checkout(this, dipilih);
        if (t == null) return;
        System.out.println("\n  " + dipilih.bayar(t.getTotal()));
        System.out.println("\n--- STRUK ---");
        t.cetak();
        if (t.getStatus() == StatusTransaksi.MENUNGGU_VALIDASI_RESEP) {
            System.out.println("  Catatan: pesanan menunggu validasi resep oleh apoteker.");
        }
    }

    // Menampilkan riwayat transaksi milik pelanggan ini
    private void riwayat(PharmaCareSystem sistem) {
        System.out.println("\n--- RIWAYAT TRANSAKSI ---");
        boolean ada = false;
        for (Transaksi t : sistem.getDaftarTransaksi()) {
            if (t.getPelanggan() == this) {
                t.cetak();
                System.out.println("  ------------------------------");
                ada = true;
            }
        }
        if (!ada) System.out.println("  Belum ada transaksi.");
    }
}

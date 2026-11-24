import java.util.ArrayList;

interface Produk {
    String getJudul();
    String getPenulis();
    int getHarga();
    int getBeratGram();      // hanya masuk akal untuk buku cetak
    String linkUnduh();      // hanya masuk akal untuk buku digital & audio
    int getDurasiMenit();    // hanya masuk akal untuk buku audio
}

class Buku implements Produk {
    String jenis;            // "CETAK", "DIGITAL", atau "AUDIO"
    String judul;
    String penulis;
    int harga;
    int beratGram;
    double ukuranMB;
    int durasiMenit;

    Buku(String jenis, String judul, String penulis, int harga,
         int beratGram, double ukuranMB, int durasiMenit) {
        this.jenis = jenis;
        this.judul = judul;
        this.penulis = penulis;
        this.harga = harga;
        this.beratGram = beratGram;
        this.ukuranMB = ukuranMB;
        this.durasiMenit = durasiMenit;
    }

    public String getJudul() {
        return judul;
    }

    public String getPenulis() {
        return penulis;
    }

    public int getHarga() {
        return harga;
    }

    public int getBeratGram() {
        if (!jenis.equals("CETAK")) {
            throw new UnsupportedOperationException(judul + " tidak punya berat");
        }
        return beratGram;
    }

    public String linkUnduh() {
        String slug = judul.toLowerCase().replace(' ', '-');
        if (jenis.equals("DIGITAL")) {
            return "https://toko.buku/ebook/" + slug;
        } else if (jenis.equals("AUDIO")) {
            return "https://toko.buku/audio/" + slug;
        }
        throw new UnsupportedOperationException(judul + " tidak bisa diunduh");
    }

    public int getDurasiMenit() {
        if (!jenis.equals("AUDIO")) {
            throw new UnsupportedOperationException(judul + " bukan buku audio");
        }
        return durasiMenit;
    }
}

class EmailSender {
    void kirim(String alamat, String isi) {
        System.out.println("[EMAIL " + alamat + "] " + isi);
    }
}

class FileRiwayat {
    void simpan(String baris) {
        System.out.println("[FILE riwayat.txt] " + baris);
    }
}

// SRP: tugas memformat angka rupiah dipisah sendiri
class Rupiah {
    static String format(int angka) {
        return String.format("Rp%,d", angka).replace(',', '.');
    }
}

// SRP: hanya menyimpan isi keranjang
class Keranjang {
    private final ArrayList<Buku> isi = new ArrayList<>();

    void tambah(Buku buku) {
        isi.add(buku);
    }

    ArrayList<Buku> getIsi() {
        return isi;
    }

    int jumlah() {
        return isi.size();
    }
}

// SRP: hanya menghitung harga dan total bayar
class PerhitunganHarga {
    int hitungOngkir(Buku b) {
        int kelipatan = (b.getBeratGram() + 499) / 500;
        return kelipatan * 5000;
    }

    int hargaAkhir(Buku b) {
        switch (b.jenis) {
            case "CETAK":
                return b.getHarga() + hitungOngkir(b);
            case "DIGITAL":
                return b.getHarga() * 80 / 100;
            case "AUDIO":
                return b.getHarga() * 90 / 100;
            default:
                return b.getHarga();
        }
    }

    int totalBayar(String metode, int totalBelanja) {
        if (metode.equals("TUNAI")) {
            return totalBelanja;
        } else if (metode.equals("TRANSFER")) {
            return totalBelanja + 2500;
        } else if (metode.equals("EWALLET")) {
            return totalBelanja * 98 / 100;
        }
        return totalBelanja;
    }
}

// SRP: hanya mencetak struk ke layar
class StrukPrinter {
    private final PerhitunganHarga harga;

    StrukPrinter(PerhitunganHarga harga) {
        this.harga = harga;
    }

    String keterangan(Buku b) {
        switch (b.jenis) {
            case "CETAK":
                return b.getBeratGram() + " gram, ongkir " + Rupiah.format(harga.hitungOngkir(b));
            case "DIGITAL":
                return b.ukuranMB + " MB, diskon 20%";
            case "AUDIO":
                return b.getDurasiMenit() + " menit, diskon 10%";
            default:
                return "-";
        }
    }

    String namaMetode(String metode) {
        if (metode.equals("TUNAI")) {
            return "Tunai";
        } else if (metode.equals("TRANSFER")) {
            return "Transfer (+Rp2.500)";
        } else if (metode.equals("EWALLET")) {
            return "E-Wallet (diskon 2%)";
        }
        return "?";
    }

    void cetak(String pelanggan, Keranjang keranjang, String metode, int total, int bayar) {
        System.out.println("================ STRUK =================");
        System.out.println("Pelanggan : " + pelanggan);
        for (Buku b : keranjang.getIsi()) {
            System.out.println(b.getJudul() + " (" + b.getPenulis() + ")");
            System.out.println("  Harga       : " + Rupiah.format(b.getHarga()));
            System.out.println("  Keterangan  : " + keterangan(b));
            System.out.println("  Harga akhir : " + Rupiah.format(harga.hargaAkhir(b)));
        }
        System.out.println("-----------------------------------------");
        System.out.println("Total belanja : " + Rupiah.format(total));
        System.out.println("Metode bayar  : " + namaMetode(metode));
        System.out.println("Total bayar   : " + Rupiah.format(bayar));

        int totalBerat = 0;
        for (Buku b : keranjang.getIsi()) {
            if (b.jenis.equals("CETAK")) {
                totalBerat += b.getBeratGram();
            }
        }
        System.out.println("Berat kiriman : " + totalBerat + " gram");

        System.out.println("-------------- LINK UNDUH ---------------");
        for (Buku b : keranjang.getIsi()) {
            if (!b.jenis.equals("CETAK")) {
                System.out.println(b.linkUnduh());
            }
        }
        System.out.println("-----------------------------------------");
    }
}

// SRP: hanya mengatur urutan proses checkout
class ProsesCheckout {
    private final PerhitunganHarga harga = new PerhitunganHarga();
    private final StrukPrinter printer = new StrukPrinter(harga);
    private final EmailSender email = new EmailSender();
    private final FileRiwayat riwayat = new FileRiwayat();

    void proses(Keranjang keranjang, String pelanggan, String alamatEmail, String metode) {
        int total = 0;
        for (Buku b : keranjang.getIsi()) {
            total += harga.hargaAkhir(b);
        }
        int bayar = harga.totalBayar(metode, total);

        printer.cetak(pelanggan, keranjang, metode, total, bayar);

        riwayat.simpan(pelanggan + ";" + keranjang.jumlah() + " buku;" + Rupiah.format(bayar));
        email.kirim(alamatEmail, "Terima kasih " + pelanggan + ", total bayar " + Rupiah.format(bayar));
    }
}

public class TokoBukuKotor {
    public static void main(String[] args) {
        Keranjang keranjang = new Keranjang();
        keranjang.tambah(new Buku("CETAK", "Pemrograman Java", "John Doe", 120000, 750, 0, 0));
        keranjang.tambah(new Buku("CETAK", "Algoritma Dasar", "Jane Smith", 95000, 400, 0, 0));
        keranjang.tambah(new Buku("DIGITAL", "Belajar OOP", "Ani Wijaya", 80000, 0, 12.5, 0));
        keranjang.tambah(new Buku("AUDIO", "Kisah Programmer", "Budi Santoso", 60000, 0, 0, 240));

        ProsesCheckout checkout = new ProsesCheckout();
        checkout.proses(keranjang, "Sari", "sari@mail.com", "EWALLET");
    }
}
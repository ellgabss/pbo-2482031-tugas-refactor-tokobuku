import java.util.ArrayList;

interface Produk {
    String getJudul();
    String getPenulis();
    int getHarga();
    int getBeratGram();      // hanya masuk akal untuk buku cetak
    String linkUnduh();      // hanya masuk akal untuk buku digital & audio
    int getDurasiMenit();    // hanya masuk akal untuk buku audio
}

// OCP: harga akhir dan keterangan dihitung oleh tiap jenis buku sendiri
abstract class Buku implements Produk {
    final String jenis;      // masih dipakai sementara (dihapus di tahap LSP + ISP)
    final String judul;
    final String penulis;
    final int harga;

    Buku(String jenis, String judul, String penulis, int harga) {
        this.jenis = jenis;
        this.judul = judul;
        this.penulis = penulis;
        this.harga = harga;
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
        throw new UnsupportedOperationException(judul + " tidak punya berat");
    }

    public String linkUnduh() {
        throw new UnsupportedOperationException(judul + " tidak bisa diunduh");
    }

    public int getDurasiMenit() {
        throw new UnsupportedOperationException(judul + " bukan buku audio");
    }

    String slug() {
        return judul.toLowerCase().replace(' ', '-');
    }

    public abstract int hargaAkhir();

    public abstract String keterangan();
}

class BukuCetak extends Buku {
    private final int beratGram;

    BukuCetak(String judul, String penulis, int harga, int beratGram) {
        super("CETAK", judul, penulis, harga);
        this.beratGram = beratGram;
    }

    @Override
    public int getBeratGram() {
        return beratGram;
    }

    private int hitungOngkir() {
        int kelipatan = (beratGram + 499) / 500;
        return kelipatan * 5000;
    }

    @Override
    public int hargaAkhir() {
        return harga + hitungOngkir();
    }

    @Override
    public String keterangan() {
        return beratGram + " gram, ongkir " + Rupiah.format(hitungOngkir());
    }
}

class BukuDigital extends Buku {
    private final double ukuranMB;

    BukuDigital(String judul, String penulis, int harga, double ukuranMB) {
        super("DIGITAL", judul, penulis, harga);
        this.ukuranMB = ukuranMB;
    }

    @Override
    public String linkUnduh() {
        return "https://toko.buku/ebook/" + slug();
    }

    @Override
    public int hargaAkhir() {
        return harga * 80 / 100;
    }

    @Override
    public String keterangan() {
        return ukuranMB + " MB, diskon 20%";
    }
}

class BukuAudio extends Buku {
    private final int durasiMenit;

    BukuAudio(String judul, String penulis, int harga, int durasiMenit) {
        super("AUDIO", judul, penulis, harga);
        this.durasiMenit = durasiMenit;
    }

    @Override
    public String linkUnduh() {
        return "https://toko.buku/audio/" + slug();
    }

    @Override
    public int getDurasiMenit() {
        return durasiMenit;
    }

    @Override
    public int hargaAkhir() {
        return harga * 90 / 100;
    }

    @Override
    public String keterangan() {
        return durasiMenit + " menit, diskon 10%";
    }
}

// OCP: metode bayar baru cukup ditambah sebagai class baru
interface MetodeBayar {
    String nama();
    int totalBayar(int totalBelanja);
}

class Tunai implements MetodeBayar {
    @Override
    public String nama() {
        return "Tunai";
    }

    @Override
    public int totalBayar(int totalBelanja) {
        return totalBelanja;
    }
}

class Transfer implements MetodeBayar {
    @Override
    public String nama() {
        return "Transfer (+Rp2.500)";
    }

    @Override
    public int totalBayar(int totalBelanja) {
        return totalBelanja + 2500;
    }
}

class EWallet implements MetodeBayar {
    @Override
    public String nama() {
        return "E-Wallet (diskon 2%)";
    }

    @Override
    public int totalBayar(int totalBelanja) {
        return totalBelanja * 98 / 100;
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

class Rupiah {
    static String format(int angka) {
        return String.format("Rp%,d", angka).replace(',', '.');
    }
}

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

class StrukPrinter {
    void cetak(String pelanggan, Keranjang keranjang, MetodeBayar metode, int total, int bayar) {
        System.out.println("================ STRUK =================");
        System.out.println("Pelanggan : " + pelanggan);
        for (Buku b : keranjang.getIsi()) {
            System.out.println(b.getJudul() + " (" + b.getPenulis() + ")");
            System.out.println("  Harga       : " + Rupiah.format(b.getHarga()));
            System.out.println("  Keterangan  : " + b.keterangan());
            System.out.println("  Harga akhir : " + Rupiah.format(b.hargaAkhir()));
        }
        System.out.println("-----------------------------------------");
        System.out.println("Total belanja : " + Rupiah.format(total));
        System.out.println("Metode bayar  : " + metode.nama());
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

class ProsesCheckout {
    private final StrukPrinter printer = new StrukPrinter();
    private final EmailSender email = new EmailSender();
    private final FileRiwayat riwayat = new FileRiwayat();

    void proses(Keranjang keranjang, String pelanggan, String alamatEmail, MetodeBayar metode) {
        int total = 0;
        for (Buku b : keranjang.getIsi()) {
            total += b.hargaAkhir();
        }
        int bayar = metode.totalBayar(total);

        printer.cetak(pelanggan, keranjang, metode, total, bayar);

        riwayat.simpan(pelanggan + ";" + keranjang.jumlah() + " buku;" + Rupiah.format(bayar));
        email.kirim(alamatEmail, "Terima kasih " + pelanggan + ", total bayar " + Rupiah.format(bayar));
    }
}

public class TokoBukuKotor {
    public static void main(String[] args) {
        Keranjang keranjang = new Keranjang();
        keranjang.tambah(new BukuCetak("Pemrograman Java", "John Doe", 120000, 750));
        keranjang.tambah(new BukuCetak("Algoritma Dasar", "Jane Smith", 95000, 400));
        keranjang.tambah(new BukuDigital("Belajar OOP", "Ani Wijaya", 80000, 12.5));
        keranjang.tambah(new BukuAudio("Kisah Programmer", "Budi Santoso", 60000, 240));

        ProsesCheckout checkout = new ProsesCheckout();
        checkout.proses(keranjang, "Sari", "sari@mail.com", new EWallet());
    }
}
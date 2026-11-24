import java.util.ArrayList;

// ISP: Produk hanya berisi hal yang berlaku untuk SEMUA buku
interface Produk {
    String getJudul();
    String getPenulis();
    int getHarga();
    int hargaAkhir();
    String keterangan();
}

// ISP: role interface, hanya dimiliki buku yang bisa dikirim
interface BisaDikirim {
    int getBeratGram();
}

// ISP: role interface, hanya dimiliki buku yang bisa diunduh
interface BisaDiunduh {
    String linkUnduh();
}

// LSP: tidak ada lagi method yang melempar UnsupportedOperationException
abstract class Buku implements Produk {
    protected final String judul;
    protected final String penulis;
    protected final int harga;

    Buku(String judul, String penulis, int harga) {
        this.judul = judul;
        this.penulis = penulis;
        this.harga = harga;
    }

    @Override
    public String getJudul() {
        return judul;
    }

    @Override
    public String getPenulis() {
        return penulis;
    }

    @Override
    public int getHarga() {
        return harga;
    }

    protected String slug() {
        return judul.toLowerCase().replace(' ', '-');
    }
}

class BukuCetak extends Buku implements BisaDikirim {
    private final int beratGram;

    BukuCetak(String judul, String penulis, int harga, int beratGram) {
        super(judul, penulis, harga);
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

class BukuDigital extends Buku implements BisaDiunduh {
    private final double ukuranMB;

    BukuDigital(String judul, String penulis, int harga, double ukuranMB) {
        super(judul, penulis, harga);
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

class BukuAudio extends Buku implements BisaDiunduh {
    private final int durasiMenit;

    BukuAudio(String judul, String penulis, int harga, int durasiMenit) {
        super(judul, penulis, harga);
        this.durasiMenit = durasiMenit;
    }

    @Override
    public String linkUnduh() {
        return "https://toko.buku/audio/" + slug();
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

// DIP: abstraksi untuk penyimpan riwayat dan pengirim notifikasi
interface PenyimpanRiwayat {
    void simpan(String baris);
}

interface Notifikasi {
    void kirim(String tujuan, String isi);
}

class FileRiwayat implements PenyimpanRiwayat {
    @Override
    public void simpan(String baris) {
        System.out.println("[FILE riwayat.txt] " + baris);
    }
}

class EmailNotifikasi implements Notifikasi {
    @Override
    public void kirim(String tujuan, String isi) {
        System.out.println("[EMAIL " + tujuan + "] " + isi);
    }
}

class Rupiah {
    static String format(int angka) {
        return String.format("Rp%,d", angka).replace(',', '.');
    }
}

// Keranjang memisahkan buku menurut kemampuannya, tanpa instanceof
class Keranjang {
    private final ArrayList<Produk> isi = new ArrayList<>();
    private final ArrayList<BisaDikirim> dikirim = new ArrayList<>();
    private final ArrayList<BisaDiunduh> diunduh = new ArrayList<>();

    void tambah(BukuCetak buku) {
        isi.add(buku);
        dikirim.add(buku);
    }

    void tambah(BukuDigital buku) {
        isi.add(buku);
        diunduh.add(buku);
    }

    void tambah(BukuAudio buku) {
        isi.add(buku);
        diunduh.add(buku);
    }

    ArrayList<Produk> getIsi() {
        return isi;
    }

    ArrayList<BisaDiunduh> getDiunduh() {
        return diunduh;
    }

    int jumlah() {
        return isi.size();
    }

    int totalBerat() {
        int total = 0;
        for (BisaDikirim d : dikirim) {
            total += d.getBeratGram();
        }
        return total;
    }
}

class StrukPrinter {
    void cetak(String pelanggan, Keranjang keranjang, MetodeBayar metode, int total, int bayar) {
        System.out.println("================ STRUK =================");
        System.out.println("Pelanggan : " + pelanggan);
        for (Produk p : keranjang.getIsi()) {
            System.out.println(p.getJudul() + " (" + p.getPenulis() + ")");
            System.out.println("  Harga       : " + Rupiah.format(p.getHarga()));
            System.out.println("  Keterangan  : " + p.keterangan());
            System.out.println("  Harga akhir : " + Rupiah.format(p.hargaAkhir()));
        }
        System.out.println("-----------------------------------------");
        System.out.println("Total belanja : " + Rupiah.format(total));
        System.out.println("Metode bayar  : " + metode.nama());
        System.out.println("Total bayar   : " + Rupiah.format(bayar));

        System.out.println("Berat kiriman : " + keranjang.totalBerat() + " gram");

        System.out.println("-------------- LINK UNDUH ---------------");
        for (BisaDiunduh d : keranjang.getDiunduh()) {
            System.out.println(d.linkUnduh());
        }
        System.out.println("-----------------------------------------");
    }
}

// DIP: semua dependensi masuk lewat constructor, tidak ada "new" di sini
class ProsesCheckout {
    private final StrukPrinter printer;
    private final PenyimpanRiwayat riwayat;
    private final Notifikasi notifikasi;

    ProsesCheckout(StrukPrinter printer, PenyimpanRiwayat riwayat, Notifikasi notifikasi) {
        this.printer = printer;
        this.riwayat = riwayat;
        this.notifikasi = notifikasi;
    }

    void proses(Keranjang keranjang, String pelanggan, String alamatEmail, MetodeBayar metode) {
        int total = 0;
        for (Produk p : keranjang.getIsi()) {
            total += p.hargaAkhir();
        }
        int bayar = metode.totalBayar(total);

        printer.cetak(pelanggan, keranjang, metode, total, bayar);

        riwayat.simpan(pelanggan + ";" + keranjang.jumlah() + " buku;" + Rupiah.format(bayar));
        notifikasi.kirim(alamatEmail, "Terima kasih " + pelanggan + ", total bayar " + Rupiah.format(bayar));
    }
}

public class TokoBukuKotor {
    public static void main(String[] args) {
        Keranjang keranjang = new Keranjang();
        keranjang.tambah(new BukuCetak("Pemrograman Java", "John Doe", 120000, 750));
        keranjang.tambah(new BukuCetak("Algoritma Dasar", "Jane Smith", 95000, 400));
        keranjang.tambah(new BukuDigital("Belajar OOP", "Ani Wijaya", 80000, 12.5));
        keranjang.tambah(new BukuAudio("Kisah Programmer", "Budi Santoso", 60000, 240));

        // DIP: main adalah satu-satunya tempat yang menyusun (new) object-object ini
        StrukPrinter printer = new StrukPrinter();
        PenyimpanRiwayat riwayat = new FileRiwayat();
        Notifikasi notifikasi = new EmailNotifikasi();
        ProsesCheckout checkout = new ProsesCheckout(printer, riwayat, notifikasi);

        MetodeBayar metode = new EWallet();
        checkout.proses(keranjang, "Sari", "sari@mail.com", metode);
    }
}
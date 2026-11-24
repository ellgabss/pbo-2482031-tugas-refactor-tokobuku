public class TokoBukuSolid {
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
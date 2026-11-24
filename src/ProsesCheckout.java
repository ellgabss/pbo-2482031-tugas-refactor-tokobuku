// DIP: semua dependensi masuk lewat constructor, tidak ada "new" di sini
public class ProsesCheckout {
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
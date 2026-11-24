public class StrukPrinter {
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
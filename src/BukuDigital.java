public class BukuDigital extends Buku implements BisaDiunduh {
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
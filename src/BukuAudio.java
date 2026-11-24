public class BukuAudio extends Buku implements BisaDiunduh {
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
public class BukuCetak extends Buku implements BisaDikirim {
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
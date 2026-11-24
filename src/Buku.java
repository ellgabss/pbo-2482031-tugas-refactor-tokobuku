// LSP: semua turunan Buku bisa dipakai di mana pun Buku dipakai, tanpa kejutan
public abstract class Buku implements Produk {
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
// ISP: Produk hanya berisi hal yang berlaku untuk SEMUA buku
public interface Produk {
    String getJudul();
    String getPenulis();
    int getHarga();
    int hargaAkhir();
    String keterangan();
}
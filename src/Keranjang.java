import java.util.ArrayList;

// Keranjang memisahkan buku menurut kemampuannya (role interface)
public class Keranjang {
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
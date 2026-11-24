**Laporan Refactor Toko Buku**
Nama :Ellen G. Serumena
NIM  :2482031

Berkas awal (TokoBukuKotor.java) berjalan dengan benar tapi melanggar kelima prinsip SOLID. 
Hasil refactor menghasilkan keluaran yang sama persis dengan berkas awal (TokoBukuKotor.java). 
Berikut penjelasan untuk setiap prinsip SOLIDnya.

**S — Single Responsibility Principle**
(a) Kode awal yang melanggar
class Toko {
ArrayList<Buku> keranjang = new ArrayList<>();
EmailSender email = new EmailSender();
FileRiwayat riwayat = new FileRiwayat();
// ...
int hitungOngkir(Buku b) { ... }
int hargaAkhir(Buku b) { ... }
String namaMetode(String metode) { ... }
void checkout(String pelanggan, String alamatEmail, String metode) {
System.out.println("================ STRUK =================");
// ... hitung total, cetak struk, simpan riwayat, kirim email
}
String formatRupiah(int angka) { ... }
}

(b) Masalahnya
Class Toko mengerjakan banyak hal sekaligus: 
menyimpan isi keranjang, menghitung harga dan ongkir, memformat angka rupiah, 
mencetak struk, menyimpan riwayat, dan mengirim email. 
Kalau format struk berubah, aturan ongkir berubah, atau cara kirim email berubah, 
semuanya harus mengubah class yang sama. 
Satu class punya banyak alasan untuk berubah.

(c) Perbaikan
Tiap tugas diberi class sendiri: 
Keranjang (menyimpan isi keranjang), StrukPrinter (mencetak struk), 
ProsesCheckout (mengatur urutan checkout), Rupiah (memformat angka), 
serta perhitungan harga yang dipindahkan ke Buku dan turunannya dan perhitungan total bayar ke MetodeBayar.

**O — Open/Closed Principle**
(a) Kode awal yang melanggar
int hargaAkhir(Buku b) {
switch (b.jenis) {
case "CETAK":   return b.getHarga() + hitungOngkir(b);
case "DIGITAL": return b.getHarga() * 80 / 100;
case "AUDIO":   return b.getHarga() * 90 / 100;
default:        return b.getHarga();
}
}

int totalBayar(String metode, int totalBelanja) {
if (metode.equals("TUNAI")) { ... }
else if (metode.equals("TRANSFER")) { ... }
else if (metode.equals("EWALLET")) { ... }
return totalBelanja;
}

(b) Masalahnya
Setiap kali ada jenis buku baru atau metode bayar baru, 
kita harus membuka Toko dan menambah case atau else if di beberapa method (hargaAkhir, keterangan, namaMetode, totalBayar). 
Kalau satu tempat terlewat, program salah. Class seharusnya bisa ditambah fiturnya tanpa dibongkar.

(c) Perbaikan
Aturan harga dan keterangan dipindahkan ke tiap jenis buku: 
Buku (abstract) dengan turunan BukuCetak, BukuDigital, BukuAudio, 
masing-masing mengisi hargaAkhir() dan keterangan(). 
Metode bayar dibuat interface MetodeBayar dengan implementasi Tunai, Transfer, dan EWallet. 
Jenis atau metode baru cukup berupa class baru, tanpa mengubah kode yang sudah ada.

**L — Liskov Substitution Principle**
(a) Kode awal yang melanggar
public int getBeratGram() {
if (!jenis.equals("CETAK")) {
throw new UnsupportedOperationException(judul + " tidak punya berat");
}
return beratGram;
}
(linkUnduh() dan getDurasiMenit() bermasalah serupa.)

(b) Masalahnya
Buku berjanji lewat interface Produk bahwa semua buku bisa ditanya beratnya, 
tetapi buku digital dan audio malah melempar exception. 
Kode yang memakai Produk terlihat aman, tetapi bisa meledak saat program berjalan. 
Artinya objek Buku tidak bisa dipakai sebagai pengganti Produk secara aman. 
Untuk menghindarinya, kode awal terpaksa memeriksa b.jenis sebelum memanggil method.

(c) Perbaikan
Method yang tidak berlaku untuk semua buku dikeluarkan dari Produk dan Buku. 
Berat hanya ada di BukuCetak, link unduh hanya ada di BukuDigital dan BukuAudio. 
Tidak ada lagi UnsupportedOperationException dan tidak ada lagi field jenis.

**I — Interface Segregation Principle**
(a) Kode awal yang melanggar
interface Produk {
String getJudul();
String getPenulis();
int getHarga();
int getBeratGram();      // hanya masuk akal untuk buku cetak
String linkUnduh();      // hanya masuk akal untuk buku digital & audio
int getDurasiMenit();    // hanya masuk akal untuk buku audio
}

(b) Masalahnya
Satu interface gemuk memaksa semua buku menanggung method yang bukan miliknya. 
Buku digital dipaksa punya getBeratGram(), buku cetak dipaksa punya linkUnduh(). 
Pengguna interface juga terpaksa tahu hal yang tidak relevan.

(c) Perbaikan
Interface dipecah menurut kemampuan:
- Produk hanya berisi hal umum (judul, penulis, harga, harga akhir, keterangan).
- BisaDikirim (getBeratGram()) dimiliki BukuCetak saja.
- BisaDiunduh (linkUnduh()) dimiliki BukuDigital dan BukuAudio.
Keranjang menyimpan daftar BisaDikirim dan BisaDiunduh sehingga 
total berat dan daftar link dihitung tanpa instanceof dan tanpa memeriksa jenis. 
Durasi dan ukuran file tidak butuh interface karena hanya dipakai di dalam class masing-masing.

**D — Dependency Inversion Principle**
(a) Kode awal yang melanggar
class Toko {
EmailSender email = new EmailSender();
FileRiwayat riwayat = new FileRiwayat();
// ...
}

(b) Masalahnya
Class tingkat atas (Toko) membuat sendiri class tingkat bawah yang konkret (EmailSender, FileRiwayat) dengan new. 
Akibatnya Toko terikat pada email dan file. Mengganti email dengan SMS, atau file dengan database, 
berarti mengubah Toko. Class ini juga sulit diuji karena tidak bisa diberi pengganti.

(c) Perbaikan
Dibuat interface Notifikasi (diimplementasi EmailNotifikasi) dan PenyimpanRiwayat (diimplementasi FileRiwayat). 
ProsesCheckout menerima StrukPrinter, PenyimpanRiwayat, dan Notifikasi lewat constructor (constructor injection) 
dan hanya mengenal interface-nya. Satu-satunya tempat yang memanggil new untuk menyusun objek-objek ini adalah main di TokoBukuSolid.

**Bagian yang tidak perlu diberi abstraksi (YAGNI / KISS)**
Tidak semua hal perlu dijadikan interface. 
Rupiah cukup berupa class dengan satu method static, 
karena aturan format rupiah hanya satu dan tidak punya data atau variasi. 
StrukPrinter dan Keranjang juga tetap class biasa tanpa interface, 
karena belum ada kebutuhan format struk lain atau keranjang jenis lain, 
dan interface hanya menambah berkas tanpa manfaat sekarang. 
Perhitungan ongkir juga dibiarkan sebagai method private di dalam BukuCetak, 
tanpa class atau strategi tersendiri, karena hanya buku cetak yang memakainya. 
Saya juga tidak membuat factory atau registry untuk metode bayar. 
Abstraksi baru layak ditambahkan ketika benar-benar muncul kebutuhan kedua, 
bukan karena "mungkin nanti perlu".
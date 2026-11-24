public class EmailNotifikasi implements Notifikasi {
    @Override
    public void kirim(String tujuan, String isi) {
        System.out.println("[EMAIL " + tujuan + "] " + isi);
    }
}
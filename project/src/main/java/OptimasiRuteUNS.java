import org.graphstream.graph.*;
import org.graphstream.graph.implementations.*;
import org.graphstream.ui.view.Viewer;

import java.util.*;

//inisialisasi node
class Halte {
    String nama;
    List<Jalur> jalurTerhubung;
    
    public Halte(String nama) {
        this.nama = nama;
        this.jalurTerhubung = new ArrayList<>();
    }
    
    public void tambahJalur(Halte tujuan, int jarak) {
        jalurTerhubung.add(new Jalur(tujuan, jarak));
    }
}

//inisialisasi edge
class Jalur {
    Halte tujuan;
    int jarak;
    
    public Jalur(Halte tujuan, int jarak) {
        this.tujuan = tujuan;
        this.jarak = jarak;
    }
}

//class untuk menyimpan semua rute
class RuteOptimal {
    List<RuteInfo> semuaRute = new ArrayList<>();
    
    public void tambahRute(int jarak, List<Halte> jalur) {
        semuaRute.add(new RuteInfo(jarak, jalur));
    }
    
    public void sortByJarak() {
        semuaRute.sort(Comparator.comparingInt(r -> r.totalJarak));
    }
    
    static class RuteInfo {
        int totalJarak;
        List<Halte> jalur;
        
        public RuteInfo(int jarak, List<Halte> jalur) {
            this.totalJarak = jarak;
            this.jalur = new ArrayList<>(jalur);
        }
    }
}

public class OptimasiRuteUNS {
    private static List<Halte> semuaHalte = new ArrayList<>();
    
    public static void main(String[] args) {
        // Inisialisasi semua halte
        Halte rektorat = new Halte("Rektorat");
        Halte kopma = new Halte("Kopma");
        Halte fib = new Halte("FIB");
        Halte ft = new Halte("FT");
        Halte feb = new Halte("Pertigaan FEB");
        Halte gersam = new Halte("Gerbang FISIP");
        Halte pfh = new Halte("Pertigaan FH");
        Halte pasca = new Halte("Perempatan Pasca");
        Halte fmipa = new Halte("FMIPA");
        Halte fp = new Halte("FP");
        Halte fk = new Halte("FK");
        Halte fkip = new Halte("FKIP");
        Halte fisip = new Halte("Gedung fisip");
        Halte fh = new Halte("Gedung FH");
      
        // Simpan semua halte dalam list
        semuaHalte.add(rektorat);
        semuaHalte.add(kopma);
        semuaHalte.add(fib);
        semuaHalte.add(ft);
        semuaHalte.add(feb);
        semuaHalte.add(gersam);
        semuaHalte.add(pfh);
        semuaHalte.add(pasca);
        semuaHalte.add(fmipa);
        semuaHalte.add(fp);
        semuaHalte.add(fk);
        semuaHalte.add(fkip);
        semuaHalte.add(fisip);
        semuaHalte.add(fh);
      
        // Menambahkan jalur satu arah antar halte
        rektorat.tambahJalur(kopma, 130);
        kopma.tambahJalur(fib, 130);
        kopma.tambahJalur(ft, 350);
        fib.tambahJalur(fh, 130);
        ft.tambahJalur(feb, 350);
        feb.tambahJalur(fisip, 270);
        pfh.tambahJalur(pasca, 120);
        pasca.tambahJalur(fmipa, 220);
        fmipa.tambahJalur(fp, 200);
        fp.tambahJalur(rektorat, 120);
        pasca.tambahJalur(fk, 270);
        fk.tambahJalur(fmipa, 400);
        gersam.tambahJalur(fkip, 80);
        fkip.tambahJalur(pfh, 180);
        fisip.tambahJalur(gersam, 50);
        kopma.tambahJalur(feb, 240);
        feb.tambahJalur(fh, 180);
        fh.tambahJalur(pfh, 90);
       
        // Temukan semua rute sirkuler
        RuteOptimal semuaRute = cariRuteSirkuler(rektorat);
        
        // Output hasil
        if (!semuaRute.semuaRute.isEmpty()) {
            System.out.println("Ditemukan " + semuaRute.semuaRute.size() + " rute sirkuler:");
            System.out.println("------------------------------------------------");
            
            for (int i = 0; i < semuaRute.semuaRute.size(); i++) {
                RuteOptimal.RuteInfo r = semuaRute.semuaRute.get(i);
                System.out.println("Rute " + (i+1) + " (Jarak: " + r.totalJarak + " meter)");
                System.out.print("Rute: ");
                for (int j = 0; j < r.jalur.size(); j++) {
                    System.out.print(r.jalur.get(j).nama);
                    if (j < r.jalur.size() - 1) {
                        System.out.print(" -> ");
                    }
                }
                System.out.println("\n------------------------------------------------");
            }

            // Output rute paling optimal
            if (!semuaRute.semuaRute.isEmpty()) {
                RuteOptimal.RuteInfo ruteOptimal = semuaRute.semuaRute.get(0);
                System.out.println("\n=== RUTE PALING OPTIMAL ===");
                System.out.println("Jarak total: " + ruteOptimal.totalJarak + " meter");
                System.out.print("Rute: ");
                for (int j = 0; j < ruteOptimal.jalur.size(); j++) {
                    System.out.print(ruteOptimal.jalur.get(j).nama);
                    if (j < ruteOptimal.jalur.size() - 1) {
                        System.out.print(" -> ");
                    }
                }
                System.out.println("\n==========================");
            }

            // Visualisasi graf dengan rute terbaik
            RuteOptimal.RuteInfo ruteTerbaik = semuaRute.semuaRute.get(0);
            visualisasiGraf(rektorat, ruteTerbaik);
        } else {
            System.out.println("Tidak ditemukan rute sirkuler yang memenuhi syarat");
        }
    }
    
    public static void visualisasiGraf(Halte awal, RuteOptimal.RuteInfo ruteOptimal) {
        System.setProperty("org.graphstream.ui", "swing");
        Graph graph = new SingleGraph("Rute Optimal UNS - Jarak: " + ruteOptimal.totalJarak + "m");
        
        // Set style untuk graf
        graph.setAttribute("ui.stylesheet", 
            "node { " +
            "   fill-color: cyan; " +
            "   size: 20px; " +
            "   text-alignment: center; " +
            "   text-style: bold; " +
            "   text-size: 15px; " +
            "} " +
            "edge { " +
            "   fill-color: darkgrey; " +
            "   arrow-size: 8px, 4px; " +
            "   text-size: 10px; " +
            "   text-alignment: along; " +
            "   size: 8px; "+
            "} " +
            "edge.highlighted { " +
            "   fill-color: red; " +
            "   size: 8px; " +
            "} " +
            "node.start { " +
            "   fill-color: green; " +
            "   size: 20px; " +
            "}" +
            "graph { " +
            "   padding: 20px; " +
            "}"
        );

        // Tambahkan node keterangan di pojok graf
        Node legendNode = graph.addNode("legend");
        legendNode.setAttribute("ui.label", "Rute merah adalah rute optimal dengan jarak(" + ruteOptimal.totalJarak + "m)");
        legendNode.setAttribute("xyz", -4, -4, 0); // Posisi di pojok kiri bawah
        legendNode.setAttribute("ui.style", "fill-color: white; text-color: black; size: 20px;");
        
        // Definisi koordinat untuk setiap halte berdasarkan peta yang diberikan
        Map<String, double[]> koordinatHalte = new HashMap<>();
        
        // Koordinat berdasarkan posisi pada peta (skala disesuaikan untuk visualisasi)
        koordinatHalte.put("Rektorat", new double[]{3, -2});
        koordinatHalte.put("Kopma", new double[]{0, 0});
        koordinatHalte.put("FIB", new double[]{0, 1.75});
        koordinatHalte.put("FT", new double[]{-3, -1});
        koordinatHalte.put("Pertigaan FEB", new double[]{-2, 2.75});
        koordinatHalte.put("Gerbang FISIP", new double[]{-1, 5.5});
        koordinatHalte.put("Pertigaan FH", new double[]{2, 3.5});
        koordinatHalte.put("Perempatan Pasca", new double[]{5, 3.5});
        koordinatHalte.put("FMIPA", new double[]{5.2, 1});
        koordinatHalte.put("FP", new double[]{4.8, -1});
        koordinatHalte.put("FK", new double[]{7, 3});
        koordinatHalte.put("FKIP", new double[]{1, 5});
        koordinatHalte.put("Gedung fisip", new double[]{-2, 5});
        koordinatHalte.put("Gedung FH", new double[]{0.8, 2.75});
        
        // Tambahkan semua node dengan koordinat tetap
        for (Halte h : semuaHalte) {
            Node node = graph.addNode(h.nama);
            node.setAttribute("ui.label", h.nama);
            
            // Set koordinat tetap untuk node
            double[] coords = koordinatHalte.get(h.nama);
            if (coords != null) {
                node.setAttribute("xyz", coords[0], coords[1], 0);
                node.setAttribute("x", coords[0]);
                node.setAttribute("y", coords[1]);
                node.setAttribute("z", 0.0);
            }
        }
        
        // Tambahkan semua edge berdasarkan jalurTerhubung
        for (Halte h : semuaHalte) {
            for (Jalur j : h.jalurTerhubung) {
                String edgeId = h.nama + "-" + j.tujuan.nama;
                try {
                    Edge e = graph.addEdge(edgeId, h.nama, j.tujuan.nama, true);
                    e.setAttribute("ui.label", j.jarak + "m");
                } catch (Exception ex) {
                    // Edge sudah ada, skip
                }
            }
        }
        
        // Highlight rute optimal jika ada
        if (ruteOptimal.jalur.size() > 1) {
            for (int i = 0; i < ruteOptimal.jalur.size() - 1; i++) {
                Halte current = ruteOptimal.jalur.get(i);
                Halte next = ruteOptimal.jalur.get(i + 1);
                String edgeId = current.nama + "-" + next.nama;
                Edge e = graph.getEdge(edgeId);
                if (e != null) {
                    e.setAttribute("ui.class", "highlighted");
                }
            }
        }
        
        // Highlight node awal/akhir
        Node startNode = graph.getNode(awal.nama);
        if (startNode != null) {
            startNode.setAttribute("ui.class", "start");
        }
        
        // Tampilkan graf dengan layout manual
        Viewer viewer = graph.display();
        viewer.setCloseFramePolicy(Viewer.CloseFramePolicy.HIDE_ONLY);
        
        // Disable auto-layout untuk mempertahankan koordinat tetap
        viewer.disableAutoLayout();
        
        // Freeze posisi semua node
        for (Node node : graph) {
            node.setAttribute("layout.frozen");
        }
    }
    
    public static RuteOptimal cariRuteSirkuler(Halte awal) {
        Map<Halte, Boolean> dikunjungi = new HashMap<>();
        List<Halte> jalurSaatIni = new ArrayList<>();
        jalurSaatIni.add(awal);
        RuteOptimal semuaSolusi = new RuteOptimal();
        
        cariRuteDFS(awal, awal, dikunjungi, jalurSaatIni, 0, semuaSolusi);
        
        // Urutkan rute berdasarkan jarak terpendek
        semuaSolusi.sortByJarak();
        return semuaSolusi;
    }
    
    private static void cariRuteDFS(Halte saatIni, Halte awal, 
                                  Map<Halte, Boolean> dikunjungi, 
                                  List<Halte> jalurSaatIni, 
                                  int jarakSaatIni, 
                                  RuteOptimal semuaSolusi) {
        if (saatIni == awal && jalurSaatIni.size() > 1) {
            semuaSolusi.tambahRute(jarakSaatIni, jalurSaatIni);
            return;
        }
        
        dikunjungi.put(saatIni, true);
        
        for (Jalur jalur : saatIni.jalurTerhubung) {
            if (!dikunjungi.getOrDefault(jalur.tujuan, false) || jalur.tujuan == awal) {
                jalurSaatIni.add(jalur.tujuan);
                cariRuteDFS(jalur.tujuan, awal, dikunjungi, jalurSaatIni, 
                          jarakSaatIni + jalur.jarak, semuaSolusi);
                jalurSaatIni.remove(jalurSaatIni.size() - 1);
            }
        }
        
        dikunjungi.put(saatIni, false);
    }
}
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package semestralka2_us.Main;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import semestralka2_us.Dynamic_hashing;
import semestralka2_us.Nehnutelnost.Nehnutelnost;
import semestralka2_us.Nehnutelnost.Nehnutelnost_byID;
import semestralka2_us.Nehnutelnost.Nehnutelnost_byScN;
import semestralka2_us.Record;
import semestralka2_us.UnsortedFile;

/**
 *
 * @author MarekPC
 */
public class Holder {

    private UnsortedFile unsortedFile;
    private Dynamic_hashing hashing_byID;
    private Dynamic_hashing hashing_byScN;

    public static String fileIDhashing_save = "ID_hash_save.txt";
    public static String fileScNHashing_save = "SCN_hash_save.txt";
    public static String fileDataSave_save = "unsorted_save.txt";
    public static String id_dataFile = "byID.bin";
    public static String id_overFile = "byID_over.bin";
    public static String scn_dataFile = "bySCN.bin";
    public static String scn_overFile = "bySCN_over.bin";
    public static String unsorted = "nehnutelnosti.bin";

    public Holder(UnsortedFile unsortedFile, Dynamic_hashing hashing_byID, Dynamic_hashing hashing_byScN) {
        this.unsortedFile = unsortedFile;
        this.hashing_byID = hashing_byID;
//        hashing_byID.printTree();
//        System.out.println("\n\n");
        this.hashing_byScN = hashing_byScN;
    }

    public UnsortedFile getUnsortedFile() {
        return unsortedFile;
    }

    public Dynamic_hashing getHashing_byID() {
        return hashing_byID;
    }

    public Dynamic_hashing getHashing_byScN() {
        return hashing_byScN;
    }

    public String zadanie1(int sup_c, String kat) throws IOException {
        Nehnutelnost_byScN toFind = new Nehnutelnost_byScN(sup_c, kat);

        Nehnutelnost_byScN found = hashing_byScN.find(toFind);

        if (found == null) {
            return "Nehnuteľnosť so súpisným číslom: " + sup_c + " a katastrom: " + kat + " sa v systéme nenachádza";
        }

        long offset = found.getDataOffset();

        Nehnutelnost n = unsortedFile.findFromOffset(offset);
        return n.toString();

    }

    String zadanie2(int id) throws IOException {
        Nehnutelnost_byID toFind = new Nehnutelnost_byID(id);
        Nehnutelnost_byID found = hashing_byID.find(toFind);
        if (found == null) {
            return "Nehnuteľnosť s ID: " + id + " sa v systéme nenachádza";
        }

        long offset = found.getDataOffset();
        Nehnutelnost n = unsortedFile.findFromOffset(offset);
        return n.toString();

    }

    String zadanie3(int id, int sup_c, String kat, String popis) throws IOException {
        Nehnutelnost_byID toFind = new Nehnutelnost_byID(id);
        Nehnutelnost_byID found = hashing_byID.find(toFind);
        if (found != null) {
            return "Nehnuteľnosť s ID: " + id + " sa v systéme už nachádza";
        }

        Nehnutelnost n = new Nehnutelnost(id, sup_c, kat, popis);
        long offset = unsortedFile.insert(n);
        Nehnutelnost_byID byID = new Nehnutelnost_byID(id, offset);
        Nehnutelnost_byScN byScN = new Nehnutelnost_byScN(sup_c, kat, offset);
        hashing_byID.insert(byID);
        hashing_byScN.insert(byScN);

        return "Nehnutelnosť úspešne pridaná";

    }

    String zadanie4(int sup_c, String kat) throws IOException {
        Nehnutelnost_byScN toFind = new Nehnutelnost_byScN(sup_c, kat);

        Nehnutelnost_byScN found = hashing_byScN.find(toFind);

        if (found == null) {
            return "Nehnuteľnosť so súpisným číslom: " + sup_c + " a katastrom: " + kat + " sa v systéme nenachádza";
        }

        long offset = found.getDataOffset();
        Nehnutelnost n = unsortedFile.findFromOffset(offset);
//        System.out.println("byID SIZE BEFORE DELETE: " + hashing_byID.getDataFileSize());
        hashing_byID.delete(new Nehnutelnost_byID(n.getId(), offset));
//        System.out.println("byID SIZE AFTER DELETE: " + hashing_byID.getDataFileSize());
        hashing_byScN.delete(found);
        unsortedFile.deleteFromOffset(offset);

        return "Vymazanie úspešné";
    }

    String zadanie5(int id, int sup_c, String kat, String popis) throws IOException {
        Nehnutelnost_byID toFind = new Nehnutelnost_byID(id);
        Nehnutelnost_byID found = hashing_byID.find(toFind);
        if (found == null) {
            return "Nehnuteľnosť s ID: " + id + " sa v systéme nenachádza";
        }
        long offset = found.getDataOffset();
        Nehnutelnost n = unsortedFile.findFromOffset(offset);
        hashing_byID.delete(found);
        unsortedFile.deleteFromOffset(offset);
        hashing_byScN.delete(new Nehnutelnost_byScN(n.getId(), n.getRealKat()));

        Nehnutelnost newNehnutelnost = new Nehnutelnost(id, sup_c, kat, popis);
        long newOffset = unsortedFile.insert(newNehnutelnost);
        hashing_byID.insert(new Nehnutelnost_byID(id, newOffset));
        hashing_byScN.insert(new Nehnutelnost_byScN(sup_c, kat, newOffset));

        return "Zmena údajov úspešná";

    }

    void saveState(String id_hashing_savetxt, String scn_hashing_savetxt, String unsorted_savetxt) throws FileNotFoundException, UnsupportedEncodingException {
        hashing_byID.saveState(id_hashing_savetxt);
        hashing_byScN.saveState(scn_hashing_savetxt);
        unsortedFile.saveState(unsorted_savetxt);
    }

    public static Holder loadHolder(String id_hashing_save, String id_dataFile, String id_overFile, String scn_hashing_save, String scn_dataFile, String scn_overFile, String unsorted_save, String dataFile, Class<Nehnutelnost_byID> byIDclass, Class<Nehnutelnost_byScN> bySCNclass, Class<Nehnutelnost> dataClass) throws IOException, FileNotFoundException, InstantiationException, IllegalAccessException {
        Dynamic_hashing byID = Dynamic_hashing.loadInstance(id_hashing_save, id_dataFile, id_overFile, byIDclass);
        byID.printTree();
        Dynamic_hashing bySCN = Dynamic_hashing.loadInstance(scn_hashing_save, scn_dataFile, scn_overFile, bySCNclass);
        UnsortedFile unsorted = UnsortedFile.loadState(unsorted_save, dataFile, dataClass);

        return new Holder(unsorted, byID, bySCN);
    }

}

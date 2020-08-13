/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package semestralka2_us.Nehnutelnost;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.BitSet;
import java.util.concurrent.ThreadLocalRandom;
import semestralka2_us.Record;
import semestralka2_us.Util.Utilities;

/**
 * Všetky údaje o nehnutelnosti, ulozene su v neusporiadanom súbore
 * @author MarekPC
 */
public class Nehnutelnost implements Record<Nehnutelnost> {
        private static int popis_maxSize = 20;
    public static int kat_maxSize = 15;

    int sup_c;
    int id;

    private String realPopis;

    private int realPopisSize;
    private int realKatSize;

    private String realKat;

    public Nehnutelnost() {
        
    }

    public Nehnutelnost(int id, int sup_c, String kat, String popis) {
        this.sup_c = sup_c;
        this.id = id;
        this.realPopis = popis;
        this.realKat = kat;

        this.realPopisSize = this.realPopis.length();
        this.realKatSize = this.realKat.length();

    }

    public Nehnutelnost(int id, int sup_c, int realKatSize, String kat, int realPopisSize, String popis) {
        this(id, sup_c, kat.substring(0, realKatSize), popis.substring(0, realPopisSize));
    }

    

    @Override
    public boolean equalsOther(Nehnutelnost other) {
        if (other.id == this.id) {
            return true;
        }
//        if (other.realKat.compareTo(this.realKat) == 0 && other.sup_c == this.sup_c) {
//            return true;
//        }

        return false;

    }

    @Override
    public BitSet getHash() {

        BitSet b = BitSet.valueOf(new long[]{this.id});
        
        return b;

    }

    @Override
    public byte[] toByteArray() {
        ByteArrayOutputStream hlpByteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream hlpOutStream = new DataOutputStream(hlpByteArrayOutputStream);

        try {
            hlpOutStream.writeInt(this.id); // 4
            hlpOutStream.writeInt(this.sup_c); // 4
            // Velkost skutocneho nazvu katastru
            hlpOutStream.writeInt(this.realKatSize); //4
            // kataster doplneny na pozadovanu dlzku
            hlpOutStream.writeUTF(Utilities.fillToSize(realKat, kat_maxSize)); // 15
            // Velkost skutocneho popisu
            hlpOutStream.writeInt(this.realPopisSize); // 4 
            // popis doplneny na pozadovanu dlzku
            hlpOutStream.writeUTF(Utilities.fillToSize(realPopis, popis_maxSize)); // 20
//            int size = hlpOutStream.size();
            hlpOutStream.write(new byte[this.getByteArraySize()-hlpOutStream.size()]);
//            for (int i = 0; i < this.getByteArraySize() - size; i++) {
//                hlpOutStream.writeByte(i);
//                
//                
//            }
//            System.out.println("SIZE: "+hlpOutStream.size());
            hlpOutStream.close();
            hlpByteArrayOutputStream.close();
            return hlpByteArrayOutputStream.toByteArray();

        } catch (IOException e) {
            throw new IllegalStateException("Error during conversion (Nehnutelnost to byte array.)");
        }
    }

    @Override
    public Nehnutelnost fromByteArray(byte[] array) {
        ByteArrayInputStream inp = new ByteArrayInputStream(array);
        DataInputStream inputStream = new DataInputStream(inp);
        try {
            int id = inputStream.readInt();
//            System.out.println(id);

            int sup_c = inputStream.readInt();
            int rk = inputStream.readInt();
            String kat = inputStream.readUTF();
            int rp = inputStream.readInt();
            String p = inputStream.readUTF();
            inp.close();
            inputStream.close();
            return new Nehnutelnost(id, sup_c, rk, kat, rp, p);
//            return new Nehnutelnost(inputStream.readInt(), inputStream.readInt(), inputStream.readInt(), inputStream.readUTF(), inputStream.readInt(), inputStream.readUTF());

        } catch (IOException ex) {
            System.out.println(id);
            System.out.println(id);
            System.out.println(id);
            System.out.println(id);
            throw new IllegalStateException("Error during conversion (Nehnutelnost from byte Array)");
        }

    }

    @Override
    public String toString() {
        return "ID: " + id + "  Súp.číslo: " + sup_c + "  Kataster: " + realKat + "  Popis: " + realPopis; //+ "    "+Utilities.toStringBitSet(getHash(), 32);

    }

    @Override
    public int getByteArraySize() {
        return 60;
    }

    public int getId() {
        return id;
    }

    public String getRealKat() {
        return realKat;
    }
}

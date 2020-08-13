/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package semestralka2_us.Nehnutelnost;

import semestralka2_us.Nehnutelnost.Nehnutelnost_byID;
import semestralka2_us.Nehnutelnost.Nehnutelnost;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.BitSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import semestralka2_us.Record;
import semestralka2_us.Util.Utilities;

/**
 *  Súpisné číslo a ID nehnutelnosti v dátovom súbore nehnutelnosti, pre vyhladavanie podla supisného čísla a názvu katastra
 * @author MarekPC
 */
public class Nehnutelnost_byScN implements Record<Nehnutelnost_byScN> {

    int sup_c;
    String kataster;
    int katRealSize;
    long dataOffset;

    public Nehnutelnost_byScN() {
    }

    public Nehnutelnost_byScN(int sup_c, String kataster) {
        this.sup_c = sup_c;
        this.kataster = kataster;
    }

    
    public Nehnutelnost_byScN(int sup_c, String kataster,long dataOffset) {
        this.sup_c = sup_c;
        this.kataster = kataster;
        this.katRealSize = kataster.length();
        this.dataOffset = dataOffset;
    }

    public Nehnutelnost_byScN(int sup_c, String kataster, int katRealSize,long dataOffset) {
        this(sup_c, kataster.substring(0, katRealSize),dataOffset);
    }

    @Override
    public byte[] toByteArray() {
        ByteArrayOutputStream hlpByteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream hlpOutStream = new DataOutputStream(hlpByteArrayOutputStream);

        try {
            hlpOutStream.writeInt(sup_c); 
            hlpOutStream.writeInt(katRealSize); 
            hlpOutStream.writeUTF(Utilities.fillToSize(kataster, Nehnutelnost.kat_maxSize)); 
            hlpOutStream.writeLong(dataOffset); 
            
        } catch (IOException ex) {
            Logger.getLogger(Nehnutelnost_byID.class.getName()).log(Level.SEVERE, null, ex);
        }
        return hlpByteArrayOutputStream.toByteArray();

    }

    @Override
    public int getByteArraySize() {
        return 33;
    }

    @Override
    public Nehnutelnost_byScN fromByteArray(byte[] bytes) {
        ByteArrayInputStream inp = new ByteArrayInputStream(bytes);
        DataInputStream inputStream = new DataInputStream(inp);
        try {

            int sup_c = inputStream.readInt();
            int katLength = inputStream.readInt();
            String kat = inputStream.readUTF();
            long offset = inputStream.readLong();
                    
            return new Nehnutelnost_byScN(sup_c, kat, katLength,offset);

        } catch (IOException ex) {
            Logger.getLogger(Nehnutelnost_byID.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public BitSet getHash() {
        BitSet hash = BitSet.valueOf(new long[]{this.sup_c % 10});
        return hash;
    }

    @Override
    public String toString() {
        return "Data offset:    "+this.dataOffset+ "   Súp.číslo:   " +this.sup_c + "   Kataster:   " + this.kataster;
    }

    @Override
    public boolean equalsOther(Nehnutelnost_byScN otger) {
        return this.sup_c == otger.sup_c && (this.kataster.compareTo(otger.kataster) == 0);

    }

    public long getDataOffset() {
        return dataOffset;
    }
    

}

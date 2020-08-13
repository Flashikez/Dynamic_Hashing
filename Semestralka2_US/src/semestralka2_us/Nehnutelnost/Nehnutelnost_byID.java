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
import java.util.logging.Level;
import java.util.logging.Logger;
import semestralka2_us.Record;

/**
 * ID a offset v dátovom súbore nehnutelnosti, pre vyhladavanie podla ID
 * @author MarekPC
 */
public class Nehnutelnost_byID implements Record<Nehnutelnost_byID> {

    int id;
    long dataOffset;

    public Nehnutelnost_byID() {
    }

    public Nehnutelnost_byID(int id) {
        this.id = id;
    }

    public Nehnutelnost_byID(int id, long dataOffset) {
        this.id = id;
        this.dataOffset = dataOffset;
    }

    @Override
    public byte[] toByteArray() {
        ByteArrayOutputStream hlpByteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream hlpOutStream = new DataOutputStream(hlpByteArrayOutputStream);

        try {
            hlpOutStream.writeInt(id);
            hlpOutStream.writeLong(dataOffset);
        } catch (IOException ex) {
            Logger.getLogger(Nehnutelnost_byID.class.getName()).log(Level.SEVERE, null, ex);
        }
        return hlpByteArrayOutputStream.toByteArray();

    }

    @Override
    public int getByteArraySize() {
        return 12;
    }

    @Override
    public Nehnutelnost_byID fromByteArray(byte[] bytes) {
        ByteArrayInputStream inp = new ByteArrayInputStream(bytes);
        DataInputStream inputStream = new DataInputStream(inp);
        try {

            int id = inputStream.readInt(); //4
            long offset = inputStream.readLong(); //8

            return new Nehnutelnost_byID(id, offset);

        } catch (IOException ex) {
            Logger.getLogger(Nehnutelnost_byID.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public BitSet getHash() {
        BitSet hash = BitSet.valueOf(new long[]{this.id%5});
        return hash;
    }

    @Override
    public String toString() {
        return ""+"Data offset:   "+ this.dataOffset+"     ID:    "+ this.id   ;
    }

    @Override
    public boolean equalsOther(Nehnutelnost_byID otger) {

        return this.id == otger.id;

    }

    public int getId() {
        return id;
    }

    public long getDataOffset() {
        return dataOffset;
    }

}

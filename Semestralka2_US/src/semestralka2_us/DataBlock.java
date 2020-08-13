/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package semestralka2_us;

import semestralka2_us.Util.Utilities;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Blok dát
 * @author MarekPC
 */
public class DataBlock {

    private BitSet validity;
    private ArrayList<Record> records;
    private long nextOverFilloffset;
    private int overFill_ChainLength;
    private int blockFactor;

    public DataBlock(int blockFactor, BitSet validity, ArrayList<Record> records, long overFill_Offset, int overFill_ChainLength) {
        this.blockFactor = blockFactor;
        this.validity = validity;
        this.records = records;
        this.nextOverFilloffset = overFill_Offset;
        this.overFill_ChainLength = overFill_ChainLength;
    }

    DataBlock(int blockFactor) {
        this.records = new ArrayList<>(blockFactor);
        for (int i = 0; i < blockFactor; i++) {
            records.add(new InvalidRecord());

        }

        this.validity = new BitSet(blockFactor);
        this.blockFactor = blockFactor;
        this.overFill_ChainLength = 0;
        this.nextOverFilloffset = -1;

    }
    
   
    
    /**
     * Kontrola ci blok už obsahuje rekord
     * @param record
     * @return 
     */
    public boolean containsRecords(Record record){
        return getRecordIndex(record) != -1;
    }
    /**
     * Na výpis bloku
     * @param myOffset
     * @return 
     */
    public Block_String getBString(long myOffset){
        return new Block_String(Long.toString(myOffset), Integer.toString(overFill_ChainLength),Long.toString(nextOverFilloffset),Utilities.toStringBitSet(validity, blockFactor), records.stream().map(r-> r.toString()).collect(Collectors.toList()));
    }
    
/**
 * Pridá rekord , nastaví príslušný bit validity na 1
 * @param record 
 */
    public void addRecord(Record record) {
        int freeIndex = validity.previousClearBit(blockFactor - 1);
        validity.set(freeIndex);
        records.set(freeIndex, record);

    }

    /**
     * Odoberie pocet záznamov z bloku, využité pri spájaní blokov vrámci zreťazenia
     * @param numberOfRecords
     * @return 
     */
    public ArrayList<Record> removeNumOfRecords(int numberOfRecords) {
        ArrayList<Record> validRecords = new ArrayList<>();
        for (int i = validity.nextSetBit(0); i >= 0; i = validity.nextSetBit(i + 1)) {
            if (i > blockFactor) {
                break;
            }
            if (validRecords.size() == numberOfRecords) {
                break;
            }

            validRecords.add(records.get(i));
            validity.clear(i);

        }
        return validRecords;
    }

    /**
     * Pridá recordy z arraylistu do bloku, využité pri spájaní blokov
     * @param toAdd 
     */
    public void addRecords(ArrayList<Record> toAdd) {
        for (Record record : toAdd) {
            this.addRecord(record);

        }

    }

    public ArrayList<Record> getValidRecords() {
        ArrayList<Record> validRecords = new ArrayList<>();
        for (int i = validity.nextSetBit(0); i >= 0; i = validity.nextSetBit(i + 1)) {
            if (i > blockFactor) {
                break;
            }
            validRecords.add(records.get(i));

        }
        return validRecords;
    }

    public int getRecordIndex(Record searched) {
        for (int i = validity.nextSetBit(0); i >= 0; i = validity.nextSetBit(i + 1)) {
            if (i > blockFactor) {
                break;
            }

            Record r = records.get(i);
            if (r.equalsOther(searched)) {
                return i;
            }

        }
        return -1;
    }

    public void deleteAtIndex(int index) {
        validity.clear(index);
    }

    public int getValidCount() {
        return validity.cardinality();
    }

    public int getBlockFactor() {
        return blockFactor;
    }

    public String toString_noChain() {

        String ret = "---------- Next overFill offset: " + this.nextOverFilloffset  + "----------- \n";
        for (int i = 0; i < blockFactor; i++) {
            if (validity.get(i)) {
                ret += "1  ";
            } else {
                ret += "0  ";
            }
            ret += records.get(i).toString() + "\n";

        }
        ret += "-------------------------------------------------\n";
        return ret;

    }

    public String toString_wChain() {
        String ret = "---- Next overFill offset: " + this.nextOverFilloffset + "  Number of overFill blocks: " + this.overFill_ChainLength + "---- \n";
        for (int i = 0; i < blockFactor; i++) {
            if (validity.get(i)) {
                ret += "1  ";
            } else {
                ret += "0  ";
            }
            ret += records.get(i).toString() + "\n";

        }
        ret += "-------------------------------------------------\n";
        return ret;

    }

    public long getOverFill_Offset() {
        return nextOverFilloffset;
    }

    public int getOverFill_ChainLength() {
        return overFill_ChainLength;
    }

    public void setOverFill_Offset(long overFill_Offset) {
        this.nextOverFilloffset = overFill_Offset;
    }

    public void setOverFill_ChainLength(int overFill_ChainLength) {
        this.overFill_ChainLength = overFill_ChainLength;
    }

    /**
     * Rozdelí validne rekordy podla konkretného bitu ich hashu
     * @param bit pozícia bitu na základe, ktorého rozdelujeme
     * @param nextRecord
     * @return 
     */
    public ArrayList<Record>[] divideRecordsOnHashBit(int bit, Record nextRecord) {
        ArrayList<Record>[] divided = new ArrayList[2];
        divided[0] = new ArrayList<>();
        divided[1] = new ArrayList<>();
//        System.out.println(this.toStringValid_hash());
        for (Record record : records) {

            BitSet hash = record.getHash();
            if (!hash.get(bit)) { // 0
                divided[0].add(record);
            } else { //0
                divided[1].add(record);
            }

        }
        if (!nextRecord.getHash().get(bit)) {
            divided[0].add(nextRecord);
        } else {
            divided[1].add(nextRecord);
        }

        return divided;

    }

    public Record getRecord(Record searched) {
        for (int i = validity.nextSetBit(0); i >= 0; i = validity.nextSetBit(i + 1)) {
            if (i > blockFactor) {
                break;
            }

            Record r = records.get(i);
            if (r.equalsOther(searched)) {
                return r;
            }

        }
        return null;

    }

    public <T extends Record> byte[] toByteArray(Class<T> recordClass) {
        //
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        DataOutputStream outputStream = new DataOutputStream(byteStream);
        try {
            // BitSet to byteArray
            byte[] validityBytes = Utilities.bitSet_to_byteArr(validity, blockFactor);
            outputStream.write(validityBytes);
            outputStream.writeLong(nextOverFilloffset);
            outputStream.writeInt(overFill_ChainLength);
            int recordSize = recordClass.newInstance().getByteArraySize();

            byte[] invalidRecord = new byte[recordSize];
            for (int j = 0; j < blockFactor; j++) {
                if (validity.get(j)) { // valid
                    outputStream.write(records.get(j).toByteArray());

                } else {
                    outputStream.write(invalidRecord);
                }

            }
            outputStream.close();
            byteStream.close();

        } catch (IOException ex) {
            Logger.getLogger(DataBlock.class
                    .getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            System.err.println("Please add empty construcor as first constructor declaration!");
            Logger
                    .getLogger(DataBlock.class
                            .getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            System.err.println("Please add empty construcor as first constructor declaration!");
            Logger
                    .getLogger(DataBlock.class
                            .getName()).log(Level.SEVERE, null, ex);
        }
        return byteStream.toByteArray();

    }

    public static <T extends Record> DataBlock fromByteArray(byte[] bytes, int blockFactor, Class<T> recordClass) {
        ByteArrayInputStream inp = new ByteArrayInputStream(bytes);
        DataInputStream inputStream = new DataInputStream(inp);
        ArrayList<Record> records = new ArrayList<>(blockFactor);

        byte[] validityBytes = new byte[(blockFactor + 7) / 8];
        try {
            inputStream.read(validityBytes);
            long overFillOffset = inputStream.readLong();
            int chainLenghth = inputStream.readInt();
            T exampleInstance = recordClass.newInstance();
            int recordSize = exampleInstance.getByteArraySize();
            byte[] objectBytes;
            BitSet validity = Utilities.byteArr_to_bitSet(validityBytes);
            for (int i = 0; i < blockFactor; i++) {
                objectBytes = new byte[recordSize];
                inputStream.read(objectBytes);
                if (validity.get(i)) {
                    records.add(exampleInstance.fromByteArray(objectBytes));
                } else {
                    records.add(new InvalidRecord());
                }

            }
            inp.close();
            inputStream.close();
            return new DataBlock(blockFactor, validity, records, overFillOffset, chainLenghth);

        } catch (IOException ex) {
            Logger.getLogger(DataBlock.class
                    .getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            System.err.println("Please add empty construcor as first constructor declaration!");
            Logger
                    .getLogger(DataBlock.class
                            .getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            System.err.println("Please add empty construcor as first constructor declaration!");
            Logger
                    .getLogger(DataBlock.class
                            .getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static <T extends Record> int getByteArraySize(int blockFactor, Class<T> recordClass) {

        try {
            int validityBytesSize = (blockFactor + 7) / 8;
            T exampleInstance = recordClass.newInstance();
            int recordSize = exampleInstance.getByteArraySize();

            return validityBytesSize + 4 + 8 + recordSize * blockFactor;

        } catch (InstantiationException ex) {
            Logger.getLogger(DataBlock.class
                    .getName()).log(Level.SEVERE, null, ex);

        } catch (IllegalAccessException ex) {
            Logger.getLogger(DataBlock.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        return -1;

    }
}

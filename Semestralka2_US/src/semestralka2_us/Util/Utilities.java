/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package semestralka2_us.Util;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.BitSet;

/**
 * Utilitky , praca so suborom a pod...
 * @author MarekPC
 */
public class Utilities {

    /**
     * Doplni string na zadanu velkost
     * @param input
     * @param desiredSize
     * @return 
     */
    public static String fillToSize(String input, int desiredSize) {
        for (int i = input.length(); i < desiredSize; i++) {
            input += "X";

        }
        return input;

    }

    /**
     * Precita zadany pocet bytov zo zadanej pozicie zo zadaneho suboru
     * @param file
     * @param startPosition
     * @param numberOfBytes
     * @return
     * @throws IOException 
     */
    public static byte[] readBytesFromFile(RandomAccessFile file, long startPosition, int numberOfBytes) throws IOException {
        file.seek(startPosition);
        byte[] bytes = new byte[numberOfBytes];
        file.read(bytes);

        return bytes;

    }

    /**
     * Zapise byte array na zadanu poziciu do zadaneho suboru
     * @param file
     * @param startPosition
     * @param bytes
     * @throws IOException 
     */
    public static void writeBytesToFile(RandomAccessFile file, long startPosition, byte[] bytes) throws IOException {

        file.seek(startPosition);
        file.write(bytes);

    }

    public static void setFileLength(RandomAccessFile file, int size) throws IOException {
        file.setLength(size);
    }

    public static String toStringBitSet(BitSet set, int maxIndex) {
        StringBuilder s = new StringBuilder();
        for (int i = maxIndex - 1; i >= 0; i--) {
            s.append(set.get(i) == true ? 1 + "-" : 0 + "-");
        }
        s.setCharAt(s.length() - 1, ' ');
        return s.toString();
    }

    /**
     * Precita zadany pocet bytov z datainputstreamu
     * @param stream
     * @param numberOfBytes
     * @return
     * @throws IOException 
     */
    public static byte[] readBytesFromStream(DataInputStream stream, int numberOfBytes) throws IOException {
        byte[] bytes = new byte[numberOfBytes];
        stream.read(bytes);

        return bytes;
    }

    /**
     * Prevedie BitSet na byte arr
     * @param bitSet
     * @param blockFactor
     * @return 
     */
    public static byte[] bitSet_to_byteArr(BitSet bitSet, int blockFactor) {

        byte[] bytes = new byte[(blockFactor + 7) / 8];
        for (int i = 0; i < blockFactor; i++) {
            if (bitSet.get(i)) {
                bytes[bytes.length - i / 8 - 1] |= 1 << (i % 8);
            }
        }

        return bytes;
    }

    /**
     * Byte arr na BitSet
     * @param bytes
     * @return 
     */
    public static BitSet byteArr_to_bitSet(byte[] bytes) {
        BitSet bits = new BitSet();
        for (int i = 0; i < bytes.length * 8; i++) {
            if ((bytes[bytes.length - i / 8 - 1] & (1 << (i % 8))) > 0) {
                bits.set(i);
            }
        }
        return bits;
    }

}

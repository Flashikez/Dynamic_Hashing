/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package semestralka2_us;

import java.util.BitSet;

/**
 * Interaface pre záznam dynamického hashovania/neutriedeneho suboru
 * @author MarekPC
 */
public interface Record<T extends Record> {
    abstract byte[] toByteArray();
    abstract int getByteArraySize();
    abstract T fromByteArray(byte[] bytes);
    abstract BitSet getHash();
    abstract boolean equalsOther(T otger);
    
    @Override
    abstract String toString();
    
}

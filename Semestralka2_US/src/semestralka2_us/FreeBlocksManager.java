/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package semestralka2_us;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collections;


/**
 * Manažér wolných blokov súboru
 * @author MarekPC
 */
public class FreeBlocksManager {

    RandomAccessFile file;
    private ArrayList<Long> freeBlocks;
    private int blockSize;

    public FreeBlocksManager(RandomAccessFile pfile, int pBlockSize) {
        file = pfile;
        blockSize = pBlockSize;
        freeBlocks = new ArrayList<Long>() {
            @Override
            public boolean add(Long record) {
                int index = Collections.binarySearch(this, record);
                if (index < 0) {
                    index = ~index;
                }
                super.add(index, record);
                return true;
            }
        };

    }
    
    public void addBlock_noCheck(long offset){
        freeBlocks.add(offset);
    }

    /**
     * Pridá offset do volných blokov a ak je to možné skráti súbor od konca
     * @param offset
     * @throws IOException 
     */
    public void addFreeBlock(long offset) throws IOException {
        freeBlocks.add(offset);
        long currentSize = file.length();
        if (freeBlocks.get(freeBlocks.size() - 1) == currentSize - blockSize) {
            tryConcatFile(currentSize);
        }

    }

    public ArrayList<Long> getFreeBlocks() {
        return freeBlocks;
    }

    /**
     * Skáti súbor od zadu, pokial sa dá
     * @param currentSize
     * @throws IOException 
     */
    private void tryConcatFile(long currentSize) throws IOException {
        
        int toConcatCounter = 0;
        for (int i = freeBlocks.size() - 1; i >= 0; i--) {
            Long freeBlock = freeBlocks.get(i);
            if (freeBlock + blockSize == currentSize) {
                toConcatCounter++;
                freeBlocks.remove(freeBlock);
                currentSize -= blockSize;
            }else{
                break;
            }
        }
        if (toConcatCounter > 0) {
            file.setLength(file.length() - toConcatCounter * blockSize);
        }
    }

    /**
     * Vráti najmenší volný offset
     * @return 
     */
    public long getFreeBlock() {
        if (freeBlocks.isEmpty()) {
            return -1;
        }
        // daj najmensi offset
        return freeBlocks.remove(0);

    }

}

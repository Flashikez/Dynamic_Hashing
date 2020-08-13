/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package semestralka2_us;

import java.util.List;

/**
 *
 * @author MarekPC
 */
public class Block_String {
    public String offset;
    public String chain;
    public String nextOffset;
    public String bitSet;
    public List<String> records;

    public Block_String(String offset, String chain,String nextOverFillOffset,String bitSet, List<String> records) {
        this.offset = offset;
        this.chain = chain;
        this.records = records;
        this.bitSet = bitSet;
        this.nextOffset = nextOverFillOffset;
    }
    
    
    
}

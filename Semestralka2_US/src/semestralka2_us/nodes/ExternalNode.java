/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package semestralka2_us.nodes;

/**
 *
 * @author MarekPC
 */
public class ExternalNode extends Node {
    private int validCount;
    private long fileOffset;

    public ExternalNode(Node parent , int validCount, long fileOffset) {
        super.parent = parent;
        this.validCount = validCount;
        this.fileOffset = fileOffset;
    }

    public int getValidCount() {
        return validCount;
    }

    public long getFileOffset() {
        return fileOffset;
    }

    public void setValidCount(int validCount) {
        this.validCount = validCount;
    }

    public void setFileOffset(long fileOffset) {
        this.fileOffset = fileOffset;
    }
    public void addToCount(int add){
        this.validCount+=add;
    }
    
    public void subtractFromCount(int sub){
        this.validCount-=sub;
    }
    
    
    
}

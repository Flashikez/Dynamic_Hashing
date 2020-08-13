/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package semestralka2_us;

import semestralka2_us.nodes.ExternalNode;

/**
 *
 * @author MarekPC
 */
public class NodeBlockPair {
    public ExternalNode node;
    public DataBlock block;

    public NodeBlockPair(ExternalNode node, DataBlock block) {
        this.node = node;
        this.block = block;
    }
    
    
    
}

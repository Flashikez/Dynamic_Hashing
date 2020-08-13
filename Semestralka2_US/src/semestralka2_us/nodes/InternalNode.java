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
public class InternalNode extends Node {

    public InternalNode(Node parent, Node left , Node right) {
        super.parent = parent;
        super.left = left;
        super.right = right;
    }

    public InternalNode() {
        
    }
    
    
    
    
}

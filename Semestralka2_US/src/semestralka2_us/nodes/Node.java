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
public class Node {

    public Node parent, left, right;

    public void setLeft(Node nl) {
        this.left = nl;
    }

    public void setRight(Node nr) {
        this.right = nr;
    }

    public void setParent(Node np) {
        this.parent = np;
    }
}

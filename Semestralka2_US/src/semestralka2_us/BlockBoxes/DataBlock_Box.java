/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package semestralka2_us.BlockBoxes;

import java.util.List;
import javafx.geometry.Pos;
import javafx.scene.control.Separator;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

/**
 *
 * @author MarekPC
 */
public class DataBlock_Box extends VBox {

    private String offset, chain,nextOffset,bitSet;
    private List<String> records;

    public DataBlock_Box(String offset, String chain,String nextOffset,String bitSet, List<String> records) {
        this.offset = offset;
        this.chain = chain;
        this.records = records;
        this.bitSet = bitSet;
        this.nextOffset = nextOffset;
        init();
    }

    private void init() {
        setAlignment(Pos.CENTER);
        String cssLayout = "-fx-border-color: red;\n"
//                + "-fx-border-insets: 20;\n"
                + "-fx-border-width: 3;\n"
                + "-fx-border-style: none;\n";
        this.setStyle(cssLayout);

        setSpacing(5);
//        setPadding(new Insets(5,10,15,5));
        Text offT = new Text("Offset: "+offset);
        Text offNT = new Text("Next overFill offset: "+nextOffset);
        Text chainT = new Text("Chain length: "+chain);
        Text validity = new Text("validity: "+bitSet);
        
        Separator s = new Separator();
        Separator s2 = new Separator();
        
        getChildren().addAll(offT,offNT, chainT, s,validity,s2);
        for (String record : records) {
            getChildren().add(new Text(record));
        }

    }

}

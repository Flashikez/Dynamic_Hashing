/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package semestralka2_us.BlockBoxes;

/**
 *
 * @author MarekPC
 */
import java.util.List;
import javafx.geometry.Pos;
import javafx.scene.control.Separator;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class OverFillBlock_Box extends VBox {

    private String offset, chain,nextOffset,validty;
    private List<String> records;

    public OverFillBlock_Box(String offset, String chain,String nextOffset,String val, List<String> records) {
        this.offset = offset;
        this.chain = chain;
        this.records = records;
        this.nextOffset = nextOffset;
        this.validty = val;
        init();
    }

    private void init() {
        setAlignment(Pos.CENTER);
        String cssLayout = "-fx-border-color: blue;\n"
                + "-fx-border-insets: 20;\n"
                + "-fx-border-width: 3;\n"
                + "-fx-border-style: none;\n";
        this.setStyle(cssLayout);
        this.setSpacing(10);
//        setPadding(new Insets(5,10,15,5));
        Text offT = new Text("Offset: " + offset);
        Text offNT = new Text("Next overFill offset: "+nextOffset);
        Text vali = new Text("validity: "+validty);
        Separator s = new Separator();
        Separator s2 = new Separator();

        getChildren().addAll(offT,offNT, s,vali,s2);
        for (String record : records) {
            Text t = new Text(record);

            getChildren().add(t);
        }

    }

}

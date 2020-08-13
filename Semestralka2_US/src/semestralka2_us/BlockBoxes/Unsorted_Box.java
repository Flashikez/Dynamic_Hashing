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
public class Unsorted_Box extends VBox {
    
    private String offset;
    private List<String> records;

    public Unsorted_Box(String offset,  List<String> records) {
        this.offset = offset;
 
        this.records = records;

        init();
    }

    private void init() {
        setAlignment(Pos.CENTER);
        String cssLayout = "-fx-border-color: green;\n"
                + "-fx-border-insets: 20;\n"
                + "-fx-border-width: 3;\n"
                + "-fx-border-style: none;\n";
        this.setStyle(cssLayout);
        this.setSpacing(10);
//        setPadding(new Insets(5,10,15,5));
        Text offT = new Text("Offset: " + offset);
//        Text chainT = new Text("Chain length: "+chain);
        Separator s = new Separator();

//        divider.setStrokeWidth(3);
        getChildren().addAll(offT, s);
        for (String record : records) {
            Text t = new Text(record);
            getChildren().add(t);
        }

    }
}

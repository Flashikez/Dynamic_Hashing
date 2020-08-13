/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package semestralka2_us.BlockBoxes;

import javafx.geometry.Pos;
import javafx.scene.control.Separator;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

/**
 *
 * @author MarekPC
 */
public class RecordText extends VBox {

    private String offset;
    private String recordsText;

    public RecordText(String offset, String recordsText) {
        this.offset = offset;
        this.recordsText = recordsText;
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
        Text offT = new Text("Offset: " + offset);
        Text recordT = new Text("Record " + recordsText);
        Separator s = new Separator();

        getChildren().addAll(offT, s, recordT);

    }
}

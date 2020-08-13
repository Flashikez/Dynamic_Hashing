/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package semestralka2_us.UserInputs;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Kontrolér okna InputView, ktoré získava vstupy pre jednotlivé zadania od
 * uživateľa
 *
 * @author MarekPC
 */
public class InputController implements Initializable {

    @FXML
    Label lab1;
    @FXML
    Label lab2;
    @FXML
    Label lab3,lab4;
    @FXML
    Label labZadanie;
    @FXML
    TextField tf1;
    @FXML
    TextField tf2;
    @FXML
    TextField tf3,tf4;
    @FXML
    Button btnExe;
    @FXML
    Text textZadanie;

    private String zadaniec, zadanie, l1, l2, l3,l4, out1, out2, out3,out4;
  
    boolean b1, b2, b3,b4;

    public InputController(int zadaniec, String zadanie, String l1, String l2, String l3,String l4, boolean b1, boolean b2, boolean b3,boolean b4) {
        this.zadaniec = "Zadanie č. " + zadaniec;
        this.zadanie = zadanie;
        this.l1 = l1;
        this.l2 = l2;
        this.l3 = l3;
        this.l4 = l4;
        this.b1 = b1;
        this.b2 = b2;
        this.b3 = b3;
        this.b4 = b4;

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        labZadanie.setText(zadaniec);
        lab1.setText(l1);
        lab2.setText(l2);
        lab3.setText(l3);
        lab4.setText(l4);
        textZadanie.setText(zadanie);

        tf1.disableProperty().set(!b1);
        tf2.disableProperty().set(!b2);
        tf3.disableProperty().set(!b3);
        tf4.disableProperty().set(!b4);
        btnExe.setOnAction(e -> {
            out1 = tf1.getText();
            out2 = tf2.getText();
            out3 = tf3.getText();
            out4 = tf4.getText();
            Stage st = (Stage) btnExe.getScene().getWindow();
            st.close();

        });
       

    }


    public String getOut1() {
        return out1;
    }


    public String getOut2() {
        return out2;
    }

 
    public String getOut3() {
        return out3;
    }

    public String getOut4(){
        return out4;
    }
}

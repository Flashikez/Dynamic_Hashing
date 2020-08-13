/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package semestralka2_us.Generate;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import semestralka2_us.Dynamic_hashing;
import semestralka2_us.Main.Holder;
import semestralka2_us.Main.MainController;
import semestralka2_us.Nehnutelnost.Nehnutelnost;
import semestralka2_us.Nehnutelnost.Nehnutelnost_byID;
import semestralka2_us.Nehnutelnost.Nehnutelnost_byScN;
import semestralka2_us.UnsortedFile;
import semestralka2_us.Util.AlertManager;

/**
 *
 * @author MarekPC
 */
public class GenerateController implements Initializable {

    @FXML
    TextField tfKat;
    @FXML
    TextField tfNehn;
    @FXML
    TextField tfHash;
    @FXML
    TextField tfDataBlock;
    @FXML
    TextField tfOverBlock;
    @FXML
    Button btnGenerate;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        btnGenerate.setOnAction(e -> {
            MainController contr = null;
            try {

                contr = new MainController(new Generator().generate(Integer.parseInt(tfKat.getText()), Integer.parseInt(tfNehn.getText()), Integer.parseInt(tfHash.getText()), Integer.parseInt(tfDataBlock.getText()), Integer.parseInt(tfOverBlock.getText())));
            } catch (IOException ex) {
                AlertManager.errorAlert("Chyba", ex.getMessage());

                Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
            }
            FXMLLoader fx = new FXMLLoader();
            fx.setLocation(MainController.class.getResource("MainView.fxml"));

            Stage stage2 = new Stage();

            fx.setController(contr);
            Scene scene;

            try {
                scene = new Scene(fx.load());
                stage2.setTitle("Semestrálna práca Marek Zaťko");
                stage2.setScene(scene);
                stage2.show();

                Stage stage = (Stage) btnGenerate.getScene().getWindow();
                stage.close();
            } catch (IOException ex) {
                AlertManager.errorAlert("Chyba", ex.getMessage());
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
            }

        });

    }

    private class Generator {

        private  final int id_start = 0;
        private  final int sup_start = 0;
        private String popisy[] = {"Rodinný dom", "Ćinžiak", "Byt", "Garzonka", "Chata", "Chalupa"};
        private final Random r = new Random();
        


 
        
        private String randomPopis(){
            return popisy[r.nextInt(popisy.length)];
        }
        public Holder generate(int katCount, final int nehnCount, int hashSize, int dataBlockFactor, int overFillBlockFactor) throws FileNotFoundException, IOException {
            UnsortedFile unsortedFile = new UnsortedFile(Nehnutelnost.class, Holder.unsorted);
            Dynamic_hashing hashing_byID = new Dynamic_hashing(Holder.id_dataFile, Holder.id_overFile, hashSize, dataBlockFactor, overFillBlockFactor, Nehnutelnost_byID.class);
            Dynamic_hashing hashing_bySN = new Dynamic_hashing(Holder.scn_dataFile, Holder.scn_overFile, hashSize, dataBlockFactor, overFillBlockFactor, Nehnutelnost_byScN.class);

            int id_counter = id_start;
            for (int i = 0; i < katCount; i++) {
                String kataster = "Kataster " + i;
                int sup_counter = sup_start;
                for (int j = 0; j < nehnCount; j++) {
                    Nehnutelnost nehnutelnost = new Nehnutelnost(id_counter, sup_counter, kataster, randomPopis());
//                    System.out.println(i + "\t" + nehnutelnost.toString());
                    long dataOffset = unsortedFile.insert(nehnutelnost);
                    Nehnutelnost_byID n_ID = new Nehnutelnost_byID(id_counter, dataOffset);
                    Nehnutelnost_byScN n_ScN = new Nehnutelnost_byScN(sup_counter, kataster, dataOffset);
                    hashing_byID.insert(n_ID);
                    hashing_bySN.insert(n_ScN);

                    id_counter++;
                    sup_counter++;

                }

            }
            return new Holder(unsortedFile, hashing_byID, hashing_bySN);

        }

    }
}

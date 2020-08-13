/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package semestralka2_us.Main;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import semestralka2_us.Generate.GenerateController;
import semestralka2_us.Nehnutelnost.Nehnutelnost;
import semestralka2_us.Nehnutelnost.Nehnutelnost_byID;
import semestralka2_us.Nehnutelnost.Nehnutelnost_byScN;
import semestralka2_us.Util.AlertManager;

/**
 *
 * @author MarekPC
 */
public class Semestralka2_US extends Application {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
//         TODO code application logic here
        

        int hashMaxBit = 2;
        int dataBlockFactor = 2;
        int overFIllBlockFactor = 3;
        double insertChance = 0.5;
     
//        for (int i = 0; i < 1000000; i++) {
//            long startTime = System.nanoTime();
//            System.out.println(i);
//            Test.test(50000, insertChance,hashMaxBit,dataBlockFactor,overFIllBlockFactor);
//            long timeElapsed = System.nanoTime() - startTime;
////            System.out.println(timeElapsed);
//            System.out.println("TOOK: "+timeElapsed/1000000000.0+" seconds");
//        }
        
        
        launch(args);
      
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        ButtonType generate = new ButtonType("Generovať", ButtonBar.ButtonData.OK_DONE);
        ButtonType load = new ButtonType("Načítať zo súborov", ButtonBar.ButtonData.CANCEL_CLOSE);
        Alert alert = new Alert(Alert.AlertType.NONE, "Vyber akým spôsobom chceš naplniť systém", generate, load);
        alert.setTitle("Naplnenie systému");
        alert.showAndWait();
        if (alert.getResult() == generate) {

            Parent root = FXMLLoader.load(GenerateController.class.getResource(("GenerateView.fxml")));
            Scene scene = new Scene(root);

            primaryStage.setScene(scene);
            primaryStage.show();
        } else {
            try {
                Holder loadedHolder = Holder.loadHolder(Holder.fileIDhashing_save, Holder.id_dataFile, Holder.id_overFile, Holder.fileScNHashing_save, Holder.scn_dataFile, Holder.scn_overFile, Holder.fileDataSave_save,Holder.unsorted, Nehnutelnost_byID.class, Nehnutelnost_byScN.class, Nehnutelnost.class);
                MainController contr = new MainController(loadedHolder);
                FXMLLoader fx = new FXMLLoader();
                fx.setLocation(MainController.class.getResource("MainView.fxml"));

                Stage stage2 = new Stage();

                fx.setController(contr);
                Scene scene;

                scene = new Scene(fx.load());
                stage2.setTitle("Semestrálna práca Marek Zaťko");
                stage2.setScene(scene);
                stage2.show();

            } catch (IOException ex) {
                AlertManager.errorAlert("Chyba", ex.getClass().getName());
                Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

    }

}

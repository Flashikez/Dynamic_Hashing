/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package semestralka2_us.Main;

import java.awt.im.InputContext;
import semestralka2_us.BlockBoxes.OverFillBlock_Box;
import semestralka2_us.BlockBoxes.DataBlock_Box;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Separator;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Stage;
import semestralka2_us.BlockBoxes.Unsorted_Box;
import semestralka2_us.Block_String;
import semestralka2_us.Dynamic_hashing;
import semestralka2_us.UnsortedFile;
import semestralka2_us.UserInputs.InputController;
import semestralka2_us.Util.AlertManager;

/**
 * Hlavný GUI konktrolér, komunikuje s databázou , získava uzivatelske vstupy a prezentuje výstupy
 * @author MarekPC
 */
public class MainController implements Initializable {

    @FXML
    MenuItem miData;
    @FXML
    MenuItem miIDOVER, miIDFULL, miIDCON;
    @FXML
    MenuItem miSCOVER, miSCFULL, miSCCON, mi1, mi2, mi3, mi4, mi5;
    @FXML
    TextFlow tfOutput;
    @FXML
    Text txtFileSize;
    @FXML
    Button btnUloz;

    private Holder holder;

    private String vstup1, vstup2, vstup3, vstup4;
    boolean inputWindowClosed;

    public MainController(Holder holder) {
        this.holder = holder;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        miIDCON.setOnAction(e -> {
            showBlocksConnection(holder.getHashing_byID());

        });

        miSCCON.setOnAction(e -> {
            showBlocksConnection(holder.getHashing_byScN());
        });

        miIDFULL.setOnAction(e -> {
            showWholeFile(holder.getHashing_byID(), true);

        });
        miIDOVER.setOnAction(e -> {
            showWholeFile(holder.getHashing_byID(), false);

        });
        miSCFULL.setOnAction(e -> {
            showWholeFile(holder.getHashing_byScN(), true);

        });
        miSCOVER.setOnAction(e -> {
            showWholeFile(holder.getHashing_byScN(), false);

        });

        miData.setOnAction(e -> {
            showUnsorted(holder.getUnsortedFile());
        });

        mi1.setOnAction(e -> {
            Platform.runLater(() -> {
                txtFileSize.setText("");
                zadanie1();
            });

        });
        mi2.setOnAction(e -> {
            Platform.runLater(() -> {
                txtFileSize.setText("");
                zadanie2();
            });

        });
        mi3.setOnAction(e -> {
            Platform.runLater(() -> {
                txtFileSize.setText("");
                zadanie3();
            });

        });
        mi4.setOnAction(e -> {
            Platform.runLater(() -> {
                txtFileSize.setText("");
                zadanie4();
            });

        });
        mi5.setOnAction(e -> {
            Platform.runLater(() -> {
                txtFileSize.setText("");
                zadanie5();
            });

        });
        btnUloz.setOnAction(e -> {
            try {
                holder.saveState(Holder.fileIDhashing_save, Holder.fileScNHashing_save, Holder.fileDataSave_save);
                AlertManager.notify("Uloženie úspešné");
            } catch (IOException ex) {
                AlertManager.errorAlert("Chyba", ex.getClass().getName());
                Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);

            }

        });
    }

    private void showBlocksConnection(Dynamic_hashing hashing) {
        try {
            List<List<Block_String>> lists = hashing.getAllBlocksStrings();
            tfOutput.getChildren().clear();
            txtFileSize.setText("---");
            for (List<Block_String> list : lists) {
                for (int i = 0; i < list.size(); i++) {
                    Block_String block_string = list.get(i);
                    VBox blockText;
                    if (i == 0) {
                        // Dátový blok
                        blockText = new DataBlock_Box(block_string.offset, block_string.chain,block_string.nextOffset,block_string.bitSet, block_string.records);
                        final Separator separator = new Separator(Orientation.HORIZONTAL);
                        separator.prefWidthProperty().bind(tfOutput.widthProperty());
                        separator.setStyle("-fx-background-color: red;");
                        tfOutput.getChildren().add(separator);

                    } else {
                        blockText = new OverFillBlock_Box(block_string.offset, block_string.chain,block_string.nextOffset,block_string.bitSet, block_string.records);
                    }
                    tfOutput.getChildren().add(blockText);

                }

            }
//            System.out.println(holder.getHashing_byID().toString_Blocks());

        } catch (IOException ex) {
            AlertManager.errorAlert("Chyba", ex.getClass().getName());
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void showWholeFile(Dynamic_hashing hashing, boolean dataFile) {
        try {
            List<Block_String> list = null;

            if (dataFile) {

                list = hashing.getWholeDataFileAsStrings();
                txtFileSize.setText("Veľkosť súboru: " + hashing.getDataFileSize() + " bytes");

            } else {
                list = hashing.getWholeOverfillFileAsStrings();
                txtFileSize.setText("Veľkosť súboru: " + hashing.getOverfillFileSize() + " bytes");
            }
            tfOutput.getChildren().clear();

            if (dataFile) {
                for (Block_String block_String : list) {
                    VBox blockText = new DataBlock_Box(block_String.offset, block_String.chain,block_String.nextOffset,block_String.bitSet, block_String.records);
                    tfOutput.getChildren().add(blockText);

                }
            } else {
                for (Block_String block_String : list) {
                    VBox blockText = new OverFillBlock_Box(block_String.offset, block_String.chain,block_String.nextOffset,block_String.bitSet, block_String.records);
                    tfOutput.getChildren().add(blockText);

                }
            }

        } catch (IOException ex) {
            AlertManager.errorAlert("Chyba", ex.getClass().getName());
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void showUnsorted(UnsortedFile unsortedFile) {
        try {
            List<Block_String> list = unsortedFile.getStrings();
            tfOutput.getChildren().clear();
            txtFileSize.setText("Veľkosť súboru: " + unsortedFile.getFileSize() + " bytes");
            for (Block_String block_String : list) {
                Unsorted_Box bl = new Unsorted_Box(block_String.offset, block_String.records);
                tfOutput.getChildren().add(bl);

            }
        } catch (IOException ex) {
            AlertManager.errorAlert("Chyba", ex.getClass().getName());
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void getInputs(int zadanieC, String zadanie, String L1, String L2, String L3, String L4, boolean b1, boolean b2, boolean b3, boolean b4) {

        FXMLLoader fx = new FXMLLoader();

        fx.setLocation(InputController.class.getResource("InputView.fxml"));

        InputController contr = new InputController(zadanieC, zadanie, L1, L2, L3, L4, b1, b2, b3, b4);

        Stage stage2 = new Stage();

        fx.setController(contr);

        Scene scene;
        inputWindowClosed = false;

        try {
            scene = new Scene(fx.load());
            stage2.setScene(scene);
            stage2.initModality(Modality.WINDOW_MODAL);
            stage2.showAndWait();

        } catch (IOException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        }

        vstup1 = contr.getOut1();
        if (vstup1 == null && b1) {
            inputWindowClosed = true;
        }

        vstup2 = contr.getOut2();
        if (vstup2 == null && b2) {
            inputWindowClosed = true;
        }
        vstup3 = contr.getOut3();
        if (vstup3 == null && b3) {
            inputWindowClosed = true;
        }
        vstup4 = contr.getOut4();
        if (vstup4 == null && b4) {
            inputWindowClosed = true;
        }
        if (!inputWindowClosed) {
            tfOutput.getChildren().clear();
        }

    }

    private void zadanie1() {

        String zadanie = "Vyhľadanie nehnuteľnosti podľa súpisného čísla nehnuteľnosti a názvu katastrálneho\n"
                + "územia - vyhľadajú a vypíšu sa všetky informácie o nehnuteľnosti.";

        getInputs(1, zadanie, "Súpisné číslo", "Názov katastru:", "---", "---", true, true, false, false);
        if (!inputWindowClosed) {
            int sup_c = Integer.parseInt(vstup1);
            String kat = vstup2;

            try {
                Text t = new Text(holder.zadanie1(sup_c, kat));
                t.setFont(Font.font("Cambria", 20));
                tfOutput.getChildren().add(t);
            } catch (IOException ex) {
                AlertManager.errorAlert("Chyba", ex.getClass().getName());
                Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    private void zadanie2() {
        String zadanie = "Vyhľadanie nehnuteľnosti podľa identifikačné čísla nehnuteľnosti - vyhľadajú a vypíšu sa\n"
                + "všetky informácie o nehnuteľnosti.";

        getInputs(2, zadanie, "Identifikačné číslo:", "---", "---", "---", true, false, false, false);
        if (!inputWindowClosed) {
            int id = Integer.parseInt(vstup1);

            try {
                Text t = new Text(holder.zadanie2(id));
                t.setFont(Font.font("Cambria", 20));
                tfOutput.getChildren().add(t);
            } catch (IOException ex) {
                AlertManager.errorAlert("Chyba", ex.getClass().getName());
                Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void zadanie3() {
        String zadanie = "Pridanie – na základe vstupných údajov pridá záznam do evidencie (nie je potrebné kontrolovať\n"
                + "jedinečnosť súpisného čísla v danom katastrálnom území ani jedinečnosť identifikačného čísla).";
        getInputs(3, zadanie, "Identifikačné číslo:", "Súpisné číslo:", "Kataster:", "Popis:", true, true, true, true);
        if (!inputWindowClosed) {
            int id = Integer.parseInt(vstup1);
            int sup_c = Integer.parseInt(vstup2);
            String kat = vstup3;
            String popis = vstup4;
            try {
                Text t = new Text(holder.zadanie3(id, sup_c, kat, popis));
                t.setFont(Font.font("Cambria", 20));
                tfOutput.getChildren().add(t);
            } catch (IOException ex) {
                AlertManager.errorAlert("Chyba", ex.getClass().getName());
                Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    private void zadanie4() {
        String zadanie = "Vyradenie – na základe súpisného čísla a názvu katastrálneho územia vyradí záznam\n"
                + "o nehnuteľnosti z evidencie.";
        getInputs(4, zadanie, "Súpisné číslo: ", "Kataster: ", "---", "---", true, true, false, false);
        if (!inputWindowClosed) {
            int sup_c = Integer.parseInt(vstup1);
            String kat = vstup2;
            try {
                Text t = new Text(holder.zadanie4(sup_c, kat));
                t.setFont(Font.font("Cambria", 20));
                tfOutput.getChildren().add(t);
            } catch (IOException ex) {
                AlertManager.errorAlert("Chyba", ex.getClass().getName());
                Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    private void zadanie5() {
        String zadanie = "Zmena – na základe identifikačné čísla nehnuteľnosti umožní meniť jednotlivé údaje\n"
                + "o nehnuteľnosti (vrátane súpisného čísla, mena katastrálneho územia a popisu).";
        getInputs(3, zadanie, "Identifikačné číslo:", "Nové súpisné číslo:", "Nový kataster:", "Nový popis:", true, true, true, true);
        if (!inputWindowClosed) {
            int id = Integer.parseInt(vstup1);
            int sup_c = Integer.parseInt(vstup2);
            String kat = vstup3;
            String popis = vstup4;
            try {
                Text t = new Text(holder.zadanie5(id, sup_c, kat, popis));
                t.setFont(Font.font("Cambria", 20));
                tfOutput.getChildren().add(t);
            } catch (IOException ex) {
                AlertManager.errorAlert("Chyba", ex.getClass().getName());
                Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package semestralka2_us.Util;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

/**
 * Pomocná trieda na zobrazovanie upozornení a potvrdzovacích okien
 * @author MarekPC
 */
public class AlertManager {
    
    public static void notify(String message) {
        Alert alert = new Alert(Alert.AlertType.NONE, message, ButtonType.OK);
        
        alert.showAndWait();
    }
    
    
    public static boolean confirmAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.NONE, message, ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
        alert.setTitle(title);
        alert.showAndWait();
        return (alert.getResult() == ButtonType.YES);
        
    }
    
    public static void errorAlert(String title, String message){
        Alert alert = new Alert(Alert.AlertType.ERROR,message,ButtonType.OK);
        alert.setTitle(title);
        alert.showAndWait();
    }
    
}

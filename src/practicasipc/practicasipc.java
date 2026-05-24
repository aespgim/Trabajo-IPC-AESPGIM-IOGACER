/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package practicasipc;

/**
 *
 * @author sandr
 */
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class practicasipc extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
       
        Parent root = FXMLLoader.load(getClass().getResource("/vista/Login.fxml"));
        
        
        Scene scene = new Scene(root);
        
        
        stage.setScene(scene);
        stage.setTitle("ApexRun - Login"); 
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
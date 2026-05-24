/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package controlador;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.beans.binding.Bindings;       
import javafx.beans.binding.BooleanBinding; 
import javafx.event.ActionEvent;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.fxml.FXMLLoader;
import java.io.IOException;

/**
 * FXML Controller class
 *
 * @author sandr
 */
public class LoginControlador implements Initializable {

    @FXML
    private TextField txtUsuario;
    @FXML
    private PasswordField txtPassword;
    @FXML
    private Button btnIniciar;
    @FXML
    private Button btnRegistrar;
    @FXML
    private Label lblErrores;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        BooleanBinding camposVacios = Bindings.or(
            txtUsuario.textProperty().isEmpty(),
            txtPassword.textProperty().isEmpty()
        );
        
        btnIniciar.disableProperty().bind(camposVacios);
        
        if (lblErrores != null) {
            lblErrores.setText("");
        }
        
    }    
    
    @FXML
    private void handleIniciarSesion(ActionEvent event) {
        String nickname = txtUsuario.getText();
        String password = txtPassword.getText();

    try {
        
        upv.ipc.sportlib.SportActivityApp app = upv.ipc.sportlib.SportActivityApp.getInstance();
        
        
        boolean loginCorrecto = app.login(nickname, password);
        
        if (loginCorrecto) {
            
            
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/Principal.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("ApexRun - Panel Principal");

            stage.setWidth(1024); 
            stage.setHeight(768);

            stage.show();
            stage.centerOnScreen();
            
            Stage currentStage = (Stage) btnIniciar.getScene().getWindow();
            currentStage.close();
            
        } else {
            
            lblErrores.setText("Error: Usuario o contraseña incorrectos.");
            lblErrores.setVisible(true);
        }
        
        } catch (Exception e) {
            lblErrores.setText("Error al conectar con la base de datos.");
            lblErrores.setVisible(true);
            e.printStackTrace();
        }
        
    }
    
    @FXML
    private void handleRegistrar(ActionEvent event) {
        try {
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/Registro.fxml"));
            Parent root = loader.load();
        
            
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("ApexRun - Registro");
        
            
            stage.show();
        
            
            Stage currentStage = (Stage) btnRegistrar.getScene().getWindow();
            currentStage.close();
        
        } catch (IOException e) {
            lblErrores.setText("Error al cargar la pantalla de registro.");
            e.printStackTrace();
    }
}
    
    
}

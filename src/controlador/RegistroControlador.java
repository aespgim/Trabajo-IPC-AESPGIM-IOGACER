/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package controlador;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import upv.ipc.sportlib.SportActivityApp;
import upv.ipc.sportlib.User;

/**
 * FXML Controller class
 *
 * @author sandr
 */
public class RegistroControlador implements Initializable {

    @FXML
    private TextField txtNickname;
    @FXML
    private TextField txtEmail;
    @FXML
    private PasswordField txtPassword;
    @FXML
    private DatePicker dpFechaNacimiento;
    @FXML
    private Button btnAceptar;
    @FXML
    private Label lblErrorNickname;
    @FXML
    private Button btnCancelar;
    @FXML
    private Label lblErrorEmail;
    @FXML
    private Label lblErrorPassword;
    @FXML
    private Label lblErrorFecha;
    @FXML
    private Label lblErrorGeneral;

    /**
     * Initializes the controller class.
     */
    public void initialize(URL url, ResourceBundle rb) {
        
        limpiarErrores();

        dpFechaNacimiento.setEditable(false);
        
        txtNickname.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) { 
                if (!txtNickname.getText().isEmpty() && !User.checkNickName(txtNickname.getText())) {
                    lblErrorNickname.setText("Entre 6 y 15 caracteres (letras, números, - o _).");
                    lblErrorNickname.setVisible(true);
                } else {
                    lblErrorNickname.setText("");
                }
            }
        });

        txtEmail.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                if (!txtEmail.getText().isEmpty() && !User.checkEmail(txtEmail.getText())) {
                    lblErrorEmail.setText("Formato de email incorrecto.");
                    lblErrorEmail.setVisible(true);
                } else {
                    lblErrorEmail.setText("");
                }
            }
        });

        txtPassword.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                if (!txtPassword.getText().isEmpty() && !User.checkPassword(txtPassword.getText())) {
                    lblErrorPassword.setText("De 8 a 20 caracteres (mayúscula, minúscula, número y símbolo).");
                    lblErrorPassword.setVisible(true);
                } else {
                    lblErrorPassword.setText("");
                }
            }
        });
        
        dpFechaNacimiento.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !User.isOlderThan(newValue, 12)) {
                lblErrorFecha.setText("Debes ser mayor de 12 años.");
                lblErrorFecha.setVisible(true);
            } else {
                lblErrorFecha.setText("");
            }
        });

        
        BooleanBinding camposVacios = Bindings.or(
            txtNickname.textProperty().isEmpty(),
            Bindings.or(
                txtEmail.textProperty().isEmpty(),
                Bindings.or(
                    txtPassword.textProperty().isEmpty(),
                    dpFechaNacimiento.valueProperty().isNull()
                )
            )
        );
        btnAceptar.disableProperty().bind(camposVacios);
    }

    
    private void limpiarErrores() {
        lblErrorNickname.setText("");
        lblErrorEmail.setText("");
        lblErrorPassword.setText("");
        lblErrorFecha.setText("");
        lblErrorGeneral.setText("");
    }

    @FXML
    private void handleAceptar(ActionEvent event) {
        limpiarErrores();
        
        String nickname = txtNickname.getText();
        String email = txtEmail.getText();
        String password = txtPassword.getText();
        LocalDate fecha = dpFechaNacimiento.getValue();
        
        
        if (!User.isOlderThan(fecha, 12)) {
            lblErrorGeneral.setText("Error: Debes ser mayor de 12 años para registrarte.");
            lblErrorGeneral.setStyle("-fx-text-fill: red;");
            return;
        }

        try {
            SportActivityApp app = SportActivityApp.getInstance();
            
            
            boolean registrado = app.registerUser(nickname, email, password, fecha, "");
            
            if (registrado) {
                System.out.println("Usuario registrado con éxito.");
                volverAlLogin(); 
            } else {
                lblErrorGeneral.setText("Error: El usuario ya existe o los datos no son válidos.");
                lblErrorGeneral.setStyle("-fx-text-fill: red;");
            }
        } catch (Exception e) {
            lblErrorGeneral.setText("Error de conexión con la base de datos.");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCancelar(ActionEvent event) {
        volverAlLogin();
    }
    
    
    private void volverAlLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/Login.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("ApexRun - Login");
            stage.show();
            
            
            Stage currentStage = (Stage) btnCancelar.getScene().getWindow();
            currentStage.close();
            
        } catch (IOException e) {
            System.err.println("No se pudo cargar la vista Login.fxml");
            e.printStackTrace();
        }
    }
}

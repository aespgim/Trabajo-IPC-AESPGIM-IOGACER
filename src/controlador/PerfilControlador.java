/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package controlador;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import java.io.File;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import upv.ipc.sportlib.SportActivityApp;

/**
 * FXML Controller class
 *
 * @author sandr
 */
public class PerfilControlador implements Initializable {

    @FXML
    private ImageView imgAvatar;
    @FXML
    private Button btnCambiarImagen;
    @FXML
    private TextField txtNickname;
    @FXML
    private TextField txtCorreo;
    @FXML
    private Label lblErrorEmail;
    @FXML
    private PasswordField txtPassword;
    @FXML
    private Label lblErrorPassword;
    @FXML
    private DatePicker dateNacimiento;
    @FXML
    private Label lblErrorFecha;
    @FXML
    private Label lblErrorGeneral;
    @FXML
    private Button btnCancelar;
    @FXML
    private Button btnGuardar;
    @FXML
    private java.io.File archivoImagenSeleccionada;
    
    

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        try {
            upv.ipc.sportlib.User usuario = upv.ipc.sportlib.SportActivityApp.getInstance().getCurrentUser(); 
            
            txtNickname.setText(usuario.getNickName());
            txtCorreo.setText(usuario.getEmail());
            dateNacimiento.setValue(usuario.getBirthDate());
            
            if (usuario.getAvatar() != null) {
                imgAvatar.setImage(usuario.getAvatar());
            }
        } catch (Exception e) {
            System.err.println("Error al cargar los datos del usuario.");
        }
    }    

    @FXML
    private void seleccionarImagen(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar Nuevo Avatar");
        
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Archivos de Imagen", "*.png", "*.jpg", "*.jpeg")
        );
        
        archivoImagenSeleccionada = fileChooser.showOpenDialog(null);
        
        if (archivoImagenSeleccionada != null) {
            Image nuevaImagen = new Image(archivoImagenSeleccionada.toURI().toString());
            imgAvatar.setImage(nuevaImagen);
        }
    }

    @FXML
    private void handleCancelar(ActionEvent event) {
        
    }

    @FXML
    private void guardarPerfil(ActionEvent event) {
        try {
            upv.ipc.sportlib.User usuario = upv.ipc.sportlib.SportActivityApp.getInstance().getCurrentUser();
            

            String passFinal = txtPassword.getText().isEmpty() ? usuario.getPassword() : txtPassword.getText();
            
            String rutaAvatarFinal = (archivoImagenSeleccionada != null) ? archivoImagenSeleccionada.getAbsolutePath() : usuario.getAvatarPath();
            
            upv.ipc.sportlib.SportActivityApp.getInstance().updateCurrentUser(
                    txtCorreo.getText(), 
                    passFinal, 
                    dateNacimiento.getValue(), 
                    rutaAvatarFinal
            );
            
            System.out.println("¡Perfil actualizado correctamente!");
            
            javafx.stage.Stage stage = (javafx.stage.Stage) txtNickname.getScene().getWindow();
            stage.close();
            
        } catch (Exception e) {
            System.err.println("Error al guardar el perfil.");
        }
    }
}
    


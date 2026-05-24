/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package controlador;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import upv.ipc.sportlib.SportActivityApp;
import upv.ipc.sportlib.User;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * FXML Controller class
 *
 * @author sandr
 */
public class HistorialSesionesControlador implements Initializable {

    @FXML
    private ListView<String> listSesiones;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            upv.ipc.sportlib.User usuario = upv.ipc.sportlib.SportActivityApp.getInstance().getCurrentUser();
         java.time.format.DateTimeFormatter formateador = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm:ss");
            
            for (upv.ipc.sportlib.Session sesion : usuario.getSessions()) {              
                java.time.LocalDateTime fechaInicio = sesion.getStartTime();
                listSesiones.getItems().add("Acceso: " + fechaInicio.format(formateador));                
            }
            
            if (listSesiones.getItems().isEmpty()) {
                listSesiones.getItems().add("No hay registros de sesión anteriores.");
            }
        } catch (Exception e) {
            System.err.println("Error al cargar el historial de inicios de sesión.");
            e.printStackTrace();
        }
    }    
    
}

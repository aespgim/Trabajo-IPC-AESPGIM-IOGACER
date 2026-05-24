/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package controlador;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.AreaChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.shape.Polyline;
import javafx.stage.FileChooser;
import upv.ipc.sportlib.Activity;
import upv.ipc.sportlib.MapProjection;
import upv.ipc.sportlib.MapRegion;
import upv.ipc.sportlib.SportActivityApp;
import upv.ipc.sportlib.TrackPoint;


/**
 * FXML Controller class
 *
 * @author sandr
 */
public class PrincipalControlador implements Initializable {

    private Group zoomGroup;
    private Pane mapPane;
    private ContextMenu mapContextMenu;
    private boolean insertionMode = false;
    @FXML
    private ListView<Activity> map_listview;
    @FXML
    private ScrollPane map_scrollpane;
    @FXML
    private Slider zoom_slider;
    private MenuButton map_pin;
    @FXML
    private Label mousePosition;
    @FXML
    private SplitPane splitPane;
    @FXML
    private Button btnImportar;
    @FXML
    private Button btnCerrarSesion;
    @FXML
    private Label lblDistancia;
    @FXML
    private Label lblDuracion;
    @FXML
    private Label lblDesnivel;
    @FXML
    private AreaChart<Number, Number> chartDesnivel;
    private Activity actividadActual;
    @FXML
    private MenuItem btnModificar;
    @FXML
    private MenuItem btnHistorialSesiones;
 

   
    @FXML
    void zoomIn(ActionEvent event) {
        double sliderVal = zoom_slider.getValue();
        zoom_slider.setValue(sliderVal + 0.1);
    }

    @FXML
    void zoomOut(ActionEvent event) {
        double sliderVal = zoom_slider.getValue();
        zoom_slider.setValue(sliderVal - 0.1);
    }

    private void zoom(double scaleValue) {
        // Guardamos la posición del scroll antes de escalar
        double scrollH = map_scrollpane.getHvalue();
        double scrollV = map_scrollpane.getVvalue();

        // Aplicamos el zoom escalando el Group en ambos ejes
        zoomGroup.setScaleX(scaleValue);
        zoomGroup.setScaleY(scaleValue);

        // Restauramos la posición del scroll para que el centro visual
        // permanezca estable durante el zoom
        map_scrollpane.setHvalue(scrollH);
        map_scrollpane.setVvalue(scrollV);
    }

    //NOS HEMOS AYUDADO CON LA IA PARA SOLUCIONAR CIERTOS ERRORES QUE NO
    //SABIAMOS COMO SOLUCIONAR Y TAMBIEN PARA ALGUNOS ASPECTOS
    
    @FXML
    void listClicked(MouseEvent event) {

        Activity activity = map_listview.getSelectionModel().getSelectedItem();
        this.actividadActual = activity;
        
       
        if (activity == null) return;

        try {
            
            MapRegion region = activity.getSuggestedMap();
            File imgFile = new File(region.getImagePath());
            
            if (imgFile.exists()) {

                buildMap(imgFile);
                

                MapProjection proj = new MapProjection(region, mapPane.getPrefWidth(), mapPane.getPrefHeight());
                java.util.List<TrackPoint> puntos = activity.getTrackPoints();

                for (int i = 0; i < puntos.size() - 1; i++) {
                    TrackPoint tpActual = puntos.get(i);
                    TrackPoint tpSiguiente = puntos.get(i + 1);

                    Point2D p1 = proj.project(tpActual);
                    Point2D p2 = proj.project(tpSiguiente);

                    javafx.scene.shape.Line segmento = new javafx.scene.shape.Line(p1.getX(), p1.getY(), p2.getX(), p2.getY());
    
                    segmento.strokeWidthProperty().bind(javafx.beans.binding.Bindings.divide(3.0, zoom_slider.valueProperty()));

                    double velocidad = tpActual.speedTo(tpActual); 
                    
                    if (velocidad > 12.0) { 
                        segmento.setStroke(javafx.scene.paint.Color.GREEN);  // Muy rápido
                    } else if (velocidad > 6.0) { 
                        segmento.setStroke(javafx.scene.paint.Color.ORANGE); // Ritmo medio
                    } else { 
                    segmento.setStroke(javafx.scene.paint.Color.RED);    // Lento / Caminando
                    }
                        
                    mapPane.getChildren().add(segmento);
                }
                

                double distanciaKm = activity.getTotalDistance() / 1000.0;
                lblDistancia.setText(String.format("%.2f km", distanciaKm));
                
                java.time.Duration dur = activity.getDuration();
                lblDuracion.setText(String.format("%02d:%02d:%02d", dur.toHours(), dur.toMinutesPart(), dur.toSecondsPart()));
                
                lblDesnivel.setText(String.format("+%.2f m", activity.getElevationGain()));
                
            }
        } catch (Exception e) {
            System.err.println("Error al cargar la ruta desde la lista.");
            e.printStackTrace();
        }
    }


    private void buildMap(File imgFile) {

        if (!imgFile.exists()) {
            map_scrollpane.setContent(
                new Label("Imagen no encontrada: " + imgFile.getPath()));
            return;
        }


        Image img = new Image(imgFile.toURI().toString());
        double W = img.getWidth();
        double H = img.getHeight();


        mapPane = new Pane();
        mapPane.setPrefSize(W, H);
        mapPane.setMinSize(W, H);  
        mapPane.setMaxSize(W, H); 


        ImageView iv = new ImageView(img);
        iv.setFitWidth(W);
        iv.setFitHeight(H);
        mapPane.getChildren().add(iv);


        mapPane.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.SECONDARY) {
                
                onMapRightClick(e.getX(), e.getY());

            } else if (e.getButton() == MouseButton.PRIMARY && insertionMode) {
                
                insertionMode = false;
                mapPane.setStyle(""); 
                addPoi(e.getX(), e.getY());
            }
        });


        zoomGroup = new Group();
        Group contentGroup = new Group();
        zoomGroup.getChildren().add(mapPane);
        contentGroup.getChildren().add(zoomGroup);

        double zoom = zoom_slider.getValue();
        zoomGroup.setScaleX(zoom);
        zoomGroup.setScaleY(zoom);


        map_scrollpane.setContent(contentGroup);

    }

    private void onMapRightClick(double x, double y) {

        mapContextMenu.hide();

        final double clickX = x;
        final double clickY = y;
        mapContextMenu.getItems().get(0).setOnAction(e -> addPoi(clickX, clickY));
        mapContextMenu.getItems().get(1).setOnAction(e -> addCircle(clickX, clickY));


        mapContextMenu.show(
            mapPane.getScene().getWindow(),
            mapPane.localToScreen(x, y).getX(),
            mapPane.localToScreen(x, y).getY()
        );
    }

    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        
        chartDesnivel.setVisible(false);
        chartDesnivel.setManaged(false);
        
        

        zoom_slider.setMin(0.5);  
        zoom_slider.setMax(1.5);  
        zoom_slider.setValue(1.0); 
       
        zoom_slider.valueProperty().addListener(
            (observable, oldVal, newVal) -> zoom((Double) newVal)
        );

        
        MenuItem miText   = new MenuItem("📝 Añadir texto");
        MenuItem miCircle = new MenuItem("⭕ Añadir círculo");
        mapContextMenu = new ContextMenu(miText, miCircle);

        
        map_listview.setCellFactory(listView -> new ListCell<Activity>() {
            @Override
            protected void updateItem(Activity activity, boolean empty) {
                super.updateItem(activity, empty);

                if (empty || activity == null) {
                    setText(null);
                } else {
                    
                    String fecha = activity.getStartTime().toLocalDate().toString();
                    
                   
                    double distKm = activity.getTotalDistance() / 1000.0;
                    
                    
                    setText("Ruta " + fecha + " (" + String.format("%.2f", distKm) + " km)");
                }
            }
        });

       
        buildMap(new File("maps/valencia.jpg"));
    }

   
    
    @FXML
    private void showPosition(MouseEvent event) {
        mousePosition.setText(
            "sceneX: " + (int) event.getSceneX() +
            ", sceneY: " + (int) event.getSceneY() + "\n" +
            "         X: " + (int) event.getX() +
            ",          Y: " + (int) event.getY()
        );
    }


   
 
    private void addPoi(double x, double y) {

        if (actividadActual == null) {
            System.out.println("No hay ninguna actividad cargada para anotar.");
            return;
        }

       
        javafx.scene.control.TextInputDialog dialog = new javafx.scene.control.TextInputDialog("Mi punto de interés");
        dialog.setTitle("Nueva Anotación");
        dialog.setHeaderText("Añadir un punto en el mapa");
        dialog.setContentText("Introduce el texto de la anotación:");

       
        java.util.Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            String texto = result.get();

           
            javafx.scene.shape.Circle punto = new javafx.scene.shape.Circle(x, y, 6, javafx.scene.paint.Color.PURPLE);
            
            
            javafx.scene.control.Label etiqueta = new javafx.scene.control.Label(texto);
            etiqueta.setLayoutX(x + 10);
            etiqueta.setLayoutY(y - 10);
            etiqueta.setStyle("-fx-background-color: rgba(255, 255, 255, 0.8); -fx-padding: 3px; -fx-border-color: black; -fx-font-weight: bold;");
            
            mapPane.getChildren().addAll(punto, etiqueta);

            try {
                upv.ipc.sportlib.MapRegion region = actividadActual.getSuggestedMap();
                upv.ipc.sportlib.MapProjection proj = new upv.ipc.sportlib.MapProjection(region, mapPane.getPrefWidth(), mapPane.getPrefHeight());
                upv.ipc.sportlib.GeoPoint geoPoint = proj.unproject(x, y);

                upv.ipc.sportlib.Annotation anotacion = new upv.ipc.sportlib.Annotation(
                        upv.ipc.sportlib.AnnotationType.POINT, 
                        texto,                                 
                        "#800080",                            
                        2.0,                                   
                        java.util.List.of(geoPoint)            
                );

                upv.ipc.sportlib.SportActivityApp.getInstance().addAnnotation(actividadActual, anotacion);
                
                System.out.println("¡Anotación guardada con éxito en la actividad!");
                
            } catch (Exception e) {
                System.err.println("Error al intentar guardar la anotación en la librería.");
                e.printStackTrace();
            }
        }
    }


    private void addCircle(double x, double y) {
        Circle circle = new Circle(10, Color.RED); 
        circle.setCenterX(x);
        circle.setCenterY(y);
        mapPane.getChildren().add(circle); 
    }
    
    //AQUI TAMBIEN HEMOS IMPLEMENTADO LA IA POR EL MISMO MOTIVO QUUE EN "LISTcLICKED"
    
    @FXML
    private void handleImportar(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar archivo GPX");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos GPX", "*.gpx"));
        
        File file = fileChooser.showOpenDialog(btnImportar.getScene().getWindow());
        
        if (file != null) {
            try {
                
                SportActivityApp app = SportActivityApp.getInstance();
                Activity activity = app.importActivity(file);
                
                
                MapRegion region = activity.getSuggestedMap();
                File imgFile = new File(region.getImagePath());
                this.actividadActual = activity;
                
                if (imgFile.exists()) {
                    
                    buildMap(imgFile);
                    
                   
                    MapProjection proj = new MapProjection(region, mapPane.getPrefWidth(), mapPane.getPrefHeight());
                    
                    
                    java.util.List<TrackPoint> puntos = activity.getTrackPoints();

                    for (int i = 0; i < puntos.size() - 1; i++) {
                        TrackPoint tpActual = puntos.get(i);
                        TrackPoint tpSiguiente = puntos.get(i + 1);

                        Point2D p1 = proj.project(tpActual);
                        Point2D p2 = proj.project(tpSiguiente);

                        javafx.scene.shape.Line segmento = new javafx.scene.shape.Line(p1.getX(), p1.getY(), p2.getX(), p2.getY());
    
                        segmento.strokeWidthProperty().bind(javafx.beans.binding.Bindings.divide(3.0, zoom_slider.valueProperty()));

                        double velocidad = tpActual.speedTo(tpActual); 

                        if (velocidad > 12.0) { 
                            segmento.setStroke(javafx.scene.paint.Color.GREEN);  
                        } else if (velocidad > 6.0) { 
                            segmento.setStroke(javafx.scene.paint.Color.ORANGE); 
                        } else { 
                        segmento.setStroke(javafx.scene.paint.Color.RED);    
                        }
                        
                        mapPane.getChildren().add(segmento);
                    }
                    
                    dibujarPerfilDesnivel(activity, proj, mapPane);
                    
                    double distanciaKm = activity.getTotalDistance() / 1000.0;
                    lblDistancia.setText(String.format("Distancia: %.2f km", distanciaKm));

                    java.time.Duration dur = activity.getDuration();
                    long horas = dur.toHours();
                    long minutos = dur.toMinutesPart();
                    long segundos = dur.toSecondsPart();
                    lblDuracion.setText(String.format("Duración: %02d:%02d:%02d", horas, minutos, segundos));

                    lblDesnivel.setText(String.format("Desnivel +: %.2f m", activity.getElevationGain()));
                    
                    map_listview.getItems().add(activity);
                    
                } else {
                    System.err.println("Error: No se encuentra la imagen del mapa en " + imgFile.getPath());
                }
                
            } catch (Exception e) {
                System.err.println("Error al procesar el archivo GPX.");
                e.printStackTrace();
            }
        }
        
    }

    @FXML
    private void handleCerrarSesion(ActionEvent event) {
        try {
            
            SportActivityApp.getInstance().logout();
            
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/Login.fxml"));
            Parent root = loader.load();
            
            
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("ApeRun - Login");
            stage.show();
            
            
            Stage currentStage = (Stage) btnCerrarSesion.getScene().getWindow();
            currentStage.close();
            
        } catch (IOException e) {
            System.err.println("Error al intentar volver a la pantalla de Login.");
            e.printStackTrace();
        }
        
    }
    
    //PARA IMPLEMENTAR ESTA FUNCION "DIBUJARpERFILdESNIVEL" TAMBIEN HEMOS USADO LA IA 
    
    private void dibujarPerfilDesnivel(Activity activity, MapProjection proj, Pane mapa) {
        
        chartDesnivel.getData().clear();
        
        
        javafx.scene.chart.XYChart.Series<Number, Number> series = new javafx.scene.chart.XYChart.Series<>();
        series.setName("Perfil de Altitud");

        
        javafx.scene.shape.Circle marcador = new javafx.scene.shape.Circle(6, javafx.scene.paint.Color.BLUE);
        marcador.setVisible(false); 
        mapa.getChildren().add(marcador);

        
        double distanciaTotalKm = activity.getTotalDistance() / 1000.0;
        double stepKm = distanciaTotalKm / activity.getTrackPoints().size();
        double currentKm = 0.0;

        
        for (TrackPoint tp : activity.getTrackPoints()) {
            
            
            double elevacion = tp.getElevation(); 
            
            javafx.scene.chart.XYChart.Data<Number, Number> data = new javafx.scene.chart.XYChart.Data<>(currentKm, elevacion);
            
            javafx.scene.shape.Circle nodoInteractivo = new javafx.scene.shape.Circle(4, javafx.scene.paint.Color.TRANSPARENT);
            data.setNode(nodoInteractivo);

            nodoInteractivo.setOnMouseEntered(e -> {
                Point2D p = proj.project(tp);
                marcador.setCenterX(p.getX());
                marcador.setCenterY(p.getY());
                marcador.setVisible(true); 
            });

            
            nodoInteractivo.setOnMouseExited(e -> marcador.setVisible(false));

            series.getData().add(data);
            currentKm += stepKm;
        }
        
        
        chartDesnivel.getData().add(series);
        chartDesnivel.setVisible(true);
        chartDesnivel.setManaged(true);
    }

    @FXML
    private void abrirVentanaPerfil(ActionEvent event) {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/vista/Perfil.fxml"));
            javafx.scene.Parent root = loader.load();
            
            javafx.scene.Scene scene = new javafx.scene.Scene(root);
            javafx.stage.Stage stage = new javafx.stage.Stage();
            stage.setScene(scene);
            stage.setTitle("Modificar Perfil");
            
            
            stage.showAndWait(); 
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }

    @FXML
    private void abrirHistorialSesiones(ActionEvent event) {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/vista/HistorialSesiones.fxml"));
            javafx.scene.Parent root = loader.load();
            
            javafx.scene.Scene scene = new javafx.scene.Scene(root);
            javafx.stage.Stage stage = new javafx.stage.Stage();
            stage.setScene(scene);
            stage.setTitle("Historial de Inicios de Sesión");
            
            stage.showAndWait(); 
        } catch (Exception e) {
            System.err.println("Error al abrir el historial de sesiones.");
            e.printStackTrace();
        }
    }


}

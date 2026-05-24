/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;

import javafx.geometry.Point2D;

/**
 *
 * @author sandr
 */
public class Poi {
    private String code;
    private Point2D position;

    
    public Poi(String code, double x, double y) {
        this.code = code;
        this.position = new Point2D(x, y);
    }

    
    public String getCode() {
        return code;
    }

    public Point2D getPosition() {
        return position;
    }

    
    public void setCode(String code) {
        this.code = code;
    }

    public void setPosition(Point2D position) {
        this.position = position;
    }
    
}

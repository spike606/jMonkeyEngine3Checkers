/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.math.Vector3f;

/**
 *
 * @author Krystus Klasa przechowuje informacje o polozeniu na tablicy (2D) oraz
 * polozeniu w swiecie 3D
 */
public class Field {

    //czy na to pole moze wejsc pionek
    private boolean accessible;
    //lokalizacja w 3D
    private Vector3f fieldWorldCoordinates;
    //lokalizacja w tabllicy 2D
    private int tabXPosition;
    private int tabYPosition;
    private int checkerId;//-1 - brak bierki na tym polu bo id bierek od 0
    private int checkerColor;//0 - brak bierki na tym polu
    private int checkerQueenColor;//0 - brak bierki na tym polu

    public int getCheckerQueenColor() {
        return checkerQueenColor;
    }

    public void setCheckerQueenColor(int checkerQueenColor) {
        this.checkerQueenColor = checkerQueenColor;
    }

    public int getCheckerColor() {
        return checkerColor;
    }

    public void setCheckerColor(int checkerColor) {
        this.checkerColor = checkerColor;
    }

    public int getCheckerId() {
        return checkerId;
    }

    public void setCheckerId(int checkerId) {
        this.checkerId = checkerId;
    }

    public Vector3f getFieldWorldCoordinates() {
        return fieldWorldCoordinates;
    }

    public void setFieldWorldCoordinates(Vector3f fieldWorldCoordinates) {
        this.fieldWorldCoordinates = fieldWorldCoordinates;
    }

    public int getTabXPosition() {
        return tabXPosition;
    }

    public void setTabXPosition(int tabXPosition) {
        this.tabXPosition = tabXPosition;
    }

    public int getTabYPosition() {
        return tabYPosition;
    }

    public void setTabYPosition(int tabYPosition) {
        this.tabYPosition = tabYPosition;
    }

    public boolean isAccessible() {
        return accessible;
    }

    public void setAccessible(boolean accessible) {
        this.accessible = accessible;
    }
}

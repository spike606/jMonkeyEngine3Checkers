package mygame;

import com.jme3.math.Vector3f;

/**
 * Class store info about 2D board and 3D board
 */
public class Field {

    private boolean accessible;
    private Vector3f fieldWorldCoordinates;//3D location
    // 2D location
    private int tabXPosition;
    private int tabYPosition;
    private int checkerId;//-1 - no checker on this field 
    private int checkerColor;//0 - no checker on this field
    private int checkerQueenColor;//0 - no checker on this field

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

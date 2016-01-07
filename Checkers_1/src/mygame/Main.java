package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.font.BitmapText;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.asset.plugins.ZipLocator;

/**
 * Sample 3 - how to load an OBJ model, and OgreXML model, a material/texture,
 * or text.
 */
public class Main extends SimpleApplication {

    public static void main(String[] args) {
        Main app = new Main();
        app.start();
    }

    @Override
    public void simpleInitApp() {

        /* adding scene*/
//        assetManager.registerLocator("town.zip", ZipLocator.class);
//        Spatial gameLevel = assetManager.loadModel("main.scene");
//        gameLevel.setLocalTranslation(0, -5.2f, 0);
//        gameLevel.setLocalScale(2);
//        rootNode.attachChild(gameLevel);
        /**
         * ***
         */
//        Spatial teapot = assetManager.loadModel("Models/Teapot/Teapot.obj");
//        Material mat_default = new Material(
//                assetManager, "Common/MatDefs/Misc/ShowNormals.j3md");
//        teapot.setMaterial(mat_default);
//        rootNode.attachChild(teapot);
        // Create a wall with a simple texture from test_data
//        Box box = new Box(2.5f, 2.5f, 1.0f);
//        Spatial wall = new Geometry("Box", box);
//        Material mat_brick = new Material(
//                assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
//        mat_brick.setTexture("ColorMap",
//                assetManager.loadTexture("Textures/Terrain/BrickWall/BrickWall.jpg"));
//        wall.setMaterial(mat_brick);
//        wall.setLocalTranslation(2.0f, -2.5f, 0.0f);
//        rootNode.attachChild(wall);
        // Display a line of text with a default font1
//        guiNode.detachAllChildren();
//        guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
//        BitmapText helloText = new BitmapText(guiFont, false);
//        helloText.setSize(guiFont.getCharSet().getRenderedSize());
//        helloText.setText("Hello World");
//        helloText.setLocalTranslation(300, helloText.getLineHeight(), 0);
//        guiNode.attachChild(helloText);
        // Load a model from test_data (OgreXML + material + texture)
// Load a model from test_data (OgreXML + material + texture)
//        Spatial ninja = assetManager.loadModel("Models/Sphere/Sphere.mesh.xml");
//        ninja.scale(5f, 5f, 5f);
//        ninja.rotate(0.0f, -3.0f, 0.0f);
//        ninja.setLocalTranslation(10f, 5f, -2.0f);
//        rootNode.attachChild(ninja);
        
        
        
        /* LOAD BOARD*/
        Spatial board = assetManager.loadModel("Models/chessboard1/chessboard1.j3o");
        board.scale(1f, 1f, 1f);
        board.rotate(0.0f, -3.0f, 0.0f);
        board.setLocalTranslation(0f, 0f, 0f);
        rootNode.attachChild(board);
        /**************/
        /* LOAD CHECKER*/
        Spatial checker_bialy = assetManager.loadModel("Models/models_checkers/Checker_model_white.j3o");
        checker_bialy.scale(0.5f, 0.5f, 0.5f);
        checker_bialy.rotate(0.0f, -3.0f, 0.0f);
        checker_bialy.setLocalTranslation(0.1f, 0.1f, 0.1f);
        rootNode.attachChild(checker_bialy);
        
        Spatial checker_bialy_dama = assetManager.loadModel("Models/models_checkers/Checker_model_white_queen.j3o");
        checker_bialy_dama.scale(0.5f, 0.5f, 0.5f);
        checker_bialy_dama.rotate(0.0f, -3.0f, 0.0f);
        checker_bialy_dama.setLocalTranslation(0.3f, 0.3f, 0.3f);
        rootNode.attachChild(checker_bialy_dama);
        
        Spatial checker_czarny = assetManager.loadModel("Models/models_checkers/Checker_model_black.j3o");
        checker_czarny.scale(0.5f, 0.5f, 0.5f);
        checker_czarny.rotate(0.0f, -3.0f, 0.0f);
        checker_czarny.setLocalTranslation(-10f, -10f, -10f);
        rootNode.attachChild(checker_czarny);
        
        Spatial checker_czarny_dama = assetManager.loadModel("Models/models_checkers/Checker_model_black_queen.j3o");
        checker_czarny_dama.scale(0.5f, 0.5f, 0.5f);
        checker_czarny_dama.rotate(0.0f, -3.0f, 0.0f);
        checker_czarny_dama.setLocalTranslation(20f, 20f, 20f);
        rootNode.attachChild(checker_czarny_dama);
        /**************/
        
        
        
        
        
        
        // You must add a directional light to make the model visible!
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection(new Vector3f(-0.1f, -0.7f, -1.0f).normalizeLocal());
        rootNode.addLight(sun);

    }
}
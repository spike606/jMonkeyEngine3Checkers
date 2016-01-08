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
import com.jme3.bounding.BoundingBox;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.math.ColorRGBA;
import com.jme3.util.SkyFactory;

/**
 * Sample 3 - how to load an OBJ model, and OgreXML model, a material/texture,
 * or text.
 */
public class Main extends SimpleApplication {
        
    public static void main(String[] args) {
        Main app = new Main();
        app.start();
        
    }
Spatial checker_czarny;
    @Override
    public void simpleInitApp() {

        /* adding scene*/
//        assetManager.registerLocator("town.zip", ZipLocator.class);
//        Spatial gameLevel = assetManager.loadModel("main.scene");
//        gameLevel.setLocalTranslation(0, -5.2f, 0);
//        gameLevel.setLocalScale(2);
//        rootNode.attachChild(gameLevel);
       
        
        
        /* LOAD BOARD*/
        Spatial board = assetManager.loadModel("Models/board/chessboard1.j3o");
        board.scale(1f, 1f, 1f);
        board.rotate(0.0f, 0f, 0.0f);
        board.setLocalTranslation(0f, 0f, 0f);
        rootNode.attachChild(board);
        /**************/
        /* LOAD CHECKER*/
//        Spatial checker_bialy = assetManager.loadModel("Models/checkers/Checker_model_white.j3o");
//        checker_bialy.scale(0.5f, 0.5f, 0.5f);
//        checker_bialy.rotate(0.0f, -3.0f, 0.0f);
//        checker_bialy.setLocalTranslation(0f, 0f, 0f);
//        rootNode.attachChild(checker_bialy);
        
//        Spatial checker_bialy_dama = assetManager.loadModel("Models/checkers/Checker_model_white_queen.j3o");
//        checker_bialy_dama.scale(0.5f, 0.5f, 0.5f);
//        checker_bialy_dama.rotate(0.0f, -3.0f, 0.0f);
//        checker_bialy_dama.setLocalTranslation(0.3f, 0.3f, 0.3f);
//        rootNode.attachChild(checker_bialy_dama);
        
        checker_czarny = assetManager.loadModel("Models/checkers/Checker_model_black.j3o");
        checker_czarny.scale(1f, 1f, 1f);
        checker_czarny.rotate(0.0f, 0f, 0.0f);
        checker_czarny.setLocalTranslation(0.0f, 0.63f, 0.0f);
        rootNode.attachChild(checker_czarny);
        
        //prznies
//        checker_czarny.move(2f, 2f, 2f);
        
        
//        Spatial checker_czarny_dama = assetManager.loadModel("Models/checkers/Checker_model_black_queen.j3o");
//        checker_czarny_dama.scale(0.5f, 0.5f, 0.5f);
//        checker_czarny_dama.rotate(0.0f, -3.0f, 0.0f);
//        checker_czarny_dama.setLocalTranslation(20f, 20f, 20f);
//        rootNode.attachChild(checker_czarny_dama);
        /**************/

        
        // You must add a directional light to make the model visible!
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection(new Vector3f(-0.1f, -0.7f, -1.0f).normalizeLocal());
        rootNode.addLight(sun);
        
        
        //sky
        //viewPort.setBackgroundColor(ColorRGBA.Blue);       
//        rootNode.attachChild(SkyFactory.createSky(
//            assetManager, "Textures/sky/BrightSky.dds", false));
                rootNode.attachChild(SkyFactory.createSky(
            assetManager, "Textures/sky/space.dds", false));
        initKeys(); // load my custom keybinding

    }
    
        protected Spatial thing;
    /* Use the main event loop to trigger repeating actions. */
    @Override
    public void simpleUpdate(float tpf) {//time per second
        // make the player rotate:
        System.out.println(checker_czarny.getLocalTranslation());
        
        System.out.println(((BoundingBox)checker_czarny.getWorldBound()).getXExtent()); 
        System.out.println(((BoundingBox)checker_czarny.getWorldBound()).getYExtent()); 
        System.out.println(((BoundingBox)checker_czarny.getWorldBound()).getZExtent()); 




//        checker_czarny.scale(0.3f,0.3f,0.3f);
//        checker_czarny.scale(0.2f,0.2f,0.2f);
//        checker_czarny.scale(0.1f,0.1f,0.1f);

    }
    
    
      /** Custom Keybinding: Map named actions to inputs. */
  private void initKeys() {
    // You can map one or several inputs to one named action
    inputManager.addMapping("Left",   new KeyTrigger(KeyInput.KEY_J));
    inputManager.addMapping("Right",  new KeyTrigger(KeyInput.KEY_K));
    
    // Add the names to the action listener.
    inputManager.addListener(analogListener,"Left", "Right");
  }
    private AnalogListener analogListener = new AnalogListener() {
    public void onAnalog(String name, float value, float tpf) {

        if (name.equals("Right")) {
          Vector3f v = checker_czarny.getLocalTranslation();
          checker_czarny.setLocalTranslation(v.x + value*speed, v.y, v.z);
        }
        if (name.equals("Left")) {
          Vector3f v = checker_czarny.getLocalTranslation();
          checker_czarny.setLocalTranslation(v.x - value*speed, v.y, v.z);
        }

    }
  };
}
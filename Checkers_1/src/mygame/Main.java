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
import com.jme3.collision.CollisionResults;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.Light;
import com.jme3.light.LightList;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Node;
import com.jme3.shadow.BasicShadowRenderer;
import com.jme3.shadow.DirectionalLightShadowFilter;
import com.jme3.shadow.DirectionalLightShadowRenderer;
import com.jme3.system.AppSettings;
import com.jme3.util.SkyFactory;

/**
 * Sample 3 - how to load an OBJ model, and OgreXML model, a material/texture,
 * or text.
 */
public class Main extends SimpleApplication {

    //przod
    private static final Vector3f cam1Loc = new Vector3f(-6.2657156f, 14.529437f, 8.240483f);
    private static final Vector3f cam1Dir = new Vector3f(-0.0028091485f, -0.7335623f, -0.67961645f);
    private static final Vector3f cam1Up = new Vector3f(-0.003032215f, 0.67962223f, -0.733556f);
    private static final Vector3f cam1Left = new Vector3f(-0.99999154f, -7.532071E-8f, -0.0041334783f);
    //tyl
    private static final Vector3f cam2Loc = new Vector3f(-6.150441f, 14.342193f, -20.968948f);
    private static final Vector3f cam2Dir = new Vector3f(-0.004009098f, -0.7174297f, 0.6966194f);
    private static final Vector3f cam2Up = new Vector3f(-0.0015367586f, 0.6966286f, 0.7174303f);
    private static final Vector3f cam2Left = new Vector3f(0.99999076f, -0.0018057127f, 0.0038953684f);
    //z gory
    private static final Vector3f cam3Loc = new Vector3f(-6.1873684f, 19.925018f, -3.8860335f);
    private static final Vector3f cam3Dir = new Vector3f(0.0017965112f, -0.9943187f, -0.10642803f);
    private static final Vector3f cam3Up = new Vector3f(-1.7530489E-4f, 0.10642797f, -0.9943204f);
    private static final Vector3f cam3Left = new Vector3f(-0.99999833f, -0.0018049651f, -1.6890059E-5f);
    //z prawej
    private static final Vector3f cam4Loc = new Vector3f(8.424002f, 14.017611f, -6.085185f);
    private static final Vector3f cam4Dir = new Vector3f(-0.7035848f, -0.7105932f, -0.005055666f);
    private static final Vector3f cam4Up = new Vector3f(-0.71059096f, 0.70360076f, -0.0025426447f);
    private static final Vector3f cam4Left = new Vector3f(-0.005363941f, -0.0018036067f, 0.9999839f);
    // z rogu
    private static final Vector3f cam5Loc = new Vector3f(-16.373577f, 19.19087f, -17.024492f);
    private static final Vector3f cam5Dir = new Vector3f(0.37362808f, -0.82820004f, 0.4177161f);
    private static final Vector3f cam5Up = new Vector3f(0.55454195f, 0.5604297f, 0.6151437f);
    private static final Vector3f cam5Left = new Vector3f(0.7435626f, -0.0018061101f, -0.6686639f);
    /*STALE DO POZYCJONOWANIA*/
    private static final float X_CELL = 1.809044164f;
    private static final float Z_CELL = 1.802715f;
    private final static float CELL_POS_Y = 1.4552078f;//poziom
    //shadows
    final int SHADOWMAP_SIZE = 2048;
    /*TABLICE DO BIEREK */
    /* NODES - OD NAJWYSZYCH*/
    //ROOTNODE
    Node game_node = new Node("Game");//zawiera board i bierki
    Node board_node = new Node("boardNode");
    Node checkers_node = new Node("checkers_node");
    Node white_node = new Node("whiteCheckersNode");//zawiera wszystkie biale
    Node black_node = new Node("blackCheckersNode");
    Node[] black_checkers_nodes;//tablica z nodami kazdy ma bierke
    Node[] white_checkers_nodes;
    Spatial[] black_checkers = new Spatial[12];//tablica spatiali
    Spatial[] white_checkers = new Spatial[12];
    /**
     * *****
     */
    /* SWIATLA */
    private static final AmbientLight blueLight = new AmbientLight();//zaznaczona bierka
    private static final AmbientLight redLight = new AmbientLight();//bierka do bicia
    private static final Vector3f sunLightDirection = new Vector3f(-0.9f, -1.2f, -1.0f);

    /* pomocnicze*/
    Vector3f selectedPointCoordinates;

    /*kolumny planszy*/
    private static final float FIRST_COL_X = 0.9304199f;
    private static final float COL_X_DIFF = 1.809f;
    private static final float FIRST_COL_Z = 0.9129584f;
    private static final float COL_Z_DIFF = 1.809f;
    private float[] colXCoordinates = new float[9];
    private float[] colZCoordinates = new float[9];
    /*pozycje gdzie bierki moga sie znajdowac*/
    private Field[][] boardFields = new Field[8][8];
    private static final float[] columns = {-12.620531f, -10.811487f, -9.002442f, -7.1933985f, -5.384354f,
        -3.5753102f, -1.766266f, 0.042778164f};
    private static final float[] rows = {-12.619004f, -10.81629f, -9.013575f, -7.21086f, -5.408145f,
        -3.60543f, -1.802715f, 0.0f};

    /**
     * ***
     */
    public static void main(String[] args) {
        Main app = new Main();
        
        
        
        AppSettings settings = new AppSettings(true);

        settings.setFrameRate(60);

        app.setSettings(settings);
        
        app.start();
    }

    @Override
    public void simpleInitApp() {

        /* INFO OFF*/
        setDisplayFps(false);
        setDisplayStatView(false);

        /* LOAD BOARD*/
        Spatial board = assetManager.loadModel("Models/board/chessboard.j3o");
        board.scale(1f, 1f, 1f);
        board.rotate(0.0f, 0f, 0.0f);
        board.setLocalTranslation(0f, 0f, 0f);
        board.setShadowMode(ShadowMode.CastAndReceive);

        board_node.attachChild(board);
        game_node.attachChild(board_node);
        rootNode.attachChild(game_node);

        setUpCheckers();

        blueLight.setColor(ColorRGBA.Blue.mult(1f));
        redLight.setColor(ColorRGBA.Red.mult(1f));


        // You must add a directional light to make the model visible!
        DirectionalLight sun = new DirectionalLight();
        sun.setName("Sun");
        sun.setDirection(sunLightDirection.normalizeLocal());
        rootNode.addLight(sun);


        /* Drop shadows */
        DirectionalLightShadowRenderer dlsr = new DirectionalLightShadowRenderer(assetManager, SHADOWMAP_SIZE, 1);
        dlsr.setLight(sun);
        viewPort.addProcessor(dlsr);
        DirectionalLightShadowFilter dlsf = new DirectionalLightShadowFilter(assetManager, SHADOWMAP_SIZE, 3);
        dlsf.setLight(sun);
        dlsf.setEnabled(true);
        FilterPostProcessor fpp = new FilterPostProcessor(assetManager);
        fpp.addFilter(dlsf);
        viewPort.addProcessor(fpp);

        /* cam 1 */
        cam.setFrame(cam1Loc, cam1Left, cam1Up, cam1Dir);

        flyCam.setEnabled(true);
      flyCam.setMoveSpeed(10);






        //sky
//      viewPort.setBackgroundColor(ColorRGBA.Blue);       
//       rootNode.attachChild(SkyFactory.createSky(
//            assetManager, "Textures/sky/BrightSky.dds", false));
        rootNode.attachChild(SkyFactory.createSky(
                assetManager, "Textures/sky/space.dds", false));

        // load my custom keybinding
        initKeys();

        //calculate coordinates for board
        calculateColumns();
        calculateRows();


        setCoordinatesWhereCheckersCanBe();

    }

    /* Use the main event loop to trigger repeating actions. */
    @Override
    public void simpleUpdate(float tpf) {//time per second
//        System.out.println("Cam location: " + cam.getLocation());
//        System.out.println("Cam up : " + cam.getUp());
//        System.out.println("Cam left : " + cam.getLeft());
//        System.out.println("Cam direction : " + cam.getDirection());
        // make the player rotate:
//        System.out.println(checker_czarny.getLocalTranslation());
//        
//        System.out.println("X: " + ((BoundingBox)checker_czarny.getWorldBound()).getXExtent()); 
//        System.out.println("Y: " + ((BoundingBox)checker_czarny.getWorldBound()).getYExtent()); 
//        System.out.println("Z: " + ((BoundingBox)checker_czarny.getWorldBound()).getZExtent()); 
//        checker_czarny.scale(0.3f,0.3f,0.3f);
//        checker_czarny.scale(0.2f,0.2f,0.2f);
//        checker_czarny.scale(0.1f,0.1f,0.1f);
    }

    /**
     * Custom Keybinding: Map named actions to inputs.
     */
    private void initKeys() {
        // You can map one or several inputs to one named action
        inputManager.addMapping("Cam1", new KeyTrigger(KeyInput.KEY_1));
        inputManager.addMapping("Cam2", new KeyTrigger(KeyInput.KEY_2));
        inputManager.addMapping("Cam3", new KeyTrigger(KeyInput.KEY_3));
        inputManager.addMapping("Cam4", new KeyTrigger(KeyInput.KEY_4));
        inputManager.addMapping("Cam5", new KeyTrigger(KeyInput.KEY_5));

        inputManager.addMapping("Move", new KeyTrigger(KeyInput.KEY_6));



        inputManager.addMapping("Left", new KeyTrigger(KeyInput.KEY_J));
        inputManager.addMapping("Right", new KeyTrigger(KeyInput.KEY_K));
        inputManager.addMapping("Up", new KeyTrigger(KeyInput.KEY_I));
        inputManager.addMapping("Down", new KeyTrigger(KeyInput.KEY_M));
        inputManager.addMapping("Far", new KeyTrigger(KeyInput.KEY_U));
        inputManager.addMapping("Close", new KeyTrigger(KeyInput.KEY_O));


        inputManager.addMapping("Click", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));

        // Add the names to the action listener.
        inputManager.addListener(analogListener, "Left", "Right", "Up", "Down", "Far", "Close");
        inputManager.addListener(actionListener, "Cam1", "Cam2", "Cam3", "Cam4", "Cam5", "Click", "Move");

    }
    private ActionListener actionListener = new ActionListener() {
        public void onAction(String name, boolean keyPressed, float tpf) {
            if (name.equals("Move") && !keyPressed) {
                           System.out.println("Move to: " +  boardFields[4][1].getFieldWorldCoordinates().toString());
    
//    Node[] white_checkers_nodes;
//    Spatial[] white_checkers = new Spatial[12];
                           
    white_checkers_nodes[11].setLocalTranslation(boardFields[4][1].getFieldWorldCoordinates().subtract(new Vector3f(0.3f, 0.0f,0.3f)));
//                white_checkers_nodes[11].move(boardFields[4][1].getFieldWorldCoordinates().getX(),
//                        boardFields[4][1].getFieldWorldCoordinates().getY(), boardFields[4][1].getFieldWorldCoordinates().getZ());
//                                 white_checkers[11].setLocalTranslation(boardFields[4][1].getFieldWorldCoordinates());
//        white_checkers_nodes[11].setLocalTranslation(boardFields[4][1].getFieldWorldCoordinates());
//                white_checkers[11].move(boardFields[4][1].getFieldWorldCoordinates().getX(),
//                        boardFields[4][1].getFieldWorldCoordinates().getY(), boardFields[4][1].getFieldWorldCoordinates().getZ());
            }
            if (name.equals("Cam1") && !keyPressed) {
                cam.setFrame(cam1Loc, cam1Left, cam1Up, cam1Dir);
            }
            if (name.equals("Cam2") && !keyPressed) {
                cam.setFrame(cam2Loc, cam2Left, cam2Up, cam2Dir);
            }
            if (name.equals("Cam3") && !keyPressed) {
                cam.setFrame(cam3Loc, cam3Left, cam3Up, cam3Dir);
            }
            if (name.equals("Cam4") && !keyPressed) {
                cam.setFrame(cam4Loc, cam4Left, cam4Up, cam4Dir);
            }
            if (name.equals("Cam5") && !keyPressed) {
                cam.setFrame(cam5Loc, cam5Left, cam5Up, cam5Dir);
            }
            if (name.equals("Click") && !keyPressed) {

                // Reset results list.
                CollisionResults results = new CollisionResults();
                // Convert screen click to 3d position
                Vector2f click2d = inputManager.getCursorPosition();
                Vector3f click3d = cam.getWorldCoordinates(new Vector2f(click2d.x, click2d.y), 0f).clone();
                Vector3f dir = cam.getWorldCoordinates(new Vector2f(click2d.x, click2d.y), 1f).subtractLocal(click3d).normalizeLocal();
                // Aim the ray from the clicked spot forwards.
                Ray ray = new Ray(click3d, dir);

                System.out.println("Ray: " + ray.toString());

                // Collect intersections between ray and all nodes in results list.
                game_node.collideWith(ray, results);//koliduje tylko z bierkami i plansza

                if (results.size() > 0) {

                    // (For each “hit”, we know distance, impact point, geometry.)
//                    float dist = results.getCollision(0).getDistance();
//                    Vector3f pt = results.getCollision(0).getContactPoint();

                    selectedPointCoordinates = results.getClosestCollision().getContactPoint();

                    // The closest result is the target that the player picked:
                    Node checkerNode = results.getClosestCollision().getGeometry().getParent().getParent().getParent().getParent().getParent();

                    if (!checkerNode.getName().equals("Root Node")) {
                        //wypisz wspolrzedne klikniecia na planszy
                        System.out.println("Click position: X, Y, Z: " + selectedPointCoordinates);
                        String checkerId = checkerNode.getUserData("id").toString();
                        System.out.println("Checker Id: " + checkerId);
                        System.out.println("Checker name(node): " + checkerNode.getName());

                        if (rootNode.getChild(checkerNode.getName()).getUserData("selected").equals("false")) {
                            selectChecker(checkerNode);
                        } else if (rootNode.getChild(checkerNode.getName()).getUserData("selected").equals("true")) {
                            diselectChecker(checkerNode);
                        }
                    } else {
                        System.out.println("Field selected. Coordinates: X, Y, Z: " + selectedPointCoordinates);

                    }
                    getBoardField(selectedPointCoordinates);
                }
            }
        }
    };
    private AnalogListener analogListener = new AnalogListener() {
        public void onAnalog(String name, float value, float tpf) {
//            if (name.equals("Right")) {
//          Vector3f v = white_checkers[12].getLocalTranslation();
//          white_checkers_nodes[11].move(boardFields[1][4].getFieldWorldCoordinates().getX(),
//                  boardFields[1][4].getFieldWorldCoordinates().getY(), boardFields[1][4].getFieldWorldCoordinates().getZ());
//          
//            }
//        if (name.equals("Right")) {
//          Vector3f v = checker_czarny.getLocalTranslation();
//          checker_czarny.setLocalTranslation(v.x + value*speed, v.y , v.z);
//        }
//        if (name.equals("Left")) {
//          Vector3f v = checker_czarny.getLocalTranslation();
//          checker_czarny.setLocalTranslation(v.x - value*speed, v.y, v.z);
//        }
//        if (name.equals("Up")) {
//          Vector3f v = checker_czarny.getLocalTranslation();
//          checker_czarny.setLocalTranslation(v.x,  v.y + value*speed, v.z);
//        }
//        if (name.equals("Down")) {
//          Vector3f v = checker_czarny.getLocalTranslation();
//          checker_czarny.setLocalTranslation(v.x , v.y - value*speed, v.z);
//        }
//        if (name.equals("Far")) {
//          Vector3f v = checker_czarny.getLocalTranslation();
//          checker_czarny.setLocalTranslation(v.x,  v.y , v.z + value*speed);
//        }
//        if (name.equals("Close")) {
//          Vector3f v = checker_czarny.getLocalTranslation();
//          checker_czarny.setLocalTranslation(v.x , v.y, v.z  - value*speed);
//        }
        }
    };

    private void setUpCheckers() {

        black_checkers_nodes = new Node[12];
        white_checkers_nodes = new Node[12];
        for (int i = 0; i < 12; i++) {
            white_checkers_nodes[i] = new Node("WhiteNode" + i);
            black_checkers_nodes[i] = new Node("BlackNode" + (i + 12));
        }


        float cell_pos_x = 0.042778164f;
        float cell_pos_z = 0.0f;
        for (int i = 0; i < 12; i++) {
            white_checkers[i] = assetManager.loadModel("Models/Ch_white/Ch_white.j3o");
            black_checkers[i] = assetManager.loadModel("Models/Ch_black/Ch_black.j3o");

            if (i < 4) {
                cell_pos_x = 0.042778164f - i * X_CELL - X_CELL * (i + 1);
                cell_pos_z = 0.0f;
            }
            if (i > 3 && i < 8) {
                cell_pos_x = 0.042778164f - X_CELL * (i - 4) - X_CELL * (i - 4);
                cell_pos_z = 0.0f - Z_CELL;
            }
            if (i > 7) {
                cell_pos_x = 0.042778164f - (i - 8) * X_CELL - X_CELL * (i - 8 + 1);
                cell_pos_z = 0.0f - Z_CELL * 2;
            }
//            System.out.println("White checker. Id: " + i + " Position: X, Y, Z: " + cell_pos_x + " " + CELL_POS_Y + " " + cell_pos_z);
            white_checkers_nodes[i].setLocalTranslation(cell_pos_x, CELL_POS_Y, cell_pos_z);
            white_checkers[i].setShadowMode(ShadowMode.CastAndReceive);



            if (i < 4) {
                cell_pos_x = 0.042778164f - X_CELL * i - X_CELL * (i);
                cell_pos_z = 0.0f - Z_CELL * 7;
            }
            if (i > 3 && i < 8) {
                cell_pos_x = 0.042778164f - (i - 4) * X_CELL - X_CELL * (i - 4 + 1);
                cell_pos_z = 0.0f - Z_CELL * 6;
            }
            if (i > 7) {
                cell_pos_x = 0.042778164f - X_CELL * (i - 8) - X_CELL * (i - 8);
                cell_pos_z = 0.0f - Z_CELL * 5;
            }
//            System.out.println("Black checker. Id: " + (i + 12) + " Position: X, Y, Z: " + cell_pos_x + " " + CELL_POS_Y + " " + cell_pos_z);

            black_checkers_nodes[i].setLocalTranslation(cell_pos_x, CELL_POS_Y, cell_pos_z);
            black_checkers[i].setShadowMode(ShadowMode.CastAndReceive);


            //dodaj id
            white_checkers_nodes[i].attachChild(white_checkers[i]);
            white_checkers_nodes[i].setUserData("id", i);
            white_checkers_nodes[i].setUserData("selected", "false");

            black_checkers_nodes[i].attachChild(black_checkers[i]);
            black_checkers_nodes[i].setUserData("id", i + 12);
            black_checkers_nodes[i].setUserData("selected", "false");

            white_node.attachChild(white_checkers_nodes[i]);
            black_node.attachChild(black_checkers_nodes[i]);

        }
        //tymczasowo - wypelnij reszte pol bierkami
//        Spatial white_checkers2[] = new Spatial[12];
//        for (int i = 4; i < 12; i++) {
//            white_checkers2[i] = assetManager.loadModel("Models/Ch_white/Ch_white.j3o");
//            if (i > 3 && i < 8) {
//                cell_pos_x = 0.042778164f - X_CELL * (i - 4) - X_CELL * (i - 4);
//                cell_pos_z = 0.0f - Z_CELL * 3;
//            }
//            if (i > 7) {
//                cell_pos_x = 0.042778164f - (i - 8) * X_CELL - X_CELL * (i - 8 + 1);
//                cell_pos_z = 0.0f - Z_CELL * 4;
//            }
//            white_checkers2[i].setLocalTranslation(cell_pos_x, CELL_POS_Y, cell_pos_z);
//            white_node.attachChild(white_checkers2[i]);
//
//        }


        /**
         * PODSWIETLENIE
         */
        black_checkers_nodes[2].addLight(redLight);
        /**
         * **
         */
        checkers_node.attachChild(white_node);
        checkers_node.attachChild(black_node);

        game_node.attachChild(checkers_node);

    }

    //dodaj/usun podswietlenie dla bierek
    private void selectChecker(Node checkerNode) {
        checkerNode.addLight(blueLight);
        checkerNode.setUserData("selected", "true");
    }

    private void diselectChecker(Node checkerNode) {
        checkerNode.removeLight(blueLight);
        checkerNode.setUserData("selected", "false");
    }

    private void selectCheckerToBeat(Node checkerNode) {
        checkerNode.addLight(redLight);
//        checkerNode.setUserData("selected", "true");
    }

    private void diselectCheckerToBeat(Node checkerNode) {
        checkerNode.removeLight(redLight);
//        checkerNode.setUserData("selected", "false");
    }

    //przeksztalc koordynaty z 3d na tablice dwuwymiarowa board
    private void getBoardField(Vector3f pointCoordinates) {
        int col = 8;
        int row = 8;

        float x_pos = pointCoordinates.getX();
        float z_pos = pointCoordinates.getZ();


        for (int i = 0; i < 8; i++) {

            if (colXCoordinates[i] > x_pos) {
                col--;
            }
        }
        for (int i = 0; i < 8; i++) {

            if (colZCoordinates[i] > z_pos) {
                row--;
            }
        }
        System.out.println("Col number: " + col);
        System.out.println("Row number: " + row);

    }

    //na starcie oblicz granice miedzy polami board
    private void calculateColumns() {

        colXCoordinates[0] = FIRST_COL_X;

        for (int i = 1; i < 9; i++) {
            colXCoordinates[i] = colXCoordinates[i - 1] - COL_X_DIFF;
        }
    }

    private void calculateRows() {

        colZCoordinates[0] = FIRST_COL_Z;

        for (int i = 1; i < 9; i++) {
            colZCoordinates[i] = colZCoordinates[i - 1] - COL_Z_DIFF;
        }
    }

    private void setCoordinatesWhereCheckersCanBe() {

        for (int row = 0; row < boardFields.length; row++) {
            for (int col = 0; col < boardFields[row].length; col++) {
                boardFields[row][col] = new Field();

                if (col % 2 == 0 && row % 2 == 0 || col == row || col % 2 == 1 && row % 2 == 1) {

                    boardFields[row][col].setAccessible(false);

                } else {

                    boardFields[row][col].setAccessible(true);
                }
                //wartosci 2D
                boardFields[row][col].setTabXPosition(col);
                boardFields[row][col].setTabYPosition(row);

                //wartosci 3D
                //wysokosc taka sama dla wszystkich - poziom
                boardFields[row][col].setFieldWorldCoordinates(new Vector3f(columns[col], CELL_POS_Y, columns[row]));

                System.out.print("Row: " + boardFields[row][col].getTabXPosition() + " Col: " + boardFields[row][col].getTabYPosition() +
                        boardFields[row][col].isAccessible() + " " + "Loc: " + boardFields[row][col].getFieldWorldCoordinates() + " ");
            }
            System.out.println();
        }
    }
}
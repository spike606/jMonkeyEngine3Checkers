package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.light.DirectionalLight;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import com.jme3.audio.AudioNode;
import com.jme3.cinematic.MotionPath;
import com.jme3.cinematic.MotionPathListener;
import com.jme3.cinematic.events.MotionEvent;
import com.jme3.collision.CollisionResults;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Node;
import com.jme3.shadow.DirectionalLightShadowFilter;
import com.jme3.shadow.DirectionalLightShadowRenderer;
import com.jme3.system.AppSettings;
import com.jme3.system.JmeCanvasContext;
import com.jme3.util.SkyFactory;
import gameUI.CheckersUI;
import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Sample 3 - how to load an OBJ model, and OgreXML model, a material/texture,
 * or text.
 */
public class CheckersGame extends SimpleApplication {

    //logger
    private static final Logger logger = Logger.getLogger(CheckersGame.class.getName());
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
    private final static int SHADOWMAP_SIZE = 2048;
    /*TABLICE DO BIEREK */
    /* NODES - OD NAJWYSZYCH*/
    //ROOTNODE
    Node game_node = new Node("Game");//zawiera board i bierki
    Node board_node = new Node("boardNode");
    Node checkers_node = new Node("checkers_node");
    Node white_node;//zawiera wszystkie biale
    Node black_node;
    Node[] black_checkers_nodes;//tablica z nodami kazdy ma bierke
    Node[] white_checkers_nodes;
    Spatial[] black_checkers;//tablica spatiali
    Spatial[] white_checkers;

    /* SWIATLA */
    private static final AmbientLight blueLight = new AmbientLight();//zaznaczona bierka
    private static final AmbientLight redLight = new AmbientLight();//bierka do bicia
    private static final Vector3f sunLightDirection = new Vector3f(-1.3f, -1.9f, 0.0f);
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
    Field clickedFieldBefore;
    Node clickedNodeBefore;
    /**
     * *motion path
     */
    private MotionPath path = new MotionPath();
    private MotionEvent motionControl;
    private static final float PATH_SPEED = 1.0f;
    private static final float PATH_DURATION = 2.0f;//2 sekundy
    /**
     * *audio*
     */
    private static AudioNode gameAudioNode;
    private static AudioNode audioTickNode;
    private static AudioNode audioWinnerNode;
    private static AudioNode audioLooserNode;
    /**
     * ustawienia
     */
    private final static int RESOLUTION_WIDTH = 640;//rozdzielczosc obrazu gry
    private final static int RESOLUTION_HEIGHT = 480;
    private final static int FRAMERATE = 60;
    private final static int SAMPLES = 0;
    private final static boolean VSYNC = true;

    /* swing*/
    private static JmeCanvasContext context;
    private static Canvas canvas;
    public static CheckersUI window;
    //pomocnicze 
    private static boolean gameCreated = false;
    GameFlowClient game;
    private static boolean restartGame = false;
    public static boolean playWinner = false;
    public static boolean playLooser = false;
    public static boolean matchFinished = false;
    public static boolean startNextGame = false;
    /* modele*/
    private static final String WHITE_CHECKER_MODEL = "Models/Ch_white/Ch_white.j3o";
    private static final String WHITE_QUEEN_CHECKER_MODEL = "Models/Ch_white_queen/Ch_white_queen.j3o";
    private static final String BLACK_CHECKER_MODEL = "Models/Ch_black/Ch_black.j3o";
    private static final String BLACK_QUEEN_CHECKER_MODEL = "Models/Ch_black_queen/Ch_black_queen.j3o";
    private static final String CHESSBOARD_MODEL = "Models/board/chessboard.j3o";
    private static final String SKY = "Textures/sky/space.dds";
    private static final String TICK_SOUND = "Sounds/tick.wav";
    private static final String WINNER_SOUND = "Sounds/winner.wav";
    private static final String LOOSER_SOUND = "Sounds/looser.wav";
    /**
     * * FLAGA DO ANIMACJI
     */
    //pomocnicze przy usuwaniu modeli by nastepowalo ono dopiero po zakonczeniu animacji, zmiana na dame rowniez
    private static Node checkerNodeToDelete;//id bierki ktora ma zostac usunieta
    static boolean animInProgress = false;
    private static Field checkerFieldToDelete;
    private static String modelToChange;//model na jaki bedzie zmiana np czaarna dama
    private static int checkerIdToChange = -1;//id bierki do zmiany na dame

    /*  LABELE*/
    private static final String CONNECTING = "Connecting to server...";
    private static final String CANT_CONNECT = "Can't connect to server!";
    private static final String YOU_WON = "You won!";
    private static final String YOU_LOOSE = "You lose!";
    private static final String WAIT = "Wait for opoonent's move...";
    private static final String MOVE = "Make your move.";
    private static final String NO_INFO = "";

    /**
     * ***
     */
    public static void main(String[] args) {

        AppSettings gameSettings = new AppSettings(true);
        final CheckersGame app = new CheckersGame();
        performSettings(app, gameSettings, RESOLUTION_WIDTH, RESOLUTION_HEIGHT, FRAMERATE, SAMPLES, VSYNC);
        app.setPauseOnLostFocus(false);//by update method byla kontynuowana nawet gdy brak focus
//        app.setSettings(settings);
        app.createCanvas();
//        app.startCanvas();

        context = (JmeCanvasContext) app.getContext();
        context.setSystemListener(app);
        Dimension dim = new Dimension(RESOLUTION_WIDTH, RESOLUTION_HEIGHT);
        context.getCanvas().setPreferredSize(dim);

        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                window = new CheckersUI();

                window.gamePanel.add(context.getCanvas());//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                window.pack();
                try {
                    Thread.sleep(5000);//w celu jednoczesnego pojawienia sie okna i uruchomionego okna silnika
                } catch (InterruptedException exc) {
                }
                window.setVisible(true);
            }
        });

        app.startCanvas();


    }

    @Override
    public void simpleInitApp() {

        /* INFO OFF*/
        setDisplayFps(false);
        setDisplayStatView(false);

        /* LOAD BOARD*/
        Spatial board = assetManager.loadModel(CHESSBOARD_MODEL);
        board.scale(1f, 1f, 1f);
        board.rotate(0.0f, 0f, 0.0f);
        board.setLocalTranslation(0f, 0f, 0f);
        board.setShadowMode(ShadowMode.CastAndReceive);

        board_node.attachChild(board);
        game_node.attachChild(board_node);
        rootNode.attachChild(game_node);


        blueLight.setColor(ColorRGBA.Blue.mult(1f));
        redLight.setColor(ColorRGBA.Red.mult(1f));


        // You must add a directional light to make the model visible!
        DirectionalLight sun = new DirectionalLight();
        sun.setName("Sun");
        sun.setColor(ColorRGBA.White);
        sun.setDirection(sunLightDirection.normalizeLocal());
        rootNode.addLight(sun);

        /* Drop shadows */
        DirectionalLightShadowRenderer dlsr = new DirectionalLightShadowRenderer(assetManager, SHADOWMAP_SIZE, 1);
        dlsr.setLight(sun);
        viewPort.addProcessor(dlsr);
        DirectionalLightShadowFilter dlsf = new DirectionalLightShadowFilter(assetManager, SHADOWMAP_SIZE, 1);
        dlsf.setLight(sun);
        dlsf.setEnabled(true);
        /* dodanie filtra wywala antyaliasing ?!!!!! */
//        FilterPostProcessor fpp = new FilterPostProcessor(assetManager);
//        fpp.addFilter(dlsf);
//        fpp.setNumSamples(8); 
//        viewPort.addProcessor(fpp);

        /* cam 1 */
        cam.setFrame(cam1Loc, cam1Left, cam1Up, cam1Dir);
        flyCam.setEnabled(false);
        flyCam.setMoveSpeed(10);


        //listener do ruchu !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!1
        path.addListener(new MotionPathListener() {
            public void onWayPointReach(MotionEvent control, int wayPointIndex) {
                if (path.getNbWayPoints() == wayPointIndex + 1) {//gdy zakonczy sie przemieszczenie
                    audioTickNode.playInstance(); // play each instance once!
                    diselectAllCheckers();//diselect all

                    if (checkerNodeToDelete != null) {
                        removeCheckerNode(checkerNodeToDelete, checkerFieldToDelete);// remove opponent
                        // checker



                    }
                    if (checkerIdToChange != -1) {//gdy jest nowa dama
                        changeModel(findCheckerById(checkerIdToChange), modelToChange);
                        checkerIdToChange = -1;
                    }
                    animInProgress = false;

//                    gameEndSound();

                } else {//gdy trwa przemieszczenie
                }
            }
        });


        //sky
        rootNode.attachChild(SkyFactory.createSky(
                assetManager, SKY, false));

        // load my custom keybinding, audio
        initKeys();
        initAudio();


        //calculate coordinates for board
        calculateColumns();
        calculateRows();
        setCoordinatesWhereCheckersCanBe();
        setUpCheckers();

    }

    /* Use the main event loop to trigger repeating actions. */
    @Override
    public void simpleUpdate(float tpf) {//time per second

        if (gameCreated == false) {
            game = new GameFlowClient();
            gameCreated = true;

        }

//        System.out.println("ANIM!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" + animInProgress);

        if (Connecting.connectedToServer && animInProgress == false) {
            refreshView();
        }
        playSound();
        if ((GameFlowClient.isGameRunning() == false && matchFinished == true
                && startNextGame == true)) {


            restartGame();
            matchFinished = false;
            startNextGame = false;
        }
        setInfo();
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

        inputManager.addMapping("Click", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));

        // Add the names to the action listener.
        inputManager.addListener(actionListener, "Cam1", "Cam2", "Cam3", "Cam4", "Cam5", "Click");

    }
    private ActionListener actionListener = new ActionListener() {
        public void onAction(String name, boolean keyPressed, float tpf) {
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
                    Field clickedField = getBoardField(selectedPointCoordinates);

                    // The closest result is the target that the player picked:
                    Node checkerNode = results.getClosestCollision().getGeometry().getParent().getParent().getParent().getParent().getParent();
//                    if (!checkerNode.getName().equals("Root Node")) {
                    if (Connecting.connectedToServer) {
                        Connecting.sendMessageToServer(clickedField.getTabYPosition(), clickedField.getTabXPosition(), GameFlowClient.isResign());

                    }
//                    GameFlowClient.chosenRow = clickedField.getTabYPosition();
//                    GameFlowClient.chosenCol = clickedField.getTabXPosition();
//                    }

//                    if (!checkerNode.getName().equals("Root Node")) {
                    //wypisz wspolrzedne klikniecia na planszy
//                        System.out.println("Click position: X, Y, Z: " + selectedPointCoordinates);
//                        String checkerId = checkerNode.getUserData("id").toString();
//                        System.out.println("Checker Id: " + checkerId);
//                        System.out.println("Checker name(node): " + checkerNode.getName());

//                        if (rootNode.getChild(checkerNode.getName()).getUserData("selected").equals("false")) {
////                            rootNode.getChild(checkerNode.getName()).setUserData("position", clickedField);
//                            clickedFieldBefore = clickedField;
//                            clickedNodeBefore = checkerNode;
//                            selectChecker(checkerNode);
//                        } else if (rootNode.getChild(checkerNode.getName()).getUserData("selected").equals("true")) {
//                            diselectChecker(checkerNode);
//                        }
//                    } else {
//                        System.out.println("Field selected. Coordinates: X, Y, Z: " + selectedPointCoordinates);
//                        if (clickedNodeBefore.getUserData("selected").equals("true")) {
//                            
//                            diselectChecker(clickedNodeBefore);
//
//                            moveCheckerNode(clickedNodeBefore, clickedFieldBefore, clickedField);
//
//
//                        }
//                    }
                }
            }
        }
    };

    private void setUpCheckers() {

        //do obrocenia czarnych bierek o 180 stopni
        Quaternion roll180 = new Quaternion();
        roll180.fromAngleAxis(-FastMath.PI, Vector3f.UNIT_Y);

        black_checkers_nodes = new Node[12];
        white_checkers_nodes = new Node[12];

        white_node = new Node("whiteCheckersNode");//zawiera wszystkie biale
        black_node = new Node("blackCheckersNode");
        for (int i = 0; i < 12; i++) {
            white_checkers_nodes[i] = new Node("WhiteNode" + i);
            black_checkers_nodes[i] = new Node("BlackNode" + i);
            //obroc czarne bierki
            black_checkers_nodes[i].rotate(roll180);
        }

        float cell_pos_x = 0.042778164f;
        float cell_pos_z = 0.0f;
        black_checkers = new Spatial[12];//tablica spatiali
        white_checkers = new Spatial[12];

        for (int i = 0; i < 12; i++) {
            white_checkers[i] = assetManager.loadModel(WHITE_CHECKER_MODEL);
//                    loadModel("Models/scalak_render_bialy_008/scalak_render_bialy_008.j3o");

            black_checkers[i] = assetManager.loadModel(BLACK_CHECKER_MODEL);
            white_checkers[i].setShadowMode(ShadowMode.CastAndReceive);
            black_checkers[i].setShadowMode(ShadowMode.CastAndReceive);

            //dodaj id
            white_checkers_nodes[i].attachChild(white_checkers[i]);
            white_checkers_nodes[i].setUserData("id", i);
            white_checkers_nodes[i].setUserData("selected", false);

            black_checkers_nodes[i].attachChild(black_checkers[i]);
            black_checkers_nodes[i].setUserData("id", i + 12);
            black_checkers_nodes[i].setUserData("selected", false);

            white_node.attachChild(white_checkers_nodes[i]);
            black_node.attachChild(black_checkers_nodes[i]);
        }

        //pozycja startowa bierek oraz jej odzwierciedlenie na tablicy dwuwymiarowej by latwiej je znalezc
        white_checkers_nodes[0].setLocalTranslation(boardFields[7][6].getFieldWorldCoordinates());
        boardFields[7][6].setCheckerId(0);
        boardFields[7][6].setCheckerColor(GameFlowClient.WHITE);
        boardFields[7][6].setCheckerQueenColor(GameFlowClient.WHITE_QUEEN);

        white_checkers_nodes[0].setUserData("row", 7);
        white_checkers_nodes[0].setUserData("col", 6);

        white_checkers_nodes[1].setLocalTranslation(boardFields[7][4].getFieldWorldCoordinates());
        boardFields[7][4].setCheckerId(1);
        boardFields[7][4].setCheckerColor(GameFlowClient.WHITE);
        boardFields[7][4].setCheckerQueenColor(GameFlowClient.WHITE_QUEEN);
        white_checkers_nodes[1].setUserData("row", 7);
        white_checkers_nodes[1].setUserData("col", 4);

        white_checkers_nodes[2].setLocalTranslation(boardFields[7][2].getFieldWorldCoordinates());
        boardFields[7][2].setCheckerId(2);
        boardFields[7][2].setCheckerColor(GameFlowClient.WHITE);
        boardFields[7][2].setCheckerQueenColor(GameFlowClient.WHITE_QUEEN);
        white_checkers_nodes[2].setUserData("row", 7);
        white_checkers_nodes[2].setUserData("col", 2);

        white_checkers_nodes[3].setLocalTranslation(boardFields[7][0].getFieldWorldCoordinates());
        boardFields[7][0].setCheckerId(3);
        boardFields[7][0].setCheckerColor(GameFlowClient.WHITE);
        boardFields[7][0].setCheckerQueenColor(GameFlowClient.WHITE_QUEEN);
        white_checkers_nodes[3].setUserData("row", 7);
        white_checkers_nodes[3].setUserData("col", 0);

        white_checkers_nodes[4].setLocalTranslation(boardFields[6][7].getFieldWorldCoordinates());
        boardFields[6][7].setCheckerId(4);
        boardFields[6][7].setCheckerColor(GameFlowClient.WHITE);
        boardFields[6][7].setCheckerQueenColor(GameFlowClient.WHITE_QUEEN);

        white_checkers_nodes[4].setUserData("row", 6);
        white_checkers_nodes[4].setUserData("col", 7);

        white_checkers_nodes[5].setLocalTranslation(boardFields[6][5].getFieldWorldCoordinates());
        boardFields[6][5].setCheckerId(5);
        boardFields[6][5].setCheckerColor(GameFlowClient.WHITE);
        boardFields[6][5].setCheckerQueenColor(GameFlowClient.WHITE_QUEEN);

        white_checkers_nodes[5].setUserData("row", 6);
        white_checkers_nodes[5].setUserData("col", 5);

        white_checkers_nodes[6].setLocalTranslation(boardFields[6][3].getFieldWorldCoordinates());
        boardFields[6][3].setCheckerId(6);
        boardFields[6][3].setCheckerColor(GameFlowClient.WHITE);
        boardFields[6][3].setCheckerQueenColor(GameFlowClient.WHITE_QUEEN);

        white_checkers_nodes[6].setUserData("row", 6);
        white_checkers_nodes[6].setUserData("col", 3);

        white_checkers_nodes[7].setLocalTranslation(boardFields[6][1].getFieldWorldCoordinates());
        boardFields[6][1].setCheckerId(7);
        boardFields[6][1].setCheckerColor(GameFlowClient.WHITE);
        boardFields[6][1].setCheckerQueenColor(GameFlowClient.WHITE_QUEEN);

        white_checkers_nodes[7].setUserData("row", 6);
        white_checkers_nodes[7].setUserData("col", 1);

        white_checkers_nodes[8].setLocalTranslation(boardFields[5][6].getFieldWorldCoordinates());
        boardFields[5][6].setCheckerId(8);
        boardFields[5][6].setCheckerColor(GameFlowClient.WHITE);
        boardFields[5][6].setCheckerQueenColor(GameFlowClient.WHITE_QUEEN);

        white_checkers_nodes[8].setUserData("row", 5);
        white_checkers_nodes[8].setUserData("col", 6);

        white_checkers_nodes[9].setLocalTranslation(boardFields[5][4].getFieldWorldCoordinates());
        boardFields[5][4].setCheckerId(9);
        boardFields[5][4].setCheckerColor(GameFlowClient.WHITE);
        boardFields[5][4].setCheckerQueenColor(GameFlowClient.WHITE_QUEEN);

        white_checkers_nodes[9].setUserData("row", 5);
        white_checkers_nodes[9].setUserData("col", 4);

        white_checkers_nodes[10].setLocalTranslation(boardFields[5][2].getFieldWorldCoordinates());
        boardFields[5][2].setCheckerId(10);
        boardFields[5][2].setCheckerColor(GameFlowClient.WHITE);
        boardFields[5][2].setCheckerQueenColor(GameFlowClient.WHITE_QUEEN);

        white_checkers_nodes[10].setUserData("row", 5);
        white_checkers_nodes[10].setUserData("col", 2);

        white_checkers_nodes[11].setLocalTranslation(boardFields[5][0].getFieldWorldCoordinates());
        boardFields[5][0].setCheckerId(11);
        boardFields[5][0].setCheckerColor(GameFlowClient.WHITE);
        boardFields[5][0].setCheckerQueenColor(GameFlowClient.WHITE_QUEEN);

        white_checkers_nodes[11].setUserData("row", 5);
        white_checkers_nodes[11].setUserData("col", 0);


        black_checkers_nodes[0].setLocalTranslation(boardFields[0][7].getFieldWorldCoordinates());
        boardFields[0][7].setCheckerId(12);
        boardFields[0][7].setCheckerColor(GameFlowClient.BLACK);
        boardFields[0][7].setCheckerQueenColor(GameFlowClient.BLACK_QUEEN);

        black_checkers_nodes[0].setUserData("row", 0);
        black_checkers_nodes[0].setUserData("col", 7);

        black_checkers_nodes[1].setLocalTranslation(boardFields[0][5].getFieldWorldCoordinates());
        boardFields[0][5].setCheckerId(13);
        boardFields[0][5].setCheckerColor(GameFlowClient.BLACK);
        boardFields[0][5].setCheckerQueenColor(GameFlowClient.BLACK_QUEEN);

        black_checkers_nodes[1].setUserData("row", 0);
        black_checkers_nodes[1].setUserData("col", 5);

        black_checkers_nodes[2].setLocalTranslation(boardFields[0][3].getFieldWorldCoordinates());
        boardFields[0][3].setCheckerId(14);
        boardFields[0][3].setCheckerColor(GameFlowClient.BLACK);
        boardFields[0][3].setCheckerQueenColor(GameFlowClient.BLACK_QUEEN);

        black_checkers_nodes[2].setUserData("row", 0);
        black_checkers_nodes[2].setUserData("col", 3);

        black_checkers_nodes[3].setLocalTranslation(boardFields[0][1].getFieldWorldCoordinates());
        boardFields[0][1].setCheckerId(15);
        boardFields[0][1].setCheckerColor(GameFlowClient.BLACK);
        boardFields[0][1].setCheckerQueenColor(GameFlowClient.BLACK_QUEEN);

        black_checkers_nodes[3].setUserData("row", 0);
        black_checkers_nodes[3].setUserData("col", 1);

        black_checkers_nodes[4].setLocalTranslation(boardFields[1][6].getFieldWorldCoordinates());
        boardFields[1][6].setCheckerId(16);
        boardFields[1][6].setCheckerColor(GameFlowClient.BLACK);
        boardFields[1][6].setCheckerQueenColor(GameFlowClient.BLACK_QUEEN);

        black_checkers_nodes[4].setUserData("row", 1);
        black_checkers_nodes[4].setUserData("col", 6);

        black_checkers_nodes[5].setLocalTranslation(boardFields[1][4].getFieldWorldCoordinates());
        boardFields[1][4].setCheckerId(17);
        boardFields[1][4].setCheckerColor(GameFlowClient.BLACK);
        boardFields[1][4].setCheckerQueenColor(GameFlowClient.BLACK_QUEEN);

        black_checkers_nodes[5].setUserData("row", 1);
        black_checkers_nodes[5].setUserData("col", 4);

        black_checkers_nodes[6].setLocalTranslation(boardFields[1][2].getFieldWorldCoordinates());
        boardFields[1][2].setCheckerId(18);
        boardFields[1][2].setCheckerColor(GameFlowClient.BLACK);
        boardFields[1][2].setCheckerQueenColor(GameFlowClient.BLACK_QUEEN);

        black_checkers_nodes[6].setUserData("row", 1);
        black_checkers_nodes[6].setUserData("col", 2);

        black_checkers_nodes[7].setLocalTranslation(boardFields[1][0].getFieldWorldCoordinates());
        boardFields[1][0].setCheckerId(19);
        boardFields[1][0].setCheckerColor(GameFlowClient.BLACK);
        boardFields[1][0].setCheckerQueenColor(GameFlowClient.BLACK_QUEEN);

        black_checkers_nodes[7].setUserData("row", 1);
        black_checkers_nodes[7].setUserData("col", 0);

        black_checkers_nodes[8].setLocalTranslation(boardFields[2][7].getFieldWorldCoordinates());
        boardFields[2][7].setCheckerId(20);
        boardFields[2][7].setCheckerColor(GameFlowClient.BLACK);
        boardFields[2][7].setCheckerQueenColor(GameFlowClient.BLACK_QUEEN);

        black_checkers_nodes[8].setUserData("row", 2);
        black_checkers_nodes[8].setUserData("col", 7);

        black_checkers_nodes[9].setLocalTranslation(boardFields[2][5].getFieldWorldCoordinates());
        boardFields[2][5].setCheckerId(21);
        boardFields[2][5].setCheckerColor(GameFlowClient.BLACK);
        boardFields[2][5].setCheckerQueenColor(GameFlowClient.BLACK_QUEEN);

        black_checkers_nodes[9].setUserData("row", 2);
        black_checkers_nodes[9].setUserData("col", 5);

        black_checkers_nodes[10].setLocalTranslation(boardFields[2][3].getFieldWorldCoordinates());
        boardFields[2][3].setCheckerId(22);
        boardFields[2][3].setCheckerColor(GameFlowClient.BLACK);
        boardFields[2][3].setCheckerQueenColor(GameFlowClient.BLACK_QUEEN);

        black_checkers_nodes[10].setUserData("row", 2);
        black_checkers_nodes[10].setUserData("col", 3);

        black_checkers_nodes[11].setLocalTranslation(boardFields[2][1].getFieldWorldCoordinates());
        boardFields[2][1].setCheckerId(23);
        boardFields[2][1].setCheckerColor(GameFlowClient.BLACK);
        boardFields[2][1].setCheckerQueenColor(GameFlowClient.BLACK_QUEEN);

        black_checkers_nodes[11].setUserData("row", 2);
        black_checkers_nodes[11].setUserData("col", 1);

        checkers_node.attachChild(white_node);
        checkers_node.attachChild(black_node);

        game_node.attachChild(checkers_node);

    }

    //dodaj/usun podswietlenie dla bierek
    private void selectChecker(Node checkerNode) {
        diselectAllCheckers();
        System.out.println("selected " + checkerNode.getUserData("id"));
        if (white_node.hasChild(checkerNode) || black_node.hasChild(checkerNode)) {
            checkerNode.addLight(blueLight);
            checkerNode.setUserData("selected", true);
        }

    }

    private void diselectChecker(Node checkerNode) {
        checkerNode.removeLight(blueLight);
        checkerNode.setUserData("selected", false);
    }

    private void diselectAllCheckers() {

        for (int i = 0; i < 12; i++) {
            if (white_checkers_nodes[i].getUserData("selected")) {
                diselectChecker(white_checkers_nodes[i]);
            }
            if (black_checkers_nodes[i].getUserData("selected")) {
                diselectChecker(black_checkers_nodes[i]);
            }

        }
    }

    private void selectCheckerToBeat(Node checkerNode) {
        checkerNode.addLight(redLight);
    }

    private void diselectCheckerToBeat(Node checkerNode) {
        checkerNode.removeLight(redLight);
    }

    private Node getSelectedChecker() {

        for (int i = 0; i < 12; i++) {
            if (white_checkers_nodes[i].getUserData("selected")) {
                return white_checkers_nodes[i];
            }
            if (black_checkers_nodes[i].getUserData("selected")) {
                return black_checkers_nodes[i];
            }

        }
        return null;

    }

    //przeksztalc koordynaty z 3d na tablice dwuwymiarowa board
    private Field getBoardField(Vector3f pointCoordinates) {
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
        logger.log(Level.INFO, "Col number: {0}, Row number: {1}", new Object[]{col, row});

        return boardFields[row][col];

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

    //oblicza koordynaty na starcie 
    private void setCoordinatesWhereCheckersCanBe() {

        for (int row = 0; row < boardFields.length; row++) {
            for (int col = 0; col < boardFields[row].length; col++) {
                boardFields[row][col] = new Field();

                if (col % 2 == 0 && row % 2 == 0 || col == row || col % 2 == 1 && row % 2 == 1) {

                    boardFields[row][col].setAccessible(false);
                    boardFields[row][col].setCheckerId(-1);
                    boardFields[row][col].setCheckerColor(GameFlowClient.EMPTY);
                    boardFields[row][col].setCheckerQueenColor(GameFlowClient.EMPTY);


                } else {

                    boardFields[row][col].setAccessible(true);
                }
                //wartosci 2D
                boardFields[row][col].setTabXPosition(col);
                boardFields[row][col].setTabYPosition(row);

                //wartosci 3D
                //wysokosc taka sama dla wszystkich - poziom
                boardFields[row][col].setFieldWorldCoordinates(new Vector3f(columns[col], CELL_POS_Y, rows[row]));

            }
        }
    }

    private void removeCheckerNode(Node checkerToRemove, Field checkerField) {

        diselectAllCheckers();
        if ((Integer) checkerToRemove.getUserData("id") > 11) {
            black_node.detachChild(checkerToRemove);
        } else {
            white_node.detachChild(checkerToRemove);
        }

        checkerField.setAccessible(true);
        checkerField.setCheckerColor(GameFlowClient.EMPTY);
        checkerField.setCheckerQueenColor(GameFlowClient.EMPTY);

        checkerField.setCheckerId(-1);

        checkerFieldToDelete = null;
        checkerNodeToDelete = null;
    }
    //przenosi bierke

    private void moveCheckerNode(Node nodeToMove, Field from, Field to) {

        path.clearWayPoints();


        //motion path
        if (from.getTabXPosition() - to.getTabXPosition() == 1 || from.getTabXPosition() - to.getTabXPosition() == -1) {
            /**
             * *****ruch bierki - przesuniecie********
             */
            Vector3f startPos = new Vector3f(from.getFieldWorldCoordinates());
            Vector3f endPos = new Vector3f(to.getFieldWorldCoordinates());
            path.addWayPoint(startPos);
            path.addWayPoint(endPos);
            path.setCurveTension(0.0f);//ruch prosty
            /**
             * **********************
             */
        } else {
            /**
             * *****przykladowe dane - bicie (skok)********
             */
            Vector3f startPos = new Vector3f(from.getFieldWorldCoordinates());
            Vector3f endPos = new Vector3f(to.getFieldWorldCoordinates());

            Vector3f vectorBetween1 = FastMath.interpolateLinear(0.1f, startPos, endPos).add(new Vector3f(0.0f, 2.0f, 0.0f));
            Vector3f vectorBetween2 = FastMath.interpolateLinear(0.2f, startPos, endPos).add(new Vector3f(0.0f, 2.4f, 0.0f));
            Vector3f vectorBetween3 = FastMath.interpolateLinear(0.3f, startPos, endPos).add(new Vector3f(0.0f, 2.8f, 0.0f));
            Vector3f vectorBetween4 = FastMath.interpolateLinear(0.4f, startPos, endPos).add(new Vector3f(0.0f, 3.0f, 0.0f));
            Vector3f vectorBetween5 = FastMath.interpolateLinear(0.5f, startPos, endPos).add(new Vector3f(0.0f, 3.2f, 0.0f));
            Vector3f vectorBetween6 = FastMath.interpolateLinear(0.6f, startPos, endPos).add(new Vector3f(0.0f, 3.0f, 0.0f));
            Vector3f vectorBetween7 = FastMath.interpolateLinear(0.7f, startPos, endPos).add(new Vector3f(0.0f, 2.8f, 0.0f));
            Vector3f vectorBetween8 = FastMath.interpolateLinear(0.8f, startPos, endPos).add(new Vector3f(0.0f, 2.4f, 0.0f));
            Vector3f vectorBetween9 = FastMath.interpolateLinear(0.9f, startPos, endPos).add(new Vector3f(0.0f, 2.0f, 0.0f));


            path.addWayPoint(startPos);
            path.addWayPoint(vectorBetween1);
            path.addWayPoint(vectorBetween2);
            path.addWayPoint(vectorBetween3);
            path.addWayPoint(vectorBetween4);
            path.addWayPoint(vectorBetween5);
            path.addWayPoint(vectorBetween6);
            path.addWayPoint(vectorBetween7);
            path.addWayPoint(vectorBetween8);
            path.addWayPoint(vectorBetween9);
            path.addWayPoint(endPos);
            path.setCurveTension(0.23f);//ruch zaokraglony
            /**
             * *********************
             */
        }

        //uaktualnij w tablicy swiata 3d (tabliy field)
        to.setAccessible(false);
        to.setCheckerColor(from.getCheckerColor());
        to.setCheckerQueenColor(from.getCheckerQueenColor());
        to.setCheckerId(from.getCheckerId());
        from.setAccessible(true);
        from.setCheckerColor(GameFlowClient.EMPTY);
        from.setCheckerQueenColor(GameFlowClient.EMPTY);
        from.setCheckerId(-1);


        //zamien na damy jesli bierki sa na odpowiednich polach
        if (to.getCheckerColor() == GameFlowClient.WHITE && to.getTabYPosition() == 0) {
            modelToChange = WHITE_QUEEN_CHECKER_MODEL;
            checkerIdToChange = to.getCheckerId();
            to.setCheckerColor(GameFlowClient.WHITE_QUEEN);

//            changeModel(findCheckerById(to.getCheckerId()), WHITE_QUEEN_CHECKER_MODEL);
        }
        if (to.getCheckerColor() == GameFlowClient.BLACK && to.getTabYPosition() == 7) {
            modelToChange = BLACK_QUEEN_CHECKER_MODEL;
            checkerIdToChange = to.getCheckerId();
            to.setCheckerColor(GameFlowClient.BLACK_QUEEN);

//            changeModel(findCheckerById(to.getCheckerId()), BLACK_QUEEN_CHECKER_MODEL);

        }


        //uaktualnij dane nodes
        nodeToMove.setUserData("row", to.getTabYPosition());
        nodeToMove.setUserData("col", to.getTabXPosition());
//        path.enableDebugShape(assetManager, rootNode);//pokaz linie     

        motionControl = new MotionEvent(nodeToMove, path);//ktora bierka
        //ustawienie zachowania podczas przemieszczania sie
        motionControl.setDirectionType(MotionEvent.Direction.None);//bez obrotow     
//        motionControl.setDirectionType(MotionEvent.Direction.PathAndRotation);
//        motionControl.setRotation(new Quaternion().fromAngleNormalAxis(-FastMath.HALF_PI, Vector3f.UNIT_Y));

        //szybkosci i czas animacji
        motionControl.setInitialDuration(PATH_DURATION);//zatem 2 sek
        motionControl.setSpeed(PATH_SPEED);// 1 - 1 sekunda
        animInProgress = true;
        motionControl.play();
    }

    /**
     * We create two audio nodes.
     */
    private void initAudio() {

        gameAudioNode = new AudioNode();
        audioTickNode = new AudioNode(assetManager, TICK_SOUND, false);
        audioTickNode.setPositional(false);
        audioTickNode.setLooping(false);
        audioTickNode.setVolume(2);
        gameAudioNode.attachChild(audioTickNode);
        rootNode.attachChild(gameAudioNode);

        audioWinnerNode = new AudioNode(assetManager, WINNER_SOUND, false);
        audioWinnerNode.setPositional(false);
        audioWinnerNode.setLooping(false);
        audioWinnerNode.setVolume(3);
        gameAudioNode.attachChild(audioWinnerNode);
        rootNode.attachChild(gameAudioNode);

        audioLooserNode = new AudioNode(assetManager, LOOSER_SOUND, false);
        audioLooserNode.setPositional(false);
        audioLooserNode.setLooping(false);
        audioLooserNode.setVolume(3);
        gameAudioNode.attachChild(audioLooserNode);
        rootNode.attachChild(gameAudioNode);

    }

    private static void performSettings(SimpleApplication app, AppSettings gameSettings, int res_width, int res_height, int frameRate, int samples,
            boolean vsync) {
        gameSettings.setResolution(res_width, res_height);
        gameSettings.setFrameRate(frameRate);
        gameSettings.setSamples(samples);
        gameSettings.setVSync(vsync);

        app.setSettings(gameSettings);

    }

    private void refreshView() {

        int[][] currentBoardFromServer = GameFlowClient.getBoard();
        int chosenRow = GameFlowClient.getChosenRow();
        int chosenCol = GameFlowClient.getChosenCol();

        //zaznaczenie bierki jesli moj kolor i tylko tej ktora moze wykonac ruch - (dane z serwera)
        if (GameFlowClient.gameRunning && GameFlowClient.getMyColor() == GameFlowClient.getCurrentPlayer()
                && animInProgress == false) {

            if (chosenRow >= 0 && chosenCol >= 0) {
                if (currentBoardFromServer[chosenRow][chosenCol] == GameFlowClient.WHITE
                        || currentBoardFromServer[chosenRow][chosenCol] == GameFlowClient.WHITE_QUEEN) {
                    for (int i = 0; i < 12; i++) {
                        if (((Integer) white_checkers_nodes[i].getUserData("row") == chosenRow)
                                && ((Integer) white_checkers_nodes[i].getUserData("col") == chosenCol)) {
                            if (white_node.hasChild(white_checkers_nodes[i])) {
                                selectChecker(white_checkers_nodes[i]);

                            }

                        }
                    }
                }
                if (currentBoardFromServer[chosenRow][chosenCol] == GameFlowClient.BLACK
                        || currentBoardFromServer[chosenRow][chosenCol] == GameFlowClient.BLACK_QUEEN) {
                    for (int i = 0; i < 12; i++) {
                        if (((Integer) black_checkers_nodes[i].getUserData("row") == chosenRow)
                                && ((Integer) black_checkers_nodes[i].getUserData("col") == chosenCol)) {

                            if (black_node.hasChild(black_checkers_nodes[i])) {
                                selectChecker(black_checkers_nodes[i]);

                            }
                        }
                    }
                }
            }
        }
        //na podstawie tabliy wykonac ruchy, uaktualinic widok itp.

        //przeszukaj tablice 3d i porownaj z 2d


        ArrayList<Integer> possibleMoveFromRow = new ArrayList<Integer>();
        ArrayList<Integer> possibleMoveFromCol = new ArrayList<Integer>();

        int moveFromRow = 0;
        int moveFromCol = 0;
        int moveToRow = 0;
        int moveToCol = 0;
        boolean movePerformed = false;
//        System.out.println("GAME ARRAY: ");

        //wykonaj ruch jesli jest roznica
        for (int row = 0; row < boardFields.length; row++) {
            for (int col = 0; col < boardFields[row].length; col++) {


//                System.out.print(boardFields[row][col].getCheckerColor());




                if (boardFields[row][col].getCheckerColor() != currentBoardFromServer[row][col]) {

                    //gdy jest roznica w tablicach - przemieszczenie
                    if (boardFields[row][col].getCheckerColor() > GameFlowClient.EMPTY) {
                        movePerformed = true;


                        //POPRAWIC!!!!

                        possibleMoveFromRow.add(boardFields[row][col].getTabYPosition());
                        possibleMoveFromCol.add(boardFields[row][col].getTabXPosition());


//                        moveCheckerNode(getSelectedChecker(), boardFields[row][col], Field to)
//                        System.out.println("move gfrom row :" + moveFromRow);
//                        System.out.println("move gfrom col :" + moveFromCol);
                    }
                    if (boardFields[row][col].getCheckerColor() == GameFlowClient.EMPTY) {

                        moveToRow = boardFields[row][col].getTabYPosition();
                        moveToCol = boardFields[row][col].getTabXPosition();
                        System.out.println("move to row :" + moveToRow);
                        System.out.println("move to col :" + moveToCol);
                    }



                }
            }
//            System.out.println();

        }

        if (movePerformed) {
            System.out.println("size" + possibleMoveFromRow.size());

            for (int i = 0; i < possibleMoveFromRow.size(); i++) {
                System.out.println("possible move from row: " + possibleMoveFromRow.get(i));
                System.out.println("possible move from col: " + possibleMoveFromCol.get(i));
                System.out.println("queen color: " + boardFields[possibleMoveFromRow.get(i)][possibleMoveFromCol.get(i)].getCheckerQueenColor());
                System.out.println("queen color: " + boardFields[possibleMoveFromRow.get(i)][possibleMoveFromCol.get(i)].getCheckerColor());


                if (boardFields[possibleMoveFromRow.get(i)][possibleMoveFromCol.get(i)].getCheckerColor()
                        == currentBoardFromServer[moveToRow][moveToCol]
                        || boardFields[possibleMoveFromRow.get(i)][possibleMoveFromCol.get(i)].getCheckerQueenColor()
                        == currentBoardFromServer[moveToRow][moveToCol]) {
                    moveFromRow = possibleMoveFromRow.get(i);
                    moveFromCol = possibleMoveFromCol.get(i);
                }


            }
            System.out.println("From row: " + moveFromRow);
            System.out.println("From Col: " + moveFromCol);
            moveCheckerNode(findCheckerById(boardFields[moveFromRow][moveFromCol].getCheckerId()),
                    boardFields[moveFromRow][moveFromCol], boardFields[moveToRow][moveToCol]);


            /*
             * If move is performed by queen
             */
            if (boardFields[moveToRow][moveToCol].getCheckerColor() == GameFlowClient.WHITE_QUEEN
                    || boardFields[moveToRow][moveToCol].getCheckerColor() == GameFlowClient.BLACK_QUEEN) {

                boolean beating = true;
                int checkRow = moveFromRow;// start checking from 
                int checkCol = moveFromCol;
                if (moveFromRow < moveToRow && moveFromCol < moveToCol) {

                    while (checkCol < moveToCol && checkRow < moveToRow) {
                        checkCol++;
                        checkRow++;

                        if (checkRow == moveToRow && checkCol == moveToCol) {
                            beating = false;
                            break;
                        }
                        if (boardFields[checkRow][checkCol].getCheckerColor() != GameFlowClient.EMPTY) {
                            break;
                        }
                    }

                } else if (moveFromRow < moveToRow && moveFromCol > moveToCol) {
                    while (checkCol > moveToCol && checkRow < moveToRow) {
                        checkCol--;
                        checkRow++;
                        if (checkRow == moveToRow && checkCol == moveToCol) {
                            beating = false;
                            break;
                        }
                        if (boardFields[checkRow][checkCol].getCheckerColor() != GameFlowClient.EMPTY) {
                            break;
                        }
                    }

                } else if (moveFromRow > moveToRow && moveFromCol < moveToCol) {
                    while (checkCol < moveToCol && checkRow > moveToRow) {
                        checkCol++;
                        checkRow--;
                        if (checkRow == moveToRow && checkCol == moveToCol) {
                            beating = false;
                            break;
                        }
                        if (boardFields[checkRow][checkCol].getCheckerColor() != GameFlowClient.EMPTY) {
                            break;
                        }
                    }

                } else if (moveFromRow > moveToRow && moveFromCol > moveToCol) {
                    while (checkCol > moveToCol && checkRow > moveToRow) {

                        checkCol--;
                        checkRow--;
                        if (checkRow == moveToRow && checkCol == moveToCol) {
                            beating = false;
                            break;
                        }
                        if (boardFields[checkRow][checkCol].getCheckerColor() != GameFlowClient.EMPTY) {
                            break;
                        }
                    }

                }



                if (beating == true) {
                    int opponentCheckerCol = checkCol;
                    int opponentCheckerRow = checkRow;
                    System.out.println("oPP" + opponentCheckerRow + opponentCheckerCol);

                    checkerNodeToDelete = findCheckerById(boardFields[opponentCheckerRow][opponentCheckerCol].getCheckerId());
                    checkerFieldToDelete = boardFields[opponentCheckerRow][opponentCheckerCol];

//                    removeCheckerNode(findCheckerById(boardFields[opponentCheckerRow][opponentCheckerCol].getCheckerId()),
//                            boardFields[opponentCheckerRow][opponentCheckerCol]);// remove opponent
                    // checker
                }


            } else if (isMoveBeating(moveFromRow, moveToCol, moveFromCol, moveToRow)) //jesli bylo bicie zwykle
            {


                int opponentCheckerRow = 0;
                int opponentCheckerCol = 0;

                if (moveFromRow < moveToRow && moveFromCol < moveToCol) {
                    opponentCheckerRow = moveToRow - 1;
                    opponentCheckerCol = moveToCol - 1;
                } else if (moveFromRow < moveToRow && moveFromCol > moveToCol) {
                    opponentCheckerRow = moveToRow - 1;
                    opponentCheckerCol = moveToCol + 1;
                } else if (moveFromRow > moveToRow && moveFromCol < moveToCol) {
                    opponentCheckerRow = moveToRow + 1;
                    opponentCheckerCol = moveToCol - 1;
                } else if (moveFromRow > moveToRow && moveFromCol > moveToCol) {
                    opponentCheckerRow = moveToRow + 1;
                    opponentCheckerCol = moveToCol + 1;
                }
                checkerNodeToDelete = findCheckerById(boardFields[opponentCheckerRow][opponentCheckerCol].getCheckerId());
                checkerFieldToDelete = boardFields[opponentCheckerRow][opponentCheckerCol];

//                removeCheckerNode(findCheckerById(boardFields[opponentCheckerRow][opponentCheckerCol].getCheckerId()),
//                        boardFields[opponentCheckerRow][opponentCheckerCol]);// remove opponent
                // checker


            }

        }
        movePerformed = false;


    }

    private Node findCheckerById(int checkerId) {

        for (int i = 0; i < 12; i++) {
            if ((Integer) white_checkers_nodes[i].getUserData("id") == checkerId) {
                System.out.println(" ID" + white_checkers_nodes[i].getUserData("id"));

                return white_checkers_nodes[i];
            }
            if ((Integer) black_checkers_nodes[i].getUserData("id") == checkerId) {
                System.out.println(" ID" + black_checkers_nodes[i].getUserData("id"));

                return black_checkers_nodes[i];
            }

        }

        return null;
    }

    //czy dany ruch to bicie zwykle
    private boolean isMoveBeating(int moveFromRow, int moveToCol, int moveFromCol, int moveToRow) {

        return (moveFromCol - moveToCol >= 2 || moveFromCol - moveToCol <= -2);


    }

    private void changeModel(Node checkerModelNode, String changeTo) {

        Spatial checkerModelNodeSpatial = checkerModelNode.getChild(0);
        checkerModelNode.detachChild(checkerModelNodeSpatial);
        checkerModelNodeSpatial = assetManager.loadModel(changeTo);
        checkerModelNode.attachChild(checkerModelNodeSpatial);


//                white_checkers_nodes[0].detachChild(white_checkers[0]);
//                white_checkers[0] = assetManager.loadModel(WHITE_QUEEN_CHECKER_MODEL);
//                white_checkers_nodes[0].attachChild(white_checkers[0]);



//        checkerModel = assetManager.loadModel(changeTo);

    }

    private void restartGame() {
        GameFlowClient.setWinner(-1);//bo gra juz sie zakonczyla 
//        gameEndSound();
        checkers_node.detachAllChildren();
        setCoordinatesWhereCheckersCanBe();
        setUpCheckers();
        gameCreated = false;
        restartGame = false;

    }

    public static void newGame() {
        restartGame = true;
    }

    private void playSound() {
        if (playWinner) {
            audioWinnerNode.playInstance(); // play each instance once!
            System.out.println("WYGRALEM");
            playWinner = false;
        }
        if (playLooser) {
            audioLooserNode.playInstance(); // play each instance once!
            System.out.println("PRZEGRAŁEM");
            playLooser = false;

        }

    }

    private static void setInfo() {
        if (GameFlowClient.gameRunning && GameFlowClient.getMyColor() == GameFlowClient.getCurrentPlayer()) {
            window.infoLabel.setText(MOVE);
        } else if (GameFlowClient.gameRunning && GameFlowClient.getMyColor() != GameFlowClient.getCurrentPlayer()) {
            window.infoLabel.setText(WAIT);
        } else if (!GameFlowClient.gameRunning && GameFlowClient.isTryingToConnect()) {
            window.infoLabel.setText(CONNECTING);
        } else if (!GameFlowClient.gameRunning && Connecting.connectedToServer == false) {
            window.infoLabel.setText(CANT_CONNECT);
        } else if (!GameFlowClient.gameRunning && GameFlowClient.getWinner() == GameFlowClient.getMyColor()) {
            window.infoLabel.setText(YOU_WON);
        } else if (!GameFlowClient.gameRunning && GameFlowClient.getWinner() != GameFlowClient.getMyColor()
                && GameFlowClient.getWinner() != -1) {
            window.infoLabel.setText(YOU_LOOSE);
        } else if (!GameFlowClient.gameRunning && GameFlowClient.getWinner() != GameFlowClient.getMyColor()) {
            window.infoLabel.setText(NO_INFO);
        }
    }
}
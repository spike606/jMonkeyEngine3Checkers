package ServerPackage;

import CommonPackageServer.CheckersMove;
import CommonPackageServer.MessageFromClient;
import CommonPackageServer.MessageFromServer;
import com.jme3.app.SimpleApplication;
import com.jme3.network.Client;
import com.jme3.network.ConnectionListener;
import com.jme3.network.HostedConnection;
import com.jme3.network.Network;
import com.jme3.network.Server;
import com.jme3.network.serializing.Serializer;
import com.jme3.renderer.RenderManager;
import com.jme3.system.AppSettings;
import com.jme3.system.JmeContext;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * test
 *
 * @author normenhansen
 */
public class CheckersServer extends SimpleApplication implements ConnectionListener {

    private static final int SERVER_PORT = 8901;
    private static int matchNumber = 1;
    private static Server myServer;
    private static boolean gotFirstPlayer = false;//pomocnicze
    private static boolean gotSecondPlayer = false;
    private static int newestConnection = 0; // pomocnicza do zdobycia najnowszego polaczenia


    public static void main(String[] args) throws IOException, InterruptedException {

        Serializer.registerClass(MessageFromClient.class);//konieczna serializacja wiadomosci
        Serializer.registerClass(MessageFromServer.class);
        Serializer.registerClass(CheckersMove.class);

AppSettings newSetting = new AppSettings(true);

newSetting.setFrameRate(60);



        CheckersServer app = new CheckersServer();
        
                app.setSettings(newSetting);

        app.start(JmeContext.Type.Headless);//aplikacja startuje bez okna


//        try {
//            while (true) {
//
//
//
//            }
//        } finally {
////            serversocket.close();
//        }




    }

    @Override
    public void simpleInitApp() {
        try {
            myServer = Network.createServer(SERVER_PORT);
            System.out.println("Checkers server is running");


            myServer.start();
            myServer.addConnectionListener(new CheckersServer());

        } catch (IOException ex) {
            Logger.getLogger(CheckersServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void simpleUpdate(float tpf) {
        //TODO: add update code
        
                        Match match = new Match(matchNumber);
                System.out.println("Waiting for players...");
               System.out.println("Threads: " + Thread.activeCount());

//                myServer.getConnection(SERVER_PORT) //                try {

                while(gotFirstPlayer == false){
                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException ex) {
                                Logger.getLogger(CheckersServer.class.getName()).log(Level.SEVERE, null, ex);
                            }
                                           System.out.println("Threads: " + Thread.activeCount());

                }
                match.setWhitePlayer(new Player(myServer.getConnection(newestConnection), GameData.WHITE, match));
//                Match.Player playerWhite = match.new Player(myServer.getConnection(newestConnection), GameData.WHITE);
                System.out.println("Match #" + matchNumber + ": player #1 connected.");
               System.out.println("Threads: " + Thread.activeCount());

                while(gotSecondPlayer == false){       
                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException ex) {
                                Logger.getLogger(CheckersServer.class.getName()).log(Level.SEVERE, null, ex);
                            }
               System.out.println("Threads: " + Thread.activeCount());

                }
                match.setBlackPlayer(new Player(myServer.getConnection(newestConnection), GameData.BLACK, match));
               System.out.println("Threads: " + Thread.activeCount());

//                Match.Player playerBlack = match.new Player(myServer.getConnection(newestConnection), GameData.BLACK);
                System.out.println("Match #" + matchNumber + ": player #2 connected.");

                //register server listener
                myServer.addMessageListener(match.getWhitePlayer(), MessageFromClient.class,MessageFromServer.class);
                myServer.addMessageListener(match.getBlackPlayer(), MessageFromClient.class,MessageFromServer.class);

                match.getWhitePlayer().start();
                match.getBlackPlayer().start();

//                playerWhite.start();
//                playerBlack.start();
                
                
                
                gotFirstPlayer = false;
                gotSecondPlayer = false;
//					System.out.println(java.lang.Thread.activeCount());
                matchNumber++;
                newestConnection++;

//                } catch (IOException e) {
//                    System.out.println("IOError");
//                }
    }

    public void connectionAdded(Server server, HostedConnection conn) {
        System.out.println("Client Connected: " + conn.getId());
        int numberOfConnections = conn.getId() + 1;
                System.out.println("number of connectons: " + numberOfConnections);

        if(numberOfConnections > 0 && numberOfConnections % 2 != 0 ){
            gotFirstPlayer = true;
        }
        if(numberOfConnections > 0 && numberOfConnections % 2 == 0 ){
            gotSecondPlayer= true;
            newestConnection++;
        }

    }

    public void connectionRemoved(Server server, HostedConnection conn) {
                System.out.println("Client out: " + conn.getId());

        conn.close("");
    }
    
     @Override
     public void destroy() {
      myServer.close();
      super.destroy();
  }
     
}

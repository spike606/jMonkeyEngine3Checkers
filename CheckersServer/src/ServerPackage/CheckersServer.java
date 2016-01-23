package ServerPackage;

import CommonPackageServer.CheckersMove;
import CommonPackageServer.MessageFromClient;
import CommonPackageServer.MessageFromServer;
import com.jme3.app.SimpleApplication;
import com.jme3.network.ConnectionListener;
import com.jme3.network.HostedConnection;
import com.jme3.network.Network;
import com.jme3.network.Server;
import com.jme3.network.serializing.Serializer;
import com.jme3.system.AppSettings;
import com.jme3.system.JmeContext;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * test
 *
 * @author normenhansen
 */
public class CheckersServer extends SimpleApplication implements ConnectionListener {

    //logger
    private static final Logger logger = Logger.getLogger(CheckersServer.class.getName());
    private static final int SERVER_PORT = 8901;
    private static int matchNumber = 1;
    private static Server myServer;
    private static boolean gotFirstPlayer = false;//pomocnicze
    private static boolean gotSecondPlayer = false;
    private static int newestConnection = 0; // pomocnicza do zdobycia najnowszego polaczenia

    public static void main(String[] args) {

        Serializer.registerClass(MessageFromClient.class);//konieczna serializacja wiadomosci
        Serializer.registerClass(MessageFromServer.class);
        Serializer.registerClass(CheckersMove.class);

        CheckersServer app = new CheckersServer();
        app.start(JmeContext.Type.Headless);//aplikacja startuje bez okna
    }

    @Override
    public void simpleInitApp() {
        try {
            myServer = Network.createServer(SERVER_PORT);
            logger.log(Level.INFO, "CheckersGame server is running...");
            myServer.start();
            myServer.addConnectionListener(new CheckersServer());

        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void simpleUpdate(float tpf) {
        //TODO: add update code

        Match match = new Match(matchNumber);
        logger.log(Level.INFO, "Waiting for players...");
        logger.log(Level.INFO, "Number of threads {0}", Thread.activeCount());
        logger.log(Level.INFO, "number of connections #{0}", myServer.getConnections().size());

        
//         HashMap hm = new HashMap();
//         hm.
        
        
        
        
        
        while (gotFirstPlayer == false) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                logger.log(Level.SEVERE, null, ex);
            }
//        logger.log(Level.INFO, "Number of threads1 {0}", Thread.activeCount());

        }
        match.setWhitePlayer(new Player(myServer, myServer.getConnection(newestConnection), GameData.WHITE, match));
        logger.log(Level.INFO, "Match #{0}: player #1 connected.", matchNumber);
//        logger.log(Level.INFO, "Number of threads2 {0}", Thread.activeCount());
					System.out.println("Connection!!!!:" + myServer.getConnection(newestConnection));

        while (gotSecondPlayer == false) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                logger.log(Level.SEVERE, null, ex);
            }
//        logger.log(Level.INFO, "Number of threads3 {0}", Thread.activeCount());

        }
        match.setBlackPlayer(new Player(myServer, myServer.getConnection(newestConnection), GameData.BLACK, match));
//        logger.log(Level.INFO, "Number of threads4 {0}", Thread.activeCount());

        logger.log(Level.INFO, "Match #{0}: player #2 connected.", matchNumber);

        //ustaw dane dotyczace swoich przeciwnikow
        match.getWhitePlayer().setOpponentHostedConnection(match.getBlackPlayer().getMyHostedConnection());
        match.getBlackPlayer().setOpponentHostedConnection(match.getWhitePlayer().getMyHostedConnection());

        //register server listener
        myServer.addMessageListener(match.getWhitePlayer(), MessageFromClient.class, MessageFromServer.class);
        myServer.addMessageListener(match.getBlackPlayer(), MessageFromClient.class, MessageFromServer.class);

        match.getWhitePlayer().start();
        match.getBlackPlayer().start();

        gotFirstPlayer = false;
        gotSecondPlayer = false;
        matchNumber++;
        newestConnection++;

    }

    public void connectionAdded(Server server, HostedConnection conn) {
        logger.log(Level.INFO, "Client Connected: {0}", conn.getId());
//        int numberOfConnections = conn.getId() + 1;    
        int numberOfConnections = server.getConnections().size();

        logger.log(Level.INFO, "number of connectons: : {0}", numberOfConnections);


        if (numberOfConnections > 0 && numberOfConnections % 2 != 0) {
            gotFirstPlayer = true;
        }
        if (numberOfConnections > 0 && numberOfConnections % 2 == 0) {
            gotSecondPlayer = true;
            newestConnection++;
        }

    }

    public void connectionRemoved(Server server, HostedConnection conn) {
        logger.log(Level.INFO, "Client out: {0}", conn.getId());
        
        //gdy polaczenie zakonczone to przeciwnik wygrywa
    
        
        
//	System.out.println("Close 1");
//
//        conn.close("");



    }

    @Override
    public void destroy() {
        myServer.close();
        super.destroy();
    }
}

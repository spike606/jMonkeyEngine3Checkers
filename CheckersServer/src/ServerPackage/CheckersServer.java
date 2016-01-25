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
import com.jme3.system.JmeContext;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CheckersServer extends SimpleApplication implements ConnectionListener {

    //logger
    private static final Logger logger = Logger.getLogger(CheckersServer.class.getName());
    private static final int SERVER_PORT = 8902;
    private static int matchNumber = 1;
    private static Server myServer;
    private static HashMap activeConnectionsMap;//map with currently active clients, key - connection id, value - true/false - in match?
    private static int numberOfConnections;//number of active connections

    public static void main(String[] args) {

        Serializer.registerClass(MessageFromClient.class);
        Serializer.registerClass(MessageFromServer.class);//jme3 requires serialization
        Serializer.registerClass(CheckersMove.class);

        CheckersServer app = new CheckersServer();
        app.start(JmeContext.Type.Headless);//app with no windows
    }

    @Override
    public void simpleInitApp() {
        try {
            activeConnectionsMap = new HashMap();
            myServer = Network.createServer(SERVER_PORT);
            myServer.start();
            logger.log(Level.INFO, "CheckersGame server is running...");
            logger.log(Level.INFO, "Waiting for players...");
            myServer.addConnectionListener(new CheckersServer());

        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void simpleUpdate(float tpf) {


        if (numberOfConnections > 0) {

            Iterator i = activeConnectionsMap.entrySet().iterator();

            while (i.hasNext()) {
                Map.Entry firstPlayer = (Map.Entry) i.next();

                if (firstPlayer.getValue().equals(false)) {//when find player with no opponent start looking for opponent
                    Iterator j = activeConnectionsMap.entrySet().iterator();
                    while (j.hasNext()) {
                        Map.Entry secondPlayer = (Map.Entry) j.next();
                        if (secondPlayer.getValue().equals(false) && firstPlayer.getValue().equals(false)//create a match
                                && (Integer) secondPlayer.getKey() != (Integer) firstPlayer.getKey()) {
                            
                            activeConnectionsMap.put(firstPlayer.getKey(), true);//now players have opponent
                            activeConnectionsMap.put(secondPlayer.getKey(), true);

                            Match match = new Match(matchNumber);

                            match.setWhitePlayer(new Player(myServer, myServer.getConnection((Integer) firstPlayer.getKey()), GameData.WHITE, match));
                            logger.log(Level.INFO, "Match #{0}: player #1 connected.", matchNumber);
                            match.setBlackPlayer(new Player(myServer, myServer.getConnection((Integer) secondPlayer.getKey()), GameData.BLACK, match));
                            logger.log(Level.INFO, "Match #{0}: player #2 connected.", matchNumber);

                            //set info about opponent
                            match.getWhitePlayer().setOpponentHostedConnection(match.getBlackPlayer().getMyHostedConnection());
                            match.getBlackPlayer().setOpponentHostedConnection(match.getWhitePlayer().getMyHostedConnection());

                            //register server listener
                            myServer.addMessageListener(match.getWhitePlayer(), MessageFromClient.class, MessageFromServer.class);
                            myServer.addMessageListener(match.getBlackPlayer(), MessageFromClient.class, MessageFromServer.class);

                            match.getWhitePlayer().start();
                            match.getBlackPlayer().start();
                            matchNumber++;

                            break;
                        }
                    }
                }
            }
        }
    }

    public void connectionAdded(Server server, HostedConnection conn) {
        
        logger.log(Level.INFO, "Client Connected: {0}", conn.getId());

        activeConnectionsMap.put(conn.getId(), false);//put client to the list

        numberOfConnections = server.getConnections().size();

        logger.log(Level.INFO, "Number of connectons: {0}", numberOfConnections);
    }

    public void connectionRemoved(Server server, HostedConnection conn) {
        logger.log(Level.INFO, "Client out: {0}", conn.getId());

        activeConnectionsMap.remove(conn.getId());//remove client from map

        numberOfConnections = server.getConnections().size();

        logger.log(Level.INFO, "Number of connectons: : {0}", numberOfConnections);
    }

    @Override
    public void destroy() {
        myServer.close();
        super.destroy();
    }
}

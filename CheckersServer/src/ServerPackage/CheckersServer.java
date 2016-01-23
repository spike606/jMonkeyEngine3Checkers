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
import java.util.Iterator;
import java.util.Map;
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
    private static HashMap activeConnectionsMap;//mapa z aktywnymi polaczeniami/ aktywnymi w danej chwili klientami
    private static int numberOfConnections;//liczba polaczen aktywych

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
        //TODO: add update code

//        Match match = new Match(matchNumber);
//        logger.log(Level.INFO, "Waiting for players...");
//        logger.log(Level.INFO, "Number of threads {0}", Thread.activeCount());
        logger.log(Level.INFO, "number of connections #{0}", myServer.getConnections().size());


        //sprawdz czy liczba polaczen jest parzysta
        if (numberOfConnections > 0) {

            Iterator i = activeConnectionsMap.entrySet().iterator();

            while (i.hasNext()) {
                Map.Entry firstPlayer = (Map.Entry) i.next();

                if (firstPlayer.getValue().equals(false)) {//gdy znajde gracza ktory nie ma przeciwnika to poszukaj dla niego pary
                    Iterator j = activeConnectionsMap.entrySet().iterator();
                    while (j.hasNext()) {
                        Map.Entry secondPlayer = (Map.Entry) j.next();
                        if (secondPlayer.getValue().equals(false) && firstPlayer.getValue().equals(false)//jezeli znajde pare wolnych to nowy mecz
                                && (Integer) secondPlayer.getKey() != (Integer) firstPlayer.getKey()) {
                            //uaktualnij dane w mapie
                            activeConnectionsMap.put(firstPlayer.getKey(), true);
                            activeConnectionsMap.put(secondPlayer.getKey(), true);

                            //nowy mecz

                            Match match = new Match(matchNumber);


                            match.setWhitePlayer(new Player(myServer, myServer.getConnection((Integer) firstPlayer.getKey()), GameData.WHITE, match));
                            logger.log(Level.INFO, "Match #{0}: player #1 connected.", matchNumber);
                            match.setBlackPlayer(new Player(myServer, myServer.getConnection((Integer) secondPlayer.getKey()), GameData.BLACK, match));
                            logger.log(Level.INFO, "Match #{0}: player #2 connected.", matchNumber);


                            //ustaw dane dotyczace swoich przeciwnikow
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
                        System.out.println("1");

                    }
                    System.out.println("2");
                }

                System.out.println("3");


//                i.remove(); // avoids a ConcurrentModificationException
            }
        }

//        while (gotSecondPlayer == false) {
//            try {
//                Thread.sleep(100);
//            } catch (InterruptedException ex) {
//                logger.log(Level.SEVERE, null, ex);
//            }
//    }


    }

    public void connectionAdded(Server server, HostedConnection conn) {
        logger.log(Level.INFO, "Client Connected: {0}", conn.getId());
//        int numberOfConnections = conn.getId() + 1;    

        activeConnectionsMap.put(conn.getId(), false);//id klienta i false - nie jest sparowany z przeciwnikiem

        numberOfConnections = server.getConnections().size();

        logger.log(Level.INFO, "number of connectons: : {0}", numberOfConnections);


    }

    public void connectionRemoved(Server server, HostedConnection conn) {
        logger.log(Level.INFO, "Client out: {0}", conn.getId());

        //polaczenie zakonczone wiec usun klienta z listy aktywnych
        activeConnectionsMap.remove(conn.getId());

        //uaktualnij liczbe polaczen
        numberOfConnections = server.getConnections().size();

        logger.log(Level.INFO, "number of connectons: : {0}", numberOfConnections);

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

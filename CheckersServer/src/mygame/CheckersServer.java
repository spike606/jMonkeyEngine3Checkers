package mygame;

import CommonPackage.MessageFromClient;
import CommonPackage.MessageFromServer;
import com.jme3.app.SimpleApplication;
import com.jme3.network.Network;
import com.jme3.network.Server;
import com.jme3.network.serializing.Serializer;
import com.jme3.renderer.RenderManager;
import com.jme3.system.JmeContext;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * test
 * @author normenhansen
 */
public class CheckersServer extends SimpleApplication {

    public static void main(String[] args) {
        
        Serializer.registerClass(MessageFromClient.class);//konieczna serializacja wiadomosci
        Serializer.registerClass(MessageFromServer.class);
        
        
        
        CheckersServer app = new CheckersServer();
        app.start(JmeContext.Type.Headless);
    }

    @Override
    public void simpleInitApp() {
        try {
            Server myServer = Network.createServer(6143);
            myServer.start();
        } catch (IOException ex) {
            Logger.getLogger(CheckersServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void simpleUpdate(float tpf) {
        //TODO: add update code
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }
}

package in.bits.sochat.server;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * ServerWrapper contains the 'main' function of the application and is responsible to start the execution of the server.
 */
public class ServerWrapper {
    public static void main(String[] args) {
        int port = Integer.parseInt(args[0]);

        try {
            Server server = new Server(port);
        } catch (IOException ex) {
            Logger.getLogger(ServerWrapper.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

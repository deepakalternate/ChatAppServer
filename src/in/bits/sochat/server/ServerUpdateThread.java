package in.bits.sochat.server;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * ServerUpdateThread is responsible for keeping the list of online users up-to-date on the client side.
 */
public class ServerUpdateThread implements Runnable{
    private Thread thread;
    private Server server;

    public ServerUpdateThread(Server server) {
        this.server = server;
        thread = new Thread(this);
        thread.start();
    }
    
    /**
     * Thread sends updated client list to users at regular intervals.
     */
       
    @Override
    public void run() {
        
        while(true){
          try {
                Thread.sleep(5000);
                server.sendClientList();
            } catch (InterruptedException ex) {
                Logger.getLogger(ServerUpdateThread.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    
}

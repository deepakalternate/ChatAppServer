package in.bits.sochat.server;

import in.bits.sochat.bean.Message;
import java.net.Socket;

/**
 * 
 * Interface that defines the structure of the Server class.
 */
public interface ServerInterface {
    public void listen(int port);
    public void broadcast(Message message);
    public void closeConnection(Socket socket);
    public void sendClientList();
    public void unicast(Message message);
}

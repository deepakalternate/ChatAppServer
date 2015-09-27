package in.bits.sochat.server;

import in.bits.sochat.bean.Message;
import in.bits.sochat.bean.Type;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Time;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 
 * Server class contains functions which are called by the ServerThread class to perform server functions.
 */


public class Server implements ServerInterface {

    private ServerSocket serverSocket;
    private HashMap<Socket, ObjectOutputStream> clients;
    private HashMap<String, Socket> clientList;
    private ServerUpdateThread sut;
    
    /**
     * Constructor of the Server Class.
     * Initiates the hash maps 'clients' and 'clientList'.
     * 'clients' is used to store the object output stream related to a certain socket.
     * 'clientList' is used to store the username corresponding to the socket of a client.
     * Constructor calls the listen function in the Server class.
     * @param port is used to start listening on a certain port.
     * @throws IOException 
     */
    
    public Server(int port) throws IOException {
        clients = new HashMap<>();
        clientList = new HashMap<>();

        listen(port);
    }
    
    /**
     * Setter for the hash map clientList
     * @return the clientList
     */

    public HashMap<String, Socket> getClientList() {
        return clientList;
    }
    
    /**
     * listen opens a port and starts listening for communicating clients.
     * @param port is used to open a socket on a certain port.
     */

    @Override
    public void listen(int port) {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Listening on port :" + port);
            sut = new ServerUpdateThread(this);
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("Connected to :" + socket);
                ObjectOutputStream buf = new ObjectOutputStream(socket.getOutputStream());
                clients.put(socket, buf);
                new ServerThread(this, socket);
            }

        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    
    /**
     * broadcast function broadcasts the message to all the users connected to the group chat.
     * @param message is what is sent to all the users.
     */

    @Override
    public synchronized void broadcast(Message message) {
        message.setTime(new Time(System.currentTimeMillis()));
        for (Map.Entry<Socket, ObjectOutputStream> entry : clients.entrySet()) {
            try {
                entry.getValue().writeObject(message);
            } catch (IOException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    /**
     * Closes the socket of an exiting client.
     * @param socket to determine what client is exiting.
     */

    @Override
    public void closeConnection(Socket socket) {
        try {
            clients.get(socket).close();
            clients.remove(socket);
            String name = "";
            for (Map.Entry<String, Socket> entry : clientList.entrySet()) {
                if (entry.getValue().equals(socket)) {
                    name = entry.getKey();
                    break;
                }
            }

            clientList.remove(name);

        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                socket.close();

            } catch (IOException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }
    
    /**
     * Used to send the updated list of online users to all participating users.
     */

    @Override
    public synchronized void sendClientList() {
        String list = "";

        for (Map.Entry<String, Socket> entry : clientList.entrySet()) {
            list += entry.getKey() + ",";
        }
        System.out.println("Sending List-->" + list);
        broadcast(new Message(Type.LIST, "server", list, null, null));

    }
    
    /**
     * Used to send messages to a specific recipient, used for one on one communication.
     * @param message is what is sent to the specified recipient.
     */

    @Override
    public void unicast(Message message) {
        try {
            clients.get(clientList.get(message.getReceiver())).writeObject(message);
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
 * @author tarun
 */
public class Server implements ServerInterface {

    private ServerSocket serverSocket;
    private HashMap<Socket, ObjectOutputStream> clients;
    private HashMap<String, Socket> clientList;
    private ServerUpdateThread sut;

    public Server(int port) throws IOException {
        clients = new HashMap<>();
        clientList = new HashMap<>();

        listen(port);
    }

    public HashMap<String, Socket> getClientList() {
        return clientList;
    }

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

    @Override
    public synchronized void sendClientList() {
        String list = "";

        for (Map.Entry<String, Socket> entry : clientList.entrySet()) {
            list += entry.getKey() + ",";
        }
        System.out.println("Sending List-->" + list);
        broadcast(new Message(Type.LIST, "server", list, null, null));

    }

    @Override
    public void unicast(Message message) {
        try {
            clients.get(clientList.get(message.getReceiver())).writeObject(message);
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
}

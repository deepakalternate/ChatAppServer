/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package in.bits.sochat.server;

import in.bits.sochat.bean.Message;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author tarun
 */
class ServerThread extends Thread {

    private Server server;
    private Socket socket;

    public ServerThread(Server server, Socket socket) {
        this.server = server;
        this.socket = socket;
        
        start();
    }

    public void run() {
        try {
            ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
            while (true) {
                
                Message message = (Message)input.readObject();
                System.out.println("Message received --->"+message.getMessage()+"\n"+"From ---->"+message.getUser());
                
                if(message.getType().getTypeOfMessage().equalsIgnoreCase("CHAT")){
                    
                    System.out.println("Sending Message--->"+message.getMessage()+"\n"+"From ---->"+message.getUser());
                    server.broadcast(message);
                    
                }else if(message.getType().getTypeOfMessage().equalsIgnoreCase("HELLO")){
                    
                    System.out.println("HELLO Message received --->"+message.getMessage()+"\n"+"From ---->"+message.getUser());
                    server.getClientList().put(message.getUser(), socket);
                    System.out.println(server.getClientList());
                    
                } else if(message.getType().getTypeOfMessage().equalsIgnoreCase("UNICAST") || message.getType().getTypeOfMessage().equalsIgnoreCase("ACCEPT") || message.getType().getTypeOfMessage().equalsIgnoreCase("REJECT") || message.getType().getTypeOfMessage().equalsIgnoreCase("REQUEST") || message.getType().getTypeOfMessage().equalsIgnoreCase("DISCONNECT")){
                    
                    server.unicast(message);
                    
                } else if(message.getType().getTypeOfMessage().equalsIgnoreCase("LOGOUT")){
                    
                    server.closeConnection(socket);
                }
                
            }

        } catch (IOException ex) {
            Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
        }finally{
            server.closeConnection(socket);
        }

    }

}

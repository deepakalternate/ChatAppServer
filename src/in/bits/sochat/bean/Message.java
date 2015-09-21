package in.bits.sochat.bean;

import java.io.Serializable;
import java.sql.Time;

/**
 *
 * @author tarun
 */
public class Message implements Serializable{
    
    private Type type;
    private String user;
    private String message;
    private Time time;
    private String receiver;

    public Message(Type type, String user, String message, Time time, String receiver) {
        this.type = type;
        this.user = user;
        this.message = message;
        this.time = time;
        this.receiver = receiver;
    }
    
    
    
    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    
    
    public Time getTime() {
        return time;
    }

    public void setTime(Time time) {
        this.time = time;
    }

    
    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return user + ": ("+ time + ") : " + message;
    }

    /**
     * @return the receiver
     */
    public String getReceiver() {
        return receiver;
    }

    /**
     * @param receiver the receiver to set
     */
    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }
    
 }
